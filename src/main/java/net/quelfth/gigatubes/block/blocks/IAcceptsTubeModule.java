package net.quelfth.gigatubes.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.quelfth.gigatubes.block.tube_parts.TubeModule;

public interface IAcceptsTubeModule {
    public boolean acceptTubeModule(BlockState state, BlockPos pos, Level level, Direction dir, TubeModule module);
}
