# Очередь портирования Thaumic Tinkerer
Сгенерировано из `TT_OBJECT_REFERENCE.md` и дерева исходников — не заполнять руками, перегенерировать при изменениях.

**Состояние: портировано 34 из 82 объектов каталога, осталось 48.**

Порядок — по зависимостям: объект появляется после всего, что ему нужно. «Связи» — компоненты рецепта, которые сами являются объектами TT.

| # | Объект | Рецепт | Неста­бильность | Зависит от (ещё нет) | Использует (уже есть) |
|---|---|---|---|---|---|
| 1 | `BlockAspectAnalyzer` | аркан | — | — | — |
| 2 | `BlockForcefield` | нет рецепта | — | — | — |
| 3 | `BlockGas` | нет рецепта | — | — | — |
| 4 | `BlockGolemConnector` | аркан | — | — | — |
| 5 | `BlockInfusedGrain` | нет рецепта | — | — | — |
| 6 | `BlockMobilizer` | инфузия | 4 | — | — |
| 7 | `BlockMobilizerRelay` | аркан | — | — | — |
| 8 | `BlockPlatform` | аркан | — | — | — |
| 9 | `BlockRPlacer` | аркан | — | — | — |
| 10 | `BlockWarpGate` | инфузия | 8 | — | `BlockTransvectorDislocator`, `ItemKamiResource` |
| 11 | `ItemBlockFire` | нет рецепта | — | — | — |
| 12 | `ItemBlockTalisman` | инфузия | 9 | — | `ItemKamiResource` |
| 13 | `ItemBlockWarpGate` | нет рецепта | — | — | — |
| 14 | `ItemBloodSword` | инфузия | 6 | — | — |
| 15 | `ItemBrightNitor` | верстак | — | — | — |
| 16 | `ItemConnector` | аркан | — | — | — |
| 17 | `ItemDarkQuartz` | верстак | — | — | — |
| 18 | `ItemFocusShadowbeam` | инфузия | 12 | — | `ItemFocusDeflect`, `ItemKamiResource` |
| 19 | `ItemFocusXPDrain` | инфузия | 12 | — | `ItemKamiResource`, `ItemXPTalisman` |
| 20 | `ItemGas` | верстак | — | — | `BlockGaseousLight`, `BlockGaseousShadow` |
| 21 | `ItemGemBoots` | инфузия | 13 | — | `ItemKamiResource` |
| 22 | `ItemGemChest` | инфузия | 13 | — | `ItemFocusDeflect`, `ItemFocusFlight`, `ItemKamiResource` |
| 23 | `ItemGemHelm` | инфузия | 13 | — | — |
| 24 | `ItemIchorPouch` | инфузия | 9 | — | `ItemKamiResource` |
| 25 | `ItemIchorSwordAdv` | инфузия | 15 | — | `ItemIchorSword`, `ItemKamiResource` |
| 26 | `ItemIchorclothArmor` | аркан | — | — | — |
| 27 | `ItemIchorclothArmorAdv` | нет рецепта | — | — | — |
| 28 | `ItemInfusedGrain` | нет рецепта | — | — | — |
| 29 | `ItemInfusedInkwell` | инфузия | — | — | — |
| 30 | `ItemInfusedSeeds` | инфузия | — | — | — |
| 31 | `ItemMobAspect` | инфузия | — | — | — |
| 32 | `ItemMobDisplay` | нет рецепта | — | — | — |
| 33 | `ItemProtoclay` | инфузия | 4 | — | `ItemKamiResource` |
| 34 | `ItemRevealingHelm` | аркан | — | — | — |
| 35 | `ItemShareBook` | верстак | — | — | — |
| 36 | `ItemSpellCloth` | верстак | — | — | — |
| 37 | `BlockFireAir` | верстак | — | `ItemBrightNitor` | — |
| 38 | `BlockFireChaos` | верстак | — | `ItemBrightNitor` | — |
| 39 | `BlockFireEarth` | верстак | — | `ItemBrightNitor` | — |
| 40 | `BlockFireIgnis` | верстак | — | `ItemBrightNitor` | — |
| 41 | `BlockFireOrder` | верстак | — | `ItemBrightNitor` | — |
| 42 | `BlockFireWater` | верстак | — | `ItemBrightNitor` | — |
| 43 | `ItemGasRemover` | аркан | — | `ItemDarkQuartz` | — |
| 44 | `ItemGemLegs` | инфузия | 13 | `ItemBrightNitor` | `ItemFocusSmelt`, `ItemKamiResource` |
| 45 | `ItemInfusedPotion` | верстак | — | `ItemInfusedGrain` | — |
| 46 | `BlockSummon` | аркан | — | `BlockSummon` | — |
| 47 | `ItemFocusRecall` | инфузия | — | `ItemFocusRecall`, `ItemSkyPearl` | `ItemKamiResource` |
| 48 | `ItemSkyPearl` | инфузия | — | `ItemSkyPearl` | `ItemKamiResource` |

---

## Свойства каждого объекта

### 1. `BlockAspectAnalyzer`

- **Файл оригинала:** `common/block/BlockAspectAnalyzer.java`
- **Наследует:** `BlockModContainer`
- **Имя регистрации:** `"aspectAnalyzer"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 1, ENTROPY 1
- **Родитель в дереве исследований:** `PERIPHERALS`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockAspectAnalyzer`

### 2. `BlockForcefield`

- **Файл оригинала:** `common/block/BlockForcefield.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"forcefield"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockForcefield`

### 3. `BlockGas`

- **Файл оригинала:** `common/block/BlockGas.java`
- **Наследует:** `BlockMod`, реализует `ITTinkererBlock`
- **Имя регистрации:** `—`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockGas`

### 4. `BlockGolemConnector`

- **Файл оригинала:** `common/block/BlockGolemConnector.java`
- **Наследует:** `BlockCamo`
- **Имя регистрации:** `"golemConnector"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, ENTROPY 15
- **Родитель в дереве исследований:** `PERIPHERALS`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockGolemConnector`

### 5. `BlockInfusedGrain`

- **Файл оригинала:** `common/block/BlockInfusedGrain.java`
- **Наследует:** `BlockCrops`, реализует `ITTinkererBlock`
- **Имя регистрации:** `if (aspect == Aspect.AIR) { "INFUSED_GRAIN_BASE + "Air""; } if (aspect == Aspect.EARTH) { "INFUSED_GRAIN_BASE + "Earth""; } if (aspect == Aspect.WATER) { "INFUSED_GRAIN_BASE + "Water""; } "INFUSED_GRAIN_BASE + "Fire""`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockInfusedGrain`

### 6. `BlockMobilizer`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizer.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive"`
- **Рецепт:** инфузия, нестабильность 4
- **Аспекты:** MOTION 15, ORDER 20, MAGIC 15
- **Родитель в дереве исследований:** `MAGNETS`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizer`

### 7. `BlockMobilizerRelay`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizerRelay.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive Relay"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, EARTH 15
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizerRelay`

### 8. `BlockPlatform`

- **Файл оригинала:** `common/block/BlockPlatform.java`
- **Наследует:** `BlockCamo`
- **Имя регистрации:** `"platform"`
- **Рецепт:** аркан
- **Аспекты:** AIR 2, ENTROPY 4
- **Родитель в дереве исследований:** `CLEANSING_TALISMAN`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockPlatform`

### 9. `BlockRPlacer`

- **Файл оригинала:** `common/block/BlockRPlacer.java`
- **Наследует:** `BlockCamo`, реализует `IWandable`
- **Имя регистрации:** `"remotePlacer"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, EARTH 15, ENTROPY 5
- **Родитель в дереве исследований:** `ANIMATION_TABLET`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockRPlacer`

### 10. `BlockWarpGate`

- **Файл оригинала:** `common/block/kami/BlockWarpGate.java`
- **Наследует:** `BlockModContainer`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** инфузия, нестабильность 8
- **Аспекты:** TRAVEL 64, ELDRITCH 50, FLIGHT 50
- **Родитель в дереве исследований:** `ICHORCLOTH_CHEST_GEM`
- **Использует уже портированное:** `BlockTransvectorDislocator`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockWarpGate`

### 11. `ItemBlockFire`

- **Файл оригинала:** `common/item/ItemBlockFire.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockFire`

### 12. `ItemBlockTalisman`

- **Файл оригинала:** `common/item/kami/ItemBlockTalisman.java`
- **Наследует:** `ItemKamiBase`, реализует `IBauble`
- **Имя регистрации:** `"blockTalisman"`
- **Рецепт:** инфузия, нестабильность 9
- **Аспекты:** VOID 65, DARKNESS 32, MAGIC 50, ELDRITCH 32
- **Родитель в дереве исследований:** `ICHOR_PICK_GEM,
  ICHOR_SHOVEL_GEM`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockTalisman`

### 13. `ItemBlockWarpGate`

- **Файл оригинала:** `common/item/kami/ItemBlockWarpGate.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockWarpGate`

### 14. `ItemBloodSword`

- **Файл оригинала:** `common/item/ItemBloodSword.java`
- **Наследует:** `ItemSword`
- **Имя регистрации:** `"bloodSword"`
- **Рецепт:** инфузия, нестабильность 6
- **Аспекты:** HUNGER 20, DARKNESS 5, SOUL 10, MAN 6
- **Родитель в дереве исследований:** `CLEANSING_TALISMAN`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBloodSword`

### 15. `ItemBrightNitor`

- **Файл оригинала:** `common/item/ItemBrightNitor.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"brightNitor"`
- **Рецепт:** верстак
- **Аспекты:** ENERGY 25, LIGHT 25, AIR 10, FIRE 10
- **Родитель в дереве исследований:** `GASEOUS_LIGHT`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBrightNitor`

### 16. `ItemConnector`

- **Файл оригинала:** `common/item/ItemConnector.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"connector"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 2
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemConnector`

### 17. `ItemDarkQuartz`

- **Файл оригинала:** `common/item/quartz/ItemDarkQuartz.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"darkQuartzItem"`
- **Рецепт:** верстак
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemDarkQuartz`

### 18. `ItemFocusShadowbeam`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusShadowbeam.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusShadowbeam"`
- **Рецепт:** инфузия, нестабильность 12
- **Аспекты:** DARKNESS 65, ELDRITCH 32, MAGIC 50, WEAPON 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusShadowbeam`

### 19. `ItemFocusXPDrain`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusXPDrain.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusXPDrain"`
- **Рецепт:** инфузия, нестабильность 12
- **Аспекты:** MIND 65, TAINT 16, MAGIC 50, AURA 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemKamiResource`, `ItemXPTalisman`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusXPDrain`

### 20. `ItemGas`

- **Файл оригинала:** `common/item/ItemGas.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `setBlock == <BlockGaseousShadow> ? "gaseousShadowItem" : "gaseousLightItem"`
- **Рецепт:** верстак
- **Аспекты:** LIGHT 16, AIR 10, MOTION 8, DARKNESS 16, AIR 10, MOTION 8
- **Родитель в дереве исследований:** `GASEOUS_LIGHT`
- **Использует уже портированное:** `BlockGaseousLight`, `BlockGaseousShadow`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGas`

### 21. `ItemGemBoots`

- **Файл оригинала:** `common/item/kami/armor/ItemGemBoots.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothBootsGem"`
- **Рецепт:** инфузия, нестабильность 13
- **Аспекты:** EARTH 50, ARMOR 32, MINE 32, MOTION 32, LIGHT 64, PLANT 16, TRAVEL 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemBoots`

### 22. `ItemGemChest`

- **Файл оригинала:** `common/item/kami/armor/ItemGemChest.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothChestGem"`
- **Рецепт:** инфузия, нестабильность 13
- **Аспекты:** AIR 50, ARMOR 32, FLIGHT 32, ORDER 32, LIGHT 64, ELDRITCH 16, SENSES 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemFocusFlight`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemChest`

### 23. `ItemGemHelm`

- **Файл оригинала:** `common/item/kami/armor/ItemGemHelm.java`
- **Имя регистрации:** `"ichorclothHelmGem"`
- **Рецепт:** инфузия, нестабильность 13
- **Аспекты:** WATER 50, ARMOR 32, HUNGER 32, AURA 32, LIGHT 64, FLESH 16, MIND 16
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemHelm`

### 24. `ItemIchorPouch`

- **Файл оригинала:** `common/item/kami/ItemIchorPouch.java`
- **Наследует:** `ItemFocusPouch`
- **Имя регистрации:** `"ichorPouch"`
- **Рецепт:** инфузия, нестабильность 9
- **Аспекты:** VOID 64, MAN 32, CLOTH 32, ELDRITCH 32, AIR 64
- **Родитель в дереве исследований:** `ICHOR_CLOTH`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorPouch`

### 25. `ItemIchorSwordAdv`

- **Файл оригинала:** `common/item/kami/tool/ItemIchorSwordAdv.java`
- **Наследует:** `ItemIchorSword`, реализует `IAdvancedTool`
- **Имя регистрации:** `"ichorSwordGem"`
- **Рецепт:** инфузия, нестабильность 15
- **Аспекты:** AIR 50, HUNGER 64, SOUL 32, WEAPON 32, ENERGY 32, ORDER 16, CRYSTAL 16
- **Родитель в дереве исследований:** `ICHOR_TOOLS`
- **Использует уже портированное:** `ItemIchorSword`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorSwordAdv`

### 26. `ItemIchorclothArmor`

- **Файл оригинала:** `common/item/kami/armor/ItemIchorclothArmor.java`
- **Имя регистрации:** `switch (armorType) { case 3: "ichorclothBoots"; case 2: "ichorclothLegs"; case 1: "ichorclothChest"; case 0: "ichorclothHelm"; default: "INVAlID ARMOR TYPE"; }`
- **Рецепт:** аркан
- **Аспекты:** WATER 75, AIR 75, FIRE 75, EARTH 75
- **Родитель в дереве исследований:** `ICHOR_CLOTH`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorclothArmor`

### 27. `ItemIchorclothArmorAdv`

- **Файл оригинала:** `common/item/kami/armor/ItemIchorclothArmorAdv.java`
- **Наследует:** `ItemIchorclothArmor`
- **Имя регистрации:** `—`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorclothArmorAdv`

### 28. `ItemInfusedGrain`

- **Файл оригинала:** `common/item/ItemInfusedGrain.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"infusedGrain"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedGrain`

### 29. `ItemInfusedInkwell`

- **Файл оригинала:** `common/item/ItemInfusedInkwell.java`
- **Наследует:** `ItemBase`, реализует `IScribeTools`
- **Имя регистрации:** `"infusedInkwell"`
- **Рецепт:** инфузия
- **Аспекты:** VOID 8, DARKNESS 8
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedInkwell`

### 30. `ItemInfusedSeeds`

- **Файл оригинала:** `common/item/ItemInfusedSeeds.java`
- **Наследует:** `ItemSeeds`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedSeeds"`
- **Рецепт:** инфузия
- **Аспекты:** CROP 32, HARVEST 32, CROP 32, HARVEST 32, CROP 32, HARVEST 32, CROP 32, HARVEST 32
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedSeeds`

### 31. `ItemMobAspect`

- **Файл оригинала:** `common/item/ItemMobAspect.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobAspect"`
- **Рецепт:** инфузия
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobAspect`

### 32. `ItemMobDisplay`

- **Файл оригинала:** `common/item/ItemMobDisplay.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobDisplay"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobDisplay`

### 33. `ItemProtoclay`

- **Файл оригинала:** `common/item/kami/ItemProtoclay.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"protoclay"`
- **Рецепт:** инфузия, нестабильность 4
- **Аспекты:** MINE 16, TOOL 16
- **Родитель в дереве исследований:** `ICHOR_PICK_GEM`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemProtoclay`

### 34. `ItemRevealingHelm`

- **Файл оригинала:** `common/item/ItemRevealingHelm.java`
- **Наследует:** `ItemArmor`
- **Имя регистрации:** `"revealingHelm"`
- **Рецепт:** аркан
- **Аспекты:** EARTH 5, FIRE 5, WATER 5, AIR 5, ORDER 5, ENTROPY 5
- **Родитель в дереве исследований:** `GOGGLES`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemRevealingHelm`

### 35. `ItemShareBook`

- **Файл оригинала:** `common/item/ItemShareBook.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"shareBook"`
- **Рецепт:** верстак
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemShareBook`

### 36. `ItemSpellCloth`

- **Файл оригинала:** `common/item/ItemSpellCloth.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"spellCloth"`
- **Рецепт:** верстак
- **Аспекты:** MAGIC 10, ENTROPY 6, EXCHANGE 4
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSpellCloth`

### 37. `BlockFireAir`

- **Файл оригинала:** `common/block/fire/BlockFireAir.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireAir"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 5, MAGIC 5, AIR 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireAir`

### 38. `BlockFireChaos`

- **Файл оригинала:** `common/block/fire/BlockFireChaos.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireChaos"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 5, MAGIC 5, ENTROPY 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireChaos`

### 39. `BlockFireEarth`

- **Файл оригинала:** `common/block/fire/BlockFireEarth.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireEarth"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 5, MAGIC 5, EARTH 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireEarth`

### 40. `BlockFireIgnis`

- **Файл оригинала:** `common/block/fire/BlockFireIgnis.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireFire"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 10, AIR 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireIgnis`

### 41. `BlockFireOrder`

- **Файл оригинала:** `common/block/fire/BlockFireOrder.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireOrder"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 5, MAGIC 5, ORDER 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireOrder`

### 42. `BlockFireWater`

- **Файл оригинала:** `common/block/fire/BlockFireWater.java`
- **Наследует:** `BlockFireBase`
- **Имя регистрации:** `"fireWater"`
- **Рецепт:** верстак
- **Аспекты:** FIRE 5, MAGIC 5, WATER 5
- **Родитель в дереве исследований:** `BRIGHT_NITOR`
- **Блокируется:** `ItemBrightNitor`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockFireWater`

### 43. `ItemGasRemover`

- **Файл оригинала:** `common/item/ItemGasRemover.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"gasRemover"`
- **Рецепт:** аркан
- **Аспекты:** AIR 2, ORDER 2
- **Родитель в дереве исследований:** `GASEOUS_SHADOW`
- **Блокируется:** `ItemDarkQuartz`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGasRemover`

### 44. `ItemGemLegs`

- **Файл оригинала:** `common/item/kami/armor/ItemGemLegs.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothLegsGem"`
- **Рецепт:** инфузия, нестабильность 13
- **Аспекты:** FIRE 50, ARMOR 32, HEAL 32, ENERGY 32, LIGHT 64, GREED 16, ELDRITCH 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Блокируется:** `ItemBrightNitor`
- **Использует уже портированное:** `ItemFocusSmelt`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemLegs`

### 45. `ItemInfusedPotion`

- **Файл оригинала:** `common/item/ItemInfusedPotion.java`
- **Наследует:** `ItemPotion`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedPotion"`
- **Рецепт:** верстак
- **Аспекты:** AURA 5, AIR 5, AURA 5, FIRE 5, AURA 5, EARTH 5, AURA 5, WATER 5
- **Родитель в дереве исследований:** `FIRE_PERDITIO,
  FIRE_ORDO,
  FIRE_IGNIS,
  FIRE_TERRA,
  FIRE_AER,
  FIRE_AQUA`
- **Блокируется:** `ItemInfusedGrain`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedPotion`

### 46. `BlockSummon`

- **Файл оригинала:** `common/block/BlockSummon.java`
- **Наследует:** `Block`, реализует `ITTinkererBlock`
- **Имя регистрации:** `"spawner"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 50, ENTROPY 50
- **Родитель в дереве исследований:** `BLOOD_SWORD`
- **Блокируется:** `BlockSummon`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockSummon`

### 47. `ItemFocusRecall`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusRecall.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusRecall"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 100, ELDRITCH 64, MAGIC 50
- **Родитель в дереве исследований:** `WARP_GATE`
- **Блокируется:** `ItemFocusRecall`, `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusRecall`

### 48. `ItemSkyPearl`

- **Файл оригинала:** `common/item/kami/ItemSkyPearl.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"skyPearl"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 32, ELDRITCH 32, FLIGHT 32, AIR 16
- **Блокируется:** `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSkyPearl`
