package net.quelfth.gigatubes.block.blocks;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.util.GigaUtil;

public class TubeIOBlock extends Block implements IAcceptsTubeIO, ITubePlaceIn {
    public static final BooleanProperty UP_IN = BooleanProperty.create("up_in");
    public static final BooleanProperty DOWN_IN = BooleanProperty.create("down_in");
    public static final BooleanProperty NORTH_IN = BooleanProperty.create("north_in");
    public static final BooleanProperty SOUTH_IN = BooleanProperty.create("south_in");
    public static final BooleanProperty EAST_IN = BooleanProperty.create("east_in");
    public static final BooleanProperty WEST_IN = BooleanProperty.create("west_in");
    public static final BooleanProperty UP_OUT = BooleanProperty.create("up_out");
    public static final BooleanProperty DOWN_OUT = BooleanProperty.create("down_out");
    public static final BooleanProperty NORTH_OUT = BooleanProperty.create("north_out");
    public static final BooleanProperty SOUTH_OUT = BooleanProperty.create("south_out");
    public static final BooleanProperty EAST_OUT = BooleanProperty.create("east_out");
    public static final BooleanProperty WEST_OUT = BooleanProperty.create("west_out");

    private static final StateArgumentPredicate<EntityType<?>> NEVER_ARGS = (_0, _1, _2, _3) -> false;
    private static final StatePredicate NEVER = (_0, _1, _2) -> false;
    
    private static final VoxelShape SHAPE_UP = Block.box(4., 14., 4., 12., 16., 12.);
    private static final VoxelShape SHAPE_DOWN = Block.box(4., 0., 4., 12., 2., 12.);
    private static final VoxelShape SHAPE_NORTH = Block.box(4., 4., 0., 12., 12., 2.);
    private static final VoxelShape SHAPE_SOUTH = Block.box(4., 4., 14., 12., 12., 16.);
    private static final VoxelShape SHAPE_EAST = Block.box(14., 4., 4., 16., 12., 12.);
    private static final VoxelShape SHAPE_WEST = Block.box(0., 4., 4., 2., 12., 12.);


    @SuppressWarnings("null")
    @Nonnull
    private static BooleanProperty prop(final Direction dir, final boolean in) {
        if (in) {
            switch (dir) {
                case UP -> { return UP_IN; }
                case DOWN -> { return DOWN_IN; }
                case NORTH -> { return NORTH_IN; }
                case SOUTH -> { return SOUTH_IN; }
                case EAST -> { return EAST_IN; }
                case WEST -> { return WEST_IN; }
            }
        } 
        switch (dir) {
            case UP -> { return UP_OUT; }
            case DOWN -> { return DOWN_OUT; }
            case NORTH -> { return NORTH_OUT; }
            case SOUTH -> { return SOUTH_OUT; }
            case EAST -> { return EAST_OUT; }
            case WEST -> { return WEST_OUT; }
        }

        return null;
    }

    public BlockState getMonoState(final Direction dir, final boolean intake) {
        return defaultBlockState().setValue(prop(dir, intake), true);
    }

    public TubeIOBlock() {
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
            .setValue(UP_IN, false)
            .setValue(DOWN_IN, false)
            .setValue(NORTH_IN, false)
            .setValue(SOUTH_IN, false)
            .setValue(EAST_IN, false)
            .setValue(WEST_IN, false)
            .setValue(UP_OUT, false)
            .setValue(DOWN_OUT, false)
            .setValue(NORTH_OUT, false)
            .setValue(SOUTH_OUT, false)
            .setValue(EAST_OUT, false)
            .setValue(WEST_OUT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(UP_IN, DOWN_IN, NORTH_IN, SOUTH_IN, EAST_IN, WEST_IN, UP_OUT, DOWN_OUT, NORTH_OUT, SOUTH_OUT, EAST_OUT, WEST_OUT);
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
    public boolean skipRendering(final BlockState pState, final BlockState pAdjacentBlockState, final Direction pSide) {
        return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
    }


    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        final BooleanOp op = BooleanOp.OR;
        if (state.getValue(UP_IN) || state.getValue(UP_OUT)) shape = Shapes.join(shape, SHAPE_UP, op);
        if (state.getValue(DOWN_IN) || state.getValue(DOWN_OUT)) shape = Shapes.join(shape, SHAPE_DOWN, op);
        if (state.getValue(NORTH_IN) || state.getValue(NORTH_OUT)) shape = Shapes.join(shape, SHAPE_NORTH, op);
        if (state.getValue(SOUTH_IN) || state.getValue(SOUTH_OUT)) shape = Shapes.join(shape, SHAPE_SOUTH, op);
        if (state.getValue(EAST_IN) || state.getValue(EAST_OUT)) shape = Shapes.join(shape, SHAPE_EAST, op);
        if (state.getValue(WEST_IN) || state.getValue(WEST_OUT)) shape = Shapes.join(shape, SHAPE_WEST, op);
        return shape;
    }

    @Override
    public boolean acceptTubeIO(BlockState state, BlockPos pos, Level level, Direction ioDir, boolean in) {
        if (state.getValue(prop(ioDir, in)))
            return false;

        level.setBlockAndUpdate(pos, state.setValue(prop(ioDir, in), true));

        return true;
    }

    @Override
    public BlockState placeInState(BlockState current, TubeBlock block) {
        return block.defaultBlockState()
            .setValue(TubeBlock.UP, current.getValue(UP_IN) || current.getValue(UP_OUT))
            .setValue(TubeBlock.DOWN, current.getValue(DOWN_IN) || current.getValue(DOWN_OUT))
            .setValue(TubeBlock.NORTH, current.getValue(NORTH_IN) || current.getValue(NORTH_OUT))
            .setValue(TubeBlock.SOUTH, current.getValue(SOUTH_IN) || current.getValue(SOUTH_OUT))
            .setValue(TubeBlock.EAST, current.getValue(EAST_IN) || current.getValue(EAST_OUT))
            .setValue(TubeBlock.WEST, current.getValue(WEST_IN) || current.getValue(WEST_OUT));

    }

    @Override
    public void applyPlacedEntityData(BlockState state, TubeBlockEntity entity) {
        for (Direction dir : Direction.values()) {
            entity.setIO(dir, true, state.getValue(prop(dir, true)));
            entity.setIO(dir, false, state.getValue(prop(dir, false)));
        }
    }

    @Override
    public void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        

        final @Nonnull Vec3 eye = player.getEyePosition();
        final @Nonnull Vec3 look = player.getLookAngle().scale(player.getBlockReach());
        final @Nonnull BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(look), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        
        final @Nonnull Vec3 offset = hit.getLocation().subtract(Vec3.atCenterOf(hit.getBlockPos())).scale(16.);
        
        final @Nonnull Direction dir = GigaUtil.predominantDir(offset);

        final Vec3 location = hit.getLocation();

        boolean success = false;

        @Nonnull BlockState newState = state;

        if (state.getValue(prop(dir, true))) {
            newState = newState.setValue(prop(dir, true), false);
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, new ItemStack(GigaItems.TUBE_INTAKE.get())));
            success = true;
        }
        
        if (state.getValue(prop(dir, false))) {
            newState = newState.setValue(prop(dir, false), false);
            level.addFreshEntity(new ItemEntity(level, location.x, location.y, location.z, new ItemStack(GigaItems.TUBE_OUTPUT.get())));
            success = true;
        }
       
        if (!success) return;

        level.playSound(player, pos, state.getBlock().getSoundType(state, level, pos, player).getBreakSound(), SoundSource.BLOCKS);

        if (emptyState(newState))
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        else
            level.setBlockAndUpdate(pos, newState);
    }

    public static boolean emptyState(BlockState state) {
        for (Direction dir : Direction.values()) {
            if (state.getValue(prop(dir, true)) || state.getValue(prop(dir, false)))
                return false;
        }
        return true;
    }
}
