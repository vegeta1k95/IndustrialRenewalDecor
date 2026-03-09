package com.cassiokf.irdecor.blocks;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import com.cassiokf.irdecor.init.ModCreativeTab;

public class BlockIRBase extends Block {

    public BlockIRBase() {
        this(Material.iron);
    }

    public BlockIRBase(Material material) {
        super(material);
        setCreativeTab(ModCreativeTab.INSTANCE);
        setStepSound(Block.soundTypeMetal);
        setHardness(5.0f);
        setResistance(12.0f);
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        // Re-render when neighbors change so computed properties (connections, down, frame) update visually
        world.markBlockForUpdate(x, y, z);
    }
}
