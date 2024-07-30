package net.quelfth.gigatubes.block.blocks;





import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITubeConnect {
    
    public boolean connectingIn(BlockState state, @Nullable BlockEntity entity, Direction dir);

    public boolean canConnectIn(BlockState state, @Nullable BlockEntity entity, Direction dir);
}
