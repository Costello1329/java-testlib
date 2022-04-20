javac \
src/testlib/components/Entity.java \
src/testlib/components/EntityComparator.java \
src/testlib/components/EntityComparatorRecord.java \
src/testlib/components/Generator.java \
src/testlib/components/MethodCall.java \
src/testlib/components/OperationsApplier.java \
src/testlib/core/Test.java \
src/testlib/core/TestContext.java \
src/testlib/core/Tester.java

jar -cf testlib.jar \
src/testlib/components/Entity.class \
src/testlib/components/EntityComparator.class \
src/testlib/components/EntityComparatorRecord.class \
src/testlib/components/Generator.class \
src/testlib/components/MethodCall.class \
src/testlib/components/OperationsApplier.class \
src/testlib/core/Test.class \
src/testlib/core/TestContext.class \
src/testlib/core/Tester.class

unlink src/testlib/components/Entity.class
unlink src/testlib/components/EntityComparator.class
unlink src/testlib/components/EntityComparatorRecord.class
unlink src/testlib/components/Generator.class
unlink src/testlib/components/MethodCall.class
unlink src/testlib/components/OperationsApplier.class
unlink src/testlib/core/Test.class
unlink src/testlib/core/TestContext.class
unlink src/testlib/core/Tester.class
