package net.quelfth.gigatubes.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;
import net.quelfth.gigatubes.block.blocks.ChadmiumCompressorBlock;
import net.quelfth.gigatubes.block.blocks.GigatubeBlock;
import net.quelfth.gigatubes.block.blocks.TubeIOBlock;
import net.quelfth.gigatubes.block.blocks.KiloCompressorBlock;
import net.quelfth.gigatubes.block.blocks.KilotubeBlock;
import net.quelfth.gigatubes.block.blocks.MegatubeBlock;
import net.quelfth.gigatubes.block.blocks.TubeBlock;
import net.quelfth.gigatubes.item.GigaItems;

import java.util.function.Supplier;

public class GigaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GigaTubes.MOD_ID);

    public static final RegistryObject<Block> CHADMIUM_BLOCK = registerBlock("chadmium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> KILOCHADMIUM_BLOCK = registerBlock("kilochadmium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> MEGACHADMIUM_BLOCK = registerBlock("megachadmium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<Block> GIGACHADMIUM_BLOCK = registerBlock("gigachadmium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));
    public static final RegistryObject<ChadmiumCompressorBlock> CHADMIUM_COMPRESSOR = registerBlock("chadmium_compressor", ChadmiumCompressorBlock::new);
    public static final RegistryObject<KiloCompressorBlock> KILO_COMPRESSOR = registerBlock("kilo_compressor", KiloCompressorBlock::new);

    public static final RegistryObject<TubeBlock> TUBE = BLOCKS.register("tube", TubeBlock::new);
    public static final RegistryObject<TubeBlock> KILOTUBE = BLOCKS.register("kilotube", KilotubeBlock::new);
    public static final RegistryObject<TubeBlock> MEGATUBE = BLOCKS.register("megatube", MegatubeBlock::new);
    public static final RegistryObject<TubeBlock> GIGATUBE = BLOCKS.register("gigatube", GigatubeBlock::new);

    public static final RegistryObject<TubeIOBlock> TUBE_IO = BLOCKS.register("tube_io", TubeIOBlock::new);
    

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        registerBlockItem(name, block);
        return block;
    }
    
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return GigaItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
