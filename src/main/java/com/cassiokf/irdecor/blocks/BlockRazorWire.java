package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

public class BlockRazorWire extends BlockIRHorizontalFacing {

    public BlockRazorWire() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Razor wire faces same direction as player (not opposite)
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = IRDirectionHelper.directionToMeta(facing);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            entity.setInWeb();
            entity.attackEntityFrom(DamageSource.cactus, 1.0f);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Computed "frame" property: true if there is no razor wire neighbor to the right of the facing direction.
     */
    public static boolean hasFrame(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection facing = getFacing(meta);
        ForgeDirection right = rotateY(facing);
        return !(world.getBlock(x + right.offsetX, y + right.offsetY, z + right.offsetZ) instanceof BlockRazorWire);
    }

    private static ForgeDirection rotateY(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.NORTH;
            default:
                return dir;
        }
    }
}
