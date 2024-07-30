package net.quelfth.gigatubes.util.code;


public final class ParagraphFormat {
    private final byte[][] formats;

    public ParagraphFormat(final String[] lines) {
        this.formats = new byte[lines.length][];
        for (int i = 0; i < lines.length; i++) 
            this.formats[i] = new byte[lines[i].length()];
    }

    public ParagraphFormat(final CodeParagraph paragraph) {
        this.formats = new byte[paragraph.numLines()][];
        for (int i = 0; i < paragraph.numLines(); i++) 
            this.formats[i] = new byte[paragraph.lineLength(i)];
    }

    public byte get(int line, int pos) {
        if (!inRange(line, pos))
            return 0;
        return formats[line][pos];
    }

    public int getRunLength(int line, int pos) {
        if (!lineInRange(line))
            return 0;
        int i;
        for (i = 1; pos + i < formats[line].length; i++)
            if (formats[line][pos + i] != formats[line][pos])
                break;
        return i;
    }

    public boolean check(int line, int pos, byte value) {
        return inRange(line, pos) && formats[line][pos] == value;
    }

    public void set(LinePos pos, byte format) {
        set(pos.line, pos.pos, format);
    }

    public void set(int line, int pos, byte format) {
        if (!inRange(line, pos))
            return;
        formats[line][pos] = format;
    }

    public void setLine(int line, int start, byte format) {
        if (!lineInRange(line))
            return;
        for (int i = start; i < formats[line].length; i++) 
            formats[line][i] = format;
    }

    public void setRange(int line, int start, int end, byte format) {
        if (!lineInRange(line))
            return;
        if (end > formats[line].length)
            end = formats[line].length;
        for (int i = start; i < end; i++) 
            formats[line][i] = format;
    }

    public void setSpan(int line_start, int start, int line_end, int end, byte format) {

        if (line_start == line_end) {
            setRange(line_start, start, end, format);
            return;
        }

        setLine(line_start, start, format);
        for (int i = line_start + 1; i < line_end; i++) 
            setLine(i, 0, format);
        
        setRange(line_end, 0, end, format);
    }

    public boolean lineInRange(int line) {
        return line >= 0 && line < formats.length;
    }

    public boolean inRange(int line, int pos) {
        return line >= 0 && line < formats.length && pos >= 0 && pos < formats[line].length;
    }
}
