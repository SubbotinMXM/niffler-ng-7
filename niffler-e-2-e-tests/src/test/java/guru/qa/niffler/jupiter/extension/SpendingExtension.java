package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;

import java.util.Date;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendApiClient spendApiClient = new SpendApiClient();

    public static final Namespace NAMESPACE = Namespace.create(SpendingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.spendings().length > 0) {
                        Spending spendingAnno = userAnno.spendings()[0];
                        SpendJson spendJson = new SpendJson(
                                null,
                                new Date(),
                                new CategoryJson(
                                        null,
                                        spendingAnno.category(),
                                        userAnno.username(),
                                        false
                                ),
                                spendingAnno.currency(),
                                spendingAnno.amount(),
                                spendingAnno.description(),
                                userAnno.username()
                        );
                        SpendJson createdSpend = spendApiClient.createSpend(spendJson);
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