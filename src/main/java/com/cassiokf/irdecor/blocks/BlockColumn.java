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
 * Column block similar to pillar but with different connection rules.
 * Connects to pillars, catwalks, platforms, and full cubes.
 */
public class BlockColumn extends BlockIRConnectable {

    @Override
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        Block neighbor = getNeighborBlock(world, x, y, z, direction);

        // UP/DOWN: only connect to solid blocks, catwalks, platforms — NOT to other pillars/columns
        // (stacked columns should show plain intermediate posts, not junction pieces)
        if (direction == ForgeDirection.DOWN) {
            if (neighbor instanceof BlockBrace) {
                return BlockBrace.allowsConnectionFrom(world, x, y - 1, z, ForgeDirection.UP);
            }
            return neighbor.isNormalCube() || neighbor instanceof BlockPlatform || neighbor instanceof BlockPillar;
        }
        if (direction == ForgeDirection.UP) {
            return neighbor.isNormalCube() || neighbor instanceof BlockCatwalk
                || neighbor instanceof BlockPlatform
                || neighbor instanceof BlockPillar;
        }
        // Horizontal connections: braces only if not blocked from this side
        if (neighbor instanceof BlockBrace) {
            int nx = x + direction.offsetX, nz = z + direction.offsetZ;
            return BlockBrace.allowsConnectionFrom(world, nx, y, nz, direction.getOpposite());
        }
        return neighbor.isNormalCube() || neighbor instanceof BlockPillar
            || neighbor instanceof BlockColumn
            || neighbor instanceof BlockCatwalk;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float x1 = 0.25f, y1 = 0.0f, z1 = 0.25f;
        float x2 = 0.75f, y2 = 1.0f, z2 = 0.75f;
        if (canConnectTo(world, x, y, z, ForgeDirection.NORTH)) z1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) z2 = 1.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.WEST)) x1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.EAST)) x2 = 1.0f;
        setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        float x1 = 0.25f, z1 = 0.25f;
        float x2 = 0.75f, z2 = 0.75f;
        if (canConnectTo(world, x, y, z, ForgeDirection.NORTH)) z1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) z2 = 1.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.WEST)) x1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.EAST)) x2 = 1.0f;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x + x1, y, z + z1, x + x2, y + 1.0, z + z2);
        if (mask.intersectsWith(box)) {
            list.add(box);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // No facing needed for column
    }
}
