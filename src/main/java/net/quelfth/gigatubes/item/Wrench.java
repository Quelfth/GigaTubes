package net.quelfth.gigatubes.item;

import net.minecraft.world.item.ItemStack;
import net.quelfth.gigatubes.tags.GigaTags;

public class Wrench {
    

    public static boolean isWrench(ItemStack item) {
        return item.is(GigaTags.WRENCH);
    }

    

    public static int wrenchLevel(ItemStack item) {
        if (!isWrench(item))
            return -1;
        if (item.is(GigaTags.WRENCH_DISMANTLE_ANY))
            return 3;
        if (item.is(GigaTags.WRENCH_DISMANTLE_HARD))
            return 2;
        if (item.is(GigaTags.WRENCH_DISMANTLE_EASY))
            return 1;
        return 0;
    }
}
