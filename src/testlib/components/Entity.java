package testlib.components;

public record Entity (Object instance, Class<?> instanceClass, String presentation) {
    public Entity (final Object value, final Class<?> type) { this(value, type, value.toString()); }
}
