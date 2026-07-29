# Очередь портирования Thaumic Tinkerer
Сгенерировано скриптом `scripts/gen_tt_queue.py` из исходников оригинала
(`../tt-original-1.7.10`) и дерева этого порта — руками не заполнять,
перегенерировать после каждого захода.

**Состояние: портировано 73 из 79, осталось 3.**

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
| 1 | `BlockWarpGate` | инфузия | 8 | по конфигу enableKami | — | `BlockTransvectorDislocator`, `ItemKamiResource` |
| 8 | `ItemSkyPearl` | инфузия | — | всегда | — | `ItemKamiResource` |
| 9 | `ItemFocusRecall` | инфузия | — | всегда | `ItemSkyPearl` | `ItemKamiResource` |

---

## Вычеркнуто как не имеющее аналога в 1.12 (1.1.32.0)

Три «объекта» очереди были строительными лесами 1.7.10, а не контентом.
Писать их пустые копии — это форма ради формы, поэтому они вычеркнуты, а то,
что они несли, разложено по местам.

- **`ItemBlockFire`** — в 1.7.10 он существовал ради `registerIcons`, где по
  классу блока выбиралась иконка (`aer`, `ignis`, …). В 1.12 иконок нет, эту
  работу делает JSON-модель предмета. Все шесть напитанных огней уже имеют и
  `ItemBlock`, и свою модель (`blockfireair.json` и соседние).
- **`ItemModFocus`** и **`ItemModKamiFocus`** — абстрактные базы фокусов,
  которые в 1.7.10 руками делали то, что в 1.12 уже умеет
  `thaumcraft.api.wands.ItemFocusBasic`: орнамент, слой глубины, тултип
  стоимости, анимация. Своего у них было ровно три значения —
  `getItemEnchantability() == 5`, `getRarity()` и `getAnimation() == WAVE`, —
  и они перенесены прямо в фокусы KAMI (`FocusShadowbeam`, `FocusXpDrain`),
  с комментарием, откуда взялись.

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

**Механизмы (блоки)** — 14

- `BlockAnimationTablet` — "animationTablet"
- `BlockEnchanter` — "enchanter"
- `BlockForcefield` — "forcefield"
- `BlockFunnel` — "funnel"
- `BlockGas` — —
- `BlockInfusedGrain` — if (aspect == Aspect.AIR) { "INFUSED_GRAIN_BASE + "Air""; } if (aspect == Aspect.EARTH) { "INFUSED_GRAIN_BASE + "Earth""; } if (aspect == Aspect.WATER) { "INFUSED_GRAIN_BASE + "Water""; } "INFUSED_GRAIN_BASE + "Fire""
- `BlockMagnet` — "magnet"
- `BlockPlatform` — "platform"
- `BlockRepairer` — "repairer"
- `BlockSummon` — "spawner"
- `BlockMobilizer` — "Levitational Locomotive"
- `BlockMobilizerRelay` — "Levitational Locomotive Relay"
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

**Предметы** — 16

- `ItemBlockMagnet` → у нас `BlockMagnetItem` — "magnet"
- `ItemBloodSword` — "bloodSword"
- `ItemBrightNitor` — "brightNitor"
- `ItemCleansingTalisman` — "cleansingTalisman"
- `ItemGas` — setBlock == ThaumicTinkerer.registry.getFirstBlockFromClass(BlockGaseousShadow.class) ? "gaseousShadowItem" : "gaseousLightItem"
- `ItemGasRemover` — "gasRemover"
- `ItemInfusedGrain` — "infusedGrain"
- `ItemInfusedInkwell` — "infusedInkwell"
- `ItemInfusedPotion` — "infusedPotion"
- `ItemInfusedSeeds` — "infusedSeeds"
- `ItemMobAspect` — "mobAspect"
- `ItemRevealingHelm` — "revealingHelm"
- `ItemShareBook` — "shareBook"
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

### 1. `BlockWarpGate`

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

### 2. `ItemBlockFire`

- **Файл оригинала:** `common/item/ItemBlockFire.java`
- **Наследует:** `ItemBlock`, реализует `ITTinkererItem`
- **Имя регистрации:** `((ITTinkererBlock) field_150939_a).getBlockName()`
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemBlockFire`

### 3. `ItemFocusShadowbeam`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusShadowbeam.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusShadowbeam"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** DARKNESS 65, ELDRITCH 32, MAGIC 50, WEAPON 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemFocusDeflect`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusShadowbeam`

### 4. `ItemFocusXPDrain`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusXPDrain.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusXPDrain"`
- **Рецепт:** инфузия
- **Нестабильность:** 12
- **Аспекты:** MIND 65, TAINT 16, MAGIC 50, AURA 32
- **Родитель в дереве исследований:** `ROD_ICHORCLOTH`
- **Использует уже портированное:** `ItemKamiResource`, `ItemXPTalisman`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusXPDrain`

### 5. `ItemIchorSwordAdv`

- **Файл оригинала:** `common/item/kami/tool/ItemIchorSwordAdv.java`
- **Наследует:** `ItemIchorSword`, реализует `IAdvancedTool`
- **Имя регистрации:** `"ichorSwordGem"`
- **Рецепт:** инфузия
- **Нестабильность:** 15
- **Аспекты:** AIR 50, HUNGER 64, SOUL 32, WEAPON 32, ENERGY 32, ORDER 16, CRYSTAL 16
- **Родитель в дереве исследований:** `ICHOR_TOOLS`
- **Использует уже портированное:** `ItemIchorSword`, `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemIchorSwordAdv`

### 6. `ItemModFocus`

- **Файл оригинала:** `common/item/foci/ItemModFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModFocus`

### 7. `ItemModKamiFocus`

- **Файл оригинала:** `common/item/kami/foci/ItemModKamiFocus.java`
- **Наследует:** `ItemBase`, реализует `IWandFocus`
- **Имя регистрации:** —
- **Рецепт:** нет рецепта
- **Регистрация:** по конфигу enableKami
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemModKamiFocus`

### 8. `ItemSkyPearl`

- **Файл оригинала:** `common/item/kami/ItemSkyPearl.java`
- **Наследует:** `ItemKamiBase`
- **Имя регистрации:** `"skyPearl"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 32, ELDRITCH 32, FLIGHT 32, AIR 16
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemSkyPearl`

### 9. `ItemFocusRecall`

- **Файл оригинала:** `common/item/kami/foci/ItemFocusRecall.java`
- **Наследует:** `ItemModKamiFocus`
- **Имя регистрации:** `"focusRecall"`
- **Рецепт:** инфузия
- **Аспекты:** TRAVEL 100, ELDRITCH 64, MAGIC 50
- **Родитель в дереве исследований:** `WARP_GATE`
- **Блокируется:** `ItemSkyPearl`
- **Использует уже портированное:** `ItemKamiResource`
- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `ItemFocusRecall`

---

## Вычеркнуто

Эти объекты не портируются и в счёт не входят. Решение принято сознательно,
а не по недосмотру, поэтому список висит здесь, а не пропадает.

- ~~`BlockAspectAnalyzer`~~ — только с ComputerCraft; этого мода в порту нет, и добавлять объект «просто так» значило бы дать игроку то, чего оригинал не даёт
- ~~`BlockGolemConnector`~~ — только с ComputerCraft; этого мода в порту нет, и добавлять объект «просто так» значило бы дать игроку то, чего оригинал не даёт
- ~~`BlockRPlacer`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
- ~~`ItemBlockWarpGate`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
- ~~`ItemMobDisplay`~~ — `shouldRegister()` в оригинале возвращает `false` — объекта нет в игре и с оригиналом
