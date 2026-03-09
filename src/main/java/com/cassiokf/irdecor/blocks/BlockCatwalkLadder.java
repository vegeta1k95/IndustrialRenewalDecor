package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Catwalk ladder with facing + active (cage) toggle + computed down.
 * Always climbable. When active (caged), has side walls on 3 sides.
 * Facing direction has the ladder panel.
 */
public class BlockCatwalkLadder extends BlockIRHorizontalFacing {

    public BlockCatwalkLadder() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Ladder faces same direction as player
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = IRDirectionHelper.directionToMeta(facing);
        // Set active based on block below
        if (shouldStartCaged(world, x, y, z)) {
            meta |= 0x4;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    private boolean shouldStartCaged(IBlockAccess world, int x, int y, int z) {
        // Cage starts from 3rd ladder block up: need at least 2 ladders below
        int count = 0;
        int cy = y - 1;
        while (world.getBlock(x, cy, z) instanceof BlockCatwalkLadder) {
            count++;
            cy--;
        }
        return count >= 2;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        // Sneak + empty hand to toggle cage
        if (player.isSneaking() && player.getCurrentEquippedItem() == null) {
            if (!world.isRemote) {
                int meta = world.getBlockMetadata(x, y, z);
                meta ^= 0x4;
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
        return false;
    }

    public static boolean isActive(int meta) {
        return (meta & 0x4) != 0;
    }

    /**
     * Computed "down" property: show floor plate if block below is not a ladder/hatch/stair.
     */
    public static boolean hasDownPlate(IBlockAccess world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        return !(below instanceof BlockCatwalkLadder || below instanceof BlockCatwalkHatch
            || below instanceof BlockCatwalkStair);
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection face = getFacing(meta);
        // Thin bounding box on the ladder side
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
        ForgeDirection face = getFacing(meta);
        boolean active = isActive(meta);
        boolean down = hasDownPlate(world, x, y, z);

        // Ladder back panel
        AxisAlignedBB ladder;
        switch (face) {
            case NORTH:
                ladder = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.0625);
                break;
            case SOUTH:
                ladder = AxisAlignedBB.getBoundingBox(x, y, z + 0.9375, x + 1.0, y + 1.0, z + 1.0);
                break;
            case WEST:
                ladder = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.0625, y + 1.0, z + 1.0);
                break;
            case EAST:
                ladder = AxisAlignedBB.getBoundingBox(x + 0.9375, y, z, x + 1.0, y + 1.0, z + 1.0);
                break;
            default:
                return;
        }
        if (mask.intersectsWith(ladder)) list.add(ladder);

        // Cage sides when active
        if (active) {
            addCageSides(x, y, z, face, mask, list);
        }

        // Floor plate when down
        if (down) {
            AxisAlignedBB floor = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 0.03125, z + 1.0);
            if (mask.intersectsWith(floor)) list.add(floor);
        }
    }

    @SuppressWarnings("unchecked")
    private void addCageSides(int x, int y, int z, ForgeDirection face, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list) {
        // Add walls on all sides except the face direction (where you enter)
        AxisAlignedBB north = AxisAlignedBB.getBoundingBox(x, y + 0.4, z, x + 1.0, y + 1.0, z + 0.03125);
        AxisAlignedBB south = AxisAlignedBB.getBoundingBox(x, y + 0.4, z + 0.96875, x + 1.0, y + 1.0, z + 1.0);
        AxisAlignedBB west = AxisAlignedBB.getBoundingBox(x, y + 0.4, z, x + 0.03125, y + 1.0, z + 1.0);
        AxisAlignedBB east = AxisAlignedBB.getBoundingBox(x + 0.96875, y + 0.4, z, x + 1.0, y + 1.0, z + 1.0);

        // Skip the facing direction (the ladder/open side of the cage)
        ForgeDirection entry = face;
        if (entry != ForgeDirection.NORTH && mask.intersectsWith(north)) list.add(north);
        if (entry != ForgeDirection.SOUTH && mask.intersectsWith(south)) list.add(south);
        if (entry != ForgeDirection.WEST && mask.intersectsWith(west)) list.add(west);
        if (entry != ForgeDirection.EAST && mask.intersectsWith(east)) list.add(east);
    }
}
