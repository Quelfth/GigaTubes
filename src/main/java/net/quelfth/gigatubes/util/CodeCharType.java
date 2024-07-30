package net.quelfth.gigatubes.util;

import javax.annotation.Nullable;

public enum CodeCharType {
    IDENTIFIER,
    NUMERIC,
    OPERATOR,
    DELIMITER,
    ESCAPE,
    WHITESPACE;

    @Nullable
    public static CodeCharType of(char c) {
        if (c >= 'A' && c <= 'Z')
            return IDENTIFIER;
        if (c >= 'a' && c <= 'z')
            return IDENTIFIER;
        if (c == '_')
            return IDENTIFIER;
        if (c >= '0' && c <= '9')
            return NUMERIC;

        switch (c) {
            case 
                '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', '-', '_', '=', '+', '|', ':', ';', '\'', '"', ',', '.', '<', '>', '/', '?'
                -> { return OPERATOR; }
            case '(', ')', '[', ']', '{', '}'
                -> { return DELIMITER; }
            case '\\'
                -> { return ESCAPE; }
            case ' ', '\n'
                -> { return WHITESPACE; }
        }
        return null;
    }
}
