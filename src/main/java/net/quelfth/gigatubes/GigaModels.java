package net.quelfth.gigatubes;


import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;

public class GigaModels {

    public enum Model {
        TUBE_INTAKE("block/tube_intake"),
        TUBE_OUTPUT("block/tube_output"),
        TUBE_IO("block/tube_io");

        public final ResourceLocation resource;
        public final LazyCache<BakedModel> cache;

        Model(String name) {
            resource = new ResourceLocation(GigaTubes.MOD_ID, name);
            cache = new LazyCache<>(() -> Minecraft.getInstance().getModelManager().getModel(resource));
        }
    }

    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        for (Model model : Model.values()) {
            event.register(model.resource);
        }
    }

    public static void onModelBake(ModelEvent.BakingCompleted event) {
        for (Model model : Model.values()) {
            model.cache.invalidate();
        }
    }
    
}
