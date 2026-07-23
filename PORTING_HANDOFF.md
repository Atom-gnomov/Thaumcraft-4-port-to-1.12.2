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

### Release process
1. Bump `version` in `mod/build.gradle` (patch = fixes).
2. Add a `## [x.y.z]` entry at the **top** of `CHANGELOG.md` (below the pinned
   `[1.0.15]` block; entries are newest-first from there).
3. Build: `cd mod && ./gradlew.bat build -x test --console=plain -q`.
4. Deploy: replace the old jar in
   `C:/Users/<user>/AppData/Roaming/.minecraft/mods/` with
   `mod/build/libs/Thaumcraft-x.y.z-universal.jar`.
5. Commit changed files + push. Commit trailer:
   `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.

---

## Test baseline — GREEN (0 failures)

`cd mod && ./gradlew.bat test` now passes completely: **318 suites, 0 failures**
(was 21 failing at the start of this effort). The rule going forward: **keep it
at zero.** After any change, run the full suite; investigate every new failure
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
   WandUsePose*/WandPoseMath/ItemWandRenderer, ConnectedTextureUtils,
   EldritchCrustBakedModel/WardedGlassBakedModel (need ClientModelRegistry
   registration), WandEffectOrigin, BlockStoneDeviceItem. A wholesale copy of
   these previously cascaded into compile errors — port incrementally.

## Known open gameplay tasks (separate from FOREVA parity)
- Focus radial menu missing info.
- Chat overlap (partially eased by 1.0.31 warp→HUD move; re-check).
- Auto alchemy furnace "not working" — code verified byte-identical to FOREVA;
  needs a node supplying FIRE+ENTROPY+WATER. Likely not a code bug.
