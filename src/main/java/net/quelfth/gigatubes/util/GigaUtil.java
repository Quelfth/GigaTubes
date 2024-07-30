package net.quelfth.gigatubes.util;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class GigaUtil {

    public static Direction clickOctant(final BlockPos pos, final Vec3 clickLoc) {
        return predominantDir(clickLoc.subtract(Vec3.atCenterOf(pos)));
    }


    public static Direction predominantDir(final Vec3 offset) {
        final double x = offset.x;
        final double y = offset.y;
        final double z = offset.z;

        final double ax = Math.abs(x);
        final double ay = Math.abs(y);
        final double az = Math.abs(z);

        if (ax > ay) {
            if (ax > az) 
                if (x > 0)
                    return Direction.EAST;
                else
                    return Direction.WEST;
        }
        else 
            if (ay > az)
                if (y > 0)
                    return Direction.UP;
                else
                    return Direction.DOWN;

        if (z > 0)
            return Direction.SOUTH;
         else
             return Direction.NORTH;
    }

    
    

    
}


