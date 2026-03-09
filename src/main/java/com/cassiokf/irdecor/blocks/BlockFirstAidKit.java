package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Decorative first aid kit, wall-mounted with facing + onwall.
 */
public class BlockFirstAidKit extends BlockIRHorizontalFacing {

    public BlockFirstAidKit() {
        super();
        setHardness(0.8f);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = IRDirectionHelper.directionToMeta(facing);
        meta |= (world.getBlockMetadata(x, y, z) & 0x4);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        if (side >= 2 && side <= 5) {
            return 0x4; // onwall bit
        }
        return 0;
    }

    public static boolean isOnWall(int meta) {
        return (meta & 0x4) != 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection face = getFacing(meta);
        // Box is 10x10x5 pixels (0.625 x 0.625 x 0.3125), centered on wall
        switch (face) {
            case NORTH:
                setBlockBounds(0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.3125f);
                break;
            case SOUTH:
                setBlockBounds(0.1875f, 0.1875f, 0.6875f, 0.8125f, 0.8125f, 1.0f);
                break;
            case WEST:
                setBlockBounds(0.0f, 0.1875f, 0.1875f, 0.3125f, 0.8125f, 0.8125f);
                break;
            case EAST:
                setBlockBounds(0.6875f, 0.1875f, 0.1875f, 1.0f, 0.8125f, 0.8125f);
                break;
            default:
                setBlockBounds(0.1875f, 0.1875f, 0.0f, 0.8125f, 0.8125f, 0.3125f);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

}
