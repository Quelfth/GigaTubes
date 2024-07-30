package net.quelfth.gigatubes.util.code;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.quelfth.gigatubes.util.CodeCharType;
import net.quelfth.gigatubes.util.Couple;
import net.quelfth.gigatubes.util.Wrapper;

import java.util.Deque;
import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class GigaParser {

    private static void printFrags(CodeFragment[] frags) {
        for (CodeFragment fragment : frags)
            System.out.print(fragment + ",");
        System.out.println();
    }

    public static Couple<Predicate, ParagraphFormat> parseIntoPredicate(final String code) {
        return parseIntoPredicate(new CodeParagraph(code));
    }

    public static Couple<Predicate, ParagraphFormat> parseIntoPredicate(final CodeParagraph paragraph) {
        final @Nonnull ParagraphFormat formatting = new ParagraphFormat(paragraph);

        parseOutComments(paragraph, formatting);

        final @Nonnull CodeFragment[] fragments = fragment(paragraph);

        

        Predicate pred = parseDisjuncts(fragments, paragraph, formatting);



        return new Couple<>(pred, formatting);
    }

    @Nullable
    public static Predicate parseDisjuncts(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        System.out.print("Disjuncts: "); printFrags(fragments);
        Deque<CodeFragment> frags = new ArrayDeque<>();
        frags.addAll(Arrays.asList(fragments));
        List<CodeFragment> collected = new ArrayList<>();
        List<Predicate> disjuncts = new ArrayList<>();
        while_loop:
        while (frags.size() > 0) {
            CodeFragment frag = frags.removeFirst();
            if (frag instanceof LeafFragment leaf) {
                
                for (LinePos pos = frag.start(); pos.compareTo(frag.end()) < 0; pos.inc(paragraph.lineLength(pos.line))) {
                    if (paragraph.get(pos.line, pos.pos) == ',') {
                        collected.add(leaf.upto(pos));
                        disjuncts.add(parseConjuncts(collected.toArray(new CodeFragment[collected.size()]), paragraph, formatting));
                        collected.clear();
                        formatting.set(pos.line, pos.pos, CodeFormats.LOGICAL);
                        frags.addFirst(leaf.onfrom(pos.next(paragraph.lineLength(pos.line))));
                        continue while_loop;
                    }
                }
            }
            collected.add(frag);
        }

        disjuncts.add(parseConjuncts(collected.toArray(new CodeFragment[collected.size()]), paragraph, formatting));

        for (Predicate conjunct : disjuncts)
            if (conjunct == null)
                return null;

        if (disjuncts.size() == 1)
            return disjuncts.get(0);
        return new Predicate.Disjunction(disjuncts);
    }

    @Nullable
    public static Predicate parseConjuncts(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        System.out.print("Conjuncts: "); printFrags(fragments);
        Deque<CodeFragment> frags = new ArrayDeque<>();
        frags.addAll(Arrays.asList(fragments));
        List<CodeFragment> collected = new ArrayList<>();
        List<Predicate> conjuncts = new ArrayList<>();
        while_loop:
        while (frags.size() > 0) {
            CodeFragment frag = frags.removeFirst();
            if (frag instanceof LeafFragment leaf) {
                for (LinePos pos = frag.start(); pos.compareTo(frag.end()) < 0; pos.inc(paragraph.lineLength(pos.line))) {
                    if (paragraph.get(pos.line, pos.pos) == '&') {
                        collected.add(leaf.upto(pos));
                        conjuncts.add(parseNegation(collected.toArray(new CodeFragment[collected.size()]), paragraph, formatting));
                        collected.clear();
                        formatting.set(pos.line, pos.pos, CodeFormats.LOGICAL);
                        frags.addFirst(leaf.onfrom(pos.next(paragraph.lineLength(pos.line))));
                        continue while_loop;
                    }
                }
            }
            collected.add(frag);
        }

        conjuncts.add(parseNegation(collected.toArray(new CodeFragment[collected.size()]), paragraph, formatting));
        for (Predicate conjunct : conjuncts)
            if (conjunct == null)
                return null;

        if (conjuncts.size() == 1)
            return conjuncts.get(0);
        return new Predicate.Conjunction(conjuncts);
    }

    @Nullable
    public static Predicate parseNegation(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        System.out.print("Negation: "); printFrags(fragments);
        if (fragments.length == 0)
            return Predicate.NONE;
        boolean negated = false;
        if (fragments[0] instanceof LeafFragment leaf) {
            LinePos pos;
            for_loop:
            for (pos = leaf.start(); pos.compareTo(leaf.end()) < 0; pos.inc(paragraph.lineLength(pos.line))) {
                switch (paragraph.get(pos.line, pos.pos)) {
                    case ' ', '\0' -> { }
                    case '!' -> {
                        formatting.set(pos.line, pos.pos, CodeFormats.LOGICAL);
                        negated = !negated;
                    }
                    default -> {break for_loop;}
                }
            }
            fragments[0] = leaf.onfrom(pos);
        }

        Predicate predicate = parseParenthetical(fragments, paragraph, formatting);

        if (predicate == null)
            return null;

        if (negated)
            return new Predicate.Negation(predicate);

        return predicate;
    }

    public static Predicate parseParenthetical(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        System.out.print("Parentheticals: "); printFrags(fragments);
        int i;
        for (i = 0; ; i++) {
            if (i >= fragments.length)
                return Predicate.NONE;
            
            if (!fragments[i].isEmpty(paragraph))
                break;
        }
        
        if (fragments[i] instanceof LeafFragment) {
            return parseSimplePredicate(fragments, paragraph, formatting);
        }
        if (fragments[i] instanceof BranchFragment branch) {
            System.out.println(paragraph.get(branch.start()));
            if (paragraph.get(branch.start()) == '(') {
                
                formatting.set(branch.start(), CodeFormats.PARENTHESES);
                LinePos last = branch.end();
                last.pos--;
                if (last.pos < 0)
                    last = new LinePos(last.line-1, paragraph.lineLength(last.line-1)-1);

                if (last.line < 0)
                    return null;

                if (paragraph.get(last) == ')')
                    formatting.set(last, CodeFormats.PARENTHESES);

                return parseDisjuncts(branch.sub_fragments.clone(), paragraph, formatting);
            }
        }
        return null;
    }

    @Nullable
    public static Predicate parseSimplePredicate(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        
        if (fragments[0] instanceof LeafFragment leaf) {
            LinePos pos = firstNonSpace(leaf, paragraph);
            if (paragraph.charIsAt(pos.line, pos.pos, '*')) {
                formatting.set(pos, CodeFormats.WILDCARD);
                return Predicate.ANY;
            }
            if (paragraph.charsAreAt(pos.line, pos.pos, "item") && CodeCharType.of(paragraph.get(pos.line, pos.pos + 4)) != CodeCharType.IDENTIFIER) {
                formatting.setRange(pos.line, pos.pos, pos.pos + 4, CodeFormats.KEYWORD);
                CodeFragment[] frags = fragments.clone();
                pos.pos += 4;
                frags[0] = leaf.onfrom(pos);
                return parseItemPredicate(frags, paragraph, formatting);
            }
        }
        return null;
    }

    @Nullable
    public static Predicate parseItemPredicate(CodeFragment[] fragments, final CodeParagraph paragraph, final ParagraphFormat formatting) {
        if (fragments[0] instanceof LeafFragment leaf) {
            LinePos pos = firstNonSpace(leaf, paragraph);
            if (paragraph.charIsAt(pos.line, pos.pos, '*')) {
                formatting.set(pos.line, pos.pos, CodeFormats.WILDCARD);
                return Predicate.ANY_ITEM;
            } else {
                LinePos ie1 = endOfIdentifier(pos, leaf.end(), paragraph);
                LinePos nextChar = firstNonSpace(ie1, leaf.end(), paragraph);
                if (paragraph.get(nextChar.line, nextChar.pos) == ':') {
                    LinePos is2 = firstNonSpace(nextChar.next(paragraph.lineLength(nextChar.line)), leaf.end(), paragraph);
                    LinePos ie2 = endOfIdentifier(is2, leaf.end(), paragraph);
                    formatting.setRange(pos.line, pos.pos, ie1.pos, CodeFormats.NAMESPACE);
                    formatting.set(nextChar.line, nextChar.pos, CodeFormats.NAMESPACE);
                    formatting.setRange(is2.line, is2.pos, ie2.pos, CodeFormats.IDENTIFIER);
                    return Predicate.item(paragraph.getRange(pos.line, pos.pos, ie1.pos), paragraph.getRange(is2.line, is2.pos, ie2.pos));
                } else {
                    formatting.setRange(pos.line, pos.pos, ie1.pos, CodeFormats.IDENTIFIER);
                    return Predicate.item(paragraph.getRange(pos.line, pos.pos, ie1.pos));
                }
            }
        }
        return null;
    }

    public static LinePos endOfIdentifier(LinePos start, LinePos end, CodeParagraph paragraph) {
        LinePos pos;
        for (pos = start.clone(); pos.compareTo(end) < 0 && pos.line == start.line && CodeCharType.of(paragraph.get(pos.line, pos.pos)) == CodeCharType.IDENTIFIER; pos.inc(paragraph.lineLength(pos.line)));
        
        return pos;
    }

    public static LinePos firstNonSpace(LeafFragment leaf, CodeParagraph paragraph) {
        return firstNonSpace(leaf.start(), leaf.end(), paragraph);
    }

    public static LinePos firstNonSpace(LinePos start, LinePos end, CodeParagraph paragraph) {
        LinePos pos;
        for (
            pos = start; 
            pos.compareTo(end) < 0 && 
            switch (paragraph.get(pos.line, pos.pos)) {
                case ' ', '\0' -> {yield true;} 
                default -> {yield false;}
            }; 
            pos.inc(paragraph.lineLength(pos.line))
            );
        return pos;
    }

    public static void parseOutComments(final CodeParagraph paragraph, final ParagraphFormat formatting) {
        boolean block_comment = false;
        for (int i = 0; i < paragraph.numLines(); i++) {
            boolean line_comment = false;
            for (int j = 0; j < paragraph.lineLength(i); j++) {
                if (!line_comment)
                    if (!block_comment) {
                        if (paragraph.charsAreAt(i, j, "/*")) 
                            block_comment = true;
                        else if (paragraph.charsAreAt(i, j, "//")) 
                            line_comment = true;
                    }

                if (block_comment && paragraph.charsAreAt(i, j, "*/")) {
                    block_comment = false;
                    paragraph.set(i, j, ' ');
                    formatting.set(i, j, CodeFormats.BLOCK_COMMENT);
                    paragraph.set(i, j+1, ' ');
                    formatting.set(i, j+1, CodeFormats.BLOCK_COMMENT);
                    j++;
                    continue;
                }
                    
                if (block_comment) {
                    paragraph.set(i, j, ' ');
                    formatting.set(i, j, CodeFormats.BLOCK_COMMENT);
                } else if (line_comment) {
                    paragraph.set(i, j, ' ');
                    formatting.set(i, j, CodeFormats.LINE_COMMENT);
                }
            }
            
        }

       
    }

    public static CodeFragment[] fragment(final CodeParagraph paragraph) {
        return fragment(paragraph, new Wrapper<>(0), new Wrapper<>(0));
    }
    public static CodeFragment[] fragment(final CodeParagraph paragraph, Wrapper<Integer> line, Wrapper<Integer> pos) {

        List<CodeFragment> fragments = new ArrayList<>();

        

        for (int i = line.value; i < paragraph.numLines(); i++) {
            int j_start = 0;
            if (pos.value > 0 && i == line.value)
                j_start = pos.value;
            int j_end = paragraph.lineLength(i);

            

            for (int j = j_start; j < j_end; j++) {
                switch (paragraph.get(i, j)) {
                    case '(' -> {
                        fragments.add(new LeafFragment(line.value, i, pos.value, j));
                        line.value = i;
                        pos.value = j;
                        int old_line = line.value;
                        int old_pos = pos.value;
                        pos.value++;
                        CodeFragment[] frags = fragment(paragraph, line, pos);

                        fragments.add(new BranchFragment(old_line, line.value, old_pos, pos.value, frags));
                    }
                    case ')' -> {
                        fragments.add(new LeafFragment(line.value, i, pos.value, j));
                        line.value = i;
                        pos.value = j+1;
                        return fragments.toArray(new CodeFragment[fragments.size()]);
                    }
                }
                
            }
        }
        fragments.add(new LeafFragment(line.value, paragraph.numLines()-1, pos.value, paragraph.lineLength(paragraph.numLines()-1)));
        return fragments.toArray(new CodeFragment[fragments.size()]);
    }


    
    private static sealed abstract class CodeFragment {
        protected int start_line;
        protected int end_line;
        protected int start_pos;
        protected int end_pos;

        public CodeFragment(LinePos start, LinePos end) {
            start_line = start.line;
            end_line = end.line;
            start_pos = start.pos;
            end_pos = end.pos;
        }

        public CodeFragment(int start_line, int end_line, int start_pos, int end_pos) {
            this.start_line = start_line;
            this.end_line = end_line;
            this.start_pos = start_pos;
            this.end_pos = end_pos;
        }

        public LinePos start() {
            return new LinePos(start_line, start_pos);
        }

        public LinePos end() {
            return new LinePos(end_line, end_pos);
        }

        public abstract boolean isEmpty(CodeParagraph paragraph);

        @Override
        public String toString() {
            return "Fragment ["+start_line+":"+start_pos+" ~ "+end_line+":"+end_pos+"]";
        }
    }

    private static final class LeafFragment extends CodeFragment { 
        public LeafFragment(LinePos start, LinePos end) {
            super(start, end);
        }
        public LeafFragment(int start_line, int end_line, int start_pos, int end_pos) {
            super(start_line, end_line, start_pos, end_pos);
        }

        @Override
        public String toString() {
            return "Leaf"+super.toString();
        }

        public LeafFragment upto(LinePos pos) {
            return new LeafFragment(start(), pos);
        }

        public LeafFragment onfrom(LinePos pos) {
            return new LeafFragment(pos, end());
        }
        @Override
        public boolean isEmpty(CodeParagraph paragraph) {
            if (end_line < start_line)
                return true;
            if (end_line == start_line && end_pos <= start_pos)
                return true;
            for (LinePos pos = start(); pos.compareTo(end()) < 0; pos.inc(paragraph.lineLength(pos.line)))
                if (paragraph.get(pos) != ' ')
                    return false;

            return true;
        }

        
    }

    private static final class BranchFragment extends CodeFragment {
        final CodeFragment[] sub_fragments;
        public BranchFragment(int start_line, int end_line, int start_pos, int end_pos, CodeFragment[] sub_fragments) {
            super(start_line, end_line, start_pos, end_pos);
            this.sub_fragments = sub_fragments;
        }

        @Override
        public String toString() {
            String subs = "";
            for (CodeFragment fragment : sub_fragments)
                subs += fragment.toString() + ",";
            return "Branch"+super.toString()+"{"+subs+"}";
        }

        @Override
        public boolean isEmpty(CodeParagraph paragraph) {
            return false;
        }
    }

    
}
