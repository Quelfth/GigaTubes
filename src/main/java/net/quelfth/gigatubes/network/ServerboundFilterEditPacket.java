package net.quelfth.gigatubes.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.item.items.ITubeFilterItem;
import net.quelfth.gigatubes.item.items.TubeFilterItem;
import net.quelfth.gigatubes.util.Couple;
import net.quelfth.gigatubes.util.code.GigaParser;
import net.quelfth.gigatubes.util.code.Predicate;
import net.quelfth.gigatubes.util.code.ParagraphFormat;
import net.minecraft.nbt.StringTag;

public class ServerboundFilterEditPacket {
    private final String source;
    private final int slot;

    public ServerboundFilterEditPacket(String source, int slot) {
        this.source = source;
        this.slot = slot;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(source);
        buf.writeInt(slot);
    }

    public static ServerboundFilterEditPacket decode(FriendlyByteBuf buf) {
        return new ServerboundFilterEditPacket(buf.readUtf(), buf.readInt());
    }

    public static void handle(ServerboundFilterEditPacket packet, Supplier<NetworkEvent.Context> context) {
        if (!(packet.slot < 0 || packet.slot > 9 && packet.slot != 40))
            context.get().enqueueWork(() -> {
                ServerPlayer sender = context.get().getSender();
                ItemStack item = sender.getInventory().getItem(packet.slot);
                if (!(item.getItem() instanceof ITubeFilterItem))
                    return;

                Couple<Predicate, ParagraphFormat> result = GigaParser.parseIntoPredicate(packet.source);

                
                item = new ItemStack(result.$0 != null ? GigaItems.FILTER.get() : GigaItems.INVALID_FILTER.get(), item.getCount());
                item.addTagElement(TubeFilterItem.SOURCE_TAG, StringTag.valueOf(packet.source));
                if (result.$0 != null)
                    item.addTagElement(TubeFilterItem.PREDICATE_TAG, result.$0.serialize());
                sender.getInventory().setItem(packet.slot, item);
            });
        context.get().setPacketHandled(true);
    }
}
