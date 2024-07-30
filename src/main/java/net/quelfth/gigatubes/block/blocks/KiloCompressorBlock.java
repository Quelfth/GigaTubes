package net.quelfth.gigatubes.block.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.quelfth.gigatubes.block.GigaBlockEntities;
import net.quelfth.gigatubes.block.entities.KiloCompressorBlockEntity;

public class KiloCompressorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public KiloCompressorBlock() { 
        super(Properties.copy(Blocks.DEEPSLATE)); 
    }

    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext pContext) {
        return stateDefinition.any().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if(state.getBlock() != newState.getBlock() 
            && level.getBlockEntity(pos) instanceof KiloCompressorBlockEntity entity) 
                entity.drops();
        
             
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!level.isClientSide()) 
            if (level.getBlockEntity(pos) instanceof KiloCompressorBlockEntity entity) 
                NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
            else 
                throw new IllegalStateException("Out Container provider is missing!");
        
        return InteractionResult.sidedSuccess(level.isClientSide());
    }


    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new KiloCompressorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> entity) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(entity, GigaBlockEntities.KILO_COMPRESSOR.get(), (l, p, s, e) -> e.tick(l, p, s));
    }
}
