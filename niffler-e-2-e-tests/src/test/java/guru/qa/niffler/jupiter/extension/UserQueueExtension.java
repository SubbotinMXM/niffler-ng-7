package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class UserQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUESTS_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUESTS_USERS = new ConcurrentLinkedDeque<>();

    static {
        EMPTY_USERS.add(new StaticUser("MAKSIM22", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("VICTIM126", "12345", "MAKSIM", null, null));
        WITH_INCOME_REQUESTS_USERS.add(new StaticUser("GENA_INCOME", "12345", null, "VOVA_OUTCOME", null));
        WITH_OUTCOME_REQUESTS_USERS.add(new StaticUser("VOVA_OUTCOME", "12345", null, null, "GENA_INCOME"));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface UserType {
        Type value() default Type.EMPTY;
    }

    public enum Type{
        EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeEach(ExtensionContext context) throws Exception {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType().isAssignableFrom(StaticUser.class))
                .map(p -> p.getAnnotation(UserType.class))
                .forEach(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = switch (ut.value()) {
                            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUESTS_USERS.poll());
                            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUESTS_USERS.poll());
                        };
                    }
                    Allure.getLifecycle().updateTestCase(testCase -> {
                        testCase.setStart(new Date().getTime());
                    });
                    user.ifPresentOrElse(
                            u ->
                                    ((Map<UserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                                            context.getUniqueId(),
                                            key -> new HashMap<>()
                                    )).put(ut, u),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s.");
                            }
                    );
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterEach(ExtensionContext context) throws Exception {
        Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);
        if (userMap != null) {
            for (Map.Entry<UserType, StaticUser> entry : userMap.entrySet()) {
                switch (entry.getKey().value()){
                    case EMPTY -> EMPTY_USERS.add(entry.getValue());
                    case WITH_FRIEND -> WITH_FRIEND_USERS.add(entry.getValue());
                    case WITH_INCOME_REQUEST -> WITH_INCOME_REQUESTS_USERS.add(entry.getValue());
                    case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUESTS_USERS.add(entry.getValue());
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
               && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
                .get(
                        AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get()
                );
    }
}
