package net.quelfth.gigatubes.block.blocks;

import java.lang.Math;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.quelfth.gigatubes.block.GigaBlockEntities;
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.item.Wrench;
import net.quelfth.gigatubes.util.GigaUtil;


public class TubeBlock extends Block implements ITubeConnect, IAcceptsTubeIO, EntityBlock {
    
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    

    public static final BooleanProperty UNSATISFIED = BooleanProperty.create("unsatisfied");

    public static final BooleanProperty DATA = BooleanProperty.create("data");

    private static final StateArgumentPredicate<EntityType<?>> NEVER_ARGS = (_0, _1, _2, _3) -> false;
    private static final StatePredicate NEVER = (_0, _1, _2) -> false;
    
    private static final VoxelShape SHAPE_CORE = Block.box(5., 5., 5., 11., 11., 11.);
    private static final VoxelShape SHAPE_UP = Block.box(5., 11., 5., 11., 16., 11.);
    private static final VoxelShape SHAPE_DOWN = Block.box(5., 0., 5., 11., 5., 11.);
    private static final VoxelShape SHAPE_NORTH = Block.box(5., 5., 0., 11., 11., 5.);
    private static final VoxelShape SHAPE_SOUTH = Block.box(5., 5., 11., 11., 11., 16.);
    private static final VoxelShape SHAPE_EAST = Block.box(11., 5., 5., 16., 11., 11.);
    private static final VoxelShape SHAPE_WEST = Block.box(0., 5., 5., 5., 11., 11.);
    private static final VoxelShape SHAPE_UP_IO = Block.box(4., 14., 4., 12., 16., 12.);
    private static final VoxelShape SHAPE_DOWN_IO = Block.box(4., 0., 4., 12., 2., 12.);
    private static final VoxelShape SHAPE_NORTH_IO = Block.box(4., 4., 0., 12., 12., 2.);
    private static final VoxelShape SHAPE_SOUTH_IO = Block.box(4., 4., 14., 12., 12., 16.);
    private static final VoxelShape SHAPE_EAST_IO = Block.box(14., 4., 4., 16., 12., 12.);
    private static final VoxelShape SHAPE_WEST_IO = Block.box(0., 4., 4., 2., 12., 12.);

    public TubeBlock() {
        super(Properties.of()
            .mapColor(MapColor.DEEPSLATE)
            .strength(3.0f, 6.0f)
            .sound(SoundType.DEEPSLATE)
            .noOcclusion()
            .isValidSpawn(NEVER_ARGS)
            .isRedstoneConductor(NEVER)
            .isSuffocating(NEVER)
            .isViewBlocking(NEVER)
        );

        registerDefaultState(stateDefinition.any()
            .setValue(UP, false)
            .setValue(DOWN, false)
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(EAST, false)
            .setValue(WEST, false)
            .setValue(UNSATISFIED, false)
            .setValue(DATA, false)
        );
    }

    public static BooleanProperty tubeProp(Direction dir) {
        switch (dir) {
            case UP: return UP;
            case DOWN: return DOWN;
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case EAST: return EAST;
            case WEST: return WEST;
            default: return UP;
        }
    }

    public int transferSpeed() { return 1; }

    private static boolean isAnyTube(final Block block) {
        return block instanceof TubeBlock;
    }

    private static boolean canConnect(final Level level, final BlockPos pos, final Direction dir) {
        if (anyIO(level, pos, dir))
            return true;
        final BlockState state = level.getBlockState(pos.relative(dir));
        final BlockEntity entity = level.getBlockEntity(pos.relative(dir));
        return state.getBlock() instanceof ITubeConnect tube && tube.canConnectIn(state, entity, dir.getOpposite());
    }

    private static boolean shouldConnect(final Level level, final BlockPos pos, final Direction dir) {
        if (anyIO(level, pos, dir)) {
            return level.getBlockState(pos).getValue(tubeProp(dir));
        }
        final BlockState state = level.getBlockState(pos.relative(dir));
        final BlockEntity entity = level.getBlockEntity(pos.relative(dir));
        return state.getBlock() instanceof ITubeConnect tube && tube.connectingIn(state, entity, dir.getOpposite());
    }

    

    private static boolean statesMatch(final BlockState a, final BlockState b) {
        return a.getProperties().stream().allMatch(property -> a.getValue(property).equals(b.getValue(property)));
    }

    private static boolean satisfied(BlockState state, Level level,  BlockPos pos) {
        return satisfiedInDir(state, level, pos, Direction.UP) &&
                satisfiedInDir(state, level, pos, Direction.DOWN) &&
                satisfiedInDir(state, level, pos, Direction.NORTH) &&
                satisfiedInDir(state, level, pos, Direction.SOUTH) &&
                satisfiedInDir(state, level, pos, Direction.EAST) &&
                satisfiedInDir(state, level, pos, Direction.WEST);
    }

    private static boolean satisfiedInDir(final BlockState state, final Level level, final BlockPos pos, final Direction dir) {
        BlockPos neighborPos = pos.relative(dir);

        BlockState neighborState = level.getBlockState(neighborPos);
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

        if (!(neighborState.getBlock() instanceof ITubeConnect neighbor)) {
            return true;
        }

        if (!neighbor.canConnectIn(neighborState, neighborEntity, dir.getOpposite())) {
            return true;
        }

        if (neighbor.connectingIn(neighborState, neighborEntity, dir.getOpposite()) == state.getValue(tubeProp(dir))) {
            return true;
        }
        
        return false;
    }

    


    private BlockState getMaximalConnectionsState(final Level level, final BlockPos pos, final boolean stubborn) {
        final boolean up = canConnect(level, pos, Direction.UP);
        final boolean down = canConnect(level, pos, Direction.DOWN);
        final boolean north = canConnect(level, pos, Direction.NORTH);
        final boolean south = canConnect(level, pos, Direction.SOUTH);
        final boolean east = canConnect(level, pos, Direction.EAST);
        final boolean west = canConnect(level, pos, Direction.WEST);
        return defaultBlockState()
            .setValue(UP, up)
            .setValue(DOWN, down)
            .setValue(NORTH, north)
            .setValue(SOUTH, south)
            .setValue(EAST, east)
            .setValue(WEST, west)
            .setValue(UNSATISFIED, stubborn && (up || down || north || south || east || west));
    }

    private BlockState getState(final Level level, final BlockPos pos) {
        return defaultBlockState()
            .setValue(UP, shouldConnect(level, pos, Direction.UP))
            .setValue(DOWN, shouldConnect(level, pos, Direction.DOWN))
            .setValue(NORTH, shouldConnect(level, pos, Direction.NORTH))
            .setValue(SOUTH, shouldConnect(level, pos, Direction.SOUTH))
            .setValue(EAST, shouldConnect(level, pos, Direction.EAST))
            .setValue(WEST, shouldConnect(level, pos, Direction.WEST))
            .setValue(DATA, level.getBlockState(pos).getValue(DATA));
    }

    

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(
            UP,     DOWN,     NORTH,     SOUTH,     EAST,     WEST,
            // UP_IN,  DOWN_IN,  NORTH_IN,  SOUTH_IN,  EAST_IN,  WEST_IN, 
            // UP_OUT, DOWN_OUT, NORTH_OUT, SOUTH_OUT, EAST_OUT, WEST_OUT, 
            UNSATISFIED, DATA);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final @Nonnull Level level = context.getLevel();
        final @Nonnull BlockPos pos = context.getClickedPos();
        final @Nonnull Direction dir = context.getClickedFace();
        //System.out.println(dir);
        
        final @Nonnull Block block = level.getBlockState(pos.relative(dir.getOpposite())).getBlock();

        if (isAnyTube(block)) 
            return defaultBlockState().setValue(tubeProp(dir.getOpposite()), isAnyTube(block)).setValue(UNSATISFIED, true);
        else {
            return getMaximalConnectionsState(level, pos, true);
        }
    }

    

    @Override
    public boolean connectingIn(BlockState state, @Nullable BlockEntity entity, Direction dir) {
        return !anyIO(entity, dir) && state.getValue(tubeProp(dir));
    }

    @Override
    public boolean canConnectIn(BlockState state, @Nullable BlockEntity entity, Direction dir) {
        return !anyIO(entity, dir);
    }

    public static boolean interconnecting(final Level level, final BlockPos pos, final Direction dir) {
        final @Nonnull BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TubeBlock))
            return false;
        return !anyIO(level, pos, dir) && state.getValue(tubeProp(dir));
    }

    public static boolean anyIO(final Level level, final BlockPos pos, final Direction dir) {
        return anyIO(getTubeBlockEntity(level, pos), dir);
    }

    public static boolean anyIO(final @Nullable TubeBlockEntity entity, final Direction dir) {
        if (entity == null)
            return false;
        return entity.anyIO(dir);
    }

    public static boolean anyIO(final @Nullable BlockEntity entity, final Direction dir) {
        if (!(entity instanceof TubeBlockEntity tube))
            return false;
        return tube.anyIO(dir);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean piston) {
        super.neighborChanged(state, level, pos, neighbor, neighborPos, piston);

        updateState(state, level, pos);
    }

    private void updateState(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(UNSATISFIED) && !satisfied(state, level, pos)) {

            return;
        }


        final BlockState newState = getState(level, pos);
        if (!statesMatch(state, newState)) 
            level.setBlockAndUpdate(pos, newState);
    }

    

    

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) { 
        return 1.f; 
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(final BlockState state, final BlockState neighbor, final Direction dir) {
        return neighbor.is(this) && neighbor.getValue(tubeProp(dir.getOpposite())) ? true : super.skipRendering(state, neighbor, dir);
    }

    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        VoxelShape shape = SHAPE_CORE;
        final BooleanOp op = BooleanOp.OR;
        if (state.getValue(UP)) shape = Shapes.join(shape, SHAPE_UP, op);
        if (state.getValue(DOWN)) shape = Shapes.join(shape, SHAPE_DOWN, op);
        if (state.getValue(NORTH)) shape = Shapes.join(shape, SHAPE_NORTH, op);
        if (state.getValue(SOUTH)) shape = Shapes.join(shape, SHAPE_SOUTH, op);
        if (state.getValue(EAST)) shape = Shapes.join(shape, SHAPE_EAST, op);
        if (state.getValue(WEST)) shape = Shapes.join(shape, SHAPE_WEST, op);
        if (state.getValue(DATA)) {
            TubeBlockEntity entity = getTubeBlockEntity(level, pos);
            if (entity != null) {
                if (entity.anyIO(Direction.UP)) shape = Shapes.join(shape, SHAPE_UP_IO, op);
                if (entity.anyIO(Direction.DOWN)) shape = Shapes.join(shape, SHAPE_DOWN_IO, op);
                if (entity.anyIO(Direction.NORTH)) shape = Shapes.join(shape, SHAPE_NORTH_IO, op);
                if (entity.anyIO(Direction.SOUTH)) shape = Shapes.join(shape, SHAPE_SOUTH_IO, op);
                if (entity.anyIO(Direction.EAST)) shape = Shapes.join(shape, SHAPE_EAST_IO, op);
                if (entity.anyIO(Direction.WEST)) shape = Shapes.join(shape, SHAPE_WEST_IO, op);
            }
        }
        return shape;
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hit) {
        
        if (player.getItemInHand(hand).is(Items.STICK)) {
            final Direction dir = GigaUtil.predominantDir(hit.getLocation().subtract(Vec3.atCenterOf(pos)));
            boolean data = state.getValue(DATA);
            String message = "";
            if (data) {
                message += "[DATA]";
            }

            var entity = getTubeBlockEntity(level, pos);

            if (entity != null) {
                message += "[Entity]";
                if (entity.getIO(dir, true))
                    message += "I";
                if (entity.getIO(dir, false))
                    message += "O";
            }

            message += " {AnyIO: " + (anyIO(level, pos, dir) ? "true" : "false") + "}";

            if (satisfied(state, level, pos))


            if (level.isClientSide())
                player.displayClientMessage(Component.literal(message), false);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (Wrench.isWrench(player.getItemInHand(hand))) {
            final Direction dir = GigaUtil.predominantDir(hit.getLocation().subtract(Vec3.atCenterOf(pos)));
            final BooleanProperty prop = tubeProp(dir);
            if (state.getValue(prop)) {
                if (level.isClientSide()) 
                    return InteractionResult.SUCCESS;
                level.setBlockAndUpdate(pos, state.setValue(prop, false).setValue(UNSATISFIED, true));
                return InteractionResult.CONSUME;
            } else {
                if (canConnect(level, pos, dir)) {
                    if (level.isClientSide())
                        return InteractionResult.SUCCESS;
                    level.setBlockAndUpdate(pos, state.setValue(prop, true).setValue(UNSATISFIED, true));
                    return InteractionResult.CONSUME;
                }
            }

            
        }
        
        return InteractionResult.PASS;
    }


    @Override
    public void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        if (!(level.getBlockEntity(pos) instanceof final TubeBlockEntity entity)) return;

        final @Nonnull Vec3 eye = player.getEyePosition();
        final @Nonnull Vec3 look = player.getLookAngle().scale(player.getBlockReach());
        final @Nonnull BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        
        final @Nonnull Vec3 offset = hit.getLocation().subtract(Vec3.atCenterOf(hit.getBlockPos())).scale(16.);

        System.out.println(offset);

        final double absX = Math.abs(offset.x);
        final double absY = Math.abs(offset.y);
        final double absZ = Math.abs(offset.z);

        if ((absX < 6. && absY < 6. && absZ < 6.)
            || absX < 3. && (absY < 3. || absZ < 3.) || absY < 3. && absZ < 3.
            )
            return;
        
        final @Nonnull Direction dir = GigaUtil.predominantDir(offset);

        final Vec3 location = hit.getLocation();

        boolean success = false;

        if (entity.getIO(dir, true)) {
            entity.setIO(dir, true, false);
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, new ItemStack(GigaItems.TUBE_INTAKE.get())));
            success = true;
        }
        
        if (entity.getIO(dir, false)) {
            entity.setIO(dir, false, false);
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, new ItemStack(GigaItems.TUBE_OUTPUT.get())));
            success = true;
        }
       
        if (!success) return;

        level.playSound(player, pos, state.getBlock().getSoundType(state, level, pos, player).getBreakSound(), SoundSource.BLOCKS);

        level.setBlockAndUpdate(pos, state.setValue(TubeBlock.DATA, entity.shouldExist()));
        updateState(state, level, pos);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return state.getValue(DATA) ? new TubeBlockEntity(pos, state) : null;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(final BlockState state, final Level level, final BlockPos pos, final BlockState newState, final boolean moved) {
        if (state.is(newState.getBlock())) {
            if (!newState.getValue(DATA)) {
                level.removeBlockEntity(pos);
            }
            return;
        } 
        if(state.getBlock() != newState.getBlock() 
            && level.getBlockEntity(pos) instanceof TubeBlockEntity entity) 
                entity.drops();
        
        
        super.onRemove(state, level, pos, newState, moved);
        
    }

    

    @Nullable
    public static TubeBlockEntity getTubeBlockEntity(final BlockGetter level, final BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof TubeBlockEntity tube) {
            return tube;
        }
        return null;
    }

    @Override
    public boolean acceptTubeIO( BlockState state, final BlockPos pos, final Level level, final Direction ioDir, final boolean in) {
        if (!state.getValue(DATA)) {
            state = state.setValue(DATA, true);
            level.setBlockAndUpdate(pos, state);
        } 
        TubeBlockEntity tube = getTubeBlockEntity(level, pos);
        if (tube != null) {
            boolean added = tube.tryAddIO(ioDir, in);
            
            level.setBlockAndUpdate(pos, state.setValue(tubeProp(ioDir), true));
            return added;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entity) {
        if (level.isClientSide()) {
            return null;
        }
        return 
            entity == GigaBlockEntities.TUBE.get() ? 
                (BlockEntityTicker<T>) (BlockEntityTicker<TubeBlockEntity>) (l, p, s, e) -> e.tick(l, p, s) 
            : 
                null;
    }


    
    
}