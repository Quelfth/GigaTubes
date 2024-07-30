package net.quelfth.gigatubes.util.code;


public class CodeParagraph {
    private final char[][] lines;

        public CodeParagraph(final String paragraph) {
            this(paragraph.split("\n"));
        }

        public CodeParagraph(final String[] lines) {
            this.lines = new char[lines.length][];
            for (int i = 0; i < lines.length; i++) {
                this.lines[i] = new char[lines[i].length()];
                for (int j = 0; j < lines[i].length(); j++) {
                    this.lines[i][j] = lines[i].charAt(j);
                }
            }
        }
        
        public char get(LinePos pos) {
            return get(pos.line, pos.pos);
        }

        public char get(int line, int pos) {
            if (!inRange(line, pos))
                return '\0';
            return lines[line][pos];
        }

        public String getRange(int line, int start, int end) {
            String string = "";
            for (int i = start; i < end; i++)
                string += get(line, i);
            return string;
        }

        // public String getRange(int start_line, int start_pos, int end_line, int end_pos) {
        //     if (end_line < 0 || start_line > numLines())
        //         return "";
        //     if (start_line < 0) {
        //         start_line = 0;
        //         start_pos = 0;
        //     }
            
        //     if (end_line > numLines()-1) {
        //         end_line = numLines()-1;
        //         end_pos = lines[end_line].length-1;
        //     }

        //     if (start_pos < 0)
        //         start_pos = 0;
            
        //     if (end_pos > lineLength(end_line)-1)
        //         end_pos = lineLength(end_line)-1;
            
        //     for (int i = start_line; i <= end_line; i++)
        //         for (int j = start_pos)
        // }

        public void set(int line, int pos, char value) {
            lines[line][pos] = value;
        }

        

        public int numLines() {
            return lines.length;
        }

        public int lineLength(int line) {
            if (line < 0 || line >= lines.length)
                return 0;
            return lines[line].length;
        }

        private boolean inRange(int line, int pos) {
            if (line < 0 || line >= numLines())
                return false;
            if (pos < 0 || pos >= lineLength(line))
                return false;
            return true;
        }

        public boolean charIsAt(int line, int pos, char c) {
            if (!inRange(line, pos))
                return false;
            return lines[line][pos] == c;
        }

        public boolean charsAreAt(int line, int pos, String chars) {
            for (int i = 0; i < chars.length(); i++) {
                if (!charIsAt(line, pos + i, chars.charAt(i)))
                    return false;
            }
            return true;
        }
}
