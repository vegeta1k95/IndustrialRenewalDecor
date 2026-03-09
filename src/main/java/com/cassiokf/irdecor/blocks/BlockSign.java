package com.cassiokf.irdecor.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.init.ModBlocks;
import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Sign block with facing + onwall (ground vs wall-mounted).
 * Right-click cycles through sign variants (HV → RA → C).
 */
public class BlockSign extends BlockIRHorizontalFacing {

    public BlockSign() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = IRDirectionHelper.directionToMeta(facing);
        // onwall bit is already set by onBlockPlaced
        meta |= (world.getBlockMetadata(x, y, z) & 0x4);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        // side == 1 (top) = ground, side 2-5 (horizontal) = wall
        if (side >= 2 && side <= 5) {
            return 0x4; // onwall bit, facing set later in onBlockPlacedBy
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            Block next = getNextSign(world.getBlock(x, y, z));
            if (next != null) {
                int meta = world.getBlockMetadata(x, y, z);
                world.setBlock(x, y, z, next, meta, 3);
            }
        }
        return true;
    }

    private Block getNextSign(Block current) {
        Block[] signs = ModBlocks.getSigns();
        for (int i = 0; i < signs.length; i++) {
            if (signs[i] == current) {
                return signs[(i + 1) % signs.length];
            }
        }
        return null;
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
                    setBlockBounds(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.0625f);
                    break;
                case SOUTH:
                    setBlockBounds(0.125f, 0.125f, 0.9375f, 0.875f, 0.875f, 1.0f);
                    break;
                case WEST:
                    setBlockBounds(0.0f, 0.125f, 0.125f, 0.0625f, 0.875f, 0.875f);
                    break;
                case EAST:
                    setBlockBounds(0.9375f, 0.125f, 0.125f, 1.0f, 0.875f, 0.875f);
                    break;
                default:
                    setBlockBounds(0.125f, 0.125f, 0.0f, 0.875f, 0.875f, 0.0625f);
                    break;
            }
        } else {
            setBlockBounds(0.125f, 0.0f, 0.125f, 0.875f, 1.0f, 0.875f);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null; // No collision
    }
}
