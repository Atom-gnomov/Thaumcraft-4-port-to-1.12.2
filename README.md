# Thaumcraft 4 — 1.12.2 Port

A continuation/refinement of the community port of **Thaumcraft 4.2.3.5** (originally by **Azanor**,
Minecraft 1.7.10) to **Minecraft 1.12.2 / Forge**.

## Credits & lineage

This repository is **based on the [`0FL01/Thaumcraft-4.2-FOREVA`](https://github.com/0FL01/Thaumcraft-4.2-FOREVA)**
port, used under its MIT license, and continues its work.

- **Original mod:** Thaumcraft 4.2.3.5 © 2013–2015 **Azanor**.
- **1.12.2 port base:** Thaumcraft-4.2-FOREVA © 2026 **Andrey Bash and contributors** (MIT).
- **This repo:** further porting / bug-fixing / refinement, under the same MIT license.

See [`mod/LICENSE`](mod/LICENSE) and [`LICENSE`](LICENSE) for the full MIT text and copyright notices.
This is an unofficial community project, not affiliated with or endorsed by the original author.

## Status

Full 1.12.2 port with core systems operational — alchemy, infusion, golems, the research system and
the Thaumonomicon (recipe pages, focus visuals, particle rendering) all work. Remaining work is
item-specific logic, runtime parity testing, and visual/rendering polish, which we address
incrementally.

## Building

Requires a **JDK 8** (not just a JRE — ForgeGradle needs `tools.jar`).

```bash
cd mod
./gradlew build        # builds the mod jar into mod/build/libs
./gradlew runClient    # launch a dev client
```

`mod/gradle.properties` pins `org.gradle.java.home` to a local JDK 8 path. If you clone this on
another machine, point that property at your own JDK 8 install.

Toolchain: ForgeGradle 2.3, Gradle 4.10.3, Forge `1.12.2-14.23.5.2847`, mappings `stable_39`.

## Dependencies

**Baubles** (amulet/ring/belt slots) is resolved automatically from CurseMaven — see the
`dependencies` block in `mod/build.gradle`.

## Repository layout

```
mod/           the Forge mod (source + resources) — the actual port
mod/LICENSE    MIT license (FOREVA base + our continuation)
PORT_SPEC.md   per-block/-item port notes
```

Reference material (the decompiled original, extracted original assets, MCP mappings, tooling/JDK,
the upstream FOREVA clone) is kept locally but excluded from the repository via `.gitignore`.
