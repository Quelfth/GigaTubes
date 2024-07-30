package net.quelfth.gigatubes.block.tube_parts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.quelfth.gigatubes.item.items.TubeFilterItem;
import net.quelfth.gigatubes.util.code.Predicate;

public abstract class TubeModule {

    public abstract CompoundTag serialize();
    
    public static TubeModule deserialize(CompoundTag tag) {
        switch (tag.getString("Type")) {
            case "filter" -> {
                return new FilterModule(tag.getString(TubeFilterItem.SOURCE_TAG), Predicate.deserialize(tag.getCompound(TubeFilterItem.PREDICATE_TAG)));
            }
        }

        return null;
    }
    
}
