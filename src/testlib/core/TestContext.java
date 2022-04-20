package testlib.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class TestContext {
    public ClassLoader getClassLoader (final Path jar) {
        try {
            return new URLClassLoader(new URL[]{ jar.toUri().toURL() });
        } catch (final MalformedURLException exception) {
            testCrashed(exception.getMessage());
        }

        return null;
    }

    public Class<?> loadClass (final ClassLoader classLoader, final Path jar, final String className) {
        try {
            return classLoader.loadClass(className);
        } catch (final ClassNotFoundException classNotFoundException) {
            testFailed(String.format("No class %s found in %s", className, jar));
        }

        return null;
    }

    public TestContext () {
        log = new StringBuilder();
        expected = null;
        got = null;
        reason = null;
        classesToTest = null;
    }

    public TestContext (final Path[] jars, final String[][] classNames) {
        super();
        classesToTest = new Class[classNames.length][jars.length];

        for (int i = 0; i < jars.length; i ++) {
            final ClassLoader classLoader = getClassLoader(jars[i]);

            for (int j = 0; j < classNames.length; j ++)
                classesToTest[j][i] = loadClass(classLoader, jars[i], classNames[i][j]);
        }
    }

    public void logLine (final String line) {
        log.append(line);
        log.append("\n");
    }

    public void clearLog () { log = new StringBuilder(); }

    public void testFailed (final String expected, final String got, final String reason) {
        this.expected = expected;
        this.got = got;
        this.reason = reason;

        System.out.println("TEST FAILED");
        final String[] recordNames = new String[] { "log: \n", "expected: ", "got: ", "reason: " };
        final String[] records =
            new String[] { getLog(), getExpected(), getGot(), getReason() };

        for (int j = 0; j < 4; j ++)
            if (records[j] != null && !records[j].isEmpty())
                System.out.println(recordNames[j] + records[j]);

        System.exit(1);
    }

    public void testCrashed (final String cause) {
        System.out.println("TEST CRASHED");
        System.out.println(cause);
        System.exit(1);
    }

    public void testFailed (final String expected, final String got) { testFailed(expected, got, null); }
    public void testFailed (final String reason) { testFailed(null, null, reason); }
    public void testFailed () { testFailed(null, null, null); }

    public String getLog () { return log.toString(); }
    public String getExpected () { return expected; }
    public String getGot () { return got; }
    public String getReason () { return reason; }
    public Class<?>[][] getClassesToTest () { return classesToTest; }

    private StringBuilder log;
    private String expected;
    private String got;
    private String reason;
    private final Class<?>[][] classesToTest;
}
