package com.cassiokf.irdecor.blocks;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Base for blocks with 6-direction boolean connection properties computed from neighbors.
 * Subclasses override canConnectTo() to define their specific connection logic.
 */
public abstract class BlockIRConnectable extends BlockIRBase {

    public BlockIRConnectable() {
        super();
        setHardness(0.8f);
    }

    /**
     * Check if this block should connect to the neighbor in the given direction.
     * Called by computed boolean properties during rendering.
     */
    public abstract boolean canConnectTo(IBlockAccess world, int x, int y, int z, ForgeDirection direction);

    /**
     * Helper to check the neighbor block in a direction.
     */
    protected Block getNeighborBlock(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    }

    protected int getNeighborMeta(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        return world.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    }
}
