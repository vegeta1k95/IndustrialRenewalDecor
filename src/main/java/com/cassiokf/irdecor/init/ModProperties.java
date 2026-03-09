package com.cassiokf.irdecor.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

import com.cassiokf.irdecor.blocks.BlockBrace;
import com.cassiokf.irdecor.blocks.BlockCatwalkGate;
import com.cassiokf.irdecor.blocks.BlockCatwalkHatch;
import com.cassiokf.irdecor.blocks.BlockCatwalkLadder;
import com.cassiokf.irdecor.blocks.BlockCatwalkStair;
import com.cassiokf.irdecor.blocks.BlockFireExtinguisher;
import com.cassiokf.irdecor.blocks.BlockFirstAidKit;
import com.cassiokf.irdecor.blocks.BlockHandRail;
import com.cassiokf.irdecor.blocks.BlockIRConnectable;
import com.cassiokf.irdecor.blocks.BlockRazorWire;
import com.cassiokf.irdecor.blocks.BlockSign;
import com.cassiokf.irdecor.util.ComputedBooleanProperty;
import com.cassiokf.irdecor.util.FixedInventoryProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.DirectionBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

public class ModProperties {

    private static DirectionBlockProperty createHorizontalFacing(int mask) {
        return DirectionBlockProperty.facing(mask, dir -> switch (dir) {
            case SOUTH -> 0;
            case WEST -> 1;
            case NORTH -> 2;
            case EAST -> 3;
            default -> 0;
        }, meta -> switch (meta & 0x3) {
            case 0 -> ForgeDirection.SOUTH;
            case 1 -> ForgeDirection.WEST;
            case 2 -> ForgeDirection.NORTH;
            case 3 -> ForgeDirection.EAST;
            default -> ForgeDirection.SOUTH;
        });
    }

    /**
     * Register a property on both the block class (for world rendering)
     * and on each block's Item (for inventory rendering).
     */
    private static void registerOnClassAndItems(Class<? extends Block> blockClass, BlockProperty<?> prop,
        Block... blocks) {
        BlockPropertyRegistry.registerProperty(blockClass, prop);
        for (Block block : blocks) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                BlockPropertyRegistry.registerProperty(item, prop);
            }
        }
    }

    private static void registerOnItems(BlockProperty<?> prop, Block... blocks) {
        for (Block block : blocks) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                BlockPropertyRegistry.registerProperty(item, prop);
            }
        }
    }

    /**
     * DirectionBlockProperty doesn't have SupportsStacks, so register a fixed
     * inventory-only "facing" property on items. Default meta 0 = south.
     */
    private static void registerFacingWithInventory(Class<? extends Block> blockClass, Block... blocks) {
        var facingProp = createHorizontalFacing(0x3);
        BlockPropertyRegistry.registerProperty(blockClass, facingProp);
        registerOnItems(new FixedInventoryProperty("facing", "south"), blocks);
    }

    public static void register() {
        registerHandRailProperties();
        registerBraceProperties();
        registerHatchProperties();
        registerGateProperties();
        registerRazorWireProperties();
        registerConnectableProperties();
        registerStairProperties();
        registerLadderProperties();
        registerSignProperties();
        registerFireExtinguisherProperties();
        registerFirstAidKitProperties();
    }

    private static void registerHandRailProperties() {
        Block[] blocks = { ModBlocks.HANDRAIL_IRON, ModBlocks.HANDRAIL_STEEL };
        var downProp = new ComputedBooleanProperty("down", BlockHandRail::hasDownConnection, true);

        registerFacingWithInventory(BlockHandRail.class, blocks);
        registerOnClassAndItems(BlockHandRail.class, downProp, blocks);
    }

    private static void registerBraceProperties() {
        Block[] blocks = { ModBlocks.BRACE_IRON, ModBlocks.BRACE_STEEL };
        var facingProp = IntegerBlockProperty.meta("facing", 0x7, 0);

        BlockPropertyRegistry.registerProperty(BlockBrace.class, facingProp);
        registerOnItems(new FixedInventoryProperty("facing", "0"), blocks);
    }

    private static void registerHatchProperties() {
        var activeProp = BooleanBlockProperty.flag("active", 0x4);

        registerFacingWithInventory(BlockCatwalkHatch.class, ModBlocks.CATWALK_HATCH);
        registerOnClassAndItems(BlockCatwalkHatch.class, activeProp, ModBlocks.CATWALK_HATCH);
    }

    private static void registerGateProperties() {
        var activeProp = BooleanBlockProperty.flag("active", 0x4);

        registerFacingWithInventory(BlockCatwalkGate.class, ModBlocks.CATWALK_GATE);
        registerOnClassAndItems(BlockCatwalkGate.class, activeProp, ModBlocks.CATWALK_GATE);
    }

    private static void registerRazorWireProperties() {
        var frameProp = new ComputedBooleanProperty("frame", BlockRazorWire::hasFrame, true);

        registerFacingWithInventory(BlockRazorWire.class, ModBlocks.RAZOR_WIRE);
        registerOnClassAndItems(BlockRazorWire.class, frameProp, ModBlocks.RAZOR_WIRE);
    }

    private static void registerStairProperties() {
        Block[] stairs = { ModBlocks.CATWALK_STAIR_IRON, ModBlocks.CATWALK_STAIR_STEEL };
        var activeLeftProp = new ComputedBooleanProperty("active_left", BlockCatwalkStair::hasLeftRailing, true);
        var activeRightProp = new ComputedBooleanProperty("active_right", BlockCatwalkStair::hasRightRailing, true);

        BlockPropertyRegistry.registerProperty(BlockCatwalkStair.class, createHorizontalFacing(0x3));
        registerOnItems(new FixedInventoryProperty("facing", "west"), stairs);
        registerOnClassAndItems(BlockCatwalkStair.class, activeLeftProp, stairs);
        registerOnClassAndItems(BlockCatwalkStair.class, activeRightProp, stairs);
    }

    private static void registerLadderProperties() {
        Block[] ladders = { ModBlocks.CATWALK_LADDER_IRON, ModBlocks.CATWALK_LADDER_STEEL };
        var activeProp = BooleanBlockProperty.flag("active", 0x4);
        var downProp = new ComputedBooleanProperty("down", BlockCatwalkLadder::hasDownPlate, true);

        registerFacingWithInventory(BlockCatwalkLadder.class, ladders);
        registerOnClassAndItems(BlockCatwalkLadder.class, activeProp, ladders);
        registerOnClassAndItems(BlockCatwalkLadder.class, downProp, ladders);
    }

    private static void registerSignProperties() {
        Block[] signs = { ModBlocks.SIGN_HV, ModBlocks.SIGN_RA, ModBlocks.SIGN_C };
        var onwallProp = BooleanBlockProperty.flag("onwall", 0x4);

        registerFacingWithInventory(BlockSign.class, signs);
        registerOnClassAndItems(BlockSign.class, onwallProp, signs);
    }

    private static void registerFireExtinguisherProperties() {
        var onwallProp = BooleanBlockProperty.flag("onwall", 0x4);

        registerFacingWithInventory(BlockFireExtinguisher.class, ModBlocks.FIRE_EXTINGUISHER);
        registerOnClassAndItems(BlockFireExtinguisher.class, onwallProp, ModBlocks.FIRE_EXTINGUISHER);
    }

    private static void registerFirstAidKitProperties() {
        var onwallProp = BooleanBlockProperty.flag("onwall", 0x4);

        registerFacingWithInventory(BlockFirstAidKit.class, ModBlocks.FIRSTAID_KIT);
        registerOnClassAndItems(BlockFirstAidKit.class, onwallProp, ModBlocks.FIRSTAID_KIT);
    }

    private static void registerConnectableProperties() {
        String[] dirNames = { "down", "up", "north", "south", "west", "east" };
        ForgeDirection[] dirs = { ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH,
            ForgeDirection.WEST, ForgeDirection.EAST };

        // Register class-level properties for world rendering (inventoryDefault irrelevant here)
        for (int i = 0; i < 6; i++) {
            final ForgeDirection dir = dirs[i];
            var prop = new ComputedBooleanProperty(dirNames[i], (world, x, y, z) -> {
                net.minecraft.block.Block block = world.getBlock(x, y, z);
                if (block instanceof BlockIRConnectable connectable) {
                    return connectable.canConnectTo(world, x, y, z, dir);
                }
                return false;
            });
            BlockPropertyRegistry.registerProperty(BlockIRConnectable.class, prop);
        }

        // Per-block-type inventory defaults:
        // Pillars: all false (intermediate, no connections)
        // Columns: north+south true (two opposite side connections)
        // Catwalks: north+south true (one parallel pair of railings)
        // Platform: north+south true (railings on two sides)
        boolean[][] inventoryDefaults = {
            // down, up, north, south, west, east
            { false, false, false, false, false, false }, // pillar
            { false, false, false, false, true, true }, // column
            { false, false, true, true, false, false }, // catwalk
            { false, false, false, false, true, true }, // platform
        };
        Block[][] blockGroups = { { ModBlocks.PILLAR_IRON, ModBlocks.PILLAR_STEEL },
            { ModBlocks.COLUMN_IRON, ModBlocks.COLUMN_STEEL }, { ModBlocks.CATWALK_IRON, ModBlocks.CATWALK_STEEL },
            { ModBlocks.PLATFORM }, };

        for (int g = 0; g < blockGroups.length; g++) {
            for (int i = 0; i < 6; i++) {
                final ForgeDirection dir = dirs[i];
                var itemProp = new ComputedBooleanProperty(dirNames[i], (world, x, y, z) -> {
                    net.minecraft.block.Block block = world.getBlock(x, y, z);
                    if (block instanceof BlockIRConnectable connectable) {
                        return connectable.canConnectTo(world, x, y, z, dir);
                    }
                    return false;
                }, inventoryDefaults[g][i]);
                for (Block block : blockGroups[g]) {
                    Item item = Item.getItemFromBlock(block);
                    if (item != null) {
                        BlockPropertyRegistry.registerProperty(item, itemProp);
                    }
                }
            }
        }
    }
}
