package net.quelfth.gigatubes.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.quelfth.gigatubes.GigaTubes;

public class GigaTags {
    public static final TagKey<Item> WRENCH = ItemTags.create(new ResourceLocation("c", "tools/wrench"));
    public static final TagKey<Item> WRENCH_DISMANTLE_EASY = ItemTags.create(new ResourceLocation(GigaTubes.MOD_ID, "tools/wrench_dismantle_easy"));
    public static final TagKey<Item> WRENCH_DISMANTLE_HARD = ItemTags.create(new ResourceLocation(GigaTubes.MOD_ID, "tools/wrench_dismantle_hard"));
    public static final TagKey<Item> WRENCH_DISMANTLE_ANY = ItemTags.create(new ResourceLocation(GigaTubes.MOD_ID, "tools/wrench_dismantle_any"));
    public static final TagKey<Block> EASY_DISMANTLEABLE = BlockTags.create(new ResourceLocation(GigaTubes.MOD_ID, "dismantle/easy"));
    public static final TagKey<Block> HARD_DISMANTLEABLE = BlockTags.create(new ResourceLocation(GigaTubes.MOD_ID, "dismantle/hard"));

    public static final TagKey<Block> DISMANTLE_CHESTLIKE = BlockTags.create(new ResourceLocation(GigaTubes.MOD_ID, "dismantle/chestlike"));
}