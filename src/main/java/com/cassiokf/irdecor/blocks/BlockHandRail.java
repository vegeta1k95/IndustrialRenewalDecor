package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHandRail extends BlockIRHorizontalFacing {

    public BlockHandRail() {
        super();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        ForgeDirection face = getFacing(world.getBlockMetadata(x, y, z));
        switch (face) {
            case NORTH:
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
                break;
            case SOUTH:
                setBlockBounds(0.0f, 0.0f, 0.9375f, 1.0f, 1.0f, 1.0f);
                break;
            case WEST:
                setBlockBounds(0.0f, 0.0f, 0.0f, 0.0625f, 1.0f, 1.0f);
                break;
            case EAST:
                setBlockBounds(0.9375f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            default:
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        ForgeDirection face = getFacing(world.getBlockMetadata(x, y, z));
        AxisAlignedBB box;
        switch (face) {
            case NORTH:
                box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.03125);
                break;
            case SOUTH:
                box = AxisAlignedBB.getBoundingBox(x, y, z + 0.96875, x + 1.0, y + 1.0, z + 1.0);
                break;
            case WEST:
                box = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.03125, y + 1.0, z + 1.0);
                break;
            case EAST:
                box = AxisAlignedBB.getBoundingBox(x + 0.96875, y, z, x + 1.0, y + 1.0, z + 1.0);
                break;
            default:
                box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.03125);
                break;
        }
        if (mask.intersectsWith(box)) {
            list.add(box);
        }
    }

    /**
     * Check if the block below is not a full block (used by the "down" property for rendering support posts).
     */
    public static boolean hasDownConnection(IBlockAccess world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return !below.isNormalCube();
    }
}
