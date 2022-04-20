package testlib.components;

import testlib.core.TestContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record Generator (
    TestContext testContext,
    List<Class<?>> classes,
    List<Supplier<List<Entity>>> argumentSuppliers
) implements Supplier<List<Entity>> {
    @Override
    public List<Entity> get () {
        final List<List<Entity>> arguments = new ArrayList<>();

        for (final Supplier<List<Entity>> supplier : argumentSuppliers) {
            final List<Entity> currentArguments = supplier.get();
            assert currentArguments.size() == classes.size();

            arguments.add(currentArguments);
        }

        final List<Entity> result = new ArrayList<>();
        String className = null;
        String presentation = null;

        for (int i = 0; i < classes.size(); i ++)
            try {
                final Object[] argumentValues = new Object[argumentSuppliers.size()];
                final Class<?>[] argumentClasses = new Class[argumentSuppliers.size()];
                final String[] argumentPresentations = new String[argumentSuppliers.size()];

                for (int j = 0; j < argumentSuppliers.size(); j ++) {
                    argumentValues[j] = arguments.get(j).get(i).instance();
                    argumentClasses[j] = arguments.get(j).get(i).instanceClass();
                    argumentPresentations[j] = arguments.get(j).get(i).presentation();
                }

                // Запоминаем имя класса и презентацию на случай ошибки
                className = classes.get(i).getSimpleName();
                presentation = String.format(
                    "%s(%s)",
                    className,
                    Arrays.stream(argumentClasses).map(Class::getSimpleName).collect(Collectors.joining(", "))
                );

                // Инстанцируем класс
                result.add(new Entity(
                    classes.get(i).getDeclaredConstructor(argumentClasses).newInstance(argumentValues),
                    classes.get(i),
                    String.format("new %s(%s)", className, String.join(", ", argumentPresentations))
                ));
            } catch (NoSuchMethodException exception) {
                testContext.testFailed(String.format("Constructor %s not found", presentation));
            } catch (InstantiationException exception) {
                testContext.testFailed(String.format("Can't create an instance of %s", className));
            } catch (IllegalAccessException exception) {
                testContext.testFailed(String.format("Constructor %s is inaccessible", presentation));
            } catch (InvocationTargetException exception) {
                testContext.testFailed(String.format("Constructor %s has thrown an exception", presentation));
            } catch (IllegalArgumentException exception) {
                testContext.testFailed(String.format("Constructor of %s receives wrong arguments", className));
            }

        return result;
    }
}
