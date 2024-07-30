package net.quelfth.gigatubes.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quelfth.gigatubes.GigaTubes;

public class GigaSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GigaTubes.MOD_ID);

    public static final RegistryObject<SoundEvent> WRENCH_SOUND = sound("wrench_sound");

    private static RegistryObject<SoundEvent> sound(String name) {
        ResourceLocation id = new ResourceLocation(GigaTubes.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }


    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
