package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.EnumBraceOrientation;
import com.cassiokf.irdecor.util.IRDirectionHelper;

public class BlockBrace extends BlockIRBase {

    public BlockBrace() {
        super();
        setHardness(3.0f);
        setResistance(5.0f);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        // Store clicked side temporarily in metadata; onBlockPlacedBy will compute final orientation
        return side;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int tempMeta = world.getBlockMetadata(x, y, z);
        ForgeDirection clickedSide = ForgeDirection.getOrientation(tempMeta);
        ForgeDirection entityFacing = IRDirectionHelper.getHorizontalFacing(placer);
        EnumBraceOrientation orientation = EnumBraceOrientation.forFacings(clickedSide, entityFacing);
        world.setBlockMetadataWithNotify(x, y, z, orientation.getMeta(), 2);
    }

}
