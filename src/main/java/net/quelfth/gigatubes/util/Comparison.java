package net.quelfth.gigatubes.util;

import javax.annotation.Nullable;

public enum Comparison {
    GREATER,
    LESS,
    EQUAL;

    @Nullable
    public static Comparison aggregate(@Nullable Comparison a, @Nullable Comparison b) {
        if (a == null || b == null)
            return null;
        switch (a) {
            case EQUAL -> {
                return b;
            }
            case GREATER -> {
                if (b == Comparison.LESS)
                    return null;
                return Comparison.GREATER;
            }
            case LESS -> {
                if (b == Comparison.GREATER)
                    return null;
                return Comparison.LESS;
            }
        }
        return null;
    }
}
