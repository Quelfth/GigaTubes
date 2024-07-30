package net.quelfth.gigatubes.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;
import net.quelfth.gigatubes.block.GigaBlocks;
import net.quelfth.gigatubes.item.items.IncompleteTubeFilterItem;
import net.quelfth.gigatubes.item.items.TubeFilterItem;
import net.quelfth.gigatubes.item.items.TubeIntakeItem;
import net.quelfth.gigatubes.item.items.TubeItem;
import net.quelfth.gigatubes.item.items.TubeModuleItem;
import net.quelfth.gigatubes.item.items.TubeOutputItem;

public class GigaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GigaTubes.MOD_ID);
    
    public static final RegistryObject<Item> CHADMIUM_INGOT = ITEMS.register("chadmium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KILOCHADMIUM_INGOT = ITEMS.register("kilochadmium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEGACHADMIUM_INGOT = ITEMS.register("megachadmium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GIGACHADMIUM_INGOT = ITEMS.register("gigachadmium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RAW_CHADMIUM = ITEMS.register("raw_chadmium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_KILOCHADMIUM = ITEMS.register("raw_kilochadmium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_MEGACHADMIUM = ITEMS.register("raw_megachadmium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_GIGACHADMIUM = ITEMS.register("raw_gigachadmium", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CHADMIUM_NUGGET = ITEMS.register("chadmium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KILOCHADMIUM_NUGGET = ITEMS.register("kilochadmium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEGACHADMIUM_NUGGET = ITEMS.register("megachadmium_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GIGACHADMIUM_NUGGET = ITEMS.register("gigachadmium_nugget", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BETA_PARTICLE = ITEMS.register("beta_particle", () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> BETA_WRENCH = ITEMS.register("beta_wrench", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> KILO_WRENCH = ITEMS.register("kilo_wrench", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MEGA_WRENCH = ITEMS.register("mega_wrench", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GIGA_WRENCH = ITEMS.register("giga_wrench", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<TubeItem> TUBE = ITEMS.register("tube", () -> new TubeItem(GigaBlocks.TUBE.get(), new Item.Properties()));
    public static final RegistryObject<TubeItem> KILOTUBE = ITEMS.register("kilotube", () -> new TubeItem(GigaBlocks.KILOTUBE.get(), new Item.Properties()));
    public static final RegistryObject<TubeItem> MEGATUBE = ITEMS.register("megatube", () -> new TubeItem(GigaBlocks.MEGATUBE.get(), new Item.Properties()));
    public static final RegistryObject<TubeItem> GIGATUBE = ITEMS.register("gigatube", () -> new TubeItem(GigaBlocks.GIGATUBE.get(), new Item.Properties()));

    public static final RegistryObject<TubeIntakeItem> TUBE_INTAKE = ITEMS.register("tube_intake", TubeIntakeItem::new);
    public static final RegistryObject<TubeOutputItem> TUBE_OUTPUT = ITEMS.register("tube_output", TubeOutputItem::new);

    public static final RegistryObject<IncompleteTubeFilterItem> EMPTY_FILTER = ITEMS.register("empty_tube_filter", IncompleteTubeFilterItem::new);
    public static final RegistryObject<IncompleteTubeFilterItem> INVALID_FILTER = ITEMS.register("invalid_tube_filter", IncompleteTubeFilterItem::new);
    public static final RegistryObject<TubeFilterItem> FILTER = ITEMS.register("tube_filter", TubeFilterItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
