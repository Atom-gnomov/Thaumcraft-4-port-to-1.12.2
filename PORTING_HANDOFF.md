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

## Test baseline — READ THIS

`cd mod && ./gradlew.bat test` currently leaves **17 failing tests** (was 21
before this session fixed 4). BUILD FAILED is expected — it just means failing
tests exist. The rule: **do not increase the failing set.** After a change,
extract failing classes from `mod/build/test-results/test/*.xml`
(testsuite `failures>0 || errors>0`) and diff against the known set.

When a **static-guard test pins an old reconstruction you deliberately
replaced with FOREVA's version, UPDATE the guard to the new contract** (done
this session for `ItemElementalAxeStaticGuardTest`, `ClientProxyFxStaticGuardTest`
[PacketWarpMessage], `TileFluxScrubberStaticGuardTest`). That is correct, not
cheating — the guard should track the intended implementation.

### The 17 remaining failures split into two groups

**Group A — missing reference files (`NoSuchFileException`).** These tests read
reference assets that live under `mod/` at test runtime (CWD = `mod/`). FOREVA
ships them; we were missing them. Already fixed 4 by copying `mod/scripts/` and
`mod/docs/` from FOREVA. The remaining ~6 need the decompiled reference asset
tree **`thaumcraft_src/` (~15 MB, 1096 files)** placed at **`mod/thaumcraft_src/`**:
  - AlchemyFurnaceAdvancedRendererContractTest (adv_alch_furnace.obj)
  - BlockTextureAssetCoverageTest (textures/blocks)
  - FluxReservoirRendererFidelityStaticGuardTest (reservoir.obj)
  - FocusExcavationVisualParityStaticGuardTest (focus_excavation.png)
  - FocusFrostVisualParityStaticGuardTest (orb.obj)
  - RotaryMachineShellContractTest (IIOException — reads an image)
  - **DECISION PENDING (ask the user):** committing 15 MB of decompiled original
    TC4 assets to a public repo is a notable, hard-to-reverse choice. Options:
    (a) `cp -r <foreva>/thaumcraft_src mod/thaumcraft_src` and commit;
    (b) keep it local-only + add `mod/thaumcraft_src/` to `.gitignore`;
    (c) leave these 6 tests as a documented can't-run-here baseline.
    Note: once the files exist, each test still runs a *real* fidelity assertion
    that may itself pass or fail — verify per-test.

**Group B — real assertion failures (contracts our source doesn't meet).**
These are NOT environment noise; FOREVA passes them because they describe
FOREVA's implementation. Fix by adopting FOREVA's version of the source the test
reads (map below), then update the guard only if it pins a now-obsolete detail.

  | Test | Reads (primary) |
  |------|-----------------|
  | ArcaneFurnaceVisualShellContractTest | ArcaneFurnaceBakedModel.java, blockstates/blockarcanefurnace.json |
  | BlockStoneDeviceContractTest | BlockStoneDevice.java, blockstates/blockstonedevice.json |
  | ClientProxyDedicatedBeamBoltStaticGuardTest | ClientProxy.java, fx/beams/FXBeam*.java |
  | CreativeTabVisualParityStaticGuardTest | lang/en_us.lang (**CRLF issue — see below**) |
  | EldritchTesrRoutingContractTest | Eldritch*Renderer.java, models/block/blockeldritch_4.json |
  | GuiResearchRecipeStaticGuardTest | GuiResearchRecipe.java, en_us.lang |
  | InfusionRendererFidelityStaticGuardTest | TileRunicMatrixRenderer.java, ModelInfusionPillar.java |
  | ItemThaumometerStaticGuardTest | ItemThaumometer.java |
  | ReportedItemModelRoutingContractTest | Item*Renderer.java (jar/reservoir/...) |
  | RotaryMachineShellContractTest | TileCentrifugeRenderer.java (+ image asset) |
  | ThaumometerItemRendererContractTest | ItemThaumometerRenderer.java, ThaumometerPerspectiveModel.java |
  | VisEnergyRendererFidelityStaticGuardTest | TileNodeConverterRenderer.java, TileNode*Renderer.java |

### ⚠ CRLF ROOT CAUSE (investigate first — likely unblocks several Group B)

This repo checks out with **CRLF** (`git config core.autocrlf` = `true`, no
`.gitattributes`). Tests that assert `\n`-anchored substrings on reference text
files (`en_us.lang`, `.json`) fail because the bytes are `\r\n`. Example:
`CreativeTabVisualParityStaticGuardTest` checks
`lang.contains("\nitemGroup.thaumcraft=Thaumcraft\n")` — our lang line **is**
present but ends `...Thaumcraft\r\n`, so the match fails. FOREVA develops on LF.

**Recommended fix (verify, then do):** add a `.gitattributes` forcing `text eol=lf`
for reference text (`*.lang`, `*.json`, `*.md`, `scripts/*`) and renormalize the
working tree (`git add --renormalize .`). This may fix CreativeTab,
GuiResearchRecipe, and any other `\n`-anchored guard at once, and is safe for
the game (Minecraft reads either line ending). Java-source contract tests use
non-`\n` substrings, so `.java` CRLF does not affect them. **Do CRLF first**,
re-run, then only the genuinely content-diverged Group B tests remain to adopt.

---

## Already adopted this session (shipped)

- **1.0.27** mirrors + golem render/attack.
- **1.0.28** golem harvest-AI + FOREVA elemental axe + `BlockUtils` (consistent set).
- **1.0.29** jar-nodes (BlockJar/BlockJarItem/TileJarNode) + `ItemWandCasting.getHandHoldingWand`.
- **1.0.30** foci Excavation/Fire/Shock — offhand + server-owns-resources.
- **1.0.31** warp — WarpEvents/PacketWarpMessage → `PlayerNotifications` HUD, `EntityUtils.isVisibleTo`.
- **1.0.32** taint fall/cascade (BlockTaint/BlockFluidDeath/EntityFallingTaint) + flux scrubber + `slimyBubble` proxy.
- **(test)** `mod/scripts/`, `mod/docs/` reference dirs → 4 Group-A tests green.

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
