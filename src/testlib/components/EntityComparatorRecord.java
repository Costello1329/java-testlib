package testlib.components;

import java.util.List;
import java.util.function.BiPredicate;

public record EntityComparatorRecord (
    List<MethodCall> methodCallChain,
    BiPredicate<Object, Object> comparator,
    String reason
) {}
