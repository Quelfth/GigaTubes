package net.quelfth.gigatubes.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;

public class GigaRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GigaTubes.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ChadmiumCompressorRecipe>> CHADMIUM_COMPRESSOR = SERIALIZERS.register("chadmium_compressor", () -> ChadmiumCompressorRecipe.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
