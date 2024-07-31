package net.quelfth.gigatubes.util.code;

public class CodeFormats {
    public static final byte DEFAULT = 0;
    public static final byte LINE_COMMENT = 1;
    public static final byte BLOCK_COMMENT = 2;
    public static final byte PARENTHESES = 3;
    public static final byte LOGICAL = 4;
    public static final byte KEYWORD = 5;
    public static final byte WILDCARD = 6;
    public static final byte NAMESPACE = 7;
    public static final byte IDENTIFIER = 8;
    

    public static final byte ERROR = -1;

    public static int colorOf(byte format) {
        switch (format) {
            case LINE_COMMENT, BLOCK_COMMENT -> {return 0xffffff;} 
            case PARENTHESES -> {return 0x969696;}
            case LOGICAL -> {return 0x3c84bb;}
            case KEYWORD -> {return 0xb00c0c;}
            case WILDCARD -> {return 0x00ff00;}
            case NAMESPACE -> {return 0x7789ff;}
            case IDENTIFIER -> {return 0xbfffea;}


            case ERROR -> {return 0xff0000;}
            
            default -> {return 0xc3c6cb;}
        }
    }
}
