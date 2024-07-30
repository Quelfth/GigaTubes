package net.quelfth.gigatubes.block.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IAcceptsTubeIO {
    
    public boolean acceptTubeIO(BlockState state, BlockPos pos, Level level, Direction ioDir, boolean in);
}
