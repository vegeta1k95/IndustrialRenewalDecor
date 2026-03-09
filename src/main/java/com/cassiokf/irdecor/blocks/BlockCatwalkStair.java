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
 * Catwalk stair block with facing + computed side railings (active_left, active_right).
 * Side railings show when adjacent block is NOT a stair with the same facing.
 */
public class BlockCatwalkStair extends BlockIRHorizontalFacing {

    public BlockCatwalkStair() {
        super();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Stairs ascend away from the player (high part in the direction they're looking)
        ForgeDirection facing = IRDirectionHelper.getHorizontalFacing(placer);
        int meta = world.getBlockMetadata(x, y, z);
        meta = (meta & ~0x3) | IRDirectionHelper.directionToMeta(facing);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (side == 1) {
            ItemStack held = player.getCurrentEquippedItem();
            if (held == null) return false;
            Block heldBlock = Block.getBlockFromItem(held.getItem());

            // Clicking top with catwalk → place catwalk forward+up in stair's facing direction
            if (heldBlock instanceof BlockCatwalk) {
                int meta = world.getBlockMetadata(x, y, z);
                ForgeDirection face = getFacing(meta);
                int nx = x + face.offsetX;
                int ny = y + 1;
                int nz = z + face.offsetZ;
                if (world.getBlock(nx, ny, nz).isReplaceable(world, nx, ny, nz)) {
                    if (!world.isRemote) {
                        world.setBlock(nx, ny, nz, heldBlock, 0, 3);
                        world.playSoundEffect(
                            nx + 0.5, ny + 0.5, nz + 0.5,
                            heldBlock.stepSound.func_150496_b(),
                            (heldBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                            heldBlock.stepSound.getPitch() * 0.8F);
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

            // Clicking top with stair → continue the stairway (one forward, one up)
            if (heldBlock instanceof BlockCatwalkStair) {
                int meta = world.getBlockMetadata(x, y, z);
                ForgeDirection face = getFacing(meta);
                int nx = x + face.offsetX;
                int ny = y + 1;
                int nz = z + face.offsetZ;
                if (world.getBlock(nx, ny, nz).isReplaceable(world, nx, ny, nz)) {
                    if (!world.isRemote) {
                        Block stairBlock = Block.getBlockFromItem(held.getItem());
                        int newMeta = IRDirectionHelper.directionToMeta(face);
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
        }

        // Sneak + empty hand on side face → toggle railing
        if (player.isSneaking() && player.getCurrentEquippedItem() == null && side >= 2 && side <= 5) {
            if (!world.isRemote) {
                int meta = world.getBlockMetadata(x, y, z);
                ForgeDirection face = getFacing(meta);
                ForgeDirection clickedDir = ForgeDirection.getOrientation(side);
                ForgeDirection left = rotateYCCW(face);
                ForgeDirection right = rotateYCW(face);
                if (clickedDir == left) {
                    meta ^= 0x4;
                } else if (clickedDir == right) {
                    meta ^= 0x8;
                }
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.playSoundEffect(
                    x + 0.5, y + 0.5, z + 0.5,
                    "random.door_open", 1.0f,
                    world.rand.nextFloat() * 0.2f + 0.9f);
            }
            return true;
        }

        return false;
    }

    /**
     * Check if the left side should show a railing.
     * Bit 0x4 in meta forces it off. Otherwise computed from neighbors.
     */
    public static boolean hasLeftRailing(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 0x4) != 0) return false;
        ForgeDirection face = getFacing(meta);
        ForgeDirection left = rotateYCCW(face);
        Block neighbor = world.getBlock(x + left.offsetX, y + left.offsetY, z + left.offsetZ);
        if (neighbor instanceof BlockCatwalkStair) {
            int neighborMeta = world.getBlockMetadata(x + left.offsetX, y + left.offsetY, z + left.offsetZ);
            ForgeDirection neighborFace = getFacing(neighborMeta);
            return neighborFace != face;
        }
        return true;
    }

    /**
     * Check if the right side should show a railing.
     * Bit 0x8 in meta forces it off. Otherwise computed from neighbors.
     */
    public static boolean hasRightRailing(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 0x8) != 0) return false;
        ForgeDirection face = getFacing(meta);
        ForgeDirection right = rotateYCW(face);
        Block neighbor = world.getBlock(x + right.offsetX, y + right.offsetY, z + right.offsetZ);
        if (neighbor instanceof BlockCatwalkStair) {
            int neighborMeta = world.getBlockMetadata(x + right.offsetX, y + right.offsetY, z + right.offsetZ);
            ForgeDirection neighborFace = getFacing(neighborMeta);
            return neighborFace != face;
        }
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection face = getFacing(meta);

        // Lower half step (always)
        AxisAlignedBB base = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 0.5, z + 1.0);
        if (mask.intersectsWith(base)) list.add(base);

        // Upper half step in facing direction
        AxisAlignedBB upper;
        switch (face) {
            case NORTH:
                upper = AxisAlignedBB.getBoundingBox(x, y + 0.5, z, x + 1.0, y + 1.0, z + 0.5);
                break;
            case SOUTH:
                upper = AxisAlignedBB.getBoundingBox(x, y + 0.5, z + 0.5, x + 1.0, y + 1.0, z + 1.0);
                break;
            case WEST:
                upper = AxisAlignedBB.getBoundingBox(x, y + 0.5, z, x + 0.5, y + 1.0, z + 1.0);
                break;
            case EAST:
                upper = AxisAlignedBB.getBoundingBox(x + 0.5, y + 0.5, z, x + 1.0, y + 1.0, z + 1.0);
                break;
            default:
                return;
        }
        if (mask.intersectsWith(upper)) list.add(upper);

        // Side railings
        boolean left = hasLeftRailing(world, x, y, z);
        boolean right = hasRightRailing(world, x, y, z);
        addSideRailings(x, y, z, face, left, right, mask, list);
    }

    @SuppressWarnings("unchecked")
    private void addSideRailings(int x, int y, int z, ForgeDirection face, boolean left, boolean right,
        AxisAlignedBB mask, @SuppressWarnings("rawtypes") List list) {
        AxisAlignedBB leftBox = null;
        AxisAlignedBB rightBox = null;
        switch (face) {
            case NORTH:
                if (left) leftBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.03125, y + 2.0, z + 1.0);
                if (right) rightBox = AxisAlignedBB.getBoundingBox(x + 0.96875, y, z, x + 1.0, y + 2.0, z + 1.0);
                break;
            case SOUTH:
                if (left) leftBox = AxisAlignedBB.getBoundingBox(x + 0.96875, y, z, x + 1.0, y + 2.0, z + 1.0);
                if (right) rightBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.03125, y + 2.0, z + 1.0);
                break;
            case WEST:
                if (left) leftBox = AxisAlignedBB.getBoundingBox(x, y, z + 0.96875, x + 1.0, y + 2.0, z + 1.0);
                if (right) rightBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 2.0, z + 0.03125);
                break;
            case EAST:
                if (left) leftBox = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0, y + 2.0, z + 0.03125);
                if (right) rightBox = AxisAlignedBB.getBoundingBox(x, y, z + 0.96875, x + 1.0, y + 2.0, z + 1.0);
                break;
            default:
                break;
        }
        if (leftBox != null && mask.intersectsWith(leftBox)) list.add(leftBox);
        if (rightBox != null && mask.intersectsWith(rightBox)) list.add(rightBox);
    }

    private static ForgeDirection rotateYCW(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.NORTH;
            default:
                return dir;
        }
    }

    private static ForgeDirection rotateYCCW(ForgeDirection dir) {
        switch (dir) {
            case NORTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.NORTH;
            default:
                return dir;
        }
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }
}
