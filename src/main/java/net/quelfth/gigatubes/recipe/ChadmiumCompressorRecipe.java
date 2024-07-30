package net.quelfth.gigatubes.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.quelfth.gigatubes.GigaTubes;

public class ChadmiumCompressorRecipe implements Recipe<SimpleContainer> {

    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id; 
    
    public ChadmiumCompressorRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(@Nonnull SimpleContainer container, @Nonnull Level level) {
        if (level.isClientSide())
            return false;

        boolean matches = true;
        for (int i = 1; i <= 16; i++) {
            ItemStack item = container.getItem(i);
            matches &= item.getCount() == item.getMaxStackSize() && inputItems.get(0).test(item);
        }
        return matches;
    }

    @Override
    public ItemStack assemble(@Nonnull SimpleContainer container, @Nonnull RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    public static class Type implements RecipeType<ChadmiumCompressorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "chadmium_compressor";
    }

    public static class Serializer implements RecipeSerializer<ChadmiumCompressorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(GigaTubes.MOD_ID, "chadmium_compressor");

        @Override
        public ChadmiumCompressorRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject serialized) {
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serialized, "result"));
            JsonArray json_ingredients = GsonHelper.getAsJsonArray(serialized, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(json_ingredients.get(i)));
            }

            
            return new ChadmiumCompressorRecipe(ingredients, result, id);
        }

        @Override
        public @Nullable ChadmiumCompressorRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            
            return new ChadmiumCompressorRecipe(inputs, output, id);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull ChadmiumCompressorRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(RegistryAccess.EMPTY), false);
        }

        
    }

    
}