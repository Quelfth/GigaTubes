package net.quelfth.gigatubes.item.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.screen.FilterEditScreen;

public final class IncompleteTubeFilterItem extends Item implements ITubeFilterItem {
    public IncompleteTubeFilterItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            ItemStack item = player.getItemInHand(hand);
            if (item.is(GigaItems.EMPTY_FILTER.get()))
                return InteractionResultHolder.pass(item);
            item = new ItemStack(GigaItems.EMPTY_FILTER.get(), item.getCount());
            player.setItemInHand(hand, item);
            return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
        }

        if (level.isClientSide()) {
            Inventory inventory = player.getInventory();
            int slot = hand == InteractionHand.OFF_HAND ? Inventory.SLOT_OFFHAND : inventory.selected;
            ItemStack item = inventory.getItem(slot);
            CompoundTag tag = item.getTag();
            
            Minecraft.getInstance().setScreen(new FilterEditScreen(slot, tag == null ? "" : tag.getString(TubeFilterItem.SOURCE_TAG)));
            
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack item, @Nullable Level level, List<Component> tooltips, TooltipFlag advanced) {


        
        tooltips.add(Component.translatable("tooltip"+item.getDescriptionId().substring(4)));

        super.appendHoverText(item, level, tooltips, advanced);
    }
}
