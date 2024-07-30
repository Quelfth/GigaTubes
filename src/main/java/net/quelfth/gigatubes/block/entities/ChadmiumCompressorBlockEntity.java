package net.quelfth.gigatubes.block.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.quelfth.gigatubes.block.GigaBlockEntities;
import net.quelfth.gigatubes.recipe.ChadmiumCompressorRecipe;
import net.quelfth.gigatubes.screen.ChadmiumCompressorMenu;

public class ChadmiumCompressorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(17);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int max_progress = 1000;

    

    public ChadmiumCompressorBlockEntity(BlockPos pos, BlockState state) { 
        super(GigaBlockEntities.CHADMIUM_COMPRESSOR.get(), pos, state); 
        this.data = new ContainerData() {

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ChadmiumCompressorBlockEntity.this.progress;
                    case 1 -> ChadmiumCompressorBlockEntity.this.max_progress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ChadmiumCompressorBlockEntity.this.progress = value;
                    case 1 -> ChadmiumCompressorBlockEntity.this.max_progress = value;
                }
            }

            @Override
            public int getCount() { return 2; }
            
        };
    }



    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side) {

        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        final @Nonnull SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        if(level != null)
            Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.gigatubes.chadmium_compressor");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ChadmiumCompressorMenu(id, inv, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("chadmium_compressor.progress", progress);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("chadmium_compressor.progress");
    }



    public void tick(Level level, BlockPos pos, BlockState state) {
        Optional<ChadmiumCompressorRecipe> possible_recipe = getRecipe();
        if (!possible_recipe.isPresent()) {
            resetProgress();
            return;
        }
        ChadmiumCompressorRecipe recipe = possible_recipe.get();
        if (canCraft(recipe)) {
            progress++;
            setChanged(level, pos, state);

            if (progress >= max_progress) {
                craftItem(recipe);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    

    private void resetProgress() {
        progress = 0;
    }

    private boolean canCraft(ChadmiumCompressorRecipe recipe) {
        Level level = this.level;
        if (level == null) 
            return false;

        ItemStack result = recipe.getResultItem(level.registryAccess());

        return canOutput(result);
    }

    private Optional<ChadmiumCompressorRecipe> getRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        if (level != null) {
            @Nonnull Level level = this.level;
            return level.getRecipeManager().getRecipeFor(ChadmiumCompressorRecipe.Type.INSTANCE, inventory, level);
        }
        else
            return Optional.empty();
    }

    private boolean canOutput(ItemStack item) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return stack.isEmpty() || stack.is(item.getItem()) && stack.getCount() + item.getCount() <= stack.getMaxStackSize();
    }



    private void craftItem(ChadmiumCompressorRecipe recipe) {
        Level level = this.level;
        if (level == null)
            return;
        ItemStack result = recipe.getResultItem(level.registryAccess());

        for (int i = 1; i <= 16; i++) 
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        
        itemHandler.insertItem(0, result, false);
    }



    
    
    
}
