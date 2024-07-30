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
import net.quelfth.gigatubes.block.entities.ChadmiumCompressorBlockEntity;

public class ChadmiumCompressorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing");

    public ChadmiumCompressorBlock() { 
        super(Properties.copy(Blocks.DEEPSLATE)); 
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return stateDefinition.any().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.getBlock() != newState.getBlock() 
            && level.getBlockEntity(pos) instanceof ChadmiumCompressorBlockEntity entity) 
                entity.drops();
        
             
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) 
            if (level.getBlockEntity(pos) instanceof ChadmiumCompressorBlockEntity entity) 
                NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
            else 
                throw new IllegalStateException("Out Container provider is missing!");
        
        return InteractionResult.sidedSuccess(level.isClientSide());
    }


    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new ChadmiumCompressorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> entity) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(entity, GigaBlockEntities.CHADMIUM_COMPRESSOR.get(), (l, p, s, e) -> e.tick(l, p, s));
    }
}
