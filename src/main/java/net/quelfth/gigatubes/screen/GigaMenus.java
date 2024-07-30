package net.quelfth.gigatubes.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;

public class GigaMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, GigaTubes.MOD_ID);

    public static final RegistryObject<MenuType<ChadmiumCompressorMenu>> CHADMIUM_COMPRESSOR = registerMenuType("chadmium_compressor_menu", ChadmiumCompressorMenu::new);
    public static final RegistryObject<MenuType<KiloCompressorMenu>> KILO_COMPRESSOR = registerMenuType("kilo_compressor_menu", KiloCompressorMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
