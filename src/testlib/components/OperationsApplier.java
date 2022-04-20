package testlib.components;

import testlib.core.TestContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record OperationsApplier (TestContext testContext, List<MethodCall> operations) {
    public boolean hasNext () { return !operations().isEmpty(); }

    public void apply (final String instanceName, final List<Entity> entities) {
        if (!hasNext())
            return;

        final MethodCall operation = operations.get(0);

        final Class<?>[] argumentClasses =
            operation.arguments().stream().map(Entity::instanceClass).toArray(Class<?>[]::new);

        final Object[] argumentValues =
            operation.arguments().stream().map(Entity::instance).toArray(Object[]::new);

        final String[] argumentPresentations =
            operation.arguments().stream().map(Entity::presentation).toArray(String[]::new);

        final String joinedArgumentClasses =
            Arrays.stream(argumentClasses).map(Class::getSimpleName).collect(Collectors.joining(", "));
        final String joinedArgumentPresentation = String.join(", ", argumentPresentations);

        testContext.logLine(String.format("%s.%s(%s);", instanceName, operation.name(), joinedArgumentPresentation));

        for (final Entity entity : entities) {
            final Object instance = entity.instance();
            final Class<?> instanceClass = entity.instanceClass();

            final String fullMethodName = String.format("%s::%s", instanceClass.getSimpleName(), operation.name());
            final String fullMethodNameWithArgumentClasses =
                String.format("%s(%s)", fullMethodName, joinedArgumentClasses);
            final String fullMethodNameWithArgumentPresentations =
                String.format("%s(%s)", fullMethodName, joinedArgumentPresentation);

            try {
                instanceClass.getMethod(operations.get(0).name(), argumentClasses).invoke(instance, argumentValues);
            } catch (NoSuchMethodException exception) {
                testContext.testFailed(String.format(
                    "No method %s found",
                    fullMethodNameWithArgumentClasses
                ));
            } catch (IllegalAccessException exception) {
                testContext.testFailed(String.format(
                    "Method %s is inaccessible",
                    fullMethodNameWithArgumentClasses
                ));
            } catch (InvocationTargetException exception) {
                testContext.testFailed(String.format(
                    "Method %s has thrown an exception",
                    fullMethodNameWithArgumentPresentations
                ));
            } catch (IllegalArgumentException exception) {
                testContext.testFailed(String.format(
                    "Method %s receives wrong arguments",
                    fullMethodNameWithArgumentClasses
                ));
            }
        }

        operations.remove(0);
    }

    public static OperationsApplier empty (TestContext testContext) {
        return new OperationsApplier(testContext, new ArrayList<>());
    }
}
