# Thaumcraft 4 — 1.12.2 Port

A community port of **Thaumcraft 4.2.3.5** (originally by **Azanor**, Minecraft 1.7.10) to
**Minecraft 1.12.2 / Forge** — plus a full native reimplementation of **Thaumic Tinkerer** and an
original endgame module of its own.

**Latest release:** [v1.2.7.0](https://github.com/Atom-gnomov/Thaumcraft-4-port-to-1.12.2/releases/latest)
— `Thaumcraft-x.y.z.w-universal.jar`, drop into `mods/` alongside Baubles.

## What's in the jar

**Thaumcraft 4, complete.** Research and the Thaumonomicon, alchemy and the crucible, arcane
crafting, infusion (with instability), essentia distillation and tubes, wands/foci/scanning,
golems, warp, the Outer Lands dimension, worldgen (silverwood, nodes, taint) — the whole 4.2.3.5
feature set, held to the original's numbers. The porting rule is **1:1 with the 1.7.10 original**:
values are read out of the decompiled source, never invented, and the original's own bugs are
kept and documented rather than silently "fixed".

**Thaumic Tinkerer, reimplemented natively** (pixlepix / nekosune / Vazkii, CC BY-NC-SA). All 79
portable objects: transvectors, the osmotic enchanter, dynamism tablet, magnets, funnels,
nether/eldritch foci, the full TT research branch — and the **KAMI** tier complete with ichorcloth
gear, awakened tools, the warp gate and the bedrock dimension. Lives in its own creative tab.

**End Legacy** — original content (no 1.7.10 counterpart, marked as such in the research):
- **Wings infused into any chestplate**: a gliding tier and a full-flight tier that behaves like a
  real elytra with vis-powered climb; the armour wears it in its name («Парящий…», «…Вознесения»).
- **Wards** (charm slot): projectile deflection and a totem-of-undying answered from the neck —
  cheating death costs warp.
- **Spires from Beyond**: eldritch obelisks on the End's outer islands, guarded and looted.
- **New foci**: the dragon's lingering breath (a die across the six primals), the ghast's fireball,
  and life drain.

**Addon compatibility**: the mod advertises enough of the TC6 binary API that 1.12 addons which
link against Thaumcraft 6 load and work — EnderIO, Fossils & Archeology, Magic Bees, JEID are
covered by shims and smoke-tested modsets (see `mod/docs/compatibility/tc6-shim-coverage.md`).

## Status

Actively developed and played. The 1.7.10 parity work is essentially done; current work is
polish (renderer parity with the reference port, remaining GUI sprites) and End Legacy content.
Known bugs and open work live in [`KNOWN_ISSUES.md`](KNOWN_ISSUES.md); every release is logged in
[`CHANGELOG.md`](CHANGELOG.md).

The codebase carries **700+ tests** — static guards that pin ported values to the original's, and
runtime tests that assemble multiblocks and run devices in a faked world. The full suite runs on
every release.

## Building

Requires a **JDK 8** (not just a JRE — ForgeGradle needs `tools.jar`).

```bash
cd mod
./gradlew build -Dorg.gradle.java.home=<path-to-jdk8>   # jars land in mod/build/libs
./gradlew runClient                                      # dev client
```

`mod/gradle.properties` deliberately does **not** pin `org.gradle.java.home` (a committed absolute
path once broke every other machine). Point Gradle at your JDK 8 per machine: on the command line
as above, or once in `~/.gradle/gradle.properties`.

Toolchain: ForgeGradle 2.3, Gradle 4.10.3, Forge `1.12.2-14.23.5.2847`, mappings `stable_39`.

## Dependencies

**Baubles** (amulet/ring/belt/charm slots) — required; resolved automatically from CurseMaven at
build time (see the `dependencies` block in `mod/build.gradle`).

## Repository layout

```
mod/                        the Forge mod (source + resources) — the actual port
mod/docs/                   aspect maps, TC6 shim coverage, rendering notes
CHANGELOG.md                every release, with the reasoning
KNOWN_ISSUES.md             open bugs and debts, with owners' decisions recorded
PORTING_HANDOFF.md          the porting rules (read this before contributing)
THAUMIC_TINKERER_PLAN.md    the TT module: status, deviations registry
END_LEGACY_PLAN.md          the original-content module: design and spec
FOREVA_ADOPTION_QUEUE.md    fixes still to adopt from the reference port
TT_OBJECT_REFERENCE.md      every TT object's original values, extracted verbatim
```

Reference material — the decompiled TC4 original (the porting ground truth), its extracted assets
with 22 language files, the FOREVA and Thaumic Tinkerer source clones, MCP mappings and a JDK —
is kept in the working copies but excluded from the repository via `.gitignore`.

## Credits & lineage

- **Original mod:** Thaumcraft 4.2.3.5 © 2013–2015 **Azanor**.
- **Base port:** [0FL01/Thaumcraft-4.2-FOREVA](https://github.com/0FL01/Thaumcraft-4.2-FOREVA) —
  this repo continues from it and still adopts its fixes (kept locally as a reference clone).
- **Thaumic Tinkerer:** pixlepix, nekosune, originally Vazkii (CC BY-NC-SA 3.0) — reimplemented
  natively, with per-class attribution in the source.
- **This repo:** further porting, fixes and original content, under MIT
  (see [`mod/LICENSE`](mod/LICENSE) and [`LICENSE`](LICENSE)).

This is an unofficial community project, not affiliated with or endorsed by the original authors.
