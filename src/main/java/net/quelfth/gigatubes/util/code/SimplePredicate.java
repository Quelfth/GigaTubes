package net.quelfth.gigatubes.util.code;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract sealed class SimplePredicate extends Predicate {

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

        @Override
        public boolean identical(Predicate predicate) {
            return predicate instanceof Any;
        }

        @Override
        public boolean allows(ItemStack item) {
            return true;
        }


        @Override
        public String toString() {
            return "*";
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

        @Override
        public boolean identical(Predicate predicate) {
            return predicate instanceof Item item && item.namespace.equals(namespace) && item.id.equals(id);
        }

        @Override
        public boolean allows(ItemStack item) {
            
            return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(namespace, id)) 
                && item.is(ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, id)));
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

        @Override
        public boolean identical(Predicate predicate) {
            return predicate instanceof AnyItem;
        }

        @Override
        public boolean allows(ItemStack item) {
            return true;
        }

        
    }
}
