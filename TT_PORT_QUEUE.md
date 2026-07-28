# Очередь портирования Thaumic Tinkerer
Сгенерировано скриптом `scripts/gen_tt_queue.py` из исходников оригинала
(`../tt-original-1.7.10`) и дерева этого порта — руками не заполнять,
перегенерировать после каждого захода.

**Состояние: портировано 56 из 84 объектов каталога, осталось 28.**

Порядок — по зависимостям: объект появляется после всего, что ему нужно.
«Связи» — компоненты рецепта, которые сами являются объектами TT.
Точные значения любого объекта — в [`TT_OBJECT_REFERENCE.md`](TT_OBJECT_REFERENCE.md).

> **Колонка «Регистрация» — читать первой.** `НЕ РЕГИСТРИРУЕТСЯ` значит, что
> `shouldRegister()` в оригинале возвращает `false` и объекта в игре нет
> вообще (`BlockRPlacer`). «Только с X» — он появляется лишь при
> установленном моде X (аспектализатор и голем-соединитель просят
> ComputerCraft). Портировать такое «просто по таблице» значит добавить в
> игру то, чего в оригинале нет.

> **Осторожно: таблица знает только про зависимости по рецепту.**
> Поведенческие связи она не видит, и их надо проверять глазами по
> исходнику. Пример: `ItemSkyPearl` числится свободным, но настраивается
> только кликом по `BlockWarpGate` — без портала это мёртвый предмет.
> Перед тем как брать объект, прочитай его класс целиком.

| # | Объект | Рецепт | Неста­бильность | Регистрация | Зависит от (ещё нет) | Использует (уже есть) |
|---|---|---|---|---|---|---|
| 1 | `BlockAspectAnalyzer` | аркан | — | только с ComputerCraft | — | — |
| 2 | `BlockForcefield` | нет рецепта | — | всегда | — | — |
| 3 | `BlockGolemConnector` | аркан | — | только с ComputerCraft | — | — |
| 4 | `BlockInfusedGrain` | нет рецепта | — | всегда | — | — |
| 5 | `BlockMobilizer` | инфузия | 4 | всегда | — | — |
| 6 | `BlockMobilizerRelay` | аркан | — | всегда | — | — |
| 7 | `BlockRPlacer` | аркан | — | НЕ РЕГИСТРИРУЕТСЯ | — | — |
| 8 | `BlockSummon` | аркан | — | всегда | — | — |
| 9 | `BlockWarpGate` | инфузия | 8 | по конфигу enableKami | — | `BlockTransvectorDislocator`, `ItemKamiResource` |
| 10 | `ItemBlockFire` | нет рецепта | — | всегда | — | — |
| 11 | `ItemBlockWarpGate` | нет рецепта | — | НЕ РЕГИСТРИРУЕТСЯ | — | — |
| 12 | `ItemBloodSword` | инфузия | 6 | всегда | — | — |
| 13 | `ItemFocusShadowbeam` | инфузия | 12 | всегда | — | `ItemFocusDeflect`, `ItemKamiResource` |
| 14 | `ItemFocusXPDrain` | инфузия | 12 | всегда | — | `ItemKamiResource`, `ItemXPTalisman` |
| 15 | `ItemGas` | тигель | — | всегда | — | `BlockGaseousLight`, `BlockGaseousShadow` |
| 16 | `ItemGasRemover` | аркан | — | всегда | — | `ItemDarkQuartz` |
| 17 | `ItemIchorSwordAdv` | инфузия | 15 | всегда | — | `ItemIchorSword`, `ItemKamiResource` |
| 18 | `ItemInfusedGrain` | нет рецепта | — | всегда | — | — |
| 19 | `ItemInfusedInkwell` | инфузия + верстак | 2 | всегда | — | — |
| 20 | `ItemInfusedSeeds` | инфузия | 5 | всегда | — | — |
| 21 | `ItemMobAspect` | инфузия + верстак | 4 | всегда | — | — |
| 22 | `ItemMobDisplay` | нет рецепта | — | НЕ РЕГИСТРИРУЕТСЯ | — | — |
| 23 | `ItemModFocus` | нет рецепта | — | всегда | — | — |
| 24 | `ItemModKamiFocus` | нет рецепта | — | по конфигу enableKami | — | — |
| 25 | `ItemShareBook` | верстак | — | всегда | — | — |
| 26 | `ItemSkyPearl` | инфузия | — | всегда | — | `ItemKamiResource` |
| 27 | `ItemFocusRecall` | инфузия | — | всегда | `ItemSkyPearl` | `ItemKamiResource` |
| 28 | `ItemInfusedPotion` | тигель | — | всегда | `ItemInfusedGrain` | — |

---

## Свойства каждого объекта

### 1. `BlockAspectAnalyzer`

- **Файл оригинала:** `common/block/BlockAspectAnalyzer.java`
- **Наследует:** `BlockModContainer`
- **Имя регистрации:** `"aspectAnalyzer"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 1, ENTROPY 1
- **Родитель в дереве исследований:** `PERIPHERALS`
- **Регистрация:** только с ComputerCraft
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockAspectAnalyzer`

### 2. `BlockForcefield`

- **Файл оригинала:** `common/block/BlockForcefield.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"forcefield"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockForcefield`

### 3. `BlockGolemConnector`

- **Файл оригинала:** `common/block/BlockGolemConnector.java`
- **Наследует:** `BlockCamo`
- **Имя регистрации:** `"golemConnector"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, ENTROPY 15
- **Родитель в дереве исследований:** `PERIPHERALS`
- **Регистрация:** только с ComputerCraft
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockGolemConnector`

### 4. `BlockInfusedGrain`

- **Файл оригинала:** `common/block/BlockInfusedGrain.java`
- **Наследует:** `BlockCrops`, реализует `ITTinkererBlock`
- **Имя регистрации:** `if (aspect == Aspect.AIR) { "INFUSED_GRAIN_BASE + "Air""; } if (aspect == Aspect.EARTH) { "INFUSED_GRAIN_BASE + "Earth""; } if (aspect == Aspect.WATER) { "INFUSED_GRAIN_BASE + "Water""; } "INFUSED_GRAIN_BASE + "Fire""`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockInfusedGrain`

### 5. `BlockMobilizer`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizer.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive"`
- **Рецепт:** инфузия
- **Нестабильность:** 4
- **Аспекты:** MOTION 15, ORDER 20, MAGIC 15
- **Родитель в дереве исследований:** `MAGNETS`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizer`

### 6. `BlockMobilizerRelay`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizerRelay.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive Relay"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, EARTH 15
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizerRelay`

### 7. `BlockRPlacer`

- **Файл оригинала:** `common/block/BlockRPlacer.java`
- **Наследует:** `BlockCamo`, реализует `IWandable`
- **Имя регистрации:** `"remotePlacer"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, EARTH 15, ENTROPY 5
- **Родитель в дереве исследований:** `ANIMATION_TABLET`
- **Регистрация:** НЕ РЕГИСТРИРУЕТСЯ
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockRPlacer`

### 8. `BlockSummon`

- **Файл оригинала:** `common/block/BlockSummon.java`
- **Наследует:** `Block`, реализует `ITTinkererBlock`
- **Имя регистрации:** `"spawner"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 50, ENTROPY 50
- **Родитель в дереве исследований:** `BLOOD_SWORD`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockSummon`

### 9. `BlockWarpGate`

- **Файл оригинала:** `common/block/kami/BlockWarpGate.java`
- **Наследует:** `BlockModContainer`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** инфузия
- **Нестабильность:** 8
- **Аспекты:** TRAVEL 64, ELDRITCH 50, FLIGHT 50
- **Родитель в дереве исследований:** `ICHORCLOTH_CHEST_GEM`
- **Регистрация:** по конфигу enableKami
- **Использует уже портированное:** `BlockTransvectorDislocator`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockWarpGate`

### 10. `ItemBlockFire`

- **Файл оригинала:** `common/item/ItemBlockFire.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockFire`

### 11. `ItemBlockWarpGate`

- **Файл оригинала:** `common/item/kami/ItemBlockWarpGate.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `"warpGate"`
- **Рецепт:** нет рецепта
- **Регистрация:** НЕ РЕГИСТРИРУЕТСЯ
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockWarpGate`

### 12. `ItemBloodSword`

- **Файл оригинала:** `common/item/ItemBloodSword.java`
- **Наследует:** `ItemSword`, реализует `IRepairable, ITTinkererItem`
- **Имя регистрации:** `"bloodSword"`
- **Рецепт:** инфузия
- **Нестабильность:** 6
- **Аспекты:** HUNGER 20, DARKNESS 5, SOUL 10, MAN 6
- **Родитель в дереве исследований:** `CLEANSING_TALISMAN`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBloodSword`

### 13. `ItemFocusShadowbeam`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusShadowbeam.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusShadowbeam"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** DARKNESS 65, ELDRITCH 32, MAGIC 50, WEAPON 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusShadowbeam`

### 14. `ItemFocusXPDrain`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusXPDrain.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusXPDrain"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** MIND 65, TAINT 16, MAGIC 50, AURA 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemKamiResource`, `ItemXPTalisman`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusXPDrain`

### 15. `ItemGas`

- **Файл оригинала:** `common/item/ItemGas.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `setBlock == ThaumicTinkerer.registry.getFirstBlockFromClass(BlockGaseousShadow.class) ? "gaseousShadowItem" : "gaseousLightItem"`
- **Рецепт:** тигель
- **Аспекты:** LIGHT 16, AIR 10, MOTION 8
- **Родитель в дереве исследований:** `GASEOUS_LIGHT`
- **Использует уже портированное:** `BlockGaseousLight`, `BlockGaseousShadow`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGas`

### 16. `ItemGasRemover`

- **Файл оригинала:** `common/item/ItemGasRemover.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"gasRemover"`
- **Рецепт:** аркан
- **Аспекты:** AIR 2, ORDER 2
- **Родитель в дереве исследований:** `GASEOUS_SHADOW`
- **Использует уже портированное:** `ItemDarkQuartz`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemGasRemover`

### 17. `ItemIchorSwordAdv`

- **Файл оригинала:** `common/item/kami/tool/ItemIchorSwordAdv.java`
- **Наследует:** `ItemIchorSword`, реализует `IAdvancedTool`
- **Имя регистрации:** `"ichorSwordGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 15
- **Аспекты:** AIR 50, HUNGER 64, SOUL 32, WEAPON 32, ENERGY 32, ORDER 16, CRYSTAL 16
- **Родитель в дереве исследований:** `ICHOR_TOOLS`
- **Использует уже портированное:** `ItemIchorSword`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorSwordAdv`

### 18. `ItemInfusedGrain`

- **Файл оригинала:** `common/item/ItemInfusedGrain.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"infusedGrain"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedGrain`

### 19. `ItemInfusedInkwell`

- **Файл оригинала:** `common/item/ItemInfusedInkwell.java`
- **Наследует:** `ItemBase`, реализует `IScribeTools`
- **Имя регистрации:** `"infusedInkwell"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 2
- **Аспекты:** VOID 8, DARKNESS 8
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedInkwell`

### 20. `ItemInfusedSeeds`

- **Файл оригинала:** `common/item/ItemInfusedSeeds.java`
- **Наследует:** `ItemSeeds`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedSeeds"`
- **Рецепт:** инфузия
- **Нестабильность:** 5
- **Аспекты:** CROP 32, HARVEST 32
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedSeeds`

### 21. `ItemMobAspect`

- **Файл оригинала:** `common/item/ItemMobAspect.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobAspect"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 4
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobAspect`

### 22. `ItemMobDisplay`

- **Файл оригинала:** `common/item/ItemMobDisplay.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobDisplay"`
- **Рецепт:** нет рецепта
- **Регистрация:** НЕ РЕГИСТРИРУЕТСЯ
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobDisplay`

### 23. `ItemModFocus`

- **Файл оригинала:** `common/item/foci/ItemModFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModFocus`

### 24. `ItemModKamiFocus`

- **Файл оригинала:** `common/item/kami/foci/ItemModKamiFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Регистрация:** по конфигу enableKami
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModKamiFocus`

### 25. `ItemShareBook`

- **Файл оригинала:** `common/item/ItemShareBook.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"shareBook"`
- **Рецепт:** верстак
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemShareBook`

### 26. `ItemSkyPearl`

- **Файл оригинала:** `common/item/kami/ItemSkyPearl.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"skyPearl"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 32, ELDRITCH 32, FLIGHT 32, AIR 16
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSkyPearl`

### 27. `ItemFocusRecall`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusRecall.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusRecall"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 100, ELDRITCH 64, MAGIC 50
- **Родитель в дереве исследований:** `WARP_GATE`
- **Блокируется:** `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusRecall`

### 28. `ItemInfusedPotion`

- **Файл оригинала:** `common/item/ItemInfusedPotion.java`
- **Наследует:** `ItemPotion`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedPotion"`
- **Рецепт:** тигель
- **Аспекты:** AURA 5, AIR 5
- **Родитель в дереве исследований:** `FIRE_PERDITIO`
- **Блокируется:** `ItemInfusedGrain`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedPotion`
