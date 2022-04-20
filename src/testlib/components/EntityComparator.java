package testlib.components;

import testlib.core.TestContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record EntityComparator(
    TestContext testContext,
    List<EntityComparatorRecord> entityComparatorRecords
) {
    public void compare (final String instanceName, final List<Entity> entities) {
        List<List<Entity>> converted = entities.stream().map(this::convert).collect(Collectors.toList());

        for (int i = 1; i < converted.size(); i ++)
            for (int j = 0; j < entityComparatorRecords.size(); j ++)
                if (!entityComparatorRecords.get(j).comparator().test(
                    converted.get(0).get(j).instance(),
                    converted.get(i).get(j).instance()
                ))
                    testContext.testFailed(
                        String.format(
                            "%s%s returned %s",
                            instanceName,
                            converted.get(0).get(j).presentation(),
                            converted.get(0).get(j).instance()
                        ),
                        String.format(
                            "%s%s returned %s",
                            instanceName,
                            converted.get(i).get(j).presentation(),
                            converted.get(i).get(j).instance()
                        ),
                        entityComparatorRecords.get(j).reason()
                    );
    }

    private List<Entity> convert (final Entity entity) {
        List<Entity> result = new ArrayList<>();

        for (final EntityComparatorRecord currentEntityComparatorRecord : entityComparatorRecords) {
            final List<MethodCall> currentMethodCallChain = currentEntityComparatorRecord.methodCallChain();
            Object current = entity.instance();
            final StringBuilder presentationChain = new StringBuilder();

            for (final MethodCall currentMethodCall : currentMethodCallChain) {
                final String methodName = currentMethodCall.name();
                final int argumentsCount = currentMethodCall.arguments().size();
                final Object[] argumentValues = new Object[argumentsCount];
                final Class<?>[] argumentClasses = new Class<?>[argumentsCount];
                final String[] argumentPresentations = new String[argumentsCount];

                for (int j = 0; j < currentMethodCall.arguments().size(); j ++) {
                    argumentValues[j] = currentMethodCall.arguments().get(j).instance();
                    argumentClasses[j] = currentMethodCall.arguments().get(j).instanceClass();
                    argumentPresentations[j] = currentMethodCall.arguments().get(j).presentation();
                }

                final String joinedArgumentsPresentation = String.join(", ", argumentPresentations);
                presentationChain.append(String.format(".%s(%s)", methodName, joinedArgumentsPresentation));

                // Подготавливаем различные презентации на случай ошибки
                final String presentation = String.format("%s::%s", current.getClass().getSimpleName(), methodName);
                final String presentationClasses = String.format(
                    "%s(%s)",
                    presentation,
                    Arrays.stream(argumentClasses).map(Class::getSimpleName).collect(Collectors.joining(", "))
                );
                final String presentationValues = String.format("%s(%s)", presentation, joinedArgumentsPresentation);

                try {
                    current = current.getClass().getMethod(methodName, argumentClasses).invoke(current, argumentValues);
                } catch (NoSuchMethodException exception) {
                    testContext.testFailed(String.format("No method %s found", presentationClasses));
                } catch (IllegalAccessException exception) {
                    testContext.testFailed(String.format("Method %s is inaccessible", presentationClasses));
                } catch (InvocationTargetException exception) {
                    testContext.testFailed(String.format(
                        "Method %s has thrown an exception: %s",
                        presentationValues,
                        exception.getTargetException().toString()
                    ));
                } catch (IllegalArgumentException exception) {
                    testContext.testFailed(String.format("Method %s receives wrong arguments", presentation));
                }
            }

            result.add(new Entity(current, current.getClass(), presentationChain.toString()));
        }

        return result;
    }
}
