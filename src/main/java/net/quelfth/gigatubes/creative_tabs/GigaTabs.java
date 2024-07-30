package net.quelfth.gigatubes.creative_tabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;
import net.quelfth.gigatubes.block.GigaBlocks;
import net.quelfth.gigatubes.item.GigaItems;

public class GigaTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GigaTubes.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GIGATUBES_TAB = 
        TABS.register(
            "gigatubes", 
            () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(GigaItems.GIGATUBE.get()))
                .title(Component.translatable("creativetab.gigatubes"))
                .displayItems((p, o) -> {
                    o.accept(GigaItems.CHADMIUM_INGOT.get());
                    o.accept(GigaItems.KILOCHADMIUM_INGOT.get());
                    o.accept(GigaItems.MEGACHADMIUM_INGOT.get());
                    o.accept(GigaItems.GIGACHADMIUM_INGOT.get());
                    o.accept(GigaItems.RAW_CHADMIUM.get());
                    o.accept(GigaItems.RAW_KILOCHADMIUM.get());
                    o.accept(GigaItems.RAW_MEGACHADMIUM.get());
                    o.accept(GigaItems.RAW_GIGACHADMIUM.get());
                    o.accept(GigaItems.CHADMIUM_NUGGET.get());
                    o.accept(GigaItems.KILOCHADMIUM_NUGGET.get());
                    o.accept(GigaItems.MEGACHADMIUM_NUGGET.get());
                    o.accept(GigaItems.GIGACHADMIUM_NUGGET.get());
                    o.accept(GigaItems.BETA_PARTICLE.get());
                    o.accept(GigaItems.BETA_WRENCH.get());
                    o.accept(GigaItems.KILO_WRENCH.get());
                    o.accept(GigaItems.MEGA_WRENCH.get());
                    o.accept(GigaItems.GIGA_WRENCH.get());
                    o.accept(GigaBlocks.CHADMIUM_BLOCK.get());
                    o.accept(GigaBlocks.KILOCHADMIUM_BLOCK.get());
                    o.accept(GigaBlocks.MEGACHADMIUM_BLOCK.get());
                    o.accept(GigaBlocks.GIGACHADMIUM_BLOCK.get());
                    o.accept(GigaBlocks.CHADMIUM_COMPRESSOR.get());
                    o.accept(GigaBlocks.KILO_COMPRESSOR.get());
                    o.accept(GigaBlocks.TUBE.get());
                    o.accept(GigaBlocks.KILOTUBE.get());
                    o.accept(GigaBlocks.MEGATUBE.get());
                    o.accept(GigaBlocks.GIGATUBE.get());
                    o.accept(GigaItems.TUBE_INTAKE.get());
                    o.accept(GigaItems.TUBE_OUTPUT.get());
                })
                .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}