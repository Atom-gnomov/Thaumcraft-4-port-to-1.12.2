# Очередь портирования Thaumic Tinkerer
Сгенерировано скриптом `scripts/gen_tt_queue.py` из исходников оригинала
(`../tt-original-1.7.10`) и дерева этого порта — руками не заполнять,
перегенерировать после каждого захода.

**Состояние: портировано 48 из 84 объектов каталога, осталось 36.**

Порядок — по зависимостям: объект появляется после всего, что ему нужно.
«Связи» — компоненты рецепта, которые сами являются объектами TT.
Точные значения любого объекта — в [`TT_OBJECT_REFERENCE.md`](TT_OBJECT_REFERENCE.md).

> **Осторожно: таблица знает только про зависимости по рецепту.**
> Поведенческие связи она не видит, и их надо проверять глазами по
> исходнику. Пример: `ItemSkyPearl` числится свободным, но настраивается
> только кликом по `BlockWarpGate` — без портала это мёртвый предмет.
> Перед тем как брать объект, прочитай его класс целиком.

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
| 10 | `BlockSummon` | аркан | — | — | — |
| 11 | `BlockWarpGate` | инфузия | 8 | — | `BlockTransvectorDislocator`, `ItemKamiResource` |
| 12 | `ItemBlockFire` | нет рецепта | — | — | — |
| 13 | `ItemBlockWarpGate` | нет рецепта | — | — | — |
| 14 | `ItemBloodSword` | инфузия | 6 | — | — |
| 15 | `ItemFocusShadowbeam` | инфузия | 12 | — | `ItemFocusDeflect`, `ItemKamiResource` |
| 16 | `ItemFocusXPDrain` | инфузия | 12 | — | `ItemKamiResource`, `ItemXPTalisman` |
| 17 | `ItemGas` | тигель | — | `BlockGaseousLight`, `BlockGaseousShadow` | — |
| 18 | `ItemGasRemover` | аркан | — | — | `ItemDarkQuartz` |
| 19 | `ItemGemBoots` | инфузия | 13 | — | `ItemKamiResource` |
| 20 | `ItemGemChest` | инфузия | 13 | — | `ItemFocusDeflect`, `ItemFocusFlight`, `ItemKamiResource` |
| 21 | `ItemGemHelm` | инфузия | 13 | — | `ItemCleansingTalisman`, `ItemKamiResource` |
| 22 | `ItemGemLegs` | инфузия | 13 | — | `ItemBrightNitor`, `ItemFocusSmelt`, `ItemKamiResource` |
| 23 | `ItemIchorSwordAdv` | инфузия | 15 | — | `ItemIchorSword`, `ItemKamiResource` |
| 24 | `ItemIchorclothArmor` | аркан | — | — | `ItemKamiResource` |
| 25 | `ItemIchorclothArmorAdv` | нет рецепта | — | — | — |
| 26 | `ItemInfusedGrain` | нет рецепта | — | — | — |
| 27 | `ItemInfusedInkwell` | инфузия + верстак | 2 | — | — |
| 28 | `ItemInfusedSeeds` | инфузия | 5 | — | — |
| 29 | `ItemMobAspect` | инфузия + верстак | 4 | — | — |
| 30 | `ItemMobDisplay` | нет рецепта | — | — | — |
| 31 | `ItemModFocus` | нет рецепта | — | — | — |
| 32 | `ItemModKamiFocus` | нет рецепта | — | — | — |
| 33 | `ItemShareBook` | верстак | — | — | — |
| 34 | `ItemSkyPearl` | инфузия | — | — | `ItemKamiResource` |
| 35 | `ItemFocusRecall` | инфузия | — | `ItemSkyPearl` | `ItemKamiResource` |
| 36 | `ItemInfusedPotion` | тигель | — | `ItemInfusedGrain` | — |

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
- **Имя регистрации:** —
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
- **Рецепт:** инфузия
- **Нестабильность:** 4
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

### 10. `BlockSummon`

- **Файл оригинала:** `common/block/BlockSummon.java`
- **Наследует:** `Block`, реализует `ITTinkererBlock`
- **Имя регистрации:** `"spawner"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 50, ENTROPY 50
- **Родитель в дереве исследований:** `BLOOD_SWORD`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockSummon`

### 11. `BlockWarpGate`

- **Файл оригинала:** `common/block/kami/BlockWarpGate.java`
- **Наследует:** `BlockModContainer`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** инфузия
- **Нестабильность:** 8
- **Аспекты:** TRAVEL 64, ELDRITCH 50, FLIGHT 50
- **Родитель в дереве исследований:** `ICHORCLOTH_CHEST_GEM`
- **Использует уже портированное:** `BlockTransvectorDislocator`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockWarpGate`

### 12. `ItemBlockFire`

- **Файл оригинала:** `common/item/ItemBlockFire.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockFire`

### 13. `ItemBlockWarpGate`

- **Файл оригинала:** `common/item/kami/ItemBlockWarpGate.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockWarpGate`

### 14. `ItemBloodSword`

- **Файл оригинала:** `common/item/ItemBloodSword.java`
- **Наследует:** `ItemSword`, реализует `IRepairable, ITTinkererItem`
- **Имя регистрации:** `"bloodSword"`
- **Рецепт:** инфузия
- **Нестабильность:** 6
- **Аспекты:** HUNGER 20, DARKNESS 5, SOUL 10, MAN 6
- **Родитель в дереве исследований:** `CLEANSING_TALISMAN`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBloodSword`

### 15. `ItemFocusShadowbeam`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusShadowbeam.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusShadowbeam"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** DARKNESS 65, ELDRITCH 32, MAGIC 50, WEAPON 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusShadowbeam`

### 16. `ItemFocusXPDrain`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusXPDrain.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusXPDrain"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** MIND 65, TAINT 16, MAGIC 50, AURA 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemKamiResource`, `ItemXPTalisman`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusXPDrain`

### 17. `ItemGas`

- **Файл оригинала:** `common/item/ItemGas.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `setBlock == ThaumicTinkerer.registry.getFirstBlockFromClass(BlockGaseousShadow.class) ? "gaseousShadowItem" : "gaseousLightItem"`
- **Рецепт:** тигель
- **Аспекты:** LIGHT 16, AIR 10, MOTION 8
- **Родитель в дереве исследований:** `GASEOUS_LIGHT`
- **Блокируется:** `BlockGaseousLight`, `BlockGaseousShadow`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGas`

### 18. `ItemGasRemover`

- **Файл оригинала:** `common/item/ItemGasRemover.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"gasRemover"`
- **Рецепт:** аркан
- **Аспекты:** AIR 2, ORDER 2
- **Родитель в дереве исследований:** `GASEOUS_SHADOW`
- **Использует уже портированное:** `ItemDarkQuartz`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGasRemover`

### 19. `ItemGemBoots`

- **Файл оригинала:** `common/item/kami/armor/ItemGemBoots.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothBootsGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 13
- **Аспекты:** EARTH 50, ARMOR 32, MINE 32, MOTION 32, LIGHT 64, PLANT 16, TRAVEL 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemBoots`

### 20. `ItemGemChest`

- **Файл оригинала:** `common/item/kami/armor/ItemGemChest.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothChestGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 13
- **Аспекты:** AIR 50, ARMOR 32, FLIGHT 32, ORDER 32, LIGHT 64, ELDRITCH 16, SENSES 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemFocusFlight`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemChest`

### 21. `ItemGemHelm`

- **Файл оригинала:** `common/item/kami/armor/ItemGemHelm.java`
- **Наследует:** —
- **Имя регистрации:** `"ichorclothHelmGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 13
- **Аспекты:** WATER 50, ARMOR 32, HUNGER 32, AURA 32, LIGHT 64, FLESH 16, MIND 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemCleansingTalisman`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemHelm`

### 22. `ItemGemLegs`

- **Файл оригинала:** `common/item/kami/armor/ItemGemLegs.java`
- **Наследует:** `ItemIchorclothArmorAdv`
- **Имя регистрации:** `"ichorclothLegsGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 13
- **Аспекты:** FIRE 50, ARMOR 32, HEAL 32, ENERGY 32, LIGHT 64, GREED 16, ELDRITCH 16
- **Родитель в дереве исследований:** `ICHORCLOTH_ARMOR`
- **Использует уже портированное:** `ItemBrightNitor`, `ItemFocusSmelt`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGemLegs`

### 23. `ItemIchorSwordAdv`

- **Файл оригинала:** `common/item/kami/tool/ItemIchorSwordAdv.java`
- **Наследует:** `ItemIchorSword`, реализует `IAdvancedTool`
- **Имя регистрации:** `"ichorSwordGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 15
- **Аспекты:** AIR 50, HUNGER 64, SOUL 32, WEAPON 32, ENERGY 32, ORDER 16, CRYSTAL 16
- **Родитель в дереве исследований:** `ICHOR_TOOLS`
- **Использует уже портированное:** `ItemIchorSword`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorSwordAdv`

### 24. `ItemIchorclothArmor`

- **Файл оригинала:** `common/item/kami/armor/ItemIchorclothArmor.java`
- **Наследует:** —
- **Имя регистрации:** `switch (armorType) { case 3: "ichorclothBoots"; case 2: "ichorclothLegs"; case 1: "ichorclothChest"; case 0: "ichorclothHelm"; default: "INVAlID ARMOR TYPE"; }`
- **Рецепт:** аркан
- **Аспекты:** WATER 75
- **Родитель в дереве исследований:** `ICHOR_CLOTH`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorclothArmor`

### 25. `ItemIchorclothArmorAdv`

- **Файл оригинала:** `common/item/kami/armor/ItemIchorclothArmorAdv.java`
- **Наследует:** `ItemIchorclothArmor`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorclothArmorAdv`

### 26. `ItemInfusedGrain`

- **Файл оригинала:** `common/item/ItemInfusedGrain.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"infusedGrain"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedGrain`

### 27. `ItemInfusedInkwell`

- **Файл оригинала:** `common/item/ItemInfusedInkwell.java`
- **Наследует:** `ItemBase`, реализует `IScribeTools`
- **Имя регистрации:** `"infusedInkwell"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 2
- **Аспекты:** VOID 8, DARKNESS 8
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedInkwell`

### 28. `ItemInfusedSeeds`

- **Файл оригинала:** `common/item/ItemInfusedSeeds.java`
- **Наследует:** `ItemSeeds`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedSeeds"`
- **Рецепт:** инфузия
- **Нестабильность:** 5
- **Аспекты:** CROP 32, HARVEST 32
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedSeeds`

### 29. `ItemMobAspect`

- **Файл оригинала:** `common/item/ItemMobAspect.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobAspect"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 4
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobAspect`

### 30. `ItemMobDisplay`

- **Файл оригинала:** `common/item/ItemMobDisplay.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobDisplay"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobDisplay`

### 31. `ItemModFocus`

- **Файл оригинала:** `common/item/foci/ItemModFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModFocus`

### 32. `ItemModKamiFocus`

- **Файл оригинала:** `common/item/kami/foci/ItemModKamiFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModKamiFocus`

### 33. `ItemShareBook`

- **Файл оригинала:** `common/item/ItemShareBook.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"shareBook"`
- **Рецепт:** верстак
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemShareBook`

### 34. `ItemSkyPearl`

- **Файл оригинала:** `common/item/kami/ItemSkyPearl.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"skyPearl"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 32, ELDRITCH 32, FLIGHT 32, AIR 16
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSkyPearl`

### 35. `ItemFocusRecall`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusRecall.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusRecall"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 100, ELDRITCH 64, MAGIC 50
- **Родитель в дереве исследований:** `WARP_GATE`
- **Блокируется:** `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusRecall`

### 36. `ItemInfusedPotion`

- **Файл оригинала:** `common/item/ItemInfusedPotion.java`
- **Наследует:** `ItemPotion`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedPotion"`
- **Рецепт:** тигель
- **Аспекты:** AURA 5, AIR 5
- **Родитель в дереве исследований:** `FIRE_PERDITIO`
- **Блокируется:** `ItemInfusedGrain`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedPotion`
