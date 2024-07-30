package net.quelfth.gigatubes.item.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.quelfth.gigatubes.block.blocks.ITubePlaceIn;
import net.quelfth.gigatubes.block.blocks.TubeBlock;
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;

public class TubeItem extends BlockItem {

    

    public TubeItem(TubeBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult placeInResult = placeIn(context);
        
        if (placeInResult.consumesAction())
            return placeInResult;
        
        
        return super.useOn(context);
    }

    private InteractionResult placeIn(final UseOnContext context) {

        final @Nonnull Level level = context.getLevel();
        final @Nonnull BlockPos pos = context.getClickedPos();
        final @Nonnull BlockState placeInState = level.getBlockState(pos);

        //System.out.println(pos);

        if (!(placeInState.getBlock() instanceof ITubePlaceIn placeIn))
            return InteractionResult.FAIL;

        if (!(getBlock() instanceof final TubeBlock block))
            return InteractionResult.PASS;

        BlockState state = placeIn.placeInState(placeInState, block);
        if (state == null)
            return InteractionResult.PASS;

        state = state.setValue(TubeBlock.DATA, true);

        if (!level.setBlock(pos, state, 1 | 2 | 8)) 
            return InteractionResult.FAIL;


        if (level.getBlockEntity(pos) instanceof final TubeBlockEntity entity) {
            //System.out.println("Setting entity data");
            placeIn.applyPlacedEntityData(placeInState, entity);
        }
        
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

    private void playSound(final Level level, final BlockPos pos, final @Nullable Player player) {
        if (player == null)
            return;
        final @Nonnull BlockState state = level.getBlockState(pos);
        final @Nonnull SoundType soundtype = state.getSoundType(level, pos, player);
        level.playSound(player, pos, getPlaceSound(state, level, pos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.f) / 2.f, soundtype.getPitch() * 0.8f);
    }

    
    
}
