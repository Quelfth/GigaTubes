package net.quelfth.gigatubes.util.code;

import javax.annotation.Nullable;

public class LinePos implements Comparable<LinePos>{
    public int line;
    public int pos;

    public LinePos(final int line, final int pos) {
        this.line = line;
        this.pos = pos;
    }

    public void inc(final int lineLength) {
        if (pos < lineLength)
            pos++;
        else {
            line++;
            pos = 0;
        }
    }

    public LinePos next(final int lineLength) {
        LinePos pos = this.clone();
        pos.inc(lineLength);
        return pos;
    }

    

    public LinePos clone() {
        return new LinePos(line, pos);
    }

    @Override
    public int compareTo(@Nullable LinePos o) {
        if (o == null)
            o = new LinePos(0, 0);
        int l = line-o.line;
        if (l != 0)
            return l;
        return pos-o.pos;
    }

    @Override
    public String toString() {
        return line+":"+pos;
    }
}