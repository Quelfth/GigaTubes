package net.quelfth.gigatubes.block.tube_parts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.item.items.TubeFilterItem;
import net.quelfth.gigatubes.util.code.Predicate;

public class FilterModule extends TubeModule {
    private String source;
    private Predicate predicate;

    public FilterModule(String source, Predicate predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("Type", StringTag.valueOf("filter"));
        tag.put("Source", StringTag.valueOf(source));
        tag.put("Predicate", predicate.serialize());
        return tag;
    }

    @Override
    public ItemStack asItem() {
        ItemStack item = new ItemStack(GigaItems.FILTER.get());
        item.addTagElement(TubeFilterItem.SOURCE_TAG, StringTag.valueOf(source));
        item.addTagElement(TubeFilterItem.PREDICATE_TAG, predicate.serialize());
        return item;
    }


    public Predicate getPredicate() {
        return predicate;
    }
}
