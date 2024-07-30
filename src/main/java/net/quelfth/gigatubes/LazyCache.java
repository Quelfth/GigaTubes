package net.quelfth.gigatubes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

public class LazyCache<T> {
    private @Nullable T value;
    private final Supplier<T> supplier;

    public LazyCache(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public void invalidate() {
        value = null;
    }
}
