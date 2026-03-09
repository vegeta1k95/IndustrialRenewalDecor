package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Base block for blocks that store horizontal facing in metadata bits 0-1.
 * SOUTH=0, WEST=1, NORTH=2, EAST=3
 */
public class BlockIRHorizontalFacing extends BlockIRBase {

    public BlockIRHorizontalFacing() {
        super();
        setHardness(0.8f);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacingOpposite(placer);
        int meta = world.getBlockMetadata(x, y, z);
        meta = (meta & ~0x3) | IRDirectionHelper.directionToMeta(facing);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    public static ForgeDirection getFacing(int meta) {
        return IRDirectionHelper.metaToDirection(meta & 0x3);
    }
}
