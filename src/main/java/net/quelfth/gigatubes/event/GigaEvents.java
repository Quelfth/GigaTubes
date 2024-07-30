package net.quelfth.gigatubes.event;


import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.quelfth.gigatubes.item.Wrench;
import net.quelfth.gigatubes.sound.GigaSounds;
import net.quelfth.gigatubes.tags.GigaTags;



public class GigaEvents {
    @SubscribeEvent 
    public void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        //System.out.println("Haha big deal");
        final @Nonnull ItemStack item = event.getItemStack();
        if (event.getEntity().isShiftKeyDown() && Wrench.isWrench(item)) {
        
            final @Nonnull Level level = event.getLevel();
            final @Nonnull BlockState block = event.getLevel().getBlockState(event.getPos());
            final @Nonnull var hit = event.getHitVec();
            final int lvl = Wrench.wrenchLevel(item);
            if (lvl >= 3 || lvl >= 2 && block.is(GigaTags.HARD_DISMANTLEABLE) || lvl >= 1 && block.is(GigaTags.EASY_DISMANTLEABLE)) {
                dismantle(event.getEntity(), level, event.getPos(), hit);

                event.setUseItem(Event.Result.ALLOW);
                
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                event.setCanceled(true);
            }
        }
    }


    private static void dismantle(final Player player, final Level level, final BlockPos pos, final HitResult hit) {

        if (!(
            dismantleChestlikeIntact(player, level, pos, hit) || 
            dismantleDirectClone(player, level, pos, hit)
        )) return;

        level.removeBlock(pos, true);

        level.playSound(player, pos, GigaSounds.WRENCH_SOUND.get(), SoundSource.BLOCKS, 1.f, 1.f);
    }

    private static boolean dismantleDirectClone(final Player player, final Level level, final BlockPos pos, final HitResult hit) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        //final var block = state.getBlock();

        final @Nonnull ItemStack item = state.getCloneItemStack(hit, level, pos, player);
            if (item.isEmpty())
                return false;

        if (!level.isClientSide()) 
            player.getInventory().placeItemBackInInventory(item);   
        
            
        return true;
    }

    private static boolean dismantleChestlikeIntact(final Player player, final Level level, final BlockPos pos, final HitResult hit) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        if (!state.is(GigaTags.DISMANTLE_CHESTLIKE))
            return false;
        
        forceDismantleChestlikeIntact(player, level, pos, hit);
        return true;
    }

    private static void forceDismantleChestlikeIntact(final Player player, final Level level, final BlockPos pos, final HitResult hit) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        //final var block = state.getBlock();

        if (level instanceof ServerLevel) {

            final @Nonnull ItemStack item = state.getCloneItemStack(hit, level, pos, player);

            if (state.hasBlockEntity()) {
                final BlockEntity entity = level.getBlockEntity(pos);
                if (entity != null) 
                    addCustomNbtData(item, entity);
                level.removeBlockEntity(pos);
            }

            player.getInventory().placeItemBackInInventory(item);
            
        }
    
    }

    private static void addCustomNbtData(final ItemStack item, final BlockEntity blockEntity) {
        final @Nonnull CompoundTag blockEntityData = blockEntity.saveWithoutMetadata();
        BlockItem.setBlockEntityData(item, blockEntity.getType(), blockEntityData);
        if (item.getItem() instanceof PlayerHeadItem && blockEntityData.contains("SkullOwner")) {
            final @Nonnull CompoundTag skullOwner = blockEntityData.getCompound("SkullOwner");
            final @Nonnull CompoundTag skullOwnerTag = item.getOrCreateTag();
            skullOwnerTag.put("SkullOwner", skullOwner);
            final @Nonnull CompoundTag skullOwner$blockEntityTag = skullOwnerTag.getCompound("BlockEntityTag");
            skullOwner$blockEntityTag.remove("SkullOwner");
            skullOwner$blockEntityTag.remove("x");
            skullOwner$blockEntityTag.remove("y");
            skullOwner$blockEntityTag.remove("z");
        } else {
            final @Nonnull CompoundTag display = new CompoundTag();
            final @Nonnull ListTag lore = new ListTag();
            //CompoundTag gigaLore = new CompoundTag();
        
            lore.add(StringTag.valueOf("{\"translate\":\"text.gigatubes.dismantled_lore\"}")); 
            display.put("Lore", lore);
            item.addTagElement("display", display);
        }
    }


}
