package net.quelfth.gigatubes;

import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.quelfth.gigatubes.block.GigaBlockEntities;
import net.quelfth.gigatubes.block.GigaBlocks;
import net.quelfth.gigatubes.block.render.TubeRenderer;
import net.quelfth.gigatubes.creative_tabs.GigaTabs;
import net.quelfth.gigatubes.event.GigaEvents;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.network.GigaPacketHandler;
import net.quelfth.gigatubes.recipe.GigaRecipes;
import net.quelfth.gigatubes.screen.ChadmiumCompressorScreen;
import net.quelfth.gigatubes.screen.KiloCompressorScreen;
import net.quelfth.gigatubes.screen.GigaMenus;
import net.quelfth.gigatubes.sound.GigaSounds;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GigaTubes.MOD_ID)
public class GigaTubes {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "gigatubes";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public GigaTubes() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(GigaModels::onModelRegister);
            eventBus.addListener(GigaModels::onModelBake);
        });

        GigaItems.register(eventBus);
        GigaBlocks.register(eventBus);
        GigaBlockEntities.register(eventBus);
        GigaTabs.register(eventBus);
        GigaMenus.register(eventBus);
        GigaRecipes.register(eventBus);
        GigaSounds.register(eventBus);
        GigaPacketHandler.register(eventBus);
        
        
        MinecraftForge.EVENT_BUS.register(this);

        eventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new GigaEvents());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(GigaItems.CHADMIUM_INGOT);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        
        @SuppressWarnings("deprecation")
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(GigaMenus.CHADMIUM_COMPRESSOR.get(), ChadmiumCompressorScreen::new);
            MenuScreens.register(GigaMenus.KILO_COMPRESSOR.get(), KiloCompressorScreen::new);
            ItemBlockRenderTypes.setRenderLayer(GigaBlocks.TUBE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(GigaBlocks.KILOTUBE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(GigaBlocks.MEGATUBE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(GigaBlocks.GIGATUBE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(GigaBlocks.TUBE_IO.get(), RenderType.cutout());
            BlockEntityRenderers.register(GigaBlockEntities.TUBE.get(), TubeRenderer::new);
            
        }
    }
}
