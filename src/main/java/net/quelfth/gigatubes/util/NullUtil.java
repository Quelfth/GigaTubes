package net.quelfth.gigatubes.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NullUtil {

    @Nonnull
    public static <T> T forgiveNull(@Nullable T value) {
        if (value == null)
            throw new NullPointerException("Tried to forgive a null value.");
        return value;
    }
}
