package testlib.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class Tester {
    public static void test (final Class<?> testsClass, final Path[] jars, final String[][] classNames) {
        final TestContext testContext = new TestContext(jars, classNames);

        Arrays
            .stream(testsClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Test.class))
            .sorted(Comparator.comparingInt(method -> method.getAnnotation(Test.class).number()))
            .forEach(method -> {
                if (!Modifier.isStatic(method.getModifiers()))
                    testContext.testCrashed(String.format("Method %s is not static", method.getName()));

                if (!Arrays.equals(method.getParameterTypes(), new Class<?>[] { TestContext.class }))
                    testContext.testCrashed(String.format(
                        "Method %s should receive testlib.core.TestContext instance", method.getName()));

                final Test testAnnotation = method.getAnnotation(Test.class);
                System.out.printf("TEST %d (%s) STARTED\n", testAnnotation.number(), testAnnotation.name());

                for (int i = 0; i < testAnnotation.repeat(); i ++) {
                    try {
                        method.invoke(null, testContext);
                    } catch (IllegalAccessException exception) {
                        testContext.testCrashed("Can't access method " + method.getName());
                    } catch (InvocationTargetException exception) {
                        testContext.testCrashed(String.format(
                            "Method %s has thrown an exception\n%s",
                            method.getName(),
                            exception.getTargetException()
                        ));
                    }

                    testContext.clearLog();
                }
            });

        System.out.println("ALL TESTS PASSED");
    }
}
