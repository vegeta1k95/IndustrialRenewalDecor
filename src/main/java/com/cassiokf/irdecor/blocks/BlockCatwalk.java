package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.tileentity.TileEntityCatwalk;
import com.cassiokf.irdecor.util.IRDirectionHelper;

/**
 * Catwalk block with 6-way connection booleans and TileEntity for railing blacklist.
 * Connected sides have NO railing (open passage). Disconnected sides show railings.
 * Sneak + right-click with empty hand toggles railing on the clicked side.
 */
public class BlockCatwalk extends BlockIRConnectable {

    public BlockCatwalk() {
        super();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityCatwalk();
    }

    /**
     * Catwalk connection logic:
     * Connected = no railing (neighbor is a valid walkway continuation).
     * The property value represents whether to show the railing,
     * so we return !isValidConnection() (true = show railing = NOT connected).
     * But the TileEntity blacklist can force railing to show.
     */
    @Override
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        Block neighbor = getNeighborBlock(world, x, y, z, direction);

        if (direction == ForgeDirection.DOWN) {
            // Show bottom supports when not over a catwalk/ladder/solid block
            return !(neighbor instanceof BlockCatwalk || neighbor instanceof BlockCatwalkLadder
                || neighbor.isNormalCube());
        }
        if (direction == ForgeDirection.UP) {
            return false;
        }

        // Horizontal: show railing if there's no valid walkway neighbor
        boolean showRailing = !isHorizontalConnection(world, x, y, z, direction, neighbor);

        // TileEntity blacklist inverts the computed result (toggle)
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCatwalk catwalkTE) {
            if (catwalkTE.isFacingBlacklisted(direction)) {
                return !showRailing;
            }
        }

        return showRailing;
    }

    private boolean isHorizontalConnection(IBlockAccess world, int x, int y, int z, ForgeDirection dir,
        Block neighbor) {
        if (neighbor instanceof BlockCatwalk || neighbor instanceof BlockCatwalkGate
            || neighbor instanceof BlockCatwalkHatch
            || neighbor instanceof BlockCatwalkStair
            || neighbor instanceof BlockCatwalkLadder) {
            return true;
        }
        // Check one block below in that direction for a stair ascending toward this catwalk
        int bx = x + dir.offsetX;
        int by = y - 1;
        int bz = z + dir.offsetZ;
        Block below = world.getBlock(bx, by, bz);
        if (below instanceof BlockCatwalkStair) {
            int stairMeta = world.getBlockMetadata(bx, by, bz);
            ForgeDirection stairFacing = BlockIRHorizontalFacing.getFacing(stairMeta);
            return stairFacing == dir.getOpposite();
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        ItemStack held = player.getCurrentEquippedItem();

        // Clicking top with catwalk → place in looking direction
        if (held != null && Block.getBlockFromItem(held.getItem()) instanceof BlockCatwalk && side == 1) {
            ForgeDirection placeDir = IRDirectionHelper.getHorizontalFacing(player);
            int nx = x + placeDir.offsetX;
            int ny = y;
            int nz = z + placeDir.offsetZ;
            if (world.getBlock(nx, ny, nz).isReplaceable(world, nx, ny, nz)) {
                if (!world.isRemote) {
                    Block catwalkBlock = Block.getBlockFromItem(held.getItem());
                    world.setBlock(nx, ny, nz, catwalkBlock, 0, 3);
                    world.playSoundEffect(
                        nx + 0.5, ny + 0.5, nz + 0.5,
                        catwalkBlock.stepSound.func_150496_b(),
                        (catwalkBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                        catwalkBlock.stepSound.getPitch() * 0.8F);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                }
                player.swingItem();
                return true;
            }
        }

        // Place stair when clicking top or side with stair in hand
        if (held != null && Block.getBlockFromItem(held.getItem()) instanceof BlockCatwalkStair
            && side >= 1 && side <= 5) {
            ForgeDirection placeDir;
            if (side == 1) {
                // Top face: place on opposite side of player's look direction
                placeDir = IRDirectionHelper.getHorizontalFacing(player);
            } else {
                // Side face: place on opposite side of clicked face
                placeDir = ForgeDirection.getOrientation(side).getOpposite();
            }
            int nx = x + placeDir.offsetX;
            int ny = y;
            int nz = z + placeDir.offsetZ;
            if (world.getBlock(nx, ny, nz).isReplaceable(world, nx, ny, nz)) {
                if (!world.isRemote) {
                    Block stairBlock = Block.getBlockFromItem(held.getItem());
                    int newMeta = IRDirectionHelper.directionToMeta(placeDir);
                    world.setBlock(nx, ny, nz, stairBlock, newMeta, 3);
                    world.playSoundEffect(
                        nx + 0.5, ny + 0.5, nz + 0.5,
                        stairBlock.stepSound.func_150496_b(),
                        (stairBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                        stairBlock.stepSound.getPitch() * 0.8F);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                }
                player.swingItem();
                return true;
            }
        }

        // Sneak + empty hand to toggle railing
        if (player.isSneaking() && player.getCurrentEquippedItem() == null) {
            if (!world.isRemote) {
                ForgeDirection clickedSide = ForgeDirection.getOrientation(side);
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityCatwalk catwalkTE) {
                    catwalkTE.toggleFacing(clickedSide);
                    world.markBlockForUpdate(x, y, z);
                    world.playSoundEffect(
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        "random.door_open",
                        1.0f,
                        world.rand.nextFloat() * 0.2f + 0.9f);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        // Full block height for easy selection; actual collision is in addCollisionBoxesToList
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        // Base floor
        AxisAlignedBB base = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 0.03125, z + 1.0);
        if (mask.intersectsWith(base)) list.add(base);

        // Railings on disconnected sides (canConnectTo returns true = show railing)
        if (canConnectTo(world, x, y, z, ForgeDirection.NORTH)) {
            AxisAlignedBB railing = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 1.0, z + 0.03125);
            if (mask.intersectsWith(railing)) list.add(railing);
        }
        if (canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) {
            AxisAlignedBB railing = AxisAlignedBB.getBoundingBox(x, y, z + 0.96875, x + 1.0, y + 1.0, z + 1.0);
            if (mask.intersectsWith(railing)) list.add(railing);
        }
        if (canConnectTo(world, x, y, z, ForgeDirection.WEST)) {
            AxisAlignedBB railing = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.03125, y + 1.0, z + 1.0);
            if (mask.intersectsWith(railing)) list.add(railing);
        }
        if (canConnectTo(world, x, y, z, ForgeDirection.EAST)) {
            AxisAlignedBB railing = AxisAlignedBB.getBoundingBox(x + 0.96875, y, z, x + 1.0, y + 1.0, z + 1.0);
            if (mask.intersectsWith(railing)) list.add(railing);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // No facing needed
    }
}
