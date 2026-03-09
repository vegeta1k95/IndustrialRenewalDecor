# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

IndustrialRenewalDecor (`irdecor`) is a Minecraft 1.7.10 Forge mod built on the GTNH (GT New Horizons) build conventions. Currently a template/scaffold — the package is still `com.myname.mymodid` with placeholder classes.

## Build Commands

```bash
# Build the mod JAR
./gradlew build

# Run Minecraft client for testing
./gradlew runClient

# Run with a specific username
./gradlew runClient --username=PlayerName

# Run dedicated server
./gradlew runServer

# Format code with Spotless
./gradlew spotlessApply

# Check formatting without fixing
./gradlew spotlessCheck
```

## Build System

- **Gradle 9.2.1** with Kotlin DSL, using the `com.gtnewhorizons.gtnhconvention` plugin
- Build config is minimal — `build.gradle.kts` only applies the convention plugin; all configuration lives in `gradle.properties`
- Dependencies go in `dependencies.gradle` (Groovy DSL), custom repos in `repositories.gradle`
- Configuration caching and parallel builds are enabled
- Java 17 syntax is available via Jabel while targeting JVM 8
- A `Tags` class is auto-generated at `com.myname.mymodid.Tags` with a `VERSION` field

## Architecture

- **Entry point**: `MyMod.java` — `@Mod` annotated class, delegates all lifecycle events to proxy
- **Proxy pattern**: `CommonProxy` (server) / `ClientProxy` (client) via `@SidedProxy`
- **Config**: `Config.java` — uses Forge `Configuration` API
- **Mod ID**: `irdecor` (defined in `gradle.properties` as `modId`)
- **Mod metadata**: `src/main/resources/mcmod.info` — uses Gradle token substitution (`${modId}`, `${modVersion}`, etc.)

## Code Style

- UTF-8, LF line endings, 4-space indentation (2-space for JSON/YAML/XML/Markdown)
- Spotless formatting is enforced — run `./gradlew spotlessApply` before committing
- See `.editorconfig` for full details
