package net.quelfth.gigatubes.block.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.quelfth.gigatubes.block.entities.TubeBlockEntity;

public interface ITubePlaceIn {
    
    public BlockState placeInState(BlockState current, TubeBlock block);
    public void applyPlacedEntityData(BlockState state, TubeBlockEntity entity);
}
