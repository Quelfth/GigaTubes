package net.quelfth.gigatubes.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.quelfth.gigatubes.GigaTubes;

public class GigaPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GigaTubes.MOD_ID, "main"), 
            () -> PROTOCOL_VERSION, 
            PROTOCOL_VERSION::equals, 
            PROTOCOL_VERSION::equals
        );

    public static void register(IEventBus bus) {
        int id = 0;
        CHANNEL.registerMessage(id++, ServerboundFilterEditPacket.class, ServerboundFilterEditPacket::encode, ServerboundFilterEditPacket::decode, ServerboundFilterEditPacket::handle);
    } 
}
