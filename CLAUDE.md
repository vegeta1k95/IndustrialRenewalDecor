# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

IndustrialRenewalDecor (`irdecor`) is a Minecraft 1.7.10 Forge mod providing industrial decorative blocks (catwalks, pillars, braces, handrails, hazard signs, etc.). Built on the GTNH (GT New Horizons) build conventions with GTNHLib for JSON-based multipart block rendering.

## Build Commands

```bash
./gradlew build                          # Build the mod JAR
./gradlew runClient                      # Run Minecraft client for testing
./gradlew runClient --username=PlayerName # Run with a specific username
./gradlew runServer                      # Run dedicated server
./gradlew spotlessApply                  # Format code (required before committing)
./gradlew spotlessCheck                  # Check formatting without fixing
```

## Build System

- **Gradle 9.2.1** with Kotlin DSL, using `com.gtnewhorizons.gtnhconvention` plugin
- `build.gradle.kts` only applies the convention plugin; all configuration lives in `gradle.properties`
- Dependencies in `dependencies.gradle` (GTNHLib 0.9.37+, NEI 2.8.4-GTNH), custom repos in `repositories.gradle`
- Java 17 syntax via Jabel while targeting JVM 8
- Auto-generated `Tags` class at `com.cassiokf.irdecor.Tags` with `VERSION` field

## Architecture

### Entry Point & Lifecycle

- **`IRDecor.java`** — `@Mod` annotated class, delegates lifecycle events to proxy
- **`CommonProxy` / `ClientProxy`** — `@SidedProxy` pattern; `ClientProxy` registers mod with GTNHLib's `ModelRegistry`
- **`Config.java`** — single setting: `razorWireDamage` (Forge `Configuration` API)
- **Mod ID**: `irdecor`, **Package**: `com.cassiokf.irdecor`

### Block Registration

- **`ModBlocks.java`** — registers ~25 block instances with `GameRegistry`, organized by phase (structural support, catwalks, vertical, stairs/ladders, hazards, misc)
- **`ModProperties.java`** — registers GTNHLib block properties for rendering (facing, connections, state flags)
- **`ModCreativeTab.java`** — single creative tab, icon is `HANDRAIL_IRON`

### Block Class Hierarchy

```
BlockIRBase (JSON_ISBRH_ID rendering, non-opaque, metal sound, hardness 5.0)
├── BlockBrace (8-orientation via EnumBraceOrientation, standalone)
├── BlockIRHorizontalFacing (horizontal facing via meta bits 0-1, hardness 0.8)
│   ├── BlockHandRail, BlockCatwalkHatch, BlockCatwalkGate
│   ├── BlockCatwalkStair, BlockCatwalkLadder
│   ├── BlockRazorWire, BlockSign
│   ├── BlockFireExtinguisher, BlockFirstAidKit
│   └── (bit 0x4 used for "active"/"onwall" secondary state)
└── BlockIRConnectable (abstract, 6-way neighbor connection logic, hardness 0.8)
    ├── BlockCatwalk (TileEntity for per-face railing blacklist)
    ├── BlockPillar (horizontal connections only, no vertical stacking)
    ├── BlockColumn (like pillar but allows vertical connections)
    └── BlockPlatform
```

### GTNHLib Property System

This mod relies heavily on GTNHLib's block property system for multipart JSON model rendering. Key patterns:

- **`ComputedBooleanProperty`** (`util/`) — custom property implementing `SupportsStacks` + `SupportsWorld`; computes connection state from world neighbors at render time. Used for `north`, `south`, `east`, `west`, `up`, `down` on connectable blocks.
- **`FixedInventoryProperty`** (`util/`) — provides fixed property values for inventory rendering (items only). Needed because `DirectionBlockProperty` and `IntegerBlockProperty` lack `SupportsStacks`.
- Properties are registered on **Block classes** for world rendering and on **Item instances** (via `ItemBlock`) for inventory rendering — these are separate registration paths.

### Metadata Conventions

- **Horizontal facing**: SOUTH=0, WEST=1, NORTH=2, EAST=3 (meta bits 0-1)
- **Brace orientation**: 8 values (meta bits 0-2), mapped via `EnumBraceOrientation`
- **Boolean flags**: bit 0x4 for `active`/`onwall` secondary state

### TileEntities

- **`TileEntityCatwalk`** — stores `EnumSet<ForgeDirection>` blacklist for railing visibility; synced via description packets
- **`TileEntitySyncBase`** — abstract base handling NBT sync via `getDescriptionPacket()`/`onDataPacket()`

### Resources

- **Blockstates**: variants-based (brace) or multipart-based (catwalk, platform, handrail)
- **Models**: `src/main/resources/assets/irdecor/models/block/` — some in subdirectories per block type
- **Textures**: `src/main/resources/assets/irdecor/textures/blocks/`
- **Lang**: `src/main/resources/assets/irdecor/lang/en_US.lang`

### GTNHLib Quirks

- **Texture references** auto-prepend `textures/blocks/` — use `"irdecor:pipe"` not `"irdecor:blocks/pipe"`
- **Y-rotation for East/West** is opposite to standard Forge: East = `"y": 270`, West = `"y": 90`
- **`IntegerBlockProperty.map()`** has a Gson stringify bug — use raw integer keys in blockstates (`facing=0`)
- **`DirectionBlockProperty.facing()`** creates a property named `"facing"` (not `"direction"`)

## Code Style

- UTF-8, LF line endings, 4-space indentation (2-space for JSON/YAML/XML/Markdown)
- Spotless formatting is enforced — run `./gradlew spotlessApply` before committing
- See `.editorconfig` for full details
