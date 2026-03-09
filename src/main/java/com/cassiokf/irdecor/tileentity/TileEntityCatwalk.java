package com.cassiokf.irdecor.tileentity;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Tile entity for catwalks that stores a blacklist of directions where railings are forced.
 */
public class TileEntityCatwalk extends TileEntitySyncBase {

    private final Set<ForgeDirection> blacklistedFacings = EnumSet.noneOf(ForgeDirection.class);

    public void toggleFacing(ForgeDirection facing) {
        if (blacklistedFacings.contains(facing)) {
            blacklistedFacings.remove(facing);
            syncToClient();
        } else {
            blacklistedFacings.add(facing);
            syncToClient();
        }
    }

    public boolean isFacingBlacklisted(ForgeDirection facing) {
        return blacklistedFacings.contains(facing);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        int[] indices = new int[blacklistedFacings.size()];
        int i = 0;
        for (ForgeDirection dir : blacklistedFacings) {
            indices[i++] = dir.ordinal();
        }
        compound.setIntArray("BlacklistedFacings", indices);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        blacklistedFacings.clear();
        int[] indices = compound.getIntArray("BlacklistedFacings");
        for (int index : indices) {
            ForgeDirection dir = ForgeDirection.getOrientation(index);
            if (dir != ForgeDirection.UNKNOWN) {
                blacklistedFacings.add(dir);
            }
        }
    }
}
