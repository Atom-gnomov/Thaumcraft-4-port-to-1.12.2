# Очередь портирования Thaumic Tinkerer
Сгенерировано скриптом `scripts/gen_tt_queue.py` из исходников оригинала
(`../tt-original-1.7.10`) и дерева этого порта — руками не заполнять,
перегенерировать после каждого захода.

**Состояние: портировано 59 из 79, осталось 20.**

В каталоге 84 объектов; 5 из них вычеркнуты как недостижимые и в счёт
не идут — см. «Вычеркнуто» в конце.

Порядок — по зависимостям: объект появляется после всего, что ему нужно.
«Связи» — компоненты рецепта, которые сами являются объектами TT.
Точные значения любого объекта — в [`TT_OBJECT_REFERENCE.md`](TT_OBJECT_REFERENCE.md).

> **Осторожно: таблица знает только про зависимости по рецепту.**
> Поведенческие связи она не видит, и их надо проверять глазами по
> исходнику. Пример: `ItemSkyPearl` числится свободным, но настраивается
> только кликом по `BlockWarpGate` — без портала это мёртвый предмет.
> Перед тем как брать объект, прочитай его класс целиком.

| # | Объект | Рецепт | Неста­бильность | Регистрация | Зависит от (ещё нет) | Использует (уже есть) |
|---|---|---|---|---|---|---|
| 1 | `BlockInfusedGrain` | нет рецепта | — | всегда | — | — |
| 2 | `BlockMobilizer` | инфузия | 4 | всегда | — | — |
| 3 | `BlockMobilizerRelay` | аркан | — | всегда | — | — |
| 4 | `BlockSummon` | аркан | — | всегда | — | — |
| 5 | `BlockWarpGate` | инфузия | 8 | по конфигу enableKami | — | `BlockTransvectorDislocator`, `ItemKamiResource` |
| 6 | `ItemBlockFire` | нет рецепта | — | всегда | — | — |
| 7 | `ItemBloodSword` | инфузия | 6 | всегда | — | — |
| 8 | `ItemFocusShadowbeam` | инфузия | 12 | всегда | — | `ItemFocusDeflect`, `ItemKamiResource` |
| 9 | `ItemFocusXPDrain` | инфузия | 12 | всегда | — | `ItemKamiResource`, `ItemXPTalisman` |
| 10 | `ItemIchorSwordAdv` | инфузия | 15 | всегда | — | `ItemIchorSword`, `ItemKamiResource` |
| 11 | `ItemInfusedGrain` | нет рецепта | — | всегда | — | — |
| 12 | `ItemInfusedInkwell` | инфузия + верстак | 2 | всегда | — | — |
| 13 | `ItemInfusedSeeds` | инфузия | 5 | всегда | — | — |
| 14 | `ItemMobAspect` | инфузия + верстак | 4 | всегда | — | — |
| 15 | `ItemModFocus` | нет рецепта | — | всегда | — | — |
| 16 | `ItemModKamiFocus` | нет рецепта | — | по конфигу enableKami | — | — |
| 17 | `ItemShareBook` | верстак | — | всегда | — | — |
| 18 | `ItemSkyPearl` | инфузия | — | всегда | — | `ItemKamiResource` |
| 19 | `ItemFocusRecall` | инфузия | — | всегда | `ItemSkyPearl` | `ItemKamiResource` |
| 20 | `ItemInfusedPotion` | тигель | — | всегда | `ItemInfusedGrain` | — |

---

## Уже перенесено

Полный список того, что в моде есть. Считается по дереву исходников, а не
по памяти: объект считается перенесённым, когда в порту существует класс с
его именем (или тем, на которое он был переименован — таблица переименований
живёт в `scripts/tt_common.py`).

**Ресурсы и базовые блоки** — 6

- `BlockDarkQuartz` — "darkQuartz"
- `BlockDarkQuartzSlab` — field_150004_a ? "darkQuartzSlabFull" : "darkQuartzSlab"
- `BlockDarkQuartzStairs` — "darkQuartzStairs"
- `ItemDarkQuartz` — "darkQuartzItem"
- `ItemDarkQuartzBlock` → у нас `BlockDarkQuartzItem` — "darkQuartz"
- `ItemDarkQuartzSlab` → у нас `BlockDarkQuartzSlab` — "darkQuartzSlab"

**Механизмы (блоки)** — 10

- `BlockAnimationTablet` — "animationTablet"
- `BlockEnchanter` — "enchanter"
- `BlockForcefield` — "forcefield"
- `BlockFunnel` — "funnel"
- `BlockGas` — —
- `BlockMagnet` — "magnet"
- `BlockPlatform` — "platform"
- `BlockRepairer` — "repairer"
- `BlockTransvectorDislocator` — "dislocator"
- `BlockTransvectorInterface` — "interface"

**Элементальные костры** — 6

- `BlockFireAir` — "fireAir"
- `BlockFireChaos` — "fireChaos"
- `BlockFireEarth` — "fireEarth"
- `BlockFireIgnis` — "fireFire"
- `BlockFireOrder` — "fireOrder"
- `BlockFireWater` — "fireWater"

**Трансвекторы** — 1

- `ItemConnector` → у нас `ItemTransvectorConnector` — "connector"

**Фокусы палочки** — 7

- `ItemFocusDeflect` → у нас `FocusDeflect` — "focusDeflect"
- `ItemFocusDislocation` → у нас `FocusDislocation` — "focusDislocation"
- `ItemFocusEnderChest` → у нас `FocusEnderChest` — "focusEnderChest"
- `ItemFocusFlight` → у нас `FocusFlight` — "focusFlight"
- `ItemFocusHeal` → у нас `FocusHeal` — "focusHeal"
- `ItemFocusSmelt` → у нас `FocusSmelt` — "focusSmelt"
- `ItemFocusTelekinesis` → у нас `FocusTelekinesis` — "focusTelekinesis"

**Предметы** — 9

- `ItemBlockMagnet` → у нас `BlockMagnetItem` — "magnet"
- `ItemBrightNitor` — "brightNitor"
- `ItemCleansingTalisman` — "cleansingTalisman"
- `ItemGas` — setBlock == ThaumicTinkerer.registry.getFirstBlockFromClass(BlockGaseousShadow.class) ? "gaseousShadowItem" : "gaseousLightItem"
- `ItemGasRemover` — "gasRemover"
- `ItemRevealingHelm` — "revealingHelm"
- `ItemSoulMould` — "soulMould"
- `ItemSpellCloth` — "spellCloth"
- `ItemXPTalisman` → у нас `ItemXpTalisman` — "xpTalisman"

**KAMI — ресурсы и предметы** — 7

- `BlockBedrockPortal` — "bedrockPortal"
- `ItemBlockTalisman` — "blockTalisman"
- `ItemCatAmulet` — "catAmulet"
- `ItemIchorPouch` — "ichorPouch"
- `ItemKamiResource` — "kamiResource"
- `ItemPlacementMirror` — "placementMirror"
- `ItemProtoclay` — "protoclay"

**KAMI — броня** — 6

- `ItemGemBoots` — "ichorclothBootsGem"
- `ItemGemChest` — "ichorclothChestGem"
- `ItemGemHelm` — "ichorclothHelmGem"
- `ItemGemLegs` — "ichorclothLegsGem"
- `ItemIchorclothArmor` — switch (armorType) { case 3: "ichorclothBoots"; case 2: "ichorclothLegs"; case 1: "ichorclothChest"; case 0: "ichorclothHelm"; default: "INVAlID ARMOR TYPE"; }
- `ItemIchorclothArmorAdv` — —

**KAMI — инструменты** — 7

- `ItemIchorAxe` — "ichorAxe"
- `ItemIchorAxeAdv` — "ichorAxeGem"
- `ItemIchorPick` — "ichorPick"
- `ItemIchorPickAdv` — "ichorPickGem"
- `ItemIchorShovel` — "ichorShovel"
- `ItemIchorShovelAdv` — "ichorShovelGem"
- `ItemIchorSword` — "ichorSword"

---

## Свойства каждого объекта

### 1. `BlockInfusedGrain`

- **Файл оригинала:** `common/block/BlockInfusedGrain.java`
- **Наследует:** `BlockCrops`, реализует `ITTinkererBlock`
- **Имя регистрации:** `if (aspect == Aspect.AIR) { "INFUSED_GRAIN_BASE + "Air""; } if (aspect == Aspect.EARTH) { "INFUSED_GRAIN_BASE + "Earth""; } if (aspect == Aspect.WATER) { "INFUSED_GRAIN_BASE + "Water""; } "INFUSED_GRAIN_BASE + "Fire""`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockInfusedGrain`

### 2. `BlockMobilizer`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizer.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive"`
- **Рецепт:** инфузия
- **Нестабильность:** 4
- **Аспекты:** MOTION 15, ORDER 20, MAGIC 15
- **Родитель в дереве исследований:** `MAGNETS`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizer`

### 3. `BlockMobilizerRelay`

- **Файл оригинала:** `common/block/mobilizer/BlockMobilizerRelay.java`
- **Наследует:** `BlockMod`
- **Имя регистрации:** `"Levitational Locomotive Relay"`
- **Рецепт:** аркан
- **Аспекты:** AIR 20, ORDER 5, EARTH 15
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockMobilizerRelay`

### 4. `BlockSummon`

- **Файл оригинала:** `common/block/BlockSummon.java`
- **Наследует:** `Block`, реализует `ITTinkererBlock`
- **Имя регистрации:** `"spawner"`
- **Рецепт:** аркан
- **Аспекты:** ORDER 50, ENTROPY 50
- **Родитель в дереве исследований:** `BLOOD_SWORD`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `BlockSummon`

### 5. `BlockWarpGate`

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

### 6. `ItemBlockFire`

- **Файл оригинала:** `common/item/ItemBlockFire.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockFire`

### 7. `ItemBloodSword`

- **Файл оригинала:** `common/item/ItemBloodSword.java`
- **Наследует:** `ItemSword`, реализует `IRepairable, ITTinkererItem`
- **Имя регистрации:** `"bloodSword"`
- **Рецепт:** инфузия
- **Нестабильность:** 6
- **Аспекты:** HUNGER 20, DARKNESS 5, SOUL 10, MAN 6
- **Родитель в дереве исследований:** `CLEANSING_TALISMAN`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBloodSword`

### 8. `ItemFocusShadowbeam`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusShadowbeam.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusShadowbeam"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** DARKNESS 65, ELDRITCH 32, MAGIC 50, WEAPON 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusShadowbeam`

### 9. `ItemFocusXPDrain`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusXPDrain.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusXPDrain"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** MIND 65, TAINT 16, MAGIC 50, AURA 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemKamiResource`, `ItemXPTalisman`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusXPDrain`

### 10. `ItemIchorSwordAdv`

- **Файл оригинала:** `common/item/kami/tool/ItemIchorSwordAdv.java`
- **Наследует:** `ItemIchorSword`, реализует `IAdvancedTool`
- **Имя регистрации:** `"ichorSwordGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 15
- **Аспекты:** AIR 50, HUNGER 64, SOUL 32, WEAPON 32, ENERGY 32, ORDER 16, CRYSTAL 16
- **Родитель в дереве исследований:** `ICHOR_TOOLS`
- **Использует уже портированное:** `ItemIchorSword`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorSwordAdv`

### 11. `ItemInfusedGrain`

- **Файл оригинала:** `common/item/ItemInfusedGrain.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"infusedGrain"`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedGrain`

### 12. `ItemInfusedInkwell`

- **Файл оригинала:** `common/item/ItemInfusedInkwell.java`
- **Наследует:** `ItemBase`, реализует `IScribeTools`
- **Имя регистрации:** `"infusedInkwell"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 2
- **Аспекты:** VOID 8, DARKNESS 8
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedInkwell`

### 13. `ItemInfusedSeeds`

- **Файл оригинала:** `common/item/ItemInfusedSeeds.java`
- **Наследует:** `ItemSeeds`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedSeeds"`
- **Рецепт:** инфузия
- **Нестабильность:** 5
- **Аспекты:** CROP 32, HARVEST 32
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedSeeds`

### 14. `ItemMobAspect`

- **Файл оригинала:** `common/item/ItemMobAspect.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"mobAspect"`
- **Рецепт:** инфузия + верстак
- **Нестабильность:** 4
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemMobAspect`

### 15. `ItemModFocus`

- **Файл оригинала:** `common/item/foci/ItemModFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModFocus`

### 16. `ItemModKamiFocus`

- **Файл оригинала:** `common/item/kami/foci/ItemModKamiFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Регистрация:** по конфигу enableKami
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModKamiFocus`

### 17. `ItemShareBook`

- **Файл оригинала:** `common/item/ItemShareBook.java`
- **Наследует:** `ItemBase`
- **Имя регистрации:** `"shareBook"`
- **Рецепт:** верстак
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemShareBook`

### 18. `ItemSkyPearl`

- **Файл оригинала:** `common/item/kami/ItemSkyPearl.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"skyPearl"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 32, ELDRITCH 32, FLIGHT 32, AIR 16
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSkyPearl`

### 19. `ItemFocusRecall`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusRecall.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusRecall"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 100, ELDRITCH 64, MAGIC 50
- **Родитель в дереве исследований:** `WARP_GATE`
- **Блокируется:** `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusRecall`

### 20. `ItemInfusedPotion`

- **Файл оригинала:** `common/item/ItemInfusedPotion.java`
- **Наследует:** `ItemPotion`, реализует `ITTinkererItem`
- **Имя регистрации:** `"infusedPotion"`
- **Рецепт:** тигель
- **Аспекты:** AURA 5, AIR 5
- **Родитель в дереве исследований:** `FIRE_PERDITIO`
- **Блокируется:** `ItemInfusedGrain`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemInfusedPotion`

---

## Вычеркнуто

Эти объекты не портируются и в счёт не входят. Решение принято сознательно,
а не по недосмотру, поэтому список висит здесь, а не пропадает.

- ~~`BlockAspectAnalyzer`~~ — только с ComputerCraft; этого мода в порту нет, и добавлять объект «просто так» значило бы дать игроку то, чего оригинал не даёт
- ~~`BlockGolemConnector`~~ — только с ComputerCraft; этого мода в порту нет, и добавлять объект «просто так» значило бы дать игроку то, чего оригинал не даёт
- ~~`BlockRPlacer`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
- ~~`ItemBlockWarpGate`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
- ~~`ItemMobDisplay`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
