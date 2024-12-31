package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.GhApiClient;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;

/**
 * Суть экстеншена в автоматическом задизэбливании тестов, если указана аннотация @DisabledByIssue(номер issue)
 * 1. Ищем аннотацию на тестом/классом/родительским классом
 * 2. Если находим, то по апи делаем запрос в гитхаб о статусе этой issue
 * 3. Если она в статусе "open", то тест не запустится
 * 4. Если НЕ в статусе "open", то запустится
 * 5. Если аннотация не будет найдена, то тест будет запускаться по определнию как обычно
 */

public class IssueExtension implements ExecutionCondition {

    private final GhApiClient ghApiClient = new GhApiClient();

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return AnnotationSupport.findAnnotation( // ищем аннотацию над тестом
                        context.getRequiredTestMethod(),
                        DisabledByIssue.class)
                .or(() -> AnnotationSupport.findAnnotation( // если не находим над тестом, то ищем над классом
                        context.getRequiredTestClass(),
                        DisabledByIssue.class,
                        SearchOption.INCLUDE_ENCLOSING_CLASSES) // ищем и над родительскими супер классами
                )
                .map(
                        byIssue -> "open".equals(ghApiClient.issueState(byIssue.value()))
                                ? ConditionEvaluationResult.disabled("Disabled by issue: " + byIssue.value())
                                : ConditionEvaluationResult.enabled("Issue closed")
                ).orElseGet(
                        () -> ConditionEvaluationResult.enabled("Annotation  @DisabledByIssue not found")
                );
    }
}
