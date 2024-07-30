package net.quelfth.gigatubes.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;
import net.quelfth.gigatubes.block.entities.ChadmiumCompressorBlockEntity;
import net.quelfth.gigatubes.block.entities.KiloCompressorBlockEntity;
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;

public class GigaBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GigaTubes.MOD_ID);

    @SuppressWarnings("null")
    public static final RegistryObject<BlockEntityType<ChadmiumCompressorBlockEntity>> CHADMIUM_COMPRESSOR 
        = BLOCK_ENTITIES.register("chadmium_compressor_block_entity", () -> BlockEntityType.Builder.of(ChadmiumCompressorBlockEntity::new, GigaBlocks.CHADMIUM_COMPRESSOR.get()).build(null));
        
    @SuppressWarnings("null")
    public static final RegistryObject<BlockEntityType<KiloCompressorBlockEntity>> KILO_COMPRESSOR 
        = BLOCK_ENTITIES.register("kilo_compressor_block_entity", () -> BlockEntityType.Builder.of(KiloCompressorBlockEntity::new, GigaBlocks.KILO_COMPRESSOR.get()).build(null));

    @SuppressWarnings("null")
    public static final RegistryObject<BlockEntityType<TubeBlockEntity>> TUBE
        = BLOCK_ENTITIES.register("tube_block_entity", () -> BlockEntityType.Builder.of(TubeBlockEntity::new, GigaBlocks.TUBE.get(), GigaBlocks.KILOTUBE.get(), GigaBlocks.MEGATUBE.get(), GigaBlocks.GIGATUBE.get()).build(null));



    // private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> blockEntity(String name, BlockEntitySupplier<T> supplier, Block... block) {
    //     return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(supplier, block).build(null));
    // }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    
}
