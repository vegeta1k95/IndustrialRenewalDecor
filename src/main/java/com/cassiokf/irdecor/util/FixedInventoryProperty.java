package com.cassiokf.irdecor.util;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;

/**
 * A property that only exists for inventory rendering, returning a fixed string value.
 * Use this to provide default blockstate values (like facing) for item display
 * when the real property type doesn't support SupportsStacks.
 */
public class FixedInventoryProperty implements BlockProperty<String> {

    private final String name;
    private final String value;

    public FixedInventoryProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return String.class;
    }

    @Override
    public boolean hasTrait(BlockPropertyTrait trait) {
        return trait == BlockPropertyTrait.SupportsStacks;
    }

    @Override
    public String getValue(ItemStack stack) {
        return value;
    }

    @Override
    public String parse(String text) {
        return text;
    }

    @Override
    public String stringify(String val) {
        return val;
    }
}
