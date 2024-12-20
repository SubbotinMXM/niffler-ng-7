package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;

import java.util.Date;

import static org.junit.jupiter.api.extension.ExtensionContext.*;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class SpendExtension implements BeforeEachCallback, ParameterResolver {

    private final SpendApiClient spendApiClient = new SpendApiClient();

    public static final Namespace NAMESPACE = Namespace.create(SpendExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        findAnnotation(context.getRequiredTestMethod(), Spend.class)
                .ifPresent(anno -> {
                    SpendJson spendJson = new SpendJson(
                            null,
                            new Date(),
                            new CategoryJson(
                                    null,
                                    anno.category(),
                                    anno.username(),
                                    false
                            ),
                            anno.currency(),
                            anno.amount(),
                            anno.description(),
                            anno.username()
                    );
                    SpendJson createdSpend = spendApiClient.createSpend(spendJson);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpend);
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