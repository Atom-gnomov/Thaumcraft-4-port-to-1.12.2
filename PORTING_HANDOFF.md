# FOREVA Porting Handoff (for agents)

Working doc for continuing the adoption of upstream **FOREVA**
(`0FL01/Thaumcraft-4.2-FOREVA`) fixes/systems into **tc4-port**
(this repo — the Atom-gnomov fork). Read this before continuing.

- Mod source lives in `mod/` (Gradle project root is `mod/`, **not** the repo root).
- Active branch: `fix/gameplay-parity-1.0.15`.
- Reference clone used during this work: a shallow clone of FOREVA at a temp
  path (e.g. `$TEMP/foreva-clone`). Re-clone if absent:
  `git clone --depth 1 https://github.com/0FL01/Thaumcraft-4.2-FOREVA <tmp>`.

---

## THE RULE: never invent a value. Read the original. (mandatory)

This is the first rule of this repo and it outranks every convenience below it.
It exists because it was repeatedly broken: recipes, block hardness, item
names and tool behaviour were filled in "by analogy" instead of being read out
of the source, and every one of those had to be found and undone later.

**Local copies of both originals live outside the repo and are permanent.**
Paths are given relative to the repo root, because this work happens on more
than one machine — never hard-code a home directory into these docs.

| What | Where |
| --- | --- |
| Thaumic Tinkerer 1.7.10 source (branch `1.7.10`) | `../tt-original-1.7.10` |
| JDK 8 (Temurin 8u492) used to build | `../tools/jdk8u492-b09` |

Re-clone TT if it is ever missing (run from the repo root):

```bash
git clone -b 1.7.10 https://github.com/Thaumic-Tinkerer/ThaumicTinkerer ../tt-original-1.7.10
```

Build with that JDK rather than the system default, which is a JRE:

```bash
cd mod && ./gradlew build -x test -Dorg.gradle.java.home=../../tools/jdk8u492-b09 --console=plain
```

### The loop, every single time

1. **Look it up in [`TT_OBJECT_REFERENCE.md`](TT_OBJECT_REFERENCE.md).** It holds
   all 84 registerable Thaumic Tinkerer objects with their recipe, aspects,
   instability, constructor constants and research node, extracted verbatim.
2. **Not there, or you need behaviour rather than values? Open the original.**
   `../tt-original-1.7.10/src/main/java/thaumic/tinkerer/...`
   Read the whole class, not the method you think you need.
3. **Transcribe.** Metadata indices, aspect amounts, instability, hardness and
   resistance carry over unchanged — the meta index is the ground truth even
   when the port happens to call that subtype something else.
4. **Write what you found back into `TT_OBJECT_REFERENCE.md`** if it was not
   already there, so the next pass does not repeat the lookup.

### Where names and strings come from

The original ships **its own English *and* Russian** language files:

```
tt-original-1.7.10/src/main/resources/assets/ttinkerer/lang/en_US.lang
tt-original-1.7.10/src/main/resources/assets/ttinkerer/lang/ru_RU.lang
```

Never write a display name or translate one by hand. The magnet is a *Kinetic
Attractor* / *Кинетический притяжатель*, not a "Magnet"; the repairer is a
*Thaumic Restorer*; the funnel is an *Essentia Funnel*. All of that was invented
once and had to be corrected against these two files.

### Reproduce the original's bugs too

1:1 means 1:1. `ToolHandler.removeBlocksInIteration` compares absolute
coordinates against loop offsets, which punches holes in the dug shape near the
world origin; `removeBlockWithDrops` accepts `silk`/`fortune` and never reads
them. Both are transcribed as-is in
`thaumcraft.common.items.tinkerer.kami.tool.KamiToolHandler`, with a comment
saying why. Do not "fix" what you find — note it and keep it.

### What to do when a 1:1 port is blocked

Do **not** substitute, simplify, or approximate. Register nothing, and record
the blocker in `THAUMIC_TINKERER_PLAN.md`'s deviation register with the exact
missing prerequisite. An absent recipe is honest; an invented one is a lie that
takes a full audit to find.

---

## Adoption workflow (proven this session)

1. **Diff** our file vs FOREVA's: `diff mod/src/main/java/<path> <foreva>/src/main/java/<path>`.
2. **Adopt coherent system-sets *together*** so cross-file signatures stay
   consistent (e.g. axe + BlockUtils + harvest-AI as one set). Copying a lone
   file that depends on a diverged sibling → compile break.
3. **Resolve missing deps surgically:** if FOREVA code calls a helper we lack,
   port just that helper (e.g. `ItemWandCasting.getHandHoldingWand`,
   `EntityUtils.isVisibleTo`, `CommonProxy/ClientProxy.slimyBubble`) rather than
   copying the whole donor class.
4. **Compile:** `cd mod && ./gradlew.bat compileJava --console=plain -q` (JDK 8).
5. **Test & compare to baseline** (see below). Only *new* failures matter.
6. **Ship** (see release process).

### Version scheme (since 1.1.1.0)

Four components, `A.B.C.D`, each one less significant than the last:

| | Meaning | Bump when |
| --- | --- | --- |
| **A** | Milestone of the port as a whole | A release the whole port is judged by |
| **B** | Line of work | `0` = TC4-parity core / FOREVA adoption, `1` = Thaumic Tinkerer module |
| **C** | Feature or pass within that line | A new device, system or content set lands |
| **D** | Follow-up on an already-shipped feature | Bugfix, assets, localisation, guard-only changes |

The flat `1.0.x` numbering ran to **1.0.59** and stays as history; core-side work
continues from `1.0.60.0` under the same rules.

### Release process
1. Bump `version` in `mod/build.gradle` (see the scheme above).
2. Add a `## [x.y.z]` entry at the **top** of `CHANGELOG.md` (below the pinned
   `[1.0.15]` block; entries are newest-first from there).
3. Build: `cd mod && ./gradlew.bat build -x test --console=plain -q`.
4. Deploy: replace the old jar in
   `C:/Users/<user>/AppData/Roaming/.minecraft/mods/` with
   `mod/build/libs/Thaumcraft-x.y.z-universal.jar`.
5. Commit changed files + push. Commit trailer:
   `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.

---

## Test baseline — 5 failures as of 1.0.47 (was GREEN at 1.0.35)

At **1.0.35** `cd mod && ./gradlew.bat test` passed completely: 318 suites,
0 failures (down from 21 at the start of that effort).

**That is no longer true.** As of **1.0.47** the suite runs 539 tests with
**5 failures**, all client-render guards:

- `ArcaneFurnaceVisualShellContractTest`
- `ClientProxyDedicatedBeamBoltStaticGuardTest`
- `InfusionRendererFidelityStaticGuardTest`
- `VisEnergyRendererFidelityStaticGuardTest`
- `ReportedItemModelRoutingContractTest`

They were verified pre-existing (stash the working tree, re-run on a clean
checkout) and trace back to the FOREVA renderer/tile/block adoption of
1.0.36–1.0.45 — not to the Thaumic Tinkerer module. Either fix the renderers
or update those guards to the intended contract (see the rule below), then
restore this section to GREEN.

The rule still stands: **do not add new failures.** After any change, run the
full suite and compare against these five; investigate every *new* failure
before shipping.

When a **static-guard test pins an old reconstruction you deliberately
replaced with FOREVA's version, UPDATE the guard to the new contract** (done for
`ItemElementalAxeStaticGuardTest`, `ClientProxyFxStaticGuardTest`
[PacketWarpMessage], `TileFluxScrubberStaticGuardTest`). That is correct, not
cheating — the guard should track the intended implementation. The inverse also
happens: our guards sometimes pin deliberate local improvements over upstream
(e.g. `EldritchTesrRoutingContractTest` pins floor-anchored shells `[2, 0, 2]`
where FOREVA still floats at `[2, 2, 2]`; `HungryNodeAndGlStateParityStaticGuardTest`
pins the lightmap save/restore FOREVA's thaumometer renderer leaks). When
adopting FOREVA code, preserve those local fixes — don't blindly downgrade.

### How it got to zero (for context / future platform issues)

1. **Reference dirs** `mod/scripts/`, `mod/docs/`, and `mod/thaumcraft_src/`
   (~15 MB decompiled TC4 assets) copied from FOREVA — tests read them relative
   to `mod/` (the Gradle CWD), not the repo root.
2. **Line endings**: `.gitattributes` forces `eol=lf` for
   java/json/lang/md/sh/gradle. The CRLF checkout (`core.autocrlf=true`) broke
   every guard asserting `
`-anchored (multi-line) source blocks even when
   content matched exactly. If a guard fails but the content looks right,
   check bytes first (`file`, `cat -A`).
3. **Compiler encoding**: `build.gradle` sets `options.encoding = 'UTF-8'` for
   all JavaCompile tasks — Windows javac otherwise uses cp1251 and garbles
   `§`-literals inside test sources.
4. **Content adoption**: thaumometer (scan + renderer + TEISR json),
   GuiResearchRecipe (+ drop gui/MappingThread), wand release latch in
   ClientTickEventsFML, eldritch shell models (floor-anchored).

---

## Already adopted this session (shipped)

- **1.0.27** mirrors + golem render/attack.
- **1.0.28** golem harvest-AI + FOREVA elemental axe + `BlockUtils` (consistent set).
- **1.0.29** jar-nodes (BlockJar/BlockJarItem/TileJarNode) + `ItemWandCasting.getHandHoldingWand`.
- **1.0.30** foci Excavation/Fire/Shock — offhand + server-owns-resources.
- **1.0.31** warp — WarpEvents/PacketWarpMessage → `PlayerNotifications` HUD, `EntityUtils.isVisibleTo`.
- **1.0.32** taint fall/cascade (BlockTaint/BlockFluidDeath/EntityFallingTaint) + flux scrubber + `slimyBubble` proxy.
- **(test)** `mod/scripts/`, `mod/docs/` reference dirs → 4 Group-A tests green.
- **1.0.33** thaumometer — FOREVA scan flow + renderer (TC6 TEISR poses) with our lightmap save/restore kept.
- **1.0.34** GuiResearchRecipe (drop MappingThread) + wand-use release latch in ClientTickEventsFML; UTF-8 javac encoding.
- **1.0.35** eldritch shells floor-anchored (metas 4/5/6) + `mod/thaumcraft_src/` reference tree → **suite fully green**.

See `CHANGELOG.md` for detail.

---


## ⛔ Do NOT adopt FOREVA's FX particle/beam classes

Our FX layer is a deliberate LOCAL architecture: particles implement `ITCParticle`
and emit vertices into the shared `ParticleEngine` buffer (the engine owns
begin/draw). FOREVA's particles extend vanilla `Particle` and own their own
`buffer.begin()`/`tessellator.draw()`. These are fundamentally incompatible, and
our local guard `FxLayerAndEldritchParityStaticGuardTest` protects the ITCParticle
design (`implements ITCParticle`, `getTCParticleLayer()`, no self-owned begin/draw).

Adopting FOREVA's `FXVisSparkle`/`FXSmokeSpiral`/`FXGeneric`/`FXBoreParticles`/
`FXBeam*` — and by extension FOREVA's `ClientProxyFxStaticGuardTest`, which pins
`class FX* extends Particle` + `getFXLayer()` — cascades into the proxies,
`RenderEventHandler`, `PacketFXVisDrain` serialization, and contradicts our own
FX guards. This was attempted and fully reverted. **Skip the FX cluster.** If a
specific FX behaviour is worth porting, re-implement it inside our ITCParticle
model rather than copying FOREVA's class.

## Remaining FOREVA systems to port (after tests are green)

From the full diverged-file set (was captured as `$TEMP/adopt2.txt`; regenerate
with `git diff --name-only <foreva-base> HEAD` or by diffing trees). High-value,
roughly ordered by coherence / lower risk:

1. **Node renderers** — TileNodeRenderer, TileNodeEnergizedRenderer,
   TileNodeStabilizerRenderer (visual; validate GL state save/restore — this
   port has had TESR state-leak bugs before).
2. **Crucible fluid surface** — TileCrucibleRenderer.
3. **Tiles** — TileInfusionMatrix, TileWandPedestal, TileSensor,
   TileFocalManipulator, TileOwned, TileEldritchCrabSpawner.
4. **Entities** — EntityMindSpider, EntityPermanentItem.
5. **Outer-lands dungeon gen** — ChunkProviderOuter, GenBossRoom, GenCommon,
   MazeThread (bigger; test carefully).
6. **FX** — FXBeam*, FXVisSparkle, FXGeneric, FXBoreParticles.
7. **Blocks** — BlockAlchemyFurnace, BlockArcaneFurnace, BlockCosmeticSolid,
   BlockCustomOre, BlockMagicalLog, BlockStoneDevice, BlockWarded,
   BlockWoodenDevice, BlockEssentiaReservoir.
8. **GuiResearchRecipe** — ⚠ our `MappingThread` calls `GuiResearchRecipe.putToCache`
   which FOREVA's version lacks; reconcile (keep our `putToCache` or update the
   caller) or the mod won't compile.
9. **Higher-risk interdependent infra** (do as one careful pass, last):
   WandUsePose*/WandPoseMath/ItemWandRenderer, WandEffectOrigin,
   BlockStoneDeviceItem. A wholesale copy of these previously cascaded into
   compile errors — port incrementally. NOTE: WandEffectOrigin + WandUsePose*
   are only used by FOREVA's FX beam classes, which we do NOT adopt (ITCParticle
   conflict, see the ⛔ section) — so this cluster likely has no consumer here.

   **DONE (1.0.36):** ConnectedTextureUtils. **DONE (1.0.40):** WardedGlassBakedModel
   + BlockCosmeticOpaque connected-glass (registered in ClientModelRegistry:
   47 warded_glass_* sprites, WARDED_GLASS_MODEL, replaceWardedGlassModel).

   **DEFERRED — EldritchCrustBakedModel:** requires BlockEldritch changes that
   conflict with our local fixes. FOREVA's BlockEldritch (a) DELETES our
   `getLightValue(IBlockState)` override that seeds worldgen blocklight (crusted
   glowstone stays black without it), (b) changes the default state TYPE 0→4,
   (c) drops `meta == 9` from the INVISIBLE `getRenderType` set. Three guards
   read BlockEldritch (BlockEldritchAmbientFx, EldritchTesrRouting,
   FxLayerAndEldritchParity). To adopt: surgically add `CRUST_NEIGHBOR_MASK`
   PropertyInteger + `getActualState` (neighbor mask) + `createBlockState` entry,
   KEEP our getLightValue override, and re-verify all three guards + item render.

## Known open gameplay tasks (separate from FOREVA parity)
- Focus radial menu missing info.
- Chat overlap (partially eased by 1.0.31 warp→HUD move; re-check).
- Auto alchemy furnace "not working" — code verified byte-identical to FOREVA;
  needs a node supplying FIRE+ENTROPY+WATER. Likely not a code bug.
