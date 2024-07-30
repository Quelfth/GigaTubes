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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.quelfth.gigatubes.block.GigaBlocks;
import net.quelfth.gigatubes.block.blocks.IAcceptsTubeIO;
import net.quelfth.gigatubes.util.GigaUtil;

public abstract class TubeIOItem extends Item {

    public TubeIOItem() {
        super(new Item.Properties());
    }

    
    protected abstract boolean isIntake();


    private InteractionResult placeFree(final BlockPlaceContext context) {
        if (!context.canPlace())
            return InteractionResult.FAIL;

        final @Nonnull BlockState state = getPlaceState(context);

        if (!context.getLevel().setBlock(context.getClickedPos(), state, 0b001011)) 
            return InteractionResult.FAIL;

        final @Nonnull BlockPos pos = context.getClickedPos();
        final @Nonnull Level level = context.getLevel();
        final Player player = context.getPlayer();
        final @Nonnull ItemStack item = context.getItemInHand();
        final @Nonnull BlockState placedState = level.getBlockState(pos);

        playSound(level, pos, player);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));
        if (player == null || !player.getAbilities().instabuild) {
           item.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void playSound(final Level level, final BlockPos pos, final @Nullable Player player) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        final @Nonnull SoundType soundtype = state.getSoundType(level, pos, player);
        level.playSound(player, pos, getPlaceSound(state, level, pos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.f) / 2.f, soundtype.getPitch() * 0.8f);
    }

    private BlockState getPlaceState(BlockPlaceContext context) {
        final @Nonnull Direction dir = context.getClickedFace();
        return GigaBlocks.TUBE_IO.get().getMonoState(dir.getOpposite(), isIntake());
    }

    private static SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, @Nullable Player player) {
        return state.getSoundType(world, pos, player).getPlaceSound();
    }

    private InteractionResult placeWithin(final Level level, final BlockPos pos, final Vec3 clickLoc, final Direction clickFace, final @Nullable Player player, final ItemStack item) {

        final @Nonnull BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IAcceptsTubeIO acceptor 
            && (acceptor.acceptTubeIO(state, pos, level, GigaUtil.clickOctant(pos, clickLoc), isIntake())
            || acceptor.acceptTubeIO(state, pos, level, clickFace, isIntake()))) {
            playSound(level, pos, player);
            if (player == null || !player.getAbilities().instabuild) {item.shrink(1);}
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    private InteractionResult placeWithin(final UseOnContext context) {
        return placeWithin(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
    }

    private InteractionResult placeWithin(final BlockPlaceContext context) {
        return placeWithin(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
    }

    
    

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult placeInResult = placeWithin(context);
        if (placeInResult.consumesAction())
            return placeInResult;
        InteractionResult placeIntoResult = placeWithin(new BlockPlaceContext(context));
        if (placeIntoResult.consumesAction())
            return placeIntoResult;

        InteractionResult placeResult = placeFree(new BlockPlaceContext(context));

        return placeResult;
    }
}
