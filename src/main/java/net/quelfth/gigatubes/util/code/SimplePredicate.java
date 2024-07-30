package net.quelfth.gigatubes.util.code;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public abstract sealed class SimplePredicate implements Predicate {

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("SIMPLE", StringTag.valueOf(nbtTypeName()));
        Tag data = nbtData();
        if (data != null)
            tag.put("value", data); 
        return tag;
    }

    protected abstract String nbtTypeName();
    protected Tag nbtData() {return null;}

    public static final class Any extends SimplePredicate {

        @Override
        protected String nbtTypeName() {
            return "any";
        }

    }

    public static final class Item extends SimplePredicate {
        public final String namespace;
        public final String id;

        public Item(String id) {
            this("minecraft", id);
        }

        public Item(String namespace, String id) {
            this.namespace = namespace;
            this.id = id;
        }

        @Override
        public String toString() {
            return "item "+namespace+":"+id;
        }

        @Override
        protected String nbtTypeName() {
            return "item";
        }

        @Override
        protected Tag nbtData() {
            return StringTag.valueOf(namespace+":"+id);
        }
    }

    public static final class AnyItem extends SimplePredicate {
        @Override
        public String toString() {
            return "item *";
        }

        @Override
        protected String nbtTypeName() {
            return "any_item";
        }
    }
}
