package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Platform block: full solid block base with side railings on disconnected sides.
 * Railings extend 1 block above the platform.
 * isTopSolid so things can be placed on top.
 */
public class BlockPlatform extends BlockIRConnectable {

    @Override
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        Block neighbor = getNeighborBlock(world, x, y, z, direction);

        if (direction == ForgeDirection.DOWN) {
            return neighbor.isNormalCube() || neighbor instanceof BlockBrace
                || neighbor instanceof BlockPlatform
                || neighbor instanceof BlockPillar
                || neighbor instanceof BlockColumn;
        }
        if (direction == ForgeDirection.UP) {
            return neighbor.isNormalCube() || neighbor instanceof BlockPlatform || neighbor instanceof BlockPillar;
        }
        // Horizontal: connected to other platforms, full cubes, catwalks, stairs, gates
        return neighbor instanceof BlockPlatform || neighbor.isNormalCube()
            || neighbor instanceof BlockCatwalkGate
            || neighbor instanceof BlockCatwalkStair;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        // Solid base
        AxisAlignedBB base = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        if (mask.intersectsWith(base)) list.add(base);

        // Railings on disconnected horizontal sides when there's nothing on top
        if (!canConnectTo(world, x, y, z, ForgeDirection.UP)) {
            if (!canConnectTo(world, x, y, z, ForgeDirection.NORTH)) {
                AxisAlignedBB r = AxisAlignedBB.getBoundingBox(x, y + 1.0, z, x + 1.0, y + 1.0, z + 0.03125);
                if (mask.intersectsWith(r)) list.add(r);
            }
            if (!canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) {
                AxisAlignedBB r = AxisAlignedBB.getBoundingBox(x, y + 1.0, z + 0.96875, x + 1.0, y + 1.0, z + 1.0);
                if (mask.intersectsWith(r)) list.add(r);
            }
            if (!canConnectTo(world, x, y, z, ForgeDirection.WEST)) {
                AxisAlignedBB r = AxisAlignedBB.getBoundingBox(x, y + 1.0, z, x + 0.03125, y + 1.0, z + 1.0);
                if (mask.intersectsWith(r)) list.add(r);
            }
            if (!canConnectTo(world, x, y, z, ForgeDirection.EAST)) {
                AxisAlignedBB r = AxisAlignedBB.getBoundingBox(x + 0.96875, y + 1.0, z, x + 1.0, y + 1.0, z + 1.0);
                if (mask.intersectsWith(r)) list.add(r);
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // No facing needed
    }
}
