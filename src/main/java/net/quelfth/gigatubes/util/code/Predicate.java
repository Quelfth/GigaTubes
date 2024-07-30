package net.quelfth.gigatubes.util.code;

import java.lang.Iterable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;

public sealed interface Predicate permits Predicate.Disjunction, Predicate.Conjunction, Predicate.Negation, SimplePredicate {


    public abstract CompoundTag serialize();

    public static final class Disjunction implements Predicate {
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
    }

    public static final class Conjunction implements Predicate {
        private final List<Predicate> conjuncts;

        public Conjunction(Iterable<Predicate> iter) {
            conjuncts = new ArrayList<Predicate>();
            for (Predicate p : iter) {
                if (p instanceof Disjunction disjunct) 
                    conjuncts.addAll(disjunct.disjuncts);
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
    }

    public static final class Negation implements Predicate {
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
