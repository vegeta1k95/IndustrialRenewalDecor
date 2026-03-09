package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

public class BlockCatwalkHatch extends BlockIRHorizontalFacing {

    public BlockCatwalkHatch() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Hatch faces same direction as player (not opposite)
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = IRDirectionHelper.directionToMeta(facing);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            meta ^= 0x4; // toggle active bit
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            boolean opened = (meta & 0x4) != 0;
            world.playSoundEffect(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                opened ? "irdecor:gate_opening" : "irdecor:gate_closing",
                1.0f,
                world.rand.nextFloat() * 0.2f + 0.9f);
        }
        return true;
    }

    public static boolean isActive(int meta) {
        return (meta & 0x4) != 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (isActive(meta)) {
            // Open: thin panel on the opposite side from facing
            ForgeDirection face = getFacing(meta);
            switch (face) {
                case NORTH:
                    setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.125f);
                    break;
                case SOUTH:
                    setBlockBounds(0.0f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f);
                    break;
                case WEST:
                    setBlockBounds(0.0f, 0.0f, 0.0f, 0.125f, 1.0f, 1.0f);
                    break;
                case EAST:
                    setBlockBounds(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                default:
                    setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
                    break;
            }
        } else {
            // Closed: flat platform
            setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        int meta = world.getBlockMetadata(x, y, z);
        if (isActive(meta)) {
            // Open: thin collision on the opposite side from facing (where the panel swings to)
            ForgeDirection face = getFacing(meta);
            AxisAlignedBB box;
            switch (face) {
                case NORTH:
                    box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.0625);
                    break;
                case SOUTH:
                    box = AxisAlignedBB.getBoundingBox(x, y, z + 0.9375, x + 1.0, y + 1.0, z + 1.0);
                    break;
                case WEST:
                    box = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.0625, y + 1.0, z + 1.0);
                    break;
                case EAST:
                    box = AxisAlignedBB.getBoundingBox(x + 0.9375, y, z, x + 1.0, y + 1.0, z + 1.0);
                    break;
                default:
                    return;
            }
            if (mask.intersectsWith(box)) {
                list.add(box);
            }
        } else {
            // Closed: solid platform
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 0.25, z + 1.0);
            if (mask.intersectsWith(box)) {
                list.add(box);
            }
        }
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return isActive(world.getBlockMetadata(x, y, z));
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }
}
