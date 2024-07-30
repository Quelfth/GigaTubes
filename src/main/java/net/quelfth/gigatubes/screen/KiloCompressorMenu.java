package net.quelfth.gigatubes.screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.quelfth.gigatubes.block.GigaBlocks;
import net.quelfth.gigatubes.block.entities.KiloCompressorBlockEntity;

public class KiloCompressorMenu extends AbstractContainerMenu {

    public final KiloCompressorBlockEntity entity;
    private final Level level;
    private final ContainerData data;

    public KiloCompressorMenu(int id, Inventory inv, FriendlyByteBuf extra) {
        this(id, inv, inv.player.level().getBlockEntity(extra.readBlockPos()), new SimpleContainerData(17));
    }

    private static final int Y_OFFSET = -9;

    public KiloCompressorMenu(int id, Inventory inv, @Nullable BlockEntity entity, ContainerData data) {
        super(GigaMenus.KILO_COMPRESSOR.get(), id);
        checkContainerSize(inv, 17);
        this.entity = (KiloCompressorBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
            this.addSlot(new SlotItemHandler(h, 0, 136, 44 + Y_OFFSET));
            for(int i = 0; i < 4; i++)
                for(int j = 0; j < 4; j++)
                    addSlot(new SlotItemHandler(h, j + i*4 + 1, 20 + j*18, 17 + i * 18 + Y_OFFSET));
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 22;
        if (maxProgress <= 0 || progress <= 0 || progress >= maxProgress)
            return 0;
        
        return 1 + progressArrowSize * progress / maxProgress;
    }

    private static final int HOTBAR_LENGTH = 9;
    private static final int INVENTORY_ROWS = 3;
    private static final int INVENTORY_COLUMNS = 9;
    private static final int INVENTORY_LENGTH = INVENTORY_COLUMNS * INVENTORY_ROWS;
    private static final int PLAYER_SLOTS = HOTBAR_LENGTH + INVENTORY_LENGTH;
    private static final int PLAYER_SLOT_MIN = 0;
    private static final int PLAYER_SLOT_MAX = PLAYER_SLOT_MIN + PLAYER_SLOTS;
    private static final int BLOCK_SLOT_MIN = PLAYER_SLOT_MIN + PLAYER_SLOTS;

    private static final int BLOCK_SLOTS = 17;
    private static final int BLOCK_SLOT_MAX = BLOCK_SLOT_MIN + BLOCK_SLOTS;

    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();

        if (index < PLAYER_SLOT_MAX) {
            if (!moveItemStackTo(stack, BLOCK_SLOT_MIN, BLOCK_SLOT_MAX, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < BLOCK_SLOT_MAX) {
            if (!moveItemStackTo(stack, PLAYER_SLOT_MIN, PLAYER_SLOT_MAX, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }

        if (stack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return stillValid(ContainerLevelAccess.create(level, entity.getBlockPos()), player, GigaBlocks.KILO_COMPRESSOR.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j) 
                addSlot(new Slot(inv, j + i*9 + 9, 8 + j*18, 102 + Y_OFFSET + i*18));
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i)
            this.addSlot(new Slot(inv, i, 8 + i*18, 160 + Y_OFFSET));
    }
    
}
