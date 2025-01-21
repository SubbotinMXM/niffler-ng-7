package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;

import java.util.Date;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendDbClient spendDbClient = new SpendDbClient();
    private final CategoryDbClient categoryDbClient = new CategoryDbClient();


    public static final Namespace NAMESPACE = Namespace.create(SpendingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.spendings().length > 0) {
                        Spending spendingAnno = userAnno.spendings()[0];

                        // Проверяем, существует ли категория
                        CategoryJson categoryJson = categoryDbClient.findCategoryByUsernameAndCategoryName(
                                userAnno.username(),
                                spendingAnno.category()
                        ).orElseGet(() -> { // Если категории нет, создаем её
                            CategoryJson newCategory = new CategoryJson(
                                    null,
                                    spendingAnno.category(),
                                    userAnno.username(),
                                    false
                            );
                            return categoryDbClient.createCategory(newCategory);
                        });


                        SpendJson spendJson = new SpendJson(
                                null,
                                new Date(),
                                categoryJson,
                                spendingAnno.currency(),
                                spendingAnno.amount(),
                                spendingAnno.description(),
                                userAnno.username()
                        );
                        SpendJson createdSpend = spendDbClient.createSpend(spendJson);
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpend);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}