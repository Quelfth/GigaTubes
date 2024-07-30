package net.quelfth.gigatubes.item.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.quelfth.gigatubes.block.blocks.IAcceptsTubeModule;
import net.quelfth.gigatubes.block.tube_parts.TubeModule;
import net.quelfth.gigatubes.util.GigaUtil;

public abstract class TubeModuleItem extends Item {

    public TubeModuleItem() {
        super(new Item.Properties());
    }

    public abstract TubeModule asModule(ItemStack item);

    private static void playSound(final Level level, final BlockPos pos, final @Nullable Player player) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        final @Nonnull SoundType soundtype = state.getSoundType(level, pos, player);
        level.playSound(player, pos, getPlaceSound(state, level, pos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.f) / 2.f, soundtype.getPitch() * 0.8f);
    }

    private static SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, @Nullable Player player) {
        return state.getSoundType(world, pos, player).getPlaceSound();
    }

    private InteractionResult placeWithin(final UseOnContext context) {
        return placeWithin(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
    }

    private InteractionResult placeWithin(final BlockPlaceContext context) {
        return placeWithin(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
    }
    
    private InteractionResult placeWithin(final Level level, final BlockPos pos, final Vec3 clickLoc, final Direction clickFace, final @Nullable Player player, final ItemStack item) {

        final @Nonnull BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IAcceptsTubeModule acceptor 
            && (acceptor.acceptTubeModule(state, pos, level, GigaUtil.clickOctant(pos, clickLoc), asModule(item))
            || acceptor.acceptTubeModule(state, pos, level, clickFace, asModule(item)))) {
            playSound(level, pos, player);
            if (player == null || !player.getAbilities().instabuild) {item.shrink(1);}
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult placeInResult = placeWithin(context);
        if (placeInResult.consumesAction())
            return placeInResult;
        InteractionResult placeIntoResult = placeWithin(new BlockPlaceContext(context));
        return placeIntoResult;
    }

}
