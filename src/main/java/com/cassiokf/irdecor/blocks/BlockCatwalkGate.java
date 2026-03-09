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

public class BlockCatwalkGate extends BlockIRHorizontalFacing {

    public BlockCatwalkGate() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Gate faces the player's look direction (appears on the far side of the block)
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = world.getBlockMetadata(x, y, z);
        meta = (meta & ~0x3) | IRDirectionHelper.directionToMeta(facing);
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
        ForgeDirection face = getFacing(meta);
        switch (face) {
            case NORTH:
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
                break;
            case SOUTH:
                setBlockBounds(0.0f, 0.0f, 0.9375f, 1.0f, 1.0f, 1.0f);
                break;
            case WEST:
                setBlockBounds(0.0f, 0.0f, 0.0f, 0.0625f, 1.0f, 1.0f);
                break;
            case EAST:
                setBlockBounds(0.9375f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                break;
            default:
                setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0625f);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean active = isActive(meta);
        ForgeDirection face = getFacing(meta);

        if (!active) {
            // Closed: full wall collision (1.5 high)
            AxisAlignedBB box;
            switch (face) {
                case NORTH:
                    box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.03125);
                    break;
                case SOUTH:
                    box = AxisAlignedBB.getBoundingBox(x, y, z + 0.96875, x + 1.0, y + 1.0, z + 1.0);
                    break;
                case WEST:
                    box = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.03125, y + 1.0, z + 1.0);
                    break;
                case EAST:
                    box = AxisAlignedBB.getBoundingBox(x + 0.96875, y, z, x + 1.0, y + 1.0, z + 1.0);
                    break;
                default:
                    return;
            }
            if (mask.intersectsWith(box)) {
                list.add(box);
            }
        } else {
            // Open: only corner posts
            AxisAlignedBB post1;
            AxisAlignedBB post2;
            switch (face) {
                case NORTH:
                    post1 = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.125, y + 1.0, z + 0.125);
                    post2 = AxisAlignedBB.getBoundingBox(x + 0.875, y, z, x + 1.0, y + 1.0, z + 0.125);
                    break;
                case SOUTH:
                    post1 = AxisAlignedBB.getBoundingBox(x, y, z + 0.875, x + 0.125, y + 1.0, z + 1.0);
                    post2 = AxisAlignedBB.getBoundingBox(x + 0.875, y, z + 0.875, x + 1.0, y + 1.0, z + 1.0);
                    break;
                case WEST:
                    post1 = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.125, y + 1.0, z + 0.125);
                    post2 = AxisAlignedBB.getBoundingBox(x, y, z + 0.875, x + 0.125, y + 1.0, z + 1.0);
                    break;
                case EAST:
                    post1 = AxisAlignedBB.getBoundingBox(x + 0.875, y, z, x + 1.0, y + 1.0, z + 0.125);
                    post2 = AxisAlignedBB.getBoundingBox(x + 0.875, y, z + 0.875, x + 1.0, y + 1.0, z + 1.0);
                    break;
                default:
                    return;
            }
            if (mask.intersectsWith(post1)) {
                list.add(post1);
            }
            if (mask.intersectsWith(post2)) {
                list.add(post2);
            }
        }
    }
}
