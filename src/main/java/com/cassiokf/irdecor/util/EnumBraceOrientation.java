package com.cassiokf.irdecor.util;

import net.minecraftforge.common.util.ForgeDirection;

public enum EnumBraceOrientation {

    DOWN_EAST(0, "down_east"),
    EAST(1, "east"),
    WEST(2, "west"),
    SOUTH(3, "south"),
    NORTH(4, "north"),
    DOWN_WEST(5, "down_west"),
    DOWN_SOUTH(6, "down_south"),
    DOWN_NORTH(7, "down_north");

    private static final EnumBraceOrientation[] META_LOOKUP = new EnumBraceOrientation[values().length];
    private static final String[] NAMES = new String[values().length];

    static {
        for (EnumBraceOrientation orientation : values()) {
            META_LOOKUP[orientation.meta] = orientation;
            NAMES[orientation.meta] = orientation.name;
        }
    }

    private final int meta;
    private final String name;

    EnumBraceOrientation(int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    public static EnumBraceOrientation byMetadata(int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
        }
        return META_LOOKUP[meta];
    }

    public static EnumBraceOrientation forFacings(ForgeDirection clickedSide, ForgeDirection entityFacing) {
        switch (clickedSide) {
            case DOWN:
            case UP:
                switch (entityFacing) {
                    case EAST:
                        return DOWN_EAST;
                    case NORTH:
                        return DOWN_NORTH;
                    case SOUTH:
                        return DOWN_SOUTH;
                    case WEST:
                        return DOWN_WEST;
                    default:
                        return DOWN_NORTH;
                }
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            case EAST:
                return WEST;
            default:
                return NORTH;
        }
    }

    public static String[] getNames() {
        return NAMES;
    }

    public int getMeta() {
        return meta;
    }

    public String getSerializedName() {
        return name;
    }
}
