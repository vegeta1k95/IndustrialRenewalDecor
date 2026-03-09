package com.cassiokf.irdecor.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
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

    /**
     * Check if a connection to the brace at (x,y,z) is allowed from the given side.
     * A brace placed by clicking a block's east face blocks connections from its east side, etc.
     */
    public static boolean allowsConnectionFrom(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int meta = world.getBlockMetadata(x, y, z);
        EnumBraceOrientation orientation = EnumBraceOrientation.byMetadata(meta);
        switch (orientation) {
            case EAST:
                // Placed from side (click WEST), target to east
                return side == ForgeDirection.UP || side == ForgeDirection.EAST;
            case WEST:
                return side == ForgeDirection.UP || side == ForgeDirection.WEST;
            case NORTH:
                return side == ForgeDirection.UP || side == ForgeDirection.NORTH;
            case SOUTH:
                return side == ForgeDirection.UP || side == ForgeDirection.SOUTH;
            case DOWN_EAST:
                if ((meta & 0x8) != 0) return side == ForgeDirection.UP; // from bottom, target above
                return side == ForgeDirection.WEST || side == ForgeDirection.DOWN; // from top; player faced east, so
                                                                                   // player side = west
            case DOWN_WEST:
                if ((meta & 0x8) != 0) return side == ForgeDirection.UP;
                return side == ForgeDirection.EAST || side == ForgeDirection.DOWN;
            case DOWN_NORTH:
                if ((meta & 0x8) != 0) return side == ForgeDirection.UP;
                return side == ForgeDirection.SOUTH || side == ForgeDirection.DOWN;
            case DOWN_SOUTH:
                if ((meta & 0x8) != 0) return side == ForgeDirection.UP;
                return side == ForgeDirection.NORTH || side == ForgeDirection.DOWN;
            default:
                return false;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int tempMeta = world.getBlockMetadata(x, y, z);
        ForgeDirection clickedSide = ForgeDirection.getOrientation(tempMeta);
        ForgeDirection entityFacing = IRDirectionHelper.getHorizontalFacing(placer);
        EnumBraceOrientation orientation = EnumBraceOrientation.forFacings(clickedSide, entityFacing);
        int finalMeta = orientation.getMeta();
        // For DOWN_ variants, store bit 3 to distinguish UP vs DOWN click
        if (clickedSide == ForgeDirection.DOWN) {
            finalMeta |= 0x8;
        }
        world.setBlockMetadataWithNotify(x, y, z, finalMeta, 2);
    }

}
