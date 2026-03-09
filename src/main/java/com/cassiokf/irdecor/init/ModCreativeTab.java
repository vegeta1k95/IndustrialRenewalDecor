package com.cassiokf.irdecor.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.cassiokf.irdecor.IRDecor;

public class ModCreativeTab extends CreativeTabs {

    public static final ModCreativeTab INSTANCE = new ModCreativeTab();

    private ModCreativeTab() {
        super(IRDecor.MODID);
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(ModBlocks.HANDRAIL_IRON);
    }
}
