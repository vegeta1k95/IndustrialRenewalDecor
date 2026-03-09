package com.cassiokf.irdecor.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Vertical support pillar with 6-way connections.
 * Connects to full cubes, other pillars, columns, braces, catwalks, etc.
 * Click with another pillar to stack on top.
 */
public class BlockPillar extends BlockIRConnectable {

    @Override
    public boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        Block neighbor = getNeighborBlock(world, x, y, z, direction);

        // UP/DOWN: only connect to solid blocks, catwalks, platforms — NOT to other pillars/columns
        // (stacked pillars should show plain intermediate posts, not junction pieces)
        if (direction == ForgeDirection.DOWN) {
            return neighbor.isNormalCube() || neighbor instanceof BlockPlatform
                || neighbor instanceof BlockColumn;
        }
        if (direction == ForgeDirection.UP) {
            return neighbor.isNormalCube() || neighbor instanceof BlockCatwalk
                || neighbor instanceof BlockPlatform || neighbor instanceof BlockColumn;
        }
        // Horizontal connections
        return neighbor instanceof BlockColumn || neighbor instanceof BlockBrace || neighbor instanceof BlockHandRail;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return false;

        Item heldItem = held.getItem();
        Block thisBlock = world.getBlock(x, y, z);

        // Click with same type of pillar to stack
        if (Item.getItemFromBlock(thisBlock) == heldItem) {
            int n = 1;
            while (world.getBlock(x, y + n, z) instanceof BlockPillar) {
                n++;
            }
            if (world.getBlock(x, y + n, z)
                .isReplaceable(world, x, y + n, z)) {
                if (!world.isRemote) {
                    world.setBlock(x, y + n, z, thisBlock, 0, 3);
                    world.playSoundEffect(x + 0.5, y + n + 0.5, z + 0.5, "step.anvil", 0.8f, 1.0f);
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float x1 = 0.25f, y1 = 0.0f, z1 = 0.25f;
        float x2 = 0.75f, y2 = 1.0f, z2 = 0.75f;
        if (canConnectTo(world, x, y, z, ForgeDirection.NORTH)) z1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) z2 = 1.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.WEST)) x1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.EAST)) x2 = 1.0f;
        setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask,
        @SuppressWarnings("rawtypes") List list, Entity entity) {
        float x1 = 0.25f, z1 = 0.25f;
        float x2 = 0.75f, z2 = 0.75f;
        if (canConnectTo(world, x, y, z, ForgeDirection.NORTH)) z1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.SOUTH)) z2 = 1.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.WEST)) x1 = 0.0f;
        if (canConnectTo(world, x, y, z, ForgeDirection.EAST)) x2 = 1.0f;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x + x1, y, z + z1, x + x2, y + 1.0, z + z2);
        if (mask.intersectsWith(box)) {
            list.add(box);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // No facing needed for pillar
    }
}
