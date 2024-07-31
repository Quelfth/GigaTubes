package net.quelfth.gigatubes.util.code;

import java.lang.Iterable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;


public sealed abstract class Predicate permits Predicate.Disjunction, Predicate.Conjunction, Predicate.Negation, SimplePredicate {


    public abstract CompoundTag serialize();

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (obj instanceof Predicate pred)
            return identical(pred);
        return false;
    }

    public abstract boolean identical(Predicate predicate);

    public abstract boolean allows(ItemStack item);

    public static final class Disjunction extends Predicate {
        private final List<Predicate> disjuncts;

        public Disjunction(Iterable<Predicate> iter) {
            disjuncts = new ArrayList<Predicate>();
            for (Predicate p : iter) {
                if (p instanceof Disjunction disjunct) 
                    disjuncts.addAll(disjunct.disjuncts);
                else
                    disjuncts.add(p);
            }
        }

        public Disjunction() {
            disjuncts = new ArrayList<>();
        }

        @Override
        public String toString() {
            if (disjuncts.size() < 1)
                return "!*";
            
            if (disjuncts.size() == 1)
                return "|"+disjuncts.get(0);

            String string = "("+disjuncts.get(0).toString()+")";
            for (int i = 0; i < disjuncts.size(); i++)
                string += "|("+disjuncts.get(i)+")";
            return string;
        }

        @Override
        public CompoundTag serialize() {
            ListTag elements = new ListTag();
            for (Predicate disjunct : disjuncts)
                elements.add(disjunct.serialize());
            CompoundTag tag = new CompoundTag();
            tag.put("OR", elements);
            return tag;
        }

        @Override
        public boolean identical(Predicate predicate) {
            if (predicate instanceof Disjunction disjunction && disjuncts.size() == disjunction.disjuncts.size()) {
                for (int i = 0; i < disjuncts.size(); i++)
                    if (!disjuncts.get(i).identical(disjunction.disjuncts.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        public boolean directlyContains(Predicate predicate) {
            for (Predicate disjunct : disjuncts)
                if (disjunct.identical(predicate))
                    return true;
            return false;
        }

        @Override
        public boolean allows(ItemStack item) {
            for (Predicate disjunct : disjuncts)
                if (disjunct.allows(item))
                    return true;
            return false;
        }
    }

    public static final class Conjunction extends Predicate {
        private final List<Predicate> conjuncts;

        public Conjunction(Iterable<Predicate> iter) {
            conjuncts = new ArrayList<Predicate>();
            for (Predicate p : iter) {
                if (p instanceof Conjunction conjunct) 
                    conjuncts.addAll(conjunct.conjuncts);
                else
                    conjuncts.add(p);
            }
        }

        public Conjunction(Predicate... preds) {
            conjuncts = new ArrayList<Predicate>();
            for (Predicate p : preds) {
                if (p instanceof Conjunction conjunct) 
                    conjuncts.addAll(conjunct.conjuncts);
                else
                    conjuncts.add(p);
            }
        }

        public Conjunction() {
            this.conjuncts = new ArrayList<>();
        }

        @Override
        public String toString() {
            if (conjuncts.size() < 1)
                return "*";

            if (conjuncts.size() == 1)
                return "&"+conjuncts.get(0);

            String string = "("+conjuncts.get(0).toString()+")";
            for (int i = 0; i < conjuncts.size(); i++)
                string += "&("+conjuncts.get(i)+")";
            return string;
        }

        @Override
        public CompoundTag serialize() {
            ListTag elements = new ListTag();
            for (Predicate conjunct : conjuncts)
                elements.add(conjunct.serialize());
            CompoundTag tag = new CompoundTag();
            tag.put("AND", elements);
            return tag;
        }

        @Override
        public boolean identical(Predicate predicate) {
            if (predicate instanceof Conjunction conjunction && conjuncts.size() == conjunction.conjuncts.size()) {
                for (int i = 0; i < conjuncts.size(); i++)
                    if (!conjuncts.get(i).identical(conjunction.conjuncts.get(i)))
                        return false;
                return true;
            }
            return false;
        }

        public boolean directlyContains(Predicate predicate) {
            for (Predicate conjunct : conjuncts)
                if (conjunct.identical(predicate))
                    return true;
            return false;
        }

        @Override
        public boolean allows(ItemStack item) {
            for (Predicate conjunct : conjuncts)
                if (!conjunct.allows(item))
                    return false;
            return true;
        }
    }

    public static final class Negation extends Predicate {
        private final Predicate negand;

        public Negation(Predicate negand) {
            this.negand = negand;
        }

        @Override
        public String toString() {
            return "!("+negand+")";
        }

        @Override
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.put("NOT", negand.serialize());
            return tag;
        }

        @Override
        public boolean identical(Predicate predicate) {
            return predicate instanceof Negation negation && negation.negand.identical(negand);
        }

        @Override
        public boolean allows(ItemStack item) {
            return !negand.allows(item);
        }
    }
    
    public static final Predicate ANY = new SimplePredicate.Any();
    public static final Predicate NONE = new Negation(new SimplePredicate.Any());


    public static final Predicate ANY_ITEM = new SimplePredicate.AnyItem();
    public static Predicate item(String namespace, String id) {
        return new SimplePredicate.Item(namespace, id);
    }

    public static Predicate item(String id) {
        return new SimplePredicate.Item(id);
    }

    public static Predicate and(Predicate p, Predicate q) {
        if (p.identical(q))
            return p;
        if (p.obviousSuperset(q))
            return q;
        if (q.obviousSuperset(p))
            return p;
        return new Conjunction(p, q);
    }

    public boolean obviousSuperset(Predicate that) {
        return this instanceof Disjunction dis && dis.directlyContains(that) || that instanceof Conjunction con && con.directlyContains(this);
    }

    public static Predicate deserialize(CompoundTag tag) {
        if (tag.get("OR") instanceof ListTag disjunction) {
            List<Predicate> disjuncts = new ArrayList<>();
            for (int i = 0; i < disjunction.size(); i++)
                if(disjunction.get(i) instanceof CompoundTag disjunct)
                    disjuncts.add(deserialize(disjunct));
            return new Disjunction(disjuncts);
        }
        if (tag.get("AND") instanceof ListTag conjunction) {
            List<Predicate> conjuncts = new ArrayList<>();
            for (int i = 0; i < conjunction.size(); i++)
                if(conjunction.get(i) instanceof CompoundTag conjunct)
                    conjuncts.add(deserialize(conjunct));
            return new Conjunction(conjuncts);
        }
        if (tag.get("NOT") instanceof CompoundTag negand) {
            return new Negation(deserialize(negand));
        }
        if (tag.get("SIMPLE") instanceof StringTag simple) {
            switch (simple.getAsString()) {
                case "any" -> {
                    return ANY;
                }
                case "item" -> {
                    String item = tag.getString("value");
                    if (item.equals(""))
                        return null;
                    String[] parts = item.split(":");
                    if (parts.length != 2)
                        return null;
                    return item(parts[0], parts[1]);
                }
                case "any_item" -> {
                    return ANY_ITEM;
                }
            }
        }


        return null;
    }
}
