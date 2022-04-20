package testlib.components;

import java.util.List;

public record MethodCall (String name, List<Entity> arguments) {
    public MethodCall (final String name) { this(name, List.of()); }
}
