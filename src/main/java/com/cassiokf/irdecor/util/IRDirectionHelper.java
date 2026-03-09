package com.cassiokf.irdecor.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public final class IRDirectionHelper {

    private IRDirectionHelper() {}

    /**
     * Get the horizontal facing direction from a living entity (opposite of where they look).
     */
    public static ForgeDirection getHorizontalFacing(EntityLivingBase entity) {
        int facing = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        switch (facing) {
            case 0:
                return ForgeDirection.SOUTH;
            case 1:
                return ForgeDirection.WEST;
            case 2:
                return ForgeDirection.NORTH;
            case 3:
                return ForgeDirection.EAST;
            default:
                return ForgeDirection.SOUTH;
        }
    }

    /**
     * Get the horizontal facing direction opposite to where the entity looks.
     */
    public static ForgeDirection getHorizontalFacingOpposite(EntityLivingBase entity) {
        return getHorizontalFacing(entity).getOpposite();
    }

    /**
     * Convert a horizontal ForgeDirection to a 2-bit metadata value (0-3).
     * SOUTH=0, WEST=1, NORTH=2, EAST=3
     */
    public static int directionToMeta(ForgeDirection dir) {
        switch (dir) {
            case SOUTH:
                return 0;
            case WEST:
                return 1;
            case NORTH:
                return 2;
            case EAST:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Convert a 2-bit metadata value (0-3) to a horizontal ForgeDirection.
     */
    public static ForgeDirection metaToDirection(int meta) {
        switch (meta & 3) {
            case 0:
                return ForgeDirection.SOUTH;
            case 1:
                return ForgeDirection.WEST;
            case 2:
                return ForgeDirection.NORTH;
            case 3:
                return ForgeDirection.EAST;
            default:
                return ForgeDirection.SOUTH;
        }
    }

    /**
     * Get the ForgeDirection corresponding to which side of a block was clicked.
     */
    public static ForgeDirection fromSideId(int side) {
        return ForgeDirection.getOrientation(side);
    }
}
