# Каталог объектов Thaumic Tinkerer 1.7.10 — исходные значения

> **Это справочник, а не план.** Каждая строка ниже выписана дословно из
> оригинального исходника Thaumic Tinkerer (ветка `1.7.10`, локальная копия
> в `../tt-original-1.7.10`). Ничего здесь не придумано и не пересчитано.

## Как этим пользоваться

1. **Перед тем как портировать объект — найди его здесь.** Рецепт, аспекты,
   твёрдость, сопротивление, координаты исследования, родители — всё это уже
   извлечено.
2. **Если объекта здесь нет — открой оригинал**, а не придумывай по аналогии.
   Локальная копия исходников лежит в `C:/Users/gorba/tc4/tt-original-1.7.10`.
3. **Дописал объект — допиши строку сюда.** Каталог должен оставаться полным.

Обозначения в выписках ниже:

| Запись | Значит |
| --- | --- |
| `stack(...)` | `new ItemStack(...)` |
| `aspects` | `new AspectList()` |
| `<ClassName>` | `ThaumicTinkerer.registry.getFirstItemFromClass(ClassName.class)` |
| `"KEY"` | раскрытая константа `LibResearch.KEY_*` / `LibItemNames.*` / `LibBlockNames.*` |

Типы рецептов оригинала:

| Класс | Разворачивается в |
| --- | --- |
| `ThaumicTinkererArcaneRecipe(name, research, out, aspects, ...)` | `ThaumcraftApi.addArcaneCraftingRecipe` |
| `ThaumicTinkererInfusionRecipe(name[, research], out, instability, aspects, input, ...)` | `ThaumcraftApi.addInfusionCraftingRecipe` |
| `ThaumicTinkererCrucibleRecipe(name, out, in, aspects)` | `ThaumcraftApi.addCrucibleRecipe` |
| `ThaumicTinkererCraftingBenchRecipe(name, out, ...)` | обычный верстак, `GameRegistry.addRecipe` |
| `ThaumicTinkererRecipeMulti(...)` | несколько рецептов на один объект |

Когда в `ThaumicTinkererInfusionRecipe` два имени (`name`, `research`) — первое
это ключ рецепта в `ConfigResearch.recipes`, второе — исследование, к которому
он привязан. С одним именем оба совпадают.

---

## Ресурсы и базовые блоки

### `BlockDarkQuartz`

`common/block/quartz/BlockDarkQuartz.java` — extends `BlockMod`

**Имя регистрации:** `"darkQuartz"`

**Конструктор:**

```java
super(Material.rock);
setHardness(0.8F);
setResistance(10F);
```

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 1, stack(<BlockDarkQuartz>), "QQ", "QQ", 'Q', <ItemDarkQuartz>),
  new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 3, stack(<BlockDarkQuartz>, 2, 2), "Q", "Q", 'Q', <BlockDarkQuartz>),
  new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 4, stack(<BlockDarkQuartz>, 1, 1), "Q", "Q", 'Q', <BlockDarkQuartzSlab>) );
```

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
private static final String[] iconNames = new String[]{ "darkQuartz0", "chiseledDarkQuartz0", "pillarDarkQuartz0", null, null };
```

### `BlockDarkQuartzSlab`

`common/block/quartz/BlockDarkQuartzSlab.java` — extends `BlockSlab`  implements `ITTinkererBlock`

**Имя регистрации:** `field_150004_a ? "darkQuartzSlabFull" : "darkQuartzSlab"`

**Конструктор:**

```java
super(par2, Material.rock);
setHardness(0.8F);
setResistance(10F);
if (!par2) {
setLightOpacity(0);
setCreativeTab(ModCreativeTab.INSTANCE);
}
```

**Спецпараметры:** `ArrayList result = new ArrayList(); result.add(true); return result;`

**Рецепт:**

```java
if (isOpaqueCube()) { return null; } return new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 2,
  stack(<BlockDarkQuartzSlab>, 6),
  "QQQ",
  'Q',
  <BlockDarkQuartz>);
```

**Исследование:** нет (входит в чужую страницу).

### `BlockDarkQuartzStairs`

`common/block/quartz/BlockDarkQuartzStairs.java` — extends `BlockStairs`  implements `ITTinkererBlock`

**Имя регистрации:** `"darkQuartzStairs"`

**Конструктор:**

```java
super(ThaumicTinkerer.registry.getFirstBlockFromClass(BlockDarkQuartz.class), 0);
setCreativeTab(ModCreativeTab.INSTANCE);
```

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 5, stack(this, 4), " Q", " QQ", "QQQ", 'Q', <BlockDarkQuartz>),
  new ThaumicTinkererCraftingBenchRecipe("", stack(this, 4), "Q ", "QQ ", "QQQ", 'Q', <BlockDarkQuartz>) );
```

**Исследование:** нет (входит в чужую страницу).

### `ItemDarkQuartz`

`common/item/quartz/ItemDarkQuartz.java` — extends `ItemBase`

**Имя регистрации:** `"darkQuartzItem"`

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 0, stack(this, 8), "QQQ", "QCQ", "QQQ", 'Q', Items.quartz, 'C', Items.coal),
  new ThaumicTinkererCraftingBenchRecipe("DARK_QUARTZ" + 0, stack(this, 8), "QQQ", "QCQ", "QQQ", 'Q', Items.quartz, 'C', stack(Items.coal, 1, 1)) );
```

**Исследование:**

```java
IRegisterableResearch researchItem = (IRegisterableResearch) new TTResearchItem("DARK_QUARTZ",
  aspects,
  -2,
  2,
  0,
  stack(this),
  new ResearchPage("0"),
  ResearchHelper.recipePage("DARK_QUARTZ" + 0),
  ResearchHelper.recipePage("DARK_QUARTZ" + 1),
  ResearchHelper.recipePage("DARK_QUARTZ" + 2),
  ResearchHelper.recipePage("DARK_QUARTZ" + 3),
  ResearchHelper.recipePage("DARK_QUARTZ" + 4),
  ResearchHelper.recipePage("DARK_QUARTZ" + 5)) .setStub().setAutoUnlock().setRound().registerResearchItem(); return researchItem;
```

### `ItemDarkQuartzBlock`

`common/item/quartz/ItemDarkQuartzBlock.java` — extends `ItemMultiTexture`  implements `ITTinkererItem`

**Имя регистрации:** `"darkQuartz"`

**Конструктор:**

```java
super(ThaumicTinkerer.registry.getFirstBlockFromClass(BlockDarkQuartz.class), ThaumicTinkerer.registry.getFirstBlockFromClass(BlockDarkQuartz.class), new String[]{ "" });
```

**shouldRegister:** `return false;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `ItemDarkQuartzSlab`

`common/item/quartz/ItemDarkQuartzSlab.java` — extends `ItemSlab`  implements `ITTinkererItem`

**Имя регистрации:** `"darkQuartzSlab"`

**Конструктор:**

```java
super(par1, (BlockSlab) ThaumicTinkerer.registry.getFirstBlockFromClass(BlockDarkQuartzSlab.class), (BlockSlab) ThaumicTinkerer.registry.getBlockFromClass(BlockDarkQuartzSlab.class).get(1), false);
```

**shouldRegister:** `return false;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

---

## Механизмы (блоки)

### `BlockAnimationTablet`

`common/block/BlockAnimationTablet.java` — extends `BlockModContainer`

**Имя регистрации:** `"animationTablet"`

**Конструктор:**

```java
super(Material.iron);
setBlockBounds(0F, 0F, 0F, 1F, 1F / 16F * 2F, 1F);
setHardness(3F);
setResistance(50F);
setStepSound(soundTypeMetal);
random = new Random();
```

**Tile entity:** `TileAnimationTablet`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ANIMATION_TABLET",
  "ANIMATION_TABLET",
  stack(<BlockAnimationTablet>),
  aspects.add(Aspect.AIR, 25).add(Aspect.ORDER, 15).add(Aspect.FIRE, 10),
  "GIG",
  "ICI",
  'G',
  stack(Items.gold_ingot),
  'I',
  stack(Items.iron_ingot),
  'C',
  stack(ConfigItems.itemGolemCore, 1, 100));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("ANIMATION_TABLET",
  aspects.add(Aspect.MECHANISM, 2).add(Aspect.METAL, 1).add(Aspect.MOTION, 1).add(Aspect.ENERGY, 1),
  -8,
  2,
  4,
  stack(<BlockAnimationTablet>)).setParents("MAGNETS") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("ANIMATION_TABLET"));
```

### `BlockAspectAnalyzer`

`common/block/BlockAspectAnalyzer.java` — extends `BlockModContainer`

**Имя регистрации:** `"aspectAnalyzer"`

**Конструктор:**

```java
super(Material.wood);
setHardness(1.7F);
setResistance(1F);
setStepSound(Block.soundTypeWood);
random = new Random();
```

**Tile entity:** `TileBedrockPortal`

**shouldRegister:** `return Loader.isModLoaded("ComputerCraft");`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ASPECT_ANALYZER",
  "ASPECT_ANALYZER",
  stack(this),
  aspects.add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
  "TWT",
  "WMW",
  "TWT",
  'W',
  stack(ConfigBlocks.blockWoodenDevice, 1, 6),
  'M',
  stack(ConfigItems.itemThaumometer),
  'T',
  stack(ConfigItems.itemResource, 1, 2));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("ASPECT_ANALYZER",
  aspects.add(Aspect.MECHANISM, 2).add(Aspect.SENSES, 1).add(Aspect.MIND, 1),
  0,
  1,
  2,
  stack(this)).setParents("PERIPHERALS").setParentsHidden("GOGGLES",
  "THAUMIUM").setConcealed().setRound() .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("ASPECT_ANALYZER"));
```

### `BlockEnchanter`

`common/block/BlockEnchanter.java` — extends `BlockModContainer`

**Имя регистрации:** `"enchanter"`

**Конструктор:**

```java
super(Material.rock);
setBlockBounds(0F, 0F, 0F, 1F, 0.75F, 1F);
setHardness(5.0F);
setResistance(2000.0F);
random = new Random();
```

**Tile entity:** `TileEnchanter`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ENCHANTER",
  stack(this),
  15,
  aspects.add(Aspect.MAGIC, 50).add(Aspect.ENERGY, 20).add(Aspect.ELDRITCH, 20).add(Aspect.VOID, 20).add(Aspect.MIND, 10),
  stack(Blocks.enchanting_table),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1),
  stack(ConfigItems.itemResource, 1, 2),
  stack(ConfigItems.itemResource, 1, 2),
  stack(<ItemSpellCloth>));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("ENCHANTER",
  aspects.add(Aspect.MAGIC, 2).add(Aspect.AURA, 1).add(Aspect.ELDRITCH, 1).add(Aspect.DARKNESS, 1).add(Aspect.MIND, 1),
  5,
  4,
  5,
  stack(this)).setParents("SPELL_CLOTH") .setPages(new ResearchPage("0"),
  new ResearchPage("1"),
  new ResearchPage("2"),
  ResearchHelper.infusionPage("ENCHANTER"));
```

### `BlockForcefield`

`common/block/BlockForcefield.java` — extends `BlockMod`

**Имя регистрации:** `"forcefield"`

**Конструктор:**

```java
super(Material.air);
```

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `BlockFunnel`

`common/block/BlockFunnel.java` — extends `BlockModContainer`

**Имя регистрации:** `"funnel"`

**Конструктор:**

```java
super(Material.rock);
setHardness(3.0F);
setResistance(8.0F);
setStepSound(Block.soundTypeStone);
setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F);
random = new Random();
```

**Tile entity:** `TileFunnel`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("FUNNEL",
  "FUNNEL",
  stack(this),
  aspects.add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
  "STS",
  'S',
  stack(Blocks.stone),
  'T',
  stack(ConfigItems.itemResource, 1, 2));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("FUNNEL",
  aspects.add(Aspect.TOOL, 1).add(Aspect.TRAVEL, 2),
  0,
  -7,
  1,
  stack(this)).setParentsHidden("DISTILESSENTIA").setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("FUNNEL")).setSecondary();
```

### `BlockGas`

`common/block/BlockGas.java` — extends `BlockMod`  implements `ITTinkererBlock`

**Конструктор:**

```java
super(Material.air);
setBlockBounds(0, 0, 0, 0, 0, 0);
setTickRandomly(true);
```

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `BlockGolemConnector`

`common/block/BlockGolemConnector.java` — extends `BlockCamo`

**Имя регистрации:** `"golemConnector"`

**Конструктор:**

```java
super(Material.wood);
```

**Tile entity:** `TileGolemConnector`

**shouldRegister:** `return Loader.isModLoaded("ComputerCraft");`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("GOLEM_CONNECTOR",
  "GOLEM_CONNECTOR",
  stack(this),
  aspects.add(Aspect.AIR, 20).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 15),
  "WFW",
  "sIs",
  "WFW",
  'I',
  stack(ConfigItems.itemGolemBell),
  's',
  stack(Items.ender_pearl),
  'W',
  stack(ConfigBlocks.blockMagicalLog),
  'F',
  stack(Blocks.redstone_block));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("GOLEM_CONNECTOR",
  aspects.add(Aspect.ORDER, 1).add(Aspect.TRAVEL, 2).add(Aspect.TOOL, 1),
  1,
  0,
  0,
  stack(this)).setParents("PERIPHERALS").setParentsHidden("GOLEMBELL").setConcealed().setRound() .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("GOLEM_CONNECTOR"),
  new ResearchPage("1"),
  ResearchHelper.arcaneRecipePage("INTERFACE" + "1"),
  new ResearchPage("2"),
  new ResearchPage("3"));
```

### `BlockInfusedGrain`

`common/block/BlockInfusedGrain.java` — extends `BlockCrops`  implements `ITTinkererBlock`

**Имя регистрации:** `if (aspect == Aspect.AIR) { "INFUSED_GRAIN_BASE + "Air""; } if (aspect == Aspect.EARTH) { "INFUSED_GRAIN_BASE + "Earth""; } if (aspect == Aspect.WATER) { "INFUSED_GRAIN_BASE + "Water""; } "INFUSED_GRAIN_BASE + "Fire""`

**Конструктор:**

```java
super();
this.aspect = aspect;
```

**Спецпараметры:** `ArrayList<Object> result = new ArrayList<Object>(); result.add(Aspect.WATER); result.add(Aspect.AIR); result.add(Aspect.EARTH); return result;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `BlockMagnet`

`common/block/BlockMagnet.java` — extends `BlockModContainer`  implements `IMultiTileEntityBlock`

**Имя регистрации:** `"magnet"`

**Конструктор:**

```java
super(Material.iron);
setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 1F / 16F * 2F, 0.9375F);
setHardness(1.7F);
setResistance(1F);
setStepSound(Block.soundTypeWood);
random = new Random();
```

**Tile entity:** `TileMagnet`

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererArcaneRecipe("MAGNET", "MAGNETS", stack(this), aspects.add(Aspect.AIR, 20).add(Aspect.ORDER, 5).add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 5), " I ", "SIs", "WFW", 'I', stack(Items.iron_ingot), 's', stack(ConfigItems.itemShard, 1, 3), 'S', stack(ConfigItems.itemShard), 'W', stack(ConfigBlocks.blockMagicalLog), 'F', stack(<ItemFocusTelekinesis>)),
  new ThaumicTinkererArcaneRecipe("MOB_MAGNET", "MAGNETS", stack(this, 1, 1), aspects.add(Aspect.AIR, 20).add(Aspect.ORDER, 5).add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 5), " G ", "SGs", "WFW", 'G', RecipeHelper.oreDictOrStack(stack(Items.gold_ingot), "ingotCopper"), 's', stack(ConfigItems.itemShard, 1, 3), 'S', stack(ConfigItems.itemShard), 'W', stack(ConfigBlocks.blockMagicalLog), 'F', stack(<ItemFocusTelekinesis>)) );
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("MAGNETS",
  aspects.add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1),
  -6,
  3,
  3,
  stack(this)).setParents("INTERFACE").setConcealed() .setPages(new ResearchPage("0"),
  new ResearchPage("1"),
  ResearchHelper.arcaneRecipePage("MAGNET"),
  ResearchHelper.arcaneRecipePage("MOB_MAGNET"),
  ResearchHelper.crucibleRecipePage("MAGNETS"));
```

### `BlockPlatform`

`common/block/BlockPlatform.java` — extends `BlockCamo`

**Имя регистрации:** `"platform"`

**Конструктор:**

```java
super(Material.wood);
setHardness(2.0F);
setResistance(5.0F);
setStepSound(Block.soundTypeWood);
```

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("PLATFORM",
  "PLATFORM",
  stack(this, 2),
  aspects.add(Aspect.AIR, 2).add(Aspect.ENTROPY, 4),
  " S ",
  "G G",
  'G',
  stack(ConfigBlocks.blockWoodenDevice, 1, 6),
  'S',
  stack(ConfigBlocks.blockWoodenDevice, 1, 7));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("PLATFORM",
  aspects.add(Aspect.SENSES, 2).add(Aspect.TREE, 1).add(Aspect.MOTION, 1),
  -2,
  6,
  3,
  stack(this)).setConcealed().setParents("CLEANSING_TALISMAN") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("PLATFORM")).setSecondary();
```

### `BlockRPlacer`

`common/block/BlockRPlacer.java` — extends `BlockCamo`  implements `IWandable`

**Имя регистрации:** `"remotePlacer"`

**Конструктор:**

```java
super(Material.rock);
```

**Tile entity:** `TileRPlacer`

**shouldRegister:** `return false;`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("REMOTE_PLACER",
  "REMOTE_PLACER",
  stack(this),
  aspects.add(Aspect.AIR, 20).add(Aspect.ORDER, 5).add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 5),
  "ses",
  "sds",
  "sss",
  's',
  ConfigBlocks.blockStoneDevice,
  'e',
  Items.ender_pearl,
  'd',
  Blocks.dispenser);
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("REMOTE_PLACER",
  aspects.add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1),
  -6,
  3,
  3,
  stack(this)).setParents("ANIMATION_TABLET").setConcealed() .setPages(new ResearchPage("0"),
  new ResearchPage("1"),
  ResearchHelper.arcaneRecipePage("REMOTE_PLACER"));
```

### `BlockRepairer`

`common/block/BlockRepairer.java` — extends `BlockModContainer`

**Имя регистрации:** `"repairer"`

**Конструктор:**

```java
super(Material.iron);
setHardness(5F);
setResistance(10F);
random = new Random();
```

**Tile entity:** `TileRepairer`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("REPAIRER",
  stack(this),
  8,
  aspects.add(Aspect.TOOL, 15).add(Aspect.CRAFT, 20).add(Aspect.ORDER, 10).add(Aspect.MAGIC, 15),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 4),
  stack(Items.iron_ingot),
  stack(Items.gold_ingot),
  stack(Items.diamond),
  stack(Blocks.cobblestone),
  stack(Blocks.planks),
  stack(Items.leather),
  stack(ConfigItems.itemResource, 1, 7),
  stack(ConfigItems.itemResource, 1, 2));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("REPAIRER",
  aspects.add(Aspect.TOOL, 2).add(Aspect.CRAFT, 1).add(Aspect.ORDER, 1).add(Aspect.MAGIC, 1),
  -1,
  -9,
  3,
  stack(this)).setConcealed().setParents("FUNNEL").setParentsHidden("THAUMIUM",
  "ENCHFABRIC") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("REPAIRER"));
```

### `BlockSummon`

`common/block/BlockSummon.java` — extends `Block`  implements `ITTinkererBlock`

**Имя регистрации:** `"spawner"`

**Конструктор:**

```java
super(Material.iron);
setBlockBounds(0F, 0F, 0F, 1F, 1F / 16F * 2F, 1F);
setHardness(3F);
setResistance(50F);
setStepSound(Block.soundTypeMetal);
random = new Random();
```

**Tile entity:** `TileSummon`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("SUMMON" + "0",
  "SUMMON",
  stack(<BlockSummon>),
  aspects.add(Aspect.ORDER, 50).add(Aspect.ENTROPY, 50),
  "WWW",
  "SSS",
  'S',
  stack(Blocks.stone),
  'W',
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("SUMMON",
  aspects.add(Aspect.WEAPON, 1).add(Aspect.BEAST, 3).add(Aspect.MAGIC, 3),
  -5,
  8,
  3,
  stack(<BlockSummon>)).setParents("BLOOD_SWORD") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("SUMMON" + "0"),
  ResearchHelper.recipePage("SUMMON" + "1"),
  ResearchHelper.infusionPage("SUMMON"),
  new ResearchPage("1"));
```

### `BlockMobilizer`

`common/block/mobilizer/BlockMobilizer.java` — extends `BlockMod`

**Имя регистрации:** `"Levitational Locomotive"`

**Конструктор:**

```java
super(Material.iron);
```

**Tile entity:** `TileEntityMobilizer`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("LEVITATOR",
  stack(this),
  4,
  aspects.add(Aspect.MOTION, 15).add(Aspect.ORDER, 20).add(Aspect.MAGIC, 15),
  stack(ConfigBlocks.blockLifter),
  stack(Items.iron_ingot),
  stack(Items.feather),
  stack(Items.iron_ingot),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("LEVITATOR",
  aspects.add(Aspect.MOTION, 2).add(Aspect.ORDER, 2),
  -7,
  5,
  3,
  stack(this)).setParents("MAGNETS") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("LEVITATOR"),
  ResearchHelper.arcaneRecipePage("LEVITATOR_RELAY")).setSecondary();
```

### `BlockMobilizerRelay`

`common/block/mobilizer/BlockMobilizerRelay.java` — extends `BlockMod`

**Имя регистрации:** `"Levitational Locomotive Relay"`

**Конструктор:**

```java
super(Material.iron);
```

**Tile entity:** `TileEntityRelay`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("LEVITATOR_RELAY",
  "LEVITATOR",
  stack(this),
  aspects.add(Aspect.AIR, 20).add(Aspect.ORDER, 5).add(Aspect.EARTH, 15),
  "WFW",
  "SIs",
  "WFW",
  'I',
  stack(Items.iron_ingot),
  's',
  stack(ConfigItems.itemShard, 1, 3),
  'S',
  stack(ConfigItems.itemShard),
  'W',
  stack(ConfigBlocks.blockMagicalLog),
  'F',
  stack(Blocks.glass));
```

**Исследование:** нет (входит в чужую страницу).

### `BlockTransvectorDislocator`

`common/block/transvector/BlockTransvectorDislocator.java` — extends `BlockCamo`

**Имя регистрации:** `"dislocator"`

**Конструктор:**

```java
super(Material.iron);
setHardness(3F);
setResistance(10F);
```

**Tile entity:** `TileTransvectorDislocator`

**Рецепт:**

```java
if (!Config.allowMirrors) { return null; } return new ThaumicTinkererArcaneRecipe("DISLOCATOR",
  "DISLOCATOR",
  stack(this),
  aspects.add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5),
  " M ",
  " I ",
  " C ",
  'M',
  stack(ConfigItems.itemResource, 1, 10),
  'I',
  stack(<BlockTransvectorInterface>),
  'C',
  stack(Items.comparator));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (IRegisterableResearch) new TTResearchItem("DISLOCATOR",
  aspects.add(Aspect.TRAVEL, 2).add(Aspect.MECHANISM, 1).add(Aspect.ELDRITCH, 1),
  -6,
  1,
  3,
  stack(this)).setConcealed().setParents("INTERFACE").setParentsHidden("MIRROR") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("DISLOCATOR")).setSecondary();
```

### `BlockTransvectorInterface`

`common/block/transvector/BlockTransvectorInterface.java` — extends `BlockCamo`

**Имя регистрации:** `"interface"`

**Конструктор:**

```java
super(Material.iron);
setHardness(3F);
setResistance(10F);
```

**Tile entity:** `TileTransvectorInterface`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("INTERFACE",
  "INTERFACE",
  stack(<BlockTransvectorInterface>),
  aspects.add(Aspect.ORDER, 12).add(Aspect.ENTROPY, 16),
  "BRB",
  "LEL",
  "BRB",
  'B',
  stack(ConfigBlocks.blockCosmeticSolid, 1, 6),
  'E',
  stack(Items.ender_pearl),
  'L',
  stack(Items.dye, 1, 4),
  'R',
  stack(Items.redstone));
```

**Исследование:**

```java
return (IRegisterableResearch) new TTResearchItem("INTERFACE",
  aspects.add(Aspect.ENTROPY, 4).add(Aspect.ORDER, 4),
  -4,
  2,
  1,
  stack(<BlockTransvectorInterface>)).setParents("DARK_QUARTZ") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("INTERFACE"),
  new ResearchPage("1"),
  ResearchHelper.arcaneRecipePage("INTERFACE" + "1"),
  new ResearchPage("2"));
```

---

## Элементальные костры

### `BlockFireAir`

`common/block/fire/BlockFireAir.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireAir"`

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_AER",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.AIR, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_AER",
  aspects.add(Aspect.FIRE, 5).add(Aspect.AIR, 5),
  3,
  -7,
  2,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_AER")).setSecondary();
```

### `BlockFireChaos`

`common/block/fire/BlockFireChaos.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireChaos"`

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_PERDITIO",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_PERDITIO",
  aspects.add(Aspect.FIRE, 5).add(Aspect.ENTROPY, 5),
  2,
  -8,
  3,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_PERDITIO")).setSecondary();
```

### `BlockFireEarth`

`common/block/fire/BlockFireEarth.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireEarth"`

**Конструктор:**

```java
super();
```

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_TERRA",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.EARTH, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_TERRA",
  aspects.add(Aspect.FIRE, 5).add(Aspect.EARTH, 5),
  4,
  -6,
  2,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_TERRA")).setSecondary();
```

### `BlockFireIgnis`

`common/block/fire/BlockFireIgnis.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireFire"`

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_IGNIS",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 10).add(Aspect.AIR, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_IGNIS",
  aspects.add(Aspect.FIRE, 10),
  4,
  -4,
  2,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_IGNIS")).setSecondary();
```

### `BlockFireOrder`

`common/block/fire/BlockFireOrder.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireOrder"`

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_ORDO",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.ORDER, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_ORDO",
  aspects.add(Aspect.FIRE, 5).add(Aspect.ORDER, 5),
  3,
  -3,
  2,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_ORDO")).setSecondary();
```

### `BlockFireWater`

`common/block/fire/BlockFireWater.java` — extends `BlockFireBase`

**Имя регистрации:** `"fireWater"`

**Конструктор:**

```java
super();
```

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("FIRE_AQUA",
  stack(this),
  stack(<ItemBrightNitor>),
  aspects.add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.WATER, 5));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FIRE_AQUA",
  aspects.add(Aspect.FIRE, 5).add(Aspect.WATER, 5),
  2,
  -2,
  2,
  stack(this)).setParents("BRIGHT_NITOR").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("FIRE_AQUA")).setSecondary();
```

---

## Трансвекторы

### `ItemConnector`

`common/item/ItemConnector.java` — extends `ItemBase`

**Имя регистрации:** `"connector"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("INTERFACE" + "1",
  "INTERFACE",
  stack(this),
  aspects.add(Aspect.ORDER, 2),
  " I ",
  " WI",
  "S ",
  'I',
  stack(Items.iron_ingot),
  'W',
  stack(Items.stick),
  'S',
  stack(ConfigItems.itemShard, 1, 4));
```

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
private static final String TAG_POS_X = "posx";
private static final String TAG_POS_Y = "posy";
private static final String TAG_POS_Z = "posz";
private static final String TAG_CONNECTING_GOLEM = "ConnectingGolem";
```

---

## Фокусы палочки

### `ItemFocusDeflect`

`common/item/foci/ItemFocusDeflect.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusDeflect"`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_DEFLECT",
  stack(this),
  5,
  aspects.add(Aspect.AIR, 15).add(Aspect.ARMOR, 5).add(Aspect.ORDER, 20),
  stack(<ItemFocusFlight>),
  stack(ConfigItems.itemResource, 1, 10),
  stack(ConfigItems.itemResource, 1, 10),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 3),
  stack(ConfigItems.itemShard, 1, 4));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (TTResearchItem) new TTResearchItem("FOCUS_DEFLECT",
  aspects.add(Aspect.MOTION, 2).add(Aspect.AIR, 1).add(Aspect.ORDER, 1).add(Aspect.DEATH, 1),
  -4,
  -3,
  3,
  stack(this)).setConcealed().setParents("FOCUS_SMELT") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_DEFLECT")).setSecondary();
```

### `ItemFocusDislocation`

`common/item/foci/ItemFocusDislocation.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusDislocation"`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_DISLOCATION",
  stack(this),
  8,
  aspects.add(Aspect.ELDRITCH, 20).add(Aspect.DARKNESS, 10).add(Aspect.VOID, 25).add(Aspect.MAGIC, 20).add(Aspect.TAINT, 5),
  stack(Items.ender_pearl),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(ConfigItems.itemResource, 1, 6),
  stack(ConfigItems.itemResource, 1, 6),
  stack(ConfigItems.itemResource, 1, 6),
  stack(Items.diamond));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (TTResearchItem) new TTResearchItem("FOCUS_DISLOCATION",
  aspects.add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1),
  -5,
  -5,
  2,
  stack(this)).setSecondary().setParents("FOCUS_FLIGHT").setConcealed() .setPages(new ResearchPage("0"),
  new ResearchPage("1"),
  ResearchHelper.infusionPage("FOCUS_DISLOCATION"));
```

**Константы:**

```java
private static final String TAG_AVAILABLE = "available";
private static final String TAG_TILE_CMP = "tileCmp";
private static final String TAG_BLOCK_ID = "blockID";
private static final String TAG_BLOCK_NAME = "blockName";
private static final String TAG_BLOCK_META = "blockMeta";
private static final AspectList visUsage = new AspectList().add(Aspect.ENTROPY, 500).add(Aspect.ORDER, 500).add(Aspect.EARTH, 100);
private static final AspectList visUsageTile = new AspectList().add(Aspect.ENTROPY, 2500).add(Aspect.ORDER, 2500).add(Aspect.EARTH, 500);
private static final AspectList visUsageSpawner = new AspectList().add(Aspect.ENTROPY, 10000).add(Aspect.ORDER, 10000).add(Aspect.EARTH, 5000);
```

### `ItemFocusEnderChest`

`common/item/foci/ItemFocusEnderChest.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusEnderChest"`

**Рецепт:**

```java
if (Config.allowMirrors) { return new ThaumicTinkererArcaneRecipe("FOCUS_ENDER_CHEST",
  "FOCUS_ENDER_CHEST",
  stack(this),
  aspects.add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
  "M",
  "E",
  "P",
  'M',
  stack(ConfigBlocks.blockMirror),
  'E',
  stack(Items.ender_eye),
  'P',
  stack(ConfigItems.itemFocusPortableHole)); } return null;
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } IRegisterableResearch research = (TTResearchItem) new TTResearchItem("FOCUS_ENDER_CHEST",
  aspects.add(Aspect.ELDRITCH, 2).add(Aspect.VOID, 1).add(Aspect.MAGIC, 1),
  -6,
  -2,
  2,
  stack(this)).setParents("FOCUS_DEFLECT").setConcealed(); if (Loader.isModLoaded("EnderStorage")) { ((TTResearchItem) research).setPages(new ResearchPage("ES"),
  ResearchHelper.arcaneRecipePage("FOCUS_ENDER_CHEST")); } else { ((TTResearchItem) research).setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("FOCUS_ENDER_CHEST")); } return research;
```

**Константы:**

```java
public static final AspectList visUsage = new AspectList().add(Aspect.ENTROPY, 100).add(Aspect.ORDER, 100);
```

### `ItemFocusFlight`

`common/item/foci/ItemFocusFlight.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusFlight"`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_FLIGHT",
  stack(this),
  3,
  aspects.add(Aspect.AIR, 15).add(Aspect.MOTION, 20).add(Aspect.TRAVEL, 10),
  stack(Items.ender_pearl),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.feather),
  stack(Items.feather),
  stack(ConfigItems.itemShard, 1, 0));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FOCUS_FLIGHT",
  aspects.add(Aspect.MOTION, 1).add(Aspect.MAGIC, 1).add(Aspect.AIR, 2),
  -3,
  -4,
  2,
  stack(this)).setParents("FOCUS_SMELT").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_FLIGHT"));
```

**Константы:**

```java
private static final AspectList visUsage = new AspectList().add(Aspect.AIR, 15);
```

### `ItemFocusHeal`

`common/item/foci/ItemFocusHeal.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusHeal"`

**Конструктор:**

```java
super();
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_HEAL",
  stack(this),
  4,
  aspects.add(Aspect.HEAL, 10).add(Aspect.SOUL, 10).add(Aspect.LIFE, 15),
  stack(ConfigItems.itemFocusPech),
  stack(Items.golden_carrot),
  stack(Items.gold_nugget),
  stack(Items.gold_nugget),
  stack(Items.gold_nugget));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (TTResearchItem) new TTResearchItem("FOCUS_HEAL",
  aspects.add(Aspect.HEAL, 2).add(Aspect.SOUL, 1).add(Aspect.MAGIC, 1),
  -6,
  -4,
  2,
  stack(this)).setParents("FOCUS_DEFLECT").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_HEAL")).setSecondary();
```

**Константы:**

```java
private static final AspectList visUsage = new AspectList().add(Aspect.EARTH, 45).add(Aspect.WATER, 45);
```

### `ItemFocusSmelt`

`common/item/foci/ItemFocusSmelt.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusSmelt"`

**Конструктор:**

```java
super();
```

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("FOCUS_SMELT",
  "FOCUS_SMELT",
  stack(this),
  aspects.add(Aspect.FIRE, 10).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 6),
  "FNE",
  'F',
  stack(ConfigItems.itemFocusFire),
  'E',
  stack(ConfigItems.itemFocusExcavation),
  'N',
  stack(ConfigItems.itemResource, 1, 1));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FOCUS_SMELT",
  aspects.add(Aspect.FIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.MAGIC, 1),
  -2,
  -2,
  2,
  stack(this)).setParents("FOCUSEXCAVATION").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("FOCUS_SMELT"));
```

**Константы:**

```java
private static final AspectList visUsage = new AspectList().add(Aspect.FIRE, 45).add(Aspect.ENTROPY, 12);
```

### `ItemFocusTelekinesis`

`common/item/foci/ItemFocusTelekinesis.java` — extends `ItemModFocus`

**Имя регистрации:** `"focusTelekinesis"`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_TELEKINESIS",
  stack(this),
  5,
  aspects.add(Aspect.MOTION, 10).add(Aspect.AIR, 20).add(Aspect.ENTROPY, 20).add(Aspect.MIND, 10),
  stack(Items.ender_pearl),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.quartz),
  stack(Items.iron_ingot),
  stack(Items.gold_ingot),
  stack(ConfigItems.itemShard, 1, 0));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("FOCUS_TELEKINESIS",
  aspects.add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.MOTION, 1),
  -4,
  -6,
  2,
  stack(this)).setParents("FOCUS_FLIGHT").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_TELEKINESIS")).setSecondary();
```

**Константы:**

```java
private static final AspectList visUsage = new AspectList().add(Aspect.AIR, 5).add(Aspect.ENTROPY, 5);
```

### `ItemModFocus`

`common/item/foci/ItemModFocus.java` — extends `ItemBase`  implements `IWandFocus`

**Конструктор:**

```java
super();
setMaxDamage(1);
setNoRepair();
setMaxStackSize(1);
```

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

---

## Предметы

### `ItemBlockFire`

`common/item/ItemBlockFire.java` — extends `ItemBlock`  implements `ITTinkererItem`

**Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`

**Конструктор:**

```java
super(block);
```

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `ItemBlockMagnet`

`common/item/ItemBlockMagnet.java` — extends `ItemBlock`  implements `ITTinkererItem`

**Имя регистрации:** `"magnet"`

**Конструктор:**

```java
super(block);
setHasSubtypes(true);
```

**shouldRegister:** `return false;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `ItemBloodSword`

`common/item/ItemBloodSword.java` — extends `ItemSword`  implements `IRepairable, ITTinkererItem`

**Имя регистрации:** `"bloodSword"`

**Конструктор:**

```java
super(EnumHelper.addToolMaterial("TT_BLOOD", 0, 950, 0, 0, ThaumcraftApi.toolMatThaumium.getEnchantability()));
MinecraftForge.EVENT_BUS.register(this);
setCreativeTab(ModCreativeTab.INSTANCE);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("BLOOD_SWORD",
  stack(this),
  6,
  aspects.add(Aspect.HUNGER, 20).add(Aspect.DARKNESS, 5).add(Aspect.SOUL, 10).add(Aspect.MAN, 6),
  stack(ConfigItems.itemSwordThaumium),
  stack(Items.rotten_flesh),
  stack(Items.porkchop),
  stack(Items.beef),
  stack(Items.bone),
  stack(Items.diamond),
  stack(Items.ghast_tear));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("BLOOD_SWORD",
  aspects.add(Aspect.HUNGER, 2).add(Aspect.WEAPON, 1).add(Aspect.FLESH, 1).add(Aspect.SOUL, 1),
  -4,
  6,
  3,
  stack(this)).setParents("CLEANSING_TALISMAN") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("BLOOD_SWORD"),
  new ResearchPage("1")).setSecondary();
```

**Константы:**

```java
private static final int DAMAGE = 10;
```

### `ItemBrightNitor`

`common/item/ItemBrightNitor.java` — extends `ItemBase`

**Имя регистрации:** `"brightNitor"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("BRIGHT_NITOR",
  stack(this),
  stack(ConfigItems.itemResource, 1, 1),
  aspects.add(Aspect.ENERGY, 25).add(Aspect.LIGHT, 25).add(Aspect.AIR, 10).add(Aspect.FIRE, 10));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("BRIGHT_NITOR",
  aspects.add(Aspect.LIGHT, 2).add(Aspect.FIRE, 1).add(Aspect.ENERGY, 1).add(Aspect.AIR, 1),
  1,
  -5,
  2,
  stack(this)).setParents("GASEOUS_LIGHT").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("BRIGHT_NITOR")).setSecondary();
```

### `ItemCleansingTalisman`

`common/item/ItemCleansingTalisman.java` — extends `ItemBase`  implements `IBauble`

**Имя регистрации:** `"cleansingTalisman"`

**Конструктор:**

```java
setMaxStackSize(1);
setMaxDamage("100");
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("CLEANSING_TALISMAN",
  stack(this),
  5,
  aspects.add(Aspect.HEAL, 10).add(Aspect.TOOL, 10).add(Aspect.MAN, 20).add(Aspect.LIFE, 10),
  stack(Items.ender_pearl),
  stack(<ItemDarkQuartz>),
  stack(<ItemDarkQuartz>),
  stack(<ItemDarkQuartz>),
  stack(<ItemDarkQuartz>),
  stack(Items.ghast_tear),
  stack(ConfigItems.itemResource, 1, 1));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("CLEANSING_TALISMAN",
  aspects.add(Aspect.HEAL, 2).add(Aspect.ORDER, 1).add(Aspect.POISON, 1),
  -3,
  4,
  3,
  stack(this)).setSecondary().setParents("DARK_QUARTZ") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("CLEANSING_TALISMAN"));
```

**Константы:**

```java
private static final String TAG_ENABLED = "enabled";
```

### `ItemGas`

`common/item/ItemGas.java` — extends `ItemBase`

**Имя регистрации:** `setBlock == <BlockGaseousShadow> ? "gaseousShadowItem" : "gaseousLightItem"`

**Конструктор:**

```java
super();
this.setBlock = setBlock;
```

**Спецпараметры:** `ArrayList<Object> result = new ArrayList<Object>(); result.add(ThaumicTinkerer.registry.getFirstBlockFromClass(BlockGaseousLight.class)); return result;`

**Рецепт:**

```java
if (setBlock == <BlockGaseousLight>) { return new ThaumicTinkererCrucibleRecipe("GASEOUS_LIGHT",
  stack(this),
  stack(ConfigItems.itemEssence, 1, 0),
  aspects.add(Aspect.LIGHT, 16).add(Aspect.AIR, 10).add(Aspect.MOTION, 8)); } if (setBlock == <BlockGaseousShadow>) { return new ThaumicTinkererCrucibleRecipe("GASEOUS_SHADOW",
  stack(this),
  stack(ConfigItems.itemEssence, 1, 0),
  aspects.add(Aspect.DARKNESS, 16).add(Aspect.AIR, 10).add(Aspect.MOTION, 8)); } return null;
```

**Исследование:**

```java
if (setBlock == <BlockGaseousShadow>) { IRegisterableResearch research = (TTResearchItem) new TTResearchItem("GASEOUS_SHADOW",
  aspects.add(Aspect.DARKNESS, 2).add(Aspect.AIR, 1).add(Aspect.MOTION, 4),
  -1,
  -5,
  2,
  stack(this)).setSecondary().setParents("GASEOUS_LIGHT") .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("GASEOUS_SHADOW")); return research; } if (setBlock == <BlockGaseousLight>) { IRegisterableResearch research = (TTResearchItem) new TTResearchItem("GASEOUS_LIGHT",
  aspects.add(Aspect.LIGHT, 2).add(Aspect.AIR, 1),
  0,
  -3,
  1,
  stack(this)).setParents("NITOR") .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("GASEOUS_LIGHT")); return research; } return null;
```

### `ItemGasRemover`

`common/item/ItemGasRemover.java` — extends `ItemBase`

**Имя регистрации:** `"gasRemover"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("GAS_REMOVER",
  "GAS_REMOVER",
  stack(this),
  aspects.add(Aspect.AIR, 2).add(Aspect.ORDER, 2),
  "DDD",
  "T G",
  "QQQ",
  'D',
  stack(<ItemDarkQuartz>),
  'T',
  stack(ThaumicTinkerer.registry.getItemFromClass(ItemGas.class).get(0)),
  'G',
  stack(ThaumicTinkerer.registry.getItemFromClass(ItemGas.class).get(1)),
  'Q',
  stack(Items.quartz));
```

**Исследование:**

```java
IRegisterableResearch research = (TTResearchItem) new TTResearchItem("GAS_REMOVER",
  aspects.add(Aspect.DARKNESS, 2).add(Aspect.LIGHT, 2),
  -2,
  -7,
  0,
  stack(this)).setRound() .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("GAS_REMOVER")).setParents("GASEOUS_SHADOW"); return research;
```

### `ItemInfusedGrain`

`common/item/ItemInfusedGrain.java` — extends `ItemBase`

**Имя регистрации:** `"infusedGrain"`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `ItemInfusedInkwell`

`common/item/ItemInfusedInkwell.java` — extends `ItemBase`  implements `IScribeTools`

**Имя регистрации:** `"infusedInkwell"`

**Конструктор:**

```java
super();
setMaxDamage(800);
maxStackSize = 1;
canRepair = true;
setHasSubtypes(false);
```

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti(new ThaumicTinkererCraftingBenchRecipe("INFUSED_INKWELL" + 0, stack(this), "QQQ", "QCQ", "QQQ", 'Q', stack(Items.dye, 1, 0), 'C', stack(this, 1, 32767)),
  new ThaumicTinkererInfusionRecipe("INFUSED_INKWELL", stack(this), 2, aspects.add(Aspect.VOID, 8).add(Aspect.DARKNESS, 8), stack(ConfigItems.itemInkwell), stack(ConfigItems.itemShard, 1, 0), stack(ConfigBlocks.blockJar), stack(ConfigItems.itemResource, 1, 3)) );
```

**Исследование:** нет (входит в чужую страницу).

### `ItemInfusedPotion`

`common/item/ItemInfusedPotion.java` — extends `ItemPotion`  implements `ITTinkererItem`

**Имя регистрации:** `"infusedPotion"`

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererCrucibleRecipe("INFUSED_POTIONS" + "POT0", stack(this, 1, 0), stack(<ItemInfusedGrain>, 1, 0), aspects.add(Aspect.AURA, 5).add(Aspect.AIR, 5)),
  new ThaumicTinkererCrucibleRecipe("INFUSED_POTIONS" + "POT1", stack(this, 1, 1), stack(<ItemInfusedGrain>, 1, 1), aspects.add(Aspect.AURA, 5).add(Aspect.FIRE, 5)),
  new ThaumicTinkererCrucibleRecipe("INFUSED_POTIONS" + "POT2", stack(this, 1, 2), stack(<ItemInfusedGrain>, 1, 2), aspects.add(Aspect.AURA, 5).add(Aspect.EARTH, 5)),
  new ThaumicTinkererCrucibleRecipe("INFUSED_POTIONS" + "POT3", stack(this, 1, 3), stack(<ItemInfusedGrain>, 1, 3), aspects.add(Aspect.AURA, 5).add(Aspect.WATER, 5)) );
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("INFUSED_POTIONS",
  aspects.add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5),
  7,
  -5,
  2,
  stack(this)).setParents("FIRE_PERDITIO",
  "FIRE_ORDO",
  "FIRE_IGNIS",
  "FIRE_TERRA",
  "FIRE_AER",
  "FIRE_AQUA").setParentsHidden("INFUSION").setConcealed() .setPages(new ResearchPage("0"),
  new ResearchPage("1"),
  ResearchHelper.infusionPage("INFUSED_POTIONS", 4),
  ResearchHelper.crucibleRecipePage("INFUSED_POTIONS" + "POT0"),
  ResearchHelper.crucibleRecipePage("INFUSED_POTIONS" + "POT1"),
  ResearchHelper.crucibleRecipePage("INFUSED_POTIONS" + "POT2"),
  ResearchHelper.crucibleRecipePage("INFUSED_POTIONS" + "POT3"));
```

### `ItemInfusedSeeds`

`common/item/ItemInfusedSeeds.java` — extends `ItemSeeds`  implements `ITTinkererItem`

**Имя регистрации:** `"infusedSeeds"`

**Конструктор:**

```java
super(Blocks.wheat, Blocks.farmland);
```

**Рецепт:**

```java
return new ThaumicTinkererRecipeMulti( new ThaumicTinkererInfusionRecipe("INFUSED_POTIONS" + 0, stack(this, 1, 0), 5, aspects.add(Aspect.CROP, 32).add(Aspect.HARVEST, 32), stack(Items.wheat_seeds), stack(ConfigItems.itemShard, 1, 0), stack(ConfigItems.itemShard, 1, 0), stack(ConfigItems.itemShard, 1, 0), stack(ConfigItems.itemShard, 1, 0)),
  new ThaumicTinkererInfusionRecipe("INFUSED_POTIONS" + 1, stack(this, 1, 1), 5, aspects.add(Aspect.CROP, 32).add(Aspect.HARVEST, 32), stack(Items.wheat_seeds), stack(ConfigItems.itemShard, 1, 1), stack(ConfigItems.itemShard, 1, 1), stack(ConfigItems.itemShard, 1, 1), stack(ConfigItems.itemShard, 1, 1)),
  new ThaumicTinkererInfusionRecipe("INFUSED_POTIONS" + 2, stack(this, 1, 2), 5, aspects.add(Aspect.CROP, 32).add(Aspect.HARVEST, 32), stack(Items.wheat_seeds), stack(ConfigItems.itemShard, 1, 3), stack(ConfigItems.itemShard, 1, 3), stack(ConfigItems.itemShard, 1, 3), stack(ConfigItems.itemShard, 1, 3)),
  new ThaumicTinkererInfusionRecipe("INFUSED_POTIONS" + 3, stack(this, 1, 3), 5, aspects.add(Aspect.CROP, 32).add(Aspect.HARVEST, 32), stack(Items.wheat_seeds), stack(ConfigItems.itemShard, 1, 2), stack(ConfigItems.itemShard, 1, 2), stack(ConfigItems.itemShard, 1, 2), stack(ConfigItems.itemShard, 1, 2)) );
```

**Исследование:** нет (входит в чужую страницу).

### `ItemMobAspect`

`common/item/ItemMobAspect.java` — extends `ItemBase`

**Имя регистрации:** `"mobAspect"`

**Конструктор:**

```java
super();
setMaxStackSize(16);
```

**Рецепт:**

```java
ThaumicTinkererRecipeMulti recipeMulti = new ThaumicTinkererRecipeMulti(); for (int i = 0; i < NumericAspectHelper.values.size(); i++) { ThaumcraftApi.registerObjectTag(stack(this, 1, i),
  new int[]{i},
  aspects.add(NumericAspectHelper.getAspect(i), 8)); recipeMulti.addRecipe(new ThaumicTinkererCraftingBenchRecipe("SUMMON" + "1", stack(this, 1, i + 20), "XXX", "XXX", "XXX", 'X', stack(this, 1, i))); ItemStack input = stack(this,
  1,
  i + 20); recipeMulti.addRecipe(new ThaumicTinkererInfusionRecipe("SUMMON", stack(this, 1, i + 40), 4, aspects.add(getAspect(stack(this, 1, i)), 10), input, new ItemStack[]{input, input, input, input, input, input, input, input})); } return recipeMulti;
```

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
public static final int aspectCount = 20;
```

### `ItemMobDisplay`

`common/item/ItemMobDisplay.java` — extends `ItemBase`

**Имя регистрации:** `"mobDisplay"`

**Конструктор:**

```java
super();
setHasSubtypes(true); // This allows the item to be marked as a metadata item.
setMaxDamage(0); // This makes it so your item doesn't have the damage bar at the bottom of its icon, when "damaged" similar to the Tools.
```

**shouldRegister:** `return false;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
public static final String TAG_TYPE = "type";
```

### `ItemRevealingHelm`

`common/item/ItemRevealingHelm.java` — extends `ItemArmor`  implements `IRepairable, IRevealer, IGoggles, IVisDiscountGear, ITTinkererItem`

**Имя регистрации:** `"revealingHelm"`

**Конструктор:**

```java
super(ThaumcraftApi.armorMatThaumium, 2, 0);
setMaxDamage(500);
setCreativeTab(ModCreativeTab.INSTANCE);
```

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("REVEALING_HELM",
  "REVEALING_HELM",
  stack(this),
  aspects.add(Aspect.EARTH, 5).add(Aspect.FIRE, 5).add(Aspect.WATER, 5).add(Aspect.AIR, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5),
  "GH",
  'G',
  stack(ConfigItems.itemGoggles),
  'H',
  stack(ConfigItems.itemHelmetThaumium));
```

**Исследование:**

```java
IRegisterableResearch research; research = (TTResearchItem) new TTResearchItem("REVEALING_HELM",
  aspects.add(Aspect.AURA, 2).add(Aspect.ARMOR, 1),
  0,
  0,
  1,
  stack(this)).setParents("GOGGLES").setParentsHidden("THAUMIUM"); ((TTResearchItem) research).setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("REVEALING_HELM")); return research;
```

### `ItemShareBook`

`common/item/ItemShareBook.java` — extends `ItemBase`

**Имя регистрации:** `"shareBook"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
if (ConfigHandler.enableSurvivalShareTome) { return new ThaumicTinkererCraftingBenchRecipe("SHARE_TOME",
  stack(this),
  " S ",
  "PTP",
  " P ",
  'S',
  stack(ConfigItems.itemInkwell),
  'T',
  stack(ConfigItems.itemThaumonomicon),
  'P',
  stack(Items.paper)); } return null;
```

**Исследование:**

```java
IRegisterableResearch research = (TTResearchItem) new TTResearchItem("SHARE_TOME",
  aspects,
  0,
  -1,
  0,
  stack(this)).setStub().setAutoUnlock().setRound(); if (ConfigHandler.enableSurvivalShareTome) ((TTResearchItem) research).setPages(new ResearchPage("0"),
  ResearchHelper.recipePage("SHARE_TOME")); else ((TTResearchItem) research).setPages(new ResearchPage("0")); return research;
```

**Константы:**

```java
private static final String TAG_PLAYER = "player";
private static final String NON_ASIGNED = "[none]";
```

### `ItemSoulMould`

`common/item/ItemSoulMould.java` — extends `ItemBase`

**Имя регистрации:** `"soulMould"`

**Конструктор:**

```java
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("MAGNETS",
  stack(this),
  stack(Items.ender_pearl),
  aspects.add(Aspect.BEAST, 4).add(Aspect.MIND, 8).add(Aspect.SENSES, 8));
```

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
private static final String TAG_PATTERN = "pattern";
```

### `ItemSpellCloth`

`common/item/ItemSpellCloth.java` — extends `ItemBase`

**Имя регистрации:** `"spellCloth"`

**Конструктор:**

```java
super();
setMaxDamage("35");
setMaxStackSize(1);
setNoRepair();
CraftingManager.getInstance().getRecipeList().add(new SpellClothRecipe(this));
```

**Рецепт:**

```java
return new ThaumicTinkererCrucibleRecipe("SPELL_CLOTH",
  stack(this),
  stack(ConfigItems.itemResource, 0, 7),
  aspects.add(Aspect.MAGIC, 10).add(Aspect.ENTROPY, 6).add(Aspect.EXCHANGE, 4));
```

**Исследование:**

```java
IRegisterableResearch research = (TTResearchItem) new TTResearchItem("SPELL_CLOTH",
  aspects.add(Aspect.MAGIC, 2).add(Aspect.CLOTH, 1),
  3,
  2,
  2,
  stack(this)).setParentsHidden("ENCHFABRIC") .setPages(new ResearchPage("0"),
  ResearchHelper.crucibleRecipePage("SPELL_CLOTH")); return research;
```

### `ItemXPTalisman`

`common/item/ItemXPTalisman.java` — extends `ItemBase`  implements `IBauble`

**Имя регистрации:** `"xpTalisman"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("XP_TALISMAN",
  stack(this),
  6,
  aspects.add(Aspect.GREED, 20).add(Aspect.EXCHANGE, 10).add(Aspect.BEAST, 10).add(Aspect.MECHANISM, 5),
  stack(Items.gold_ingot),
  stack(Items.quartz),
  stack(<ItemDarkQuartz>),
  stack(ConfigItems.itemResource, 1, 5),
  stack(Items.diamond));
```

**Исследование:**

```java
return (TTResearchItem) new TTResearchItem("XP_TALISMAN",
  aspects.add(Aspect.GREED, 1).add(Aspect.MAGIC, 1).add(Aspect.MAN, 1),
  4,
  -1,
  2,
  stack(this, 1, 1)).setParents("JARBRAIN",
  "SPELL_CLOTH").setConcealed() .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("XP_TALISMAN")).setSecondary();
```

**Константы:**

```java
private static final String TAG_XP = "xp";
```

---

## KAMI — ресурсы и предметы

### `BlockBedrockPortal`

`common/block/kami/BlockBedrockPortal.java` — extends `BlockMod`

**Имя регистрации:** `"bedrockPortal"`

**Конструктор:**

```java
super(Material.portal);
setStepSound(Block.soundTypeStone);
setResistance(6000000.0F);
disableStats();
setCreativeTab(ModCreativeTab.INSTANCE);
```

**Tile entity:** `TileBedrockPortal`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `BlockWarpGate`

`common/block/kami/BlockWarpGate.java` — extends `BlockModContainer`

**Имя регистрации:** `"warpGate"`

**Конструктор:**

```java
super(Material.rock);
setHardness(5.0F);
setResistance(2000.0F);
random = new Random();
```

**Tile entity:** `TileWarpGate`

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
if (!Config.allowMirrors) { return null; } return new ThaumicTinkererInfusionRecipe("WARP_GATE",
  stack(this),
  8,
  aspects.add(Aspect.TRAVEL, 64).add(Aspect.ELDRITCH, 50).add(Aspect.FLIGHT, 50),
  stack(ConfigBlocks.blockCosmeticSolid, 1, 2),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>, 1, 7),
  stack(<BlockTransvectorDislocator>),
  stack(<ItemKamiResource>, 1, 6),
  stack(Items.diamond),
  stack(Items.feather));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (IRegisterableResearch) new KamiResearchItem("WARP_GATE",
  aspects.add(Aspect.TRAVEL, 2).add(Aspect.ELDRITCH, 1).add(Aspect.FLIGHT, 1).add(Aspect.MECHANISM, 1),
  19,
  6,
  5,
  stack(this)).setParents("ICHORCLOTH_CHEST_GEM").setParentsHidden("ICHORCLOTH_BOOTS_GEM") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("WARP_GATE"),
  new ResearchPage("1"),
  ResearchHelper.infusionPage("SKY_PEARL"));
```

### `ItemBlockTalisman`

`common/item/kami/ItemBlockTalisman.java` — extends `ItemKamiBase`  implements `IBauble`

**Имя регистрации:** `"blockTalisman"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
setHasSubtypes(true);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("BLOCK_TALISMAN",
  stack(this),
  9,
  aspects.add(Aspect.VOID, 65).add(Aspect.DARKNESS, 32).add(Aspect.MAGIC, 50).add(Aspect.ELDRITCH, 32),
  stack(ConfigItems.itemFocusPortableHole),
  stack(<ItemKamiResource>),
  stack(Blocks.ender_chest),
  stack(Items.diamond),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemResource, 1, 11),
  stack(ConfigBlocks.blockJar, 1, 3));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("BLOCK_TALISMAN",
  aspects.add(Aspect.VOID, 2).add(Aspect.DARKNESS, 1).add(Aspect.ELDRITCH, 1).add(Aspect.MAGIC, 1),
  14,
  17,
  5,
  stack(this)).setParents("ICHOR_PICK_GEM",
  "ICHOR_SHOVEL_GEM") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("BLOCK_TALISMAN"));
```

**Константы:**

```java
private static final String TAG_BLOCK_ID = "blockID";
private static final String TAG_BLOCK_NAME = "blockName";
private static final String TAG_BLOCK_META = "blockMeta";
private static final String TAG_BLOCK_COUNT = "blockCount";
```

### `ItemBlockWarpGate`

`common/item/kami/ItemBlockWarpGate.java` — extends `ItemBlock`  implements `ITTinkererItem`

**Имя регистрации:** `"warpGate"`

**Конструктор:**

```java
super(par1);
```

**shouldRegister:** `return false;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

### `ItemCatAmulet`

`common/item/kami/ItemCatAmulet.java` — extends `ItemKamiBase`  implements `IBauble`

**Имя регистрации:** `"catAmulet"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("CAT_AMULET",
  stack(this),
  8,
  aspects.add(Aspect.DARKNESS, 16).add(Aspect.ORDER, 32).add(Aspect.MIND, 16),
  stack(Blocks.quartz_block),
  stack(<ItemKamiResource>),
  stack(Items.gold_ingot),
  stack(Items.gold_ingot),
  stack(Items.dye, 1, 3),
  stack(Blocks.leaves, 1, 3),
  stack(Items.fish));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("CAT_AMULET",
  aspects.add(Aspect.MIND, 2).add(Aspect.ORDER, 1).add(Aspect.DARKNESS, 1).add(Aspect.DEATH, 1),
  13,
  10,
  5,
  stack(this)).setParents("ICHORIUM") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("CAT_AMULET"));
```

### `ItemIchorPouch`

`common/item/kami/ItemIchorPouch.java` — extends `ItemFocusPouch`  implements `IBauble, ITTinkererItem`

**Имя регистрации:** `"ichorPouch"`

**Конструктор:**

```java
super();
setCreativeTab(ModCreativeTab.INSTANCE);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHOR_POUCH",
  stack(this),
  9,
  aspects.add(Aspect.VOID, 64).add(Aspect.MAN, 32).add(Aspect.CLOTH, 32).add(Aspect.ELDRITCH, 32).add(Aspect.AIR, 64),
  stack(ConfigItems.itemFocusPouch),
  stack(<ItemKamiResource>, 1, 1),
  stack(ConfigItems.itemFocusPortableHole),
  stack(Items.diamond),
  stack(<ItemKamiResource>, 1, 1),
  stack(ConfigBlocks.blockChestHungry),
  stack(ConfigBlocks.blockJar, 1, 3));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_POUCH",
  aspects.add(Aspect.VOID, 2).add(Aspect.CLOTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.MAN, 1),
  13,
  6,
  5,
  stack(this)).setParents("ICHOR_CLOTH") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR_POUCH"));
```

### `ItemKamiResource`

`common/item/kami/ItemKamiResource.java` — extends `ItemKamiBase`

**Имя регистрации:** `"kamiResource"`

**Конструктор:**

```java
super();
setHasSubtypes(true);
```

**Рецепт:**

```java
GameRegistry.addShapelessRecipe(stack(this, 9, 3),
  stack(this, 1, 2)); return new ThaumicTinkererRecipeMulti( new ThaumicTinkererArcaneRecipe("ICHOR_CLOTH", "ICHOR_CLOTH", stack(this, 3, 1), aspects.add(Aspect.FIRE, 125).add(Aspect.EARTH, 125).add(Aspect.WATER, 125).add(Aspect.AIR, 125).add(Aspect.ORDER, 125).add(Aspect.ENTROPY, 125), "CCC", "III", "DDD", 'C', stack(ConfigItems.itemResource, 1, 7), 'I', stack(this, 1, 0), 'D', stack(Items.diamond)),
  new ThaumicTinkererArcaneRecipe("ICHORIUM", "ICHORIUM", stack(this, 1, 2), aspects.add(Aspect.FIRE, 100).add(Aspect.EARTH, 100).add(Aspect.WATER, 100).add(Aspect.AIR, 100).add(Aspect.ORDER, 100).add(Aspect.ENTROPY, 100), " T ", "IDI", " I ", 'T', stack(ConfigItems.itemResource, 1, 2), 'I', stack(this, 1, 0), 'D', stack(Items.diamond)),
  new ThaumicTinkererArcaneRecipe("CAP_ICHOR", "CAP_ICHOR", stack(this, 2, 4), aspects.add(Aspect.FIRE, 100).add(Aspect.EARTH, 100).add(Aspect.WATER, 100).add(Aspect.AIR, 100).add(Aspect.ORDER, 100).add(Aspect.ENTROPY, 100), "ICI", " M ", "ICI", 'M', stack(this, 1, 2), 'I', stack(this, 1, 0), 'C', stack(ConfigItems.itemWandCap, 1, 2)),
  new ThaumicTinkererInfusionRecipe("ICHOR", stack(this, 8, 0), 7, aspects.add(Aspect.MAN, 32).add(Aspect.LIGHT, 32).add(Aspect.SOUL, 64), stack(Items.nether_star), stack(Items.diamond), stack(this, 8, 7), stack(Items.ender_eye), stack(this, 8, 6)),
  new ThaumicTinkererInfusionRecipe("ROD_ICHORCLOTH", stack(this, 1, 5), 9, aspects.add(Aspect.MAGIC, 100).add(Aspect.LIGHT, 32).add(Aspect.TOOL, 32), stack(ConfigItems.itemWandRod, 1, 2), stack(this), stack(this, 1, 1), stack(ConfigItems.itemResource, 1, 14), stack(Items.ghast_tear), stack(ConfigItems.itemResource, 1, 14), stack(this, 1, 1)) );
```

**Исследование:**

```java
TTResearchItem research; TTResearchItemMulti researchItemMulti = new TTResearchItemMulti(); research = (TTResearchItem) new KamiResearchItem("DIMENSION_SHARDS",
  aspects,
  7,
  8,
  0,
  stack(<ItemKamiResource>, 1, 7)).setStub().setAutoUnlock().setRound(); research.setPages(new ResearchPage("0")); researchItemMulti.addResearch(research); research = new KamiResearchItem("ICHOR",
  aspects.add(Aspect.MAN, 1).add(Aspect.LIGHT, 2).add(Aspect.SOUL, 1).add(Aspect.TAINT, 1),
  9,
  8,
  5,
  stack(this, 1, 0)); research.setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR")); ResearchHelper.kamiResearch = research; researchItemMulti.addResearch(research); research = (TTResearchItem) new KamiResearchItem("ICHOR_CLOTH",
  aspects.add(Aspect.CLOTH, 2).add(Aspect.LIGHT, 1).add(Aspect.CRAFT, 1).add(Aspect.SENSES, 1),
  11,
  7,
  5,
  stack(this, 1, 1)).setConcealed().setParents("ICHOR"); research.setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("ICHOR_CLOTH")); researchItemMulti.addResearch(research); research = (TTResearchItem) new KamiResearchItem("ICHORIUM",
  aspects.add(Aspect.METAL, 2).add(Aspect.LIGHT, 1).add(Aspect.CRAFT, 1).add(Aspect.TOOL, 1),
  11,
  9,
  5,
  stack(this, 1, 2)).setConcealed().setParents("ICHOR").setParentsHidden("ICHOR_CLOTH"); research.setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("ICHORIUM")); researchItemMulti.addResearch(research); research = (TTResearchItem) new KamiResearchItem("CAP_ICHOR",
  aspects.add(Aspect.TOOL, 2).add(Aspect.METAL, 1).add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1),
  11,
  11,
  5,
  stack(this, 1, 4)).setConcealed().setParents("ICHORIUM"); research.setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("CAP_ICHOR")); researchItemMulti.addResearch(research); research = (TTResearchItem) new KamiResearchItem("ROD_ICHORCLOTH",
  aspects.add(Aspect.TOOL, 2).add(Aspect.CLOTH, 1).add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1),
  14,
  2,
  5,
  stack(this, 1, 5)).setConcealed().setParents("ICHOR_CLOTH").setParentsHidden("CAP_ICHOR"); research.setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ROD_ICHORCLOTH")); researchItemMulti.addResearch(research); return researchItemMulti;
```

### `ItemPlacementMirror`

`common/item/kami/ItemPlacementMirror.java` — extends `ItemKamiBase`

**Имя регистрации:** `"placementMirror"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("PLACEMENT_MIRROR",
  stack(this),
  12,
  aspects.add(Aspect.CRAFT, 65).add(Aspect.CRYSTAL, 32).add(Aspect.MAGIC, 50).add(Aspect.MIND, 32),
  stack(<ItemBlockTalisman>),
  stack(<ItemKamiResource>),
  stack(Blocks.dropper),
  stack(Items.diamond),
  stack(Blocks.glass),
  stack(Items.blaze_powder),
  stack(<ItemKamiResource>));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("PLACEMENT_MIRROR",
  aspects.add(Aspect.CRAFT, 2).add(Aspect.CRYSTAL, 1).add(Aspect.ELDRITCH, 1).add(Aspect.MIND, 1),
  17,
  16,
  5,
  stack(this)).setParents("BLOCK_TALISMAN") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("PLACEMENT_MIRROR"));
```

**Константы:**

```java
private static final String TAG_BLOCK_ID = "blockID";
private static final String TAG_BLOCK_NAME = "blockName";
private static final String TAG_BLOCK_META = "blockMeta";
private static final String TAG_SIZE = "size";
```

### `ItemProtoclay`

`common/item/kami/ItemProtoclay.java` — extends `ItemKamiBase`

**Имя регистрации:** `"protoclay"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("PROTOCLAY",
  stack(this),
  4,
  aspects.add(Aspect.MINE, 16).add(Aspect.TOOL, 16),
  stack(Items.clay_ball),
  stack(Blocks.dirt),
  stack(Blocks.stone),
  stack(Blocks.log),
  stack(<ItemKamiResource>, 1, 7));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("PROTOCLAY",
  aspects.add(Aspect.TOOL, 2).add(Aspect.MINE, 1).add(Aspect.MAN, 1).add(Aspect.MECHANISM, 1),
  12,
  17,
  5,
  stack(this)).setParents("ICHOR_PICK_GEM").setParentsHidden("ICHOR_SHOVEL_GEM") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("PROTOCLAY"));
```

### `ItemSkyPearl`

`common/item/kami/ItemSkyPearl.java` — extends `ItemKamiBase`

**Имя регистрации:** `"skyPearl"`

**Конструктор:**

```java
super();
setMaxStackSize(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("SKY_PEARL",
  "WARP_GATE",
  stack(<ItemSkyPearl>, 2),
  6,
  aspects.add(Aspect.TRAVEL, 32).add(Aspect.ELDRITCH, 32).add(Aspect.FLIGHT, 32).add(Aspect.AIR, 16),
  stack(Items.ender_pearl),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>, 1, 7),
  stack(Blocks.lapis_block),
  stack(Items.diamond));
```

**Исследование:** нет (входит в чужую страницу).

**Константы:**

```java
public static final String TAG_X = "x";
public static final String TAG_Y = "y";
public static final String TAG_Z = "z";
public static final String TAG_DIM = "dim";
```

---

## KAMI — броня

### `ItemGemBoots`

`common/item/kami/armor/ItemGemBoots.java` — extends `ItemIchorclothArmorAdv`

**Имя регистрации:** `"ichorclothBootsGem"`

**Конструктор:**

```java
super(3);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHORCLOTH_BOOTS_GEM",
  stack(this),
  13,
  aspects.add(Aspect.EARTH, 50).add(Aspect.ARMOR, 32).add(Aspect.MINE, 32).add(Aspect.MOTION, 32).add(Aspect.LIGHT, 64).add(Aspect.PLANT, 16).add(Aspect.TRAVEL, 16),
  stack(ThaumicTinkerer.registry.getItemFromClassAndName(ItemIchorclothArmor.class, "ichorclothBoots")),
  stack(Items.diamond, 1),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemThaumonomicon),
  stack(ConfigItems.itemFocusPrimal),
  stack(Items.golden_boots),
  stack(Blocks.grass),
  stack(ConfigBlocks.blockWoodenDevice, 1, 5),
  stack(ConfigBlocks.blockMetalDevice, 1, 8),
  stack(Items.wheat_seeds),
  stack(Blocks.wool),
  stack(Items.lead));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHORCLOTH_BOOTS_GEM",
  aspects.add(Aspect.EARTH, 2).add(Aspect.TRAVEL, 1).add(Aspect.MINE, 1).add(Aspect.PLANT, 1),
  15,
  10,
  5,
  stack(this)).setParents("ICHORCLOTH_ARMOR") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHORCLOTH_BOOTS_GEM"));
```

### `ItemGemChest`

`common/item/kami/armor/ItemGemChest.java` — extends `ItemIchorclothArmorAdv`

**Имя регистрации:** `"ichorclothChestGem"`

**Конструктор:**

```java
super(1);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHORCLOTH_CHEST_GEM",
  stack(this),
  13,
  aspects.add(Aspect.AIR, 50).add(Aspect.ARMOR, 32).add(Aspect.FLIGHT, 32).add(Aspect.ORDER, 32).add(Aspect.LIGHT, 64).add(Aspect.ELDRITCH, 16).add(Aspect.SENSES, 16),
  stack(ThaumicTinkerer.registry.getItemFromClassAndName(ItemIchorclothArmor.class, "ichorclothChest")),
  stack(Items.diamond, 1),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemFocusPrimal),
  stack(ConfigItems.itemThaumonomicon),
  stack(Items.golden_chestplate),
  stack(<ItemFocusFlight>),
  stack(ConfigItems.itemHoverHarness),
  stack(<ItemFocusDeflect>),
  stack(Items.feather),
  stack(Items.fireworks),
  stack(Items.arrow));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHORCLOTH_CHEST_GEM",
  aspects.add(Aspect.AIR, 2).add(Aspect.MOTION, 1).add(Aspect.FLIGHT, 1).add(Aspect.ELDRITCH, 1),
  17,
  7,
  5,
  stack(this)).setParents("ICHORCLOTH_ARMOR") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHORCLOTH_CHEST_GEM"));
```

### `ItemGemHelm`

`common/item/kami/armor/ItemGemHelm.java` — extends ``

**Имя регистрации:** `"ichorclothHelmGem"`

**Конструктор:**

```java
super(0);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe( "ICHORCLOTH_HELM_GEM",
  stack(this),
  13,
  aspects.add(Aspect.WATER, 50).add(Aspect.ARMOR, 32) .add(Aspect.HUNGER, 32).add(Aspect.AURA, 32) .add(Aspect.LIGHT, 64).add(Aspect.FLESH, 16) .add(Aspect.MIND, 16),
  stack(ThaumicTinkerer.registry.getItemFromClassAndName(ItemIchorclothArmor.class, "ichorclothHelm")),
  stack(Items.diamond, 1),
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class)),
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class)),
  stack(ConfigItems.itemThaumonomicon),
  stack( ConfigItems.itemFocusPrimal),
  stack( Items.golden_helmet),
  stack(Items.potionitem, 1, 8198),
  stack(ConfigItems.itemGoggles),
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemCleansingTalisman.class)),
  stack(Items.fish),
  stack(Items.cake),
  stack(Items.ender_eye));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem( "ICHORCLOTH_HELM_GEM",
  aspects .add(Aspect.WATER, 2).add(Aspect.HEAL, 1) .add(Aspect.HUNGER, 1).add(Aspect.AURA, 1),
  18,
  3,
  5,
  stack(this)).setParents( "ICHORCLOTH_ARMOR").setPages( new ResearchPage("0"),
  ResearchHelper .infusionPage("ICHORCLOTH_HELM_GEM"));
```

### `ItemGemLegs`

`common/item/kami/armor/ItemGemLegs.java` — extends `ItemIchorclothArmorAdv`

**Имя регистрации:** `"ichorclothLegsGem"`

**Конструктор:**

```java
super(2);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHORCLOTH_LEGS_GEM",
  stack(this),
  13,
  aspects.add(Aspect.FIRE, 50).add(Aspect.ARMOR, 32).add(Aspect.HEAL, 32).add(Aspect.ENERGY, 32).add(Aspect.LIGHT, 64).add(Aspect.GREED, 16).add(Aspect.ELDRITCH, 16),
  (stack(ThaumicTinkerer.registry.getItemFromClassAndName(ItemIchorclothArmor.class, "ichorclothLegs"))),
  stack(Items.diamond, 1),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemFocusPrimal),
  stack(ConfigItems.itemThaumonomicon),
  stack(Items.golden_chestplate),
  stack(Items.potionitem, 1, 8195),
  stack(<ItemFocusSmelt>),
  stack(<ItemBrightNitor>),
  stack(Items.lava_bucket),
  stack(Items.fire_charge),
  stack(Items.blaze_rod));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHORCLOTH_LEGS_GEM",
  aspects.add(Aspect.FIRE, 2).add(Aspect.HEAL, 1).add(Aspect.GREED, 1).add(Aspect.ENERGY, 1),
  17,
  9,
  5,
  stack(this)).setParents("ICHORCLOTH_ARMOR") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHORCLOTH_LEGS_GEM"),
  new ResearchPage("1"));
```

### `ItemIchorclothArmor`

`common/item/kami/armor/ItemIchorclothArmor.java` — extends ``

**Имя регистрации:** `switch (armorType) { case 3: "ichorclothBoots"; case 2: "ichorclothLegs"; case 1: "ichorclothChest"; case 0: "ichorclothHelm"; default: "INVAlID ARMOR TYPE"; }`

**Конструктор:**

```java
super(material, 0, par2);
setCreativeTab(ModCreativeTab.INSTANCE);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Спецпараметры:** `ArrayList<Object> result = new ArrayList<Object>(); result.add(1); result.add(2); result.add(3); return result;`

**Рецепт:**

```java
switch (armorType) { case 0: return new ThaumicTinkererArcaneRecipe("ICHORCLOTH_HELM",
  "ICHORCLOTH_ARMOR",
  stack(this),
  aspects.add(Aspect.WATER, 75),
  "CCC",
  "C C",
  'C',
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class), 1, 1)); case 1: return new ThaumicTinkererArcaneRecipe("ICHORCLOTH_CHEST",
  "ICHORCLOTH_ARMOR",
  stack(this),
  aspects.add(Aspect.AIR, 75),
  "C C",
  "CCC",
  "CCC",
  'C',
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class), 1, 1)); case 2: return new ThaumicTinkererArcaneRecipe("ICHORCLOTH_LEGS",
  "ICHORCLOTH_ARMOR",
  stack(this),
  aspects.add(Aspect.FIRE, 75),
  "CCC",
  "C C",
  "C C",
  'C',
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class), 1, 1)); case 3: return new ThaumicTinkererArcaneRecipe("ICHORCLOTH_BOOTS",
  "ICHORCLOTH_ARMOR",
  stack(this),
  aspects.add(Aspect.EARTH, 75),
  "C C",
  "C C",
  'C',
  stack(ThaumicTinkerer.registry .getFirstItemFromClass(ItemKamiResource.class), 1, 1)); } return null;
```

**Исследование:**

```java
return armorType != 0 ? null : (IRegisterableResearch) new KamiResearchItem( "ICHORCLOTH_ARMOR",
  aspects .add(Aspect.ARMOR, 2).add(Aspect.CLOTH, 1) .add(Aspect.LIGHT, 1).add(Aspect.CRAFT, 1),
  17,
  5,
  5,
  stack(this)) .setConcealed() .setParents("ICHOR_CLOTH") .setPages( new ResearchPage("0"),
  ResearchHelper .arcaneRecipePage("ICHORCLOTH_HELM"),
  ResearchHelper .arcaneRecipePage("ICHORCLOTH_CHEST"),
  ResearchHelper .arcaneRecipePage("ICHORCLOTH_LEGS"),
  ResearchHelper .arcaneRecipePage("ICHORCLOTH_BOOTS"));
```

### `ItemIchorclothArmorAdv`

`common/item/kami/armor/ItemIchorclothArmorAdv.java` — extends `ItemIchorclothArmor`

**Конструктор:**

```java
super(par2);
setHasSubtypes(true);
if (ticks())
MinecraftForge.EVENT_BUS.register(this);
```

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

---

## KAMI — инструменты

### `ItemIchorAxe`

`common/item/kami/tool/ItemIchorAxe.java` — extends `ItemAxe`  implements `ITTinkererItem`

**Имя регистрации:** `"ichorAxe"`

**Конструктор:**

```java
super(ThaumicTinkerer.proxy.toolMaterialIchor);
setCreativeTab(ModCreativeTab.INSTANCE);
setHarvestLevel("axe", 4);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ICHOR_AXE",
  "ICHOR_TOOLS",
  stack(this),
  aspects.add(Aspect.WATER, 75),
  "II ",
  "IR ",
  " R ",
  'R',
  stack(ConfigItems.itemWandRod, 1, 2),
  'I',
  stack(<ItemKamiResource>, 1, 2));
```

**Исследование:** нет (входит в чужую страницу).

### `ItemIchorAxeAdv`

`common/item/kami/tool/ItemIchorAxeAdv.java` — extends `ItemIchorAxe`  implements `IAdvancedTool`

**Имя регистрации:** `"ichorAxeGem"`

**Конструктор:**

```java
super();
setHasSubtypes(true);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHOR_AXE_GEM",
  stack(this),
  15,
  aspects.add(Aspect.WATER, 50).add(Aspect.MINE, 64).add(Aspect.TOOL, 32).add(Aspect.TREE, 32).add(Aspect.HARVEST, 32).add(Aspect.CROP, 16).add(Aspect.SENSES, 16),
  stack(<ItemIchorAxe>),
  stack(<ItemKamiResource>, 1, 2),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemAxeElemental),
  stack(ConfigItems.itemFocusExcavation),
  stack(Blocks.tnt),
  stack(ConfigItems.itemNugget, 1, 21),
  stack(ConfigItems.itemNugget, 1, 16),
  stack(ConfigItems.itemNugget, 1, 31),
  stack(Items.diamond),
  stack(ConfigItems.itemFocusExcavation),
  stack(ConfigItems.itemAxeElemental),
  stack(<ItemKamiResource>, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_AXE_GEM",
  aspects.add(Aspect.WATER, 2).add(Aspect.TOOL, 1).add(Aspect.TREE, 1).add(Aspect.CROP, 1),
  16,
  14,
  5,
  stack(this)).setParents("ICHOR_TOOLS") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR_AXE_GEM"));
```

### `ItemIchorPick`

`common/item/kami/tool/ItemIchorPick.java` — extends `ItemPickaxe`  implements `ITTinkererItem`

**Имя регистрации:** `"ichorPick"`

**Конструктор:**

```java
super(ThaumicTinkerer.proxy.toolMaterialIchor);
setCreativeTab(ModCreativeTab.INSTANCE);
setHarvestLevel("pickaxe", 4);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ICHOR_PICK",
  "ICHOR_TOOLS",
  stack(this),
  aspects.add(Aspect.FIRE, 75),
  "III",
  " R ",
  " R ",
  'R',
  stack(ConfigItems.itemWandRod, 1, 2),
  'I',
  stack(<ItemKamiResource>, 1, 2));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_TOOLS",
  aspects.add(Aspect.TOOL, 2).add(Aspect.WEAPON, 1).add(Aspect.METAL, 1).add(Aspect.CRAFT, 1),
  13,
  12,
  5,
  stack(this)).setConcealed().setParents("ICHORIUM").setParentsHidden("ROD_ICHORCLOTH") .setPages(new ResearchPage("0"),
  ResearchHelper.arcaneRecipePage("ICHOR_PICK"),
  ResearchHelper.arcaneRecipePage("ICHOR_SHOVEL"),
  ResearchHelper.arcaneRecipePage("ICHOR_AXE"),
  ResearchHelper.arcaneRecipePage("ICHOR_SWORD"));
```

### `ItemIchorPickAdv`

`common/item/kami/tool/ItemIchorPickAdv.java` — extends `ItemIchorPick`  implements `IAdvancedTool`

**Имя регистрации:** `"ichorPickGem"`

**Конструктор:**

```java
super();
setHasSubtypes(true);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHOR_PICK_GEM",
  stack(this),
  15,
  aspects.add(Aspect.FIRE, 50).add(Aspect.MINE, 64).add(Aspect.METAL, 32).add(Aspect.EARTH, 32).add(Aspect.HARVEST, 32).add(Aspect.GREED, 16).add(Aspect.SENSES, 16),
  stack(<ItemIchorPick>),
  stack(<ItemKamiResource>, 1, 2),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemPickElemental),
  stack(ConfigItems.itemFocusExcavation),
  stack(Blocks.tnt),
  stack(ConfigItems.itemNugget, 1, 21),
  stack(ConfigItems.itemNugget, 1, 16),
  stack(ConfigItems.itemNugget, 1, 31),
  stack(Items.diamond),
  stack(ConfigItems.itemFocusExcavation),
  stack(ConfigItems.itemPickElemental),
  stack(<ItemKamiResource>, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_PICK_GEM",
  aspects.add(Aspect.FIRE, 2).add(Aspect.TOOL, 1).add(Aspect.MINE, 1).add(Aspect.EARTH, 1),
  13,
  15,
  5,
  stack(this)).setParents("ICHOR_TOOLS") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR_PICK_GEM"),
  new ResearchPage("1"));
```

### `ItemIchorShovel`

`common/item/kami/tool/ItemIchorShovel.java` — extends `ItemSpade`  implements `ITTinkererItem`

**Имя регистрации:** `"ichorShovel"`

**Конструктор:**

```java
super(ThaumicTinkerer.proxy.toolMaterialIchor);
setCreativeTab(ModCreativeTab.INSTANCE);
setHarvestLevel("shovel", 4);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ICHOR_SHOVEL",
  "ICHOR_TOOLS",
  stack(this),
  aspects.add(Aspect.EARTH, 75),
  " I ",
  " R ",
  " R ",
  'R',
  stack(ConfigItems.itemWandRod, 1, 2),
  'I',
  stack(<ItemKamiResource>, 1, 2));
```

**Исследование:** нет (входит в чужую страницу).

### `ItemIchorShovelAdv`

`common/item/kami/tool/ItemIchorShovelAdv.java` — extends `ItemIchorShovel`  implements `IAdvancedTool`

**Имя регистрации:** `"ichorShovelGem"`

**Конструктор:**

```java
super();
setHasSubtypes(true);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHOR_SHOVEL_GEM",
  stack(this),
  15,
  aspects.add(Aspect.EARTH, 50).add(Aspect.MINE, 64).add(Aspect.TOOL, 32).add(Aspect.EARTH, 32).add(Aspect.HARVEST, 32).add(Aspect.TRAP, 16).add(Aspect.SENSES, 16),
  stack(<ItemIchorShovel>),
  stack(<ItemKamiResource>, 1, 2),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemShovelElemental),
  stack(ConfigItems.itemFocusExcavation),
  stack(Blocks.tnt),
  stack(ConfigItems.itemNugget, 1, 21),
  stack(ConfigItems.itemNugget, 1, 16),
  stack(ConfigItems.itemNugget, 1, 31),
  stack(Items.diamond),
  stack(ConfigItems.itemFocusExcavation),
  stack(ConfigItems.itemShovelElemental),
  stack(<ItemKamiResource>, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_SHOVEL_GEM",
  aspects.add(Aspect.EARTH, 2).add(Aspect.TOOL, 1).add(Aspect.MINE, 1).add(Aspect.EARTH, 1),
  15,
  15,
  5,
  stack(this)).setParents("ICHOR_TOOLS") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR_SHOVEL_GEM"));
```

### `ItemIchorSword`

`common/item/kami/tool/ItemIchorSword.java` — extends `ItemSword`  implements `ITTinkererItem`

**Имя регистрации:** `"ichorSword"`

**Конструктор:**

```java
super(ThaumicTinkerer.proxy.toolMaterialIchor);
setCreativeTab(ModCreativeTab.INSTANCE);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:**

```java
return new ThaumicTinkererArcaneRecipe("ICHOR_SWORD",
  "ICHOR_TOOLS",
  stack(this),
  aspects.add(Aspect.AIR, 75),
  " I ",
  " I ",
  " R ",
  'R',
  stack(ConfigItems.itemWandRod, 1, 2),
  'I',
  stack(<ItemKamiResource>, 1, 2));
```

**Исследование:** нет (входит в чужую страницу).

### `ItemIchorSwordAdv`

`common/item/kami/tool/ItemIchorSwordAdv.java` — extends `ItemIchorSword`  implements `IAdvancedTool`

**Имя регистрации:** `"ichorSwordGem"`

**Конструктор:**

```java
super();
setHasSubtypes(true);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("ICHOR_SWORD_GEM",
  stack(this),
  15,
  aspects.add(Aspect.AIR, 50).add(Aspect.HUNGER, 64).add(Aspect.SOUL, 32).add(Aspect.WEAPON, 32).add(Aspect.ENERGY, 32).add(Aspect.ORDER, 16).add(Aspect.CRYSTAL, 16),
  stack(<ItemIchorSword>),
  stack(<ItemKamiResource>, 1, 2),
  stack(<ItemKamiResource>),
  stack(ConfigItems.itemSwordElemental),
  stack(ConfigItems.itemFocusFrost),
  stack(Blocks.cactus),
  stack(ConfigItems.itemNugget, 1, 21),
  stack(ConfigItems.itemNugget, 1, 16),
  stack(ConfigItems.itemNugget, 1, 31),
  stack(Items.diamond),
  stack(ConfigItems.itemFocusFrost),
  stack(ConfigItems.itemSwordElemental),
  stack(<ItemKamiResource>, 1, 1));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("ICHOR_SWORD_GEM",
  aspects.add(Aspect.AIR, 2).add(Aspect.WEAPON, 1).add(Aspect.SOUL, 1).add(Aspect.HUNGER, 1),
  16,
  12,
  5,
  stack(this)).setParents("ICHOR_TOOLS") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("ICHOR_SWORD_GEM"),
  new ResearchPage("1"));
```

---

## KAMI — фокусы

### `ItemFocusRecall`

`common/item/kami/foci/ItemFocusRecall.java` — extends `ItemModKamiFocus`

**Имя регистрации:** `"focusRecall"`

**Конструктор:**

```java
super();
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_RECALL",
  stack(<ItemFocusRecall>),
  10,
  aspects.add(Aspect.TRAVEL, 100).add(Aspect.ELDRITCH, 64).add(Aspect.MAGIC, 50),
  stack(<ItemSkyPearl>),
  stack(<ItemKamiResource>),
  stack(<ItemKamiResource>),
  stack(Items.ender_pearl),
  stack(Items.diamond),
  stack(ConfigBlocks.blockMirror),
  stack(ConfigItems.itemFocusPortableHole));
```

**Исследование:**

```java
if (!Config.allowMirrors) { return null; } return (IRegisterableResearch) new KamiResearchItem("FOCUS_RECALL",
  aspects.add(Aspect.TRAVEL, 2).add(Aspect.ELDRITCH, 1).add(Aspect.FLIGHT, 1).add(Aspect.MAGIC, 1),
  20,
  8,
  5,
  stack(this)).setParents("WARP_GATE").setParentsHidden("ROD_ICHORCLOTH") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_RECALL"));
```

### `ItemFocusShadowbeam`

`common/item/kami/foci/ItemFocusShadowbeam.java` — extends `ItemModKamiFocus`

**Имя регистрации:** `"focusShadowbeam"`

**Конструктор:**

```java
super();
EntityRegistry.registerModEntity(Beam.class, "ShadowbeamStaffBeam", 0, ThaumicTinkerer.instance, 0, 0, false);
```

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_SHADOWBEAM",
  stack(this),
  12,
  aspects.add(Aspect.DARKNESS, 65).add(Aspect.ELDRITCH, 32).add(Aspect.MAGIC, 50).add(Aspect.WEAPON, 32),
  stack(ConfigItems.itemFocusShock),
  stack(<ItemKamiResource>),
  stack(Items.arrow),
  stack(Items.diamond),
  stack(ConfigItems.itemFocusExcavation),
  stack(<ItemFocusDeflect>),
  stack(<ItemKamiResource>));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("FOCUS_SHADOWBEAM",
  aspects.add(Aspect.DARKNESS, 2).add(Aspect.MAGIC, 1).add(Aspect.ELDRITCH, 1).add(Aspect.TAINT, 1),
  14,
  4,
  5,
  stack(this)).setParents("ROD_ICHORCLOTH") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_SHADOWBEAM"));
```

### `ItemFocusXPDrain`

`common/item/kami/foci/ItemFocusXPDrain.java` — extends `ItemModKamiFocus`

**Имя регистрации:** `"focusXPDrain"`

**Рецепт:**

```java
return new ThaumicTinkererInfusionRecipe("FOCUS_XP_DRAIN",
  stack(this),
  12,
  aspects.add(Aspect.MIND, 65).add(Aspect.TAINT, 16).add(Aspect.MAGIC, 50).add(Aspect.AURA, 32),
  stack(Items.ender_pearl),
  stack(<ItemKamiResource>),
  stack(Items.experience_bottle),
  stack(Items.diamond),
  stack(<ItemXPTalisman>),
  stack(Blocks.enchanting_table),
  stack(<ItemKamiResource>));
```

**Исследование:**

```java
return (IRegisterableResearch) new KamiResearchItem("FOCUS_XP_DRAIN",
  aspects.add(Aspect.MIND, 2).add(Aspect.MAGIC, 1).add(Aspect.AURA, 1).add(Aspect.MAN, 1),
  12,
  3,
  5,
  stack(this)).setParents("ROD_ICHORCLOTH") .setPages(new ResearchPage("0"),
  ResearchHelper.infusionPage("FOCUS_XP_DRAIN"));
```

### `ItemModKamiFocus`

`common/item/kami/foci/ItemModKamiFocus.java` — extends `ItemBase`  implements `IWandFocus`

**Конструктор:**

```java
super();
setMaxDamage(1);
setNoRepair();
setMaxStackSize(1);
```

**shouldRegister:** `return ConfigHandler.enableKami;`

**Рецепт:** нет (не крафтится).

**Исследование:** нет (входит в чужую страницу).

---
