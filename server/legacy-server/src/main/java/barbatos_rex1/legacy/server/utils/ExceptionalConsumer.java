package barbatos_rex1.legacy.server.utils;

@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception> {
    void consume(T consumable) throws E;
}
