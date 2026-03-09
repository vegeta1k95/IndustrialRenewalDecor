package com.cassiokf.irdecor.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;

/**
 * A boolean block property computed from the world state (not stored in metadata).
 * Supports an inventory default so multipart conditions can match for item rendering.
 */
public class ComputedBooleanProperty implements BooleanBlockProperty {

    private final String name;
    private final WorldBooleanComputer computer;
    private final boolean inventoryDefault;

    @FunctionalInterface
    public interface WorldBooleanComputer {

        boolean compute(IBlockAccess world, int x, int y, int z);
    }

    public ComputedBooleanProperty(String name, WorldBooleanComputer computer) {
        this(name, computer, false);
    }

    public ComputedBooleanProperty(String name, WorldBooleanComputer computer, boolean inventoryDefault) {
        this.name = name;
        this.computer = computer;
        this.inventoryDefault = inventoryDefault;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public java.lang.reflect.Type getType() {
        return Boolean.class;
    }

    @Override
    public boolean hasTrait(BlockPropertyTrait trait) {
        return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.SupportsStacks;
    }

    @Override
    public boolean appliesTo(IBlockAccess world, int x, int y, int z, Block block, int meta, TileEntity tile) {
        return true;
    }

    @Override
    public Boolean getValue(IBlockAccess world, int x, int y, int z) {
        return computer.compute(world, x, y, z);
    }

    @Override
    public Boolean getValue(ItemStack stack) {
        return inventoryDefault;
    }

    @Override
    public Boolean parse(String text) {
        return Boolean.parseBoolean(text);
    }

    @Override
    public String stringify(Boolean value) {
        return value.toString();
    }
}
