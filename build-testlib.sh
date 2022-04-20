cd src

javac \
testlib/components/Entity.java \
testlib/components/EntityComparator.java \
testlib/components/EntityComparatorRecord.java \
testlib/components/Generator.java \
testlib/components/MethodCall.java \
testlib/components/OperationsApplier.java \
testlib/core/Test.java \
testlib/core/TestContext.java \
testlib/core/Tester.java

jar -cf ../testlib.jar \
testlib/components/Entity.class \
testlib/components/EntityComparator.class \
testlib/components/EntityComparatorRecord.class \
testlib/components/Generator.class \
testlib/components/MethodCall.class \
testlib/components/OperationsApplier.class \
testlib/core/Test.class \
testlib/core/TestContext.class \
testlib/core/Tester.class

unlink testlib/components/Entity.class
unlink testlib/components/EntityComparator.class
unlink testlib/components/EntityComparatorRecord.class
unlink testlib/components/Generator.class
unlink testlib/components/MethodCall.class
unlink testlib/components/OperationsApplier.class
unlink testlib/core/Test.class
unlink testlib/core/TestContext.class
unlink testlib/core/Tester.class

cd ../
