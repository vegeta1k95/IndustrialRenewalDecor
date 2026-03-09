package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Decorative fire extinguisher block with facing + onwall.
 * Ground or wall-mounted depending on placement surface.
 */
public class BlockFireExtinguisher extends BlockIRHorizontalFacing {

    public BlockFireExtinguisher() {
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
        if (isOnWall(meta)) {
            ForgeDirection face = getFacing(meta);
            switch (face) {
                case NORTH:
                    setBlockBounds(0.25f, 0.0f, 0.0f, 0.75f, 1.0f, 0.5f);
                    break;
                case SOUTH:
                    setBlockBounds(0.25f, 0.0f, 0.5f, 0.75f, 1.0f, 1.0f);
                    break;
                case WEST:
                    setBlockBounds(0.0f, 0.0f, 0.25f, 0.5f, 1.0f, 0.75f);
                    break;
                case EAST:
                    setBlockBounds(0.5f, 0.0f, 0.25f, 1.0f, 1.0f, 0.75f);
                    break;
                default:
                    setBlockBounds(0.25f, 0.0f, 0.0f, 0.75f, 1.0f, 0.5f);
                    break;
            }
        } else {
            setBlockBounds(0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
