# Thaumcraft 4 Port — Addon API

This port ships the classic **Thaumcraft 4 `thaumcraft.api` package** so other
mods (addons) can integrate: register aspects, add crucible / arcane / infusion
recipes, tap the aura and essentia networks, add research, warp, loot, and
implement the wand-focus / node / goggle interfaces.

- **Mod id:** `thaumcraft`
- **Forge API token:** `@API(owner = "Thaumcraft", apiVersion = "4.2.2.0", provides = "Thaumcraft|API")`
- **API artifact:** `Thaumcraft-<version>-api.jar` (built by the `apiJar` Gradle task; contains only `thaumcraft/api/**` — no obfuscated internals)

> The API surface intentionally mirrors TC4.2.3.5. TC6-style addons are also
> partially supported through the `AspectRegistryEvent` + `AspectEventProxy`
> compatibility shim (see *Aspects* below).

---

## 1. Depending on the API

Build the API jar and hand it to your addon project:

```bash
cd mod && ./gradlew.bat apiJar     # → mod/build/libs/Thaumcraft-<version>-api.jar
```

In your addon's `build.gradle` (ForgeGradle 2.3 / MC 1.12.2), drop the jar into
a `libs/` folder and reference it compile-only (it is provided at runtime by the
installed mod):

```gradle
repositories { flatDir { dirs 'libs' } }
dependencies {
    // compile against the API, do NOT bundle it
    provided group: 'thaumcraft', name: 'Thaumcraft', version: '<version>', classifier: 'api'
}
```

Declare the dependency on your `@Mod` so load order and "missing mod" screens
behave:

```java
@Mod(modid = "myaddon", name = "My Addon", version = "1.0",
     dependencies = "required-after:thaumcraft")
public class MyAddon { }
```

All registration below should run in **preInit / init** (recipes and tags), or
in the events noted. Aspect tags are safest during `AspectRegistryEvent` or init.

---

## 2. Aspects & object tags

Attach aspects ("scan results") to items, ore-dict entries, or entities via
`thaumcraft.api.ThaumcraftApi`:

```java
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

// simple: exact item + meta
ThaumcraftApi.registerObjectTag(new ItemStack(myItem),
        new AspectList().add(Aspect.MAGIC, 5).add(Aspect.ORDER, 3));

// ore dictionary
ThaumcraftApi.registerObjectTag("ingotSilver",
        new AspectList().add(Aspect.METAL, 8).add(Aspect.DESIRE, 2));

// meta group (all share one tag set)
ThaumcraftApi.registerObjectTag(new ItemStack(myBlock), new int[]{0,1,2,3},
        new AspectList().add(Aspect.EARTH, 4));

// "complex": inherit aspects derived from the item's recipe, then add yours
ThaumcraftApi.registerComplexObjectTag(new ItemStack(myTool),
        new AspectList().add(Aspect.TOOL, 5));
```

Register a **new aspect** by constructing an `Aspect` (its constructor
auto-registers it): `new Aspect(tag, color, components[, blend])`.

**TC6-style addons** may instead subscribe to `AspectRegistryEvent` and use the
supplied `AspectEventProxy` (`register.registerObjectTag(...)` etc.), which is
posted once core tags are ready.

---

## 3. Recipes

```java
// Arcane crafting (research key gates it; use "" for none)
ThaumcraftApi.addArcaneCraftingRecipe("MYRESEARCH", resultStack,
        new AspectList().add(Aspect.AIR, 20).add(Aspect.ORDER, 20),
        "aba", "b b", "aba", 'a', Items.STICK, 'b', myGem);

ThaumcraftApi.addShapelessArcaneCraftingRecipe("MYRESEARCH", resultStack,
        new AspectList().add(Aspect.WATER, 10), inputA, inputB);

// Crucible (catalyst item + aspect cost). key is the research needed.
ThaumcraftApi.addCrucibleRecipe("MYRESEARCH", resultStack, catalystStack,
        new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENTROPY, 5));

// Infusion altar (central input + surrounding component stacks + instability)
ThaumcraftApi.addInfusionCraftingRecipe("MYRESEARCH", resultStack, /*instability*/ 3,
        new AspectList().add(Aspect.MAGIC, 40).add(Aspect.ORDER, 30),
        centralInput, new ItemStack[]{ compA, compB, compC });

// Infusion enchantment
ThaumcraftApi.addInfusionEnchantmentRecipe("MYRESEARCH", myEnchant, /*instability*/ 2,
        new AspectList().add(Aspect.FIRE, 25), new ItemStack[]{ compA, compB });
```

Lookups: `getCrucibleRecipe(stack)`, `getInfusionRecipe(stack)`,
`getCraftingRecipes()`.

Implement `thaumcraft.api.crafting.IInfusionStabiliser` on a block to make it
count as infusion-stabilising scenery.

---

## 4. Aura & essentia

```java
import thaumcraft.api.aura.AuraHelper;
float vis   = AuraHelper.getVis(world, pos);
float taken = AuraHelper.drainVis(world, pos, 10f, /*simulate*/ false);
AuraHelper.addVis(world, pos, 5f);
float flux  = AuraHelper.getFlux(world, pos);
AuraHelper.addFlux(world, pos, 2f);

import thaumcraft.api.aspects.AspectSourceHelper;
// pull essentia from connected jars/containers into your tile
boolean ok    = AspectSourceHelper.drainEssentia(myTile, Aspect.FIRE, facing, /*range*/ 6);
boolean avail = AspectSourceHelper.findEssentia(myTile, Aspect.FIRE, facing, 6);
```

For a tile to participate in essentia piping, implement
`thaumcraft.api.aspects.IEssentiaTransport` (and `IAspectContainer` if it stores
essentia). Items that carry essentia implement `IEssentiaContainerItem`.

Vis-network nodes: extend/emit through `thaumcraft.api.visnet.VisNetHandler`
and `TileVisNode`.

---

## 5. Research, warp, loot, seeds

```java
ThaumcraftApi.registerResearchLocation(new ResourceLocation("myaddon","research/main"));
ThaumcraftApi.addWarpToItem(cursedStack, 2);          // sanity cost on craft
ThaumcraftApi.addWarpToResearch("MYRESEARCH", 3);
ThaumcraftApi.addLootBagItem(lootStack, /*weight*/ 20, /*bag rarity*/ 0, 1); // 0 common,1 uncommon,2 rare
ThaumcraftApi.registerSeed(myCropBlock, mySeedStack); // growth-lamp support
ThaumcraftApi.registerScanEventhandler(myScanHandler);// react to thaumometer scans
ThaumcraftApi.registerEntityTag("modid:mob", aspects);// aspects for a mob
```

---

## 6. Interfaces you can implement

| Interface (`thaumcraft.api…`) | Put it on | Effect |
|---|---|---|
| `wands.IWandFocus`, `wands.ItemFocusBasic` | a focus item | custom wand focus |
| `wands.IWandable` | a block/tile | react to being right-clicked with a wand |
| `wands.IWandRodOnUpdate`, `IWandTriggerManager` | wand parts | rod tick / trigger hooks |
| `nodes.INode` | a tile | acts as an aura node |
| `nodes.IRevealer` / `items.IRevealer` | armor/item | reveal nodes/aspects when worn/held |
| `aspects.IAspectContainer` | a tile | stores essentia (jars, alembics) |
| `aspects.IEssentiaTransport` | a tile | pipes essentia |
| `aspects.IEssentiaContainerItem` | an item | carries essentia in NBT |
| `items.IGoggles` (`IGoggles`) | headgear | goggles-of-revealing behaviour |
| `IVisDiscountGear` (`items.IVisDiscountGear`) | armor | reduces wand vis cost |
| `IWarpingGear` | armor | applies warp while worn |
| `IRunicArmor` / `ItemRunic` | armor | runic shielding |
| `IScribeTools` | an item | usable as scribing tools |
| `IArchitect` | an item | shows placement guides |
| `IRepairable` / `IRepairableExtended` | a tool | thaumium-restore / repair enchant |

---

## 7. Events

- `aspects.AspectRegistryEvent` — TC6-compat aspect/tag registration (`register` proxy).
- `aspects.AspectRegistryEvent` is a Forge `Event`; subscribe on the event bus.
- `research.IScanEventHandler` — register with `ThaumcraftApi.registerScanEventhandler`.

---

## 8. Stability

The public method signatures in `thaumcraft.api.ThaumcraftApi`,
`aura.AuraHelper`, and `aspects.AspectSourceHelper` are covered by
`ThaumcraftApiSurfaceStaticGuardTest` so addon builds don't break silently
across versions. If you rely on other API classes and want them guarded, open an
issue. Internal (`thaumcraft.common.*` / `thaumcraft.client.*`) classes are **not**
API — do not compile against them.
