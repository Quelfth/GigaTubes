package net.quelfth.gigatubes.util;

import javax.annotation.Nullable;

import java.util.Objects;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class BlockFace {
    public final BlockPos pos;
    public final Direction dir;

    public BlockFace(final BlockPos pos, final Direction dir) {
        this.pos = pos;
        this.dir = dir;
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj instanceof final BlockFace face) 
            return face.pos.equals(pos) && face.dir.equals(dir);
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir.get3DDataValue(), pos);
    }
}
