# Thaumcraft 4 — 1.12.2 Port (WIP)

An unofficial, work-in-progress port of **Thaumcraft 4** (originally by **Azanor**, for
Minecraft 1.7.10) to **Minecraft 1.12.2 / Forge**.

> This is a fan/learning port. All original Thaumcraft concepts, textures and design are the work of
> **Azanor**. This repository contains only the ported source and derived assets needed to run the
> port; it is not affiliated with or endorsed by the original author.

## Status

**Phase 1 — "blocks as items" — COMPLETE.** All **45** Thaumcraft blocks are registered on the
1.12.2 `IForgeRegistry`, each with its texture(s), block/item models, blockstates, creative-tab
entry (where the original had one) and language keys. Behaviour is being ported incrementally in
later phases; blocks that are not yet mechanically active are faithful visual/identity stubs marked
with `TODO Phase N` in the source.

Port phases:

1. **Blocks as items** — register every block/item with textures + ItemBlock + stubs. ✅ done
2. **Properties** — metadata/blockstate properties, shapes, rendering. 🔵 in progress
3. **Mechanics** — TileEntities, essentia/vis, GUIs, active behaviours. ⬜ planned
4. **Interactions** — worldgen, research, cross-block systems. ⬜ planned

Batches completed in Phase 1:

| Batch | Contents |
|---|---|
| B1 | Decorative static blocks — candle, eldritch, loot urn/crate, mana pod |
| B2 | Device blocks as inert stubs — metal/wooden/stone device, table, jar, tube, mirror, furnaces, chest, lifter, magic box, essentia reservoir, airy, warded |
| B3 | Special blocks — arcane door, portable hole, eldritch nothing, eldritch portal |
| B4 | Fluids — flux goo, flux gas, purifying fluid, liquid death |

See [`PORT_SPEC.md`](PORT_SPEC.md) for a reverse-engineered card per block (form, texture, colour,
light, sound, mechanics-to-do).

## Building

Requires JDK 8. From the repository root:

```bash
cd mod
./gradlew build        # produces the mod jar in mod/build/libs
./gradlew runClient    # launch a dev client
```

The workspace uses ForgeGradle 2.3.4, Forge `14.23.5.2847`, mappings `snapshot_20171003`.

## Dependencies

**Baubles 1.12.2** is planned for later phases (amulet/ring/belt slots). It is **not** wired into
the current build. To integrate it, drop `Baubles-1.12.2-1.5.2.jar` into `mod/libs/` and uncomment
the `deobfCompile` line in `mod/build.gradle`.

## Repository layout

```
mod/           the Forge mod (source + resources)   ← the actual port
PORT_SPEC.md   per-block/-item port specification
```

Reference material (the decompiled original, extracted original assets, MCP mappings, tooling/JDK)
is kept locally but excluded from the repository via `.gitignore`.
