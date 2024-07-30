package net.quelfth.gigatubes.util;

public final class Wrapper<T> {
    public T value;
    public Wrapper(T value) {
        this.value = value;
    }

    public Wrapper<T> clone() {
        return new Wrapper<>(value);
    }
}
