# TC4 → 1.12.2 Port Spec / Reverse-Engineering Reference

Живой документ. На каждый блок/предмет — карточка с его оригинальным поведением (реверс с
декомпила `decompiled/`) и статусом порта. Пишется **параллельно с портом**: спека для порта
сейчас + референс механик для Phase 3+.

## Легенда статусов
- ✅ **DONE** — поведение портировано верно для текущей фазы.
- 🟡 **STUB** — блок/предмет зарегистрирован и выглядит правильно, механика помечена `TODO Phase N`.
- ⛔ **TODO** — ещё не портирован.

## Формат карточки
```
### <имя> — <рег.имя> | статус
- Material / Sound / Hardness / Resistance / Light
- Collision/BoundingBox
- Meta: <что кодирует мета, диапазон>
- Drops
- TileEntity: <класс или нет>
- Interactions/Functions: <связи с другими блоками/предметами/системами>
- Port notes: <как портировано, что отложено>
```

---

# БЛОКИ

## Декор — DONE (Phase 1)

### blockCustomOre — customore | ✅ DONE (visual), 🟡 mechanics
- Material ROCK · Sound STONE · harvest pickaxe lvl2 (meta 0 и 7)
- Meta 0..7: руды (cinnabar, инфьюзд-ore ×6 primal, амбер?). Инфьюзд 1..6 tint = ItemShard.COLORS.
- Drops: TODO Phase 2/3 (кластеры/шарды). Сейчас self.
- Interactions: инфьюзд-руда роняет shard соответствующего аспекта (Phase 3).

### blockCosmeticSolid — cosmeticsolid | ✅ DONE
- Material ROCK · Sound STONE · Meta 0..15 декоративные каменные блоки (в т.ч. arcane stone=7, eldritch=11 — база для лестниц).

### blockCosmeticOpaque — cosmeticopaque | ✅ DONE
- Material ROCK · Sound STONE · Meta 0..2.

### blockMagicalLog — magicallog | ✅ DONE
- Material WOOD · Sound WOOD · Meta 0=greatwood, 1=silverwood. TODO Phase 2: axis/leaves-decay/дерево-ген.

### blockMagicalLeaves — magicalleaves | ✅ DONE (tint)
- Material LEAVES · Sound PLANT · Meta 0=greatwood (biome foliage tint), 1=silverwood (fixed 0x8899AA).
- TODO Phase 2: decay, drop саженцев/палок.

### blockCustomPlant — customplant | ✅ DONE (visual)
- Sound PLANT · Meta 0..5 (shimmerleaf, cinderpearl, mana bean pod?, greatwood/silverwood sapling, vishroom). TODO Phase 2/3: рост, bonemeal, дропы.

### blockCrystal — crystal | ✅ DONE (visual+collision+light+sound)
- Material GLASS · **Sound custom "crystal" (1.0/1.0)** · Hardness 0.3 · Resistance 2.0 · Light 7
- Collision/BB: CLUSTER_AABB (0.15,0,0.15)-(0.85,0.95,0.85) · isOpaque/isFull false · layer CUTOUT
- Meta 0..5 = primal aspect (tint ItemShard.COLORS[meta+1]), 6 = mixed (без tint)
- Модель: статический **кластер из 6 наклонных осколков** (crystal.json), текстура `textures/models/crystal.png`, UV фасета [4,8,8,16], tintindex 0. Оригинал — анимированный TESR (TileCrystalRenderer: 1 центральный + 5 осколков, blend, мерцание) → **точный TESR = TODO Phase 3**; JSON — приближение формы.
- TileEntity: TileCrystal (aspE storage, vis-node link) — TODO Phase 3
- Interactions: растёт на нодах, крепится к грани как факел (func_149742_c), TESR, spark FX, дроп shard.

### blockTaint — taint | ✅ DONE (tint+sound)
- Material ROCK · **Sound custom "gore" (0.5/0.8)** · Hardness 0.5 · Resistance 2.0
- Meta 0=crust, 1=soil (grayscale, tint 0x6D4189 taint-biome), 2=flesh block
- TODO Phase 3: распространение, конверсия биома, урон сущностям.

### blockTaintFibres — taintfibres | ✅ DONE (light+sound)
- Bush · **Sound custom "gore" (0.5/0.8)** · Hardness 0 · Light: meta2=8, meta4=10
- Meta 0=fibres, 1=grass1, 2=grass2, 3=spore stalk
- TODO Phase 3: рост на taint soil, spore stalk, эффекты.

### Лестницы (arcane/eldritch/greatwood/silverwood) — stairs_* | ✅ DONE
- Vanilla BlockStairs, звук/твёрдость наследуются от блока-модели.

### Плиты wood/stone (+double) — slab_wood/slab_stone | ✅ DONE
- BlockSlab, VARIANT 0..1 (wood: greatwood/silverwood; stone: arcane/eldritch). Sound WOOD/STONE.
- Item-модели `models/item/slab_{wood,stone}_bottom_{0,1}.json` (parent block-модели) — были пропущены, чинило чёрную текстуру предмета в креативе.

---

## Декор — B1 (в работе)

### blockCandle — candle | 🟡 STUB (визуал+свет+коллизия done, infusion TODO)
- Material CIRCUITS · Sound CLOTH · Hardness 0.1 · **Light 14 (setLightLevel 0.95)**
- Collision: нет (getCollisionBoundingBox NULL) · BB (0.375,0,0.375)-(0.625,0.5,0.625) · isOpaque/Full false · CUTOUT
- Meta 0..15 = 16 цветов красителя, tint = Utils.colors[meta]
- Модель: восковой корпус (6,0,6)-(10,8,10) tint + **фитиль (7.6,8,7.6)-(8.4,10,8.4) текстура candlestub, без tint** (по BlockCandleRenderer iconStub)
- Placement: только на твёрдый верх блока снизу; ломается при потере опоры
- Particles: smoke+flame сверху (client)
- Interactions: `implements IInfusionStabiliser` → `canStabaliseInfusion=true` (стабилизирует инфьюжн-алтарь) — **TODO Phase 3**
- Port notes: BlockTC+META, tint через block/item color handler (Utils.colors). Партиклы/стабилизация — TODO.

### blockLootUrn — loot_urn | 🟡 STUB (визуал+коллизия done, loot TODO)
- Material CLAY · **Sound custom "urnbreak" (1.0/0.7)** · Hardness 0.15 · Resistance 0
- BB (0.125,0.0625,0.125)-(0.875,0.8125,0.875) · isOpaque/Full false · **layer CUTOUT**
- Модель: **3 короба** (по BlockLootUrnRenderer) — база (3,0,3)-(13,1,13), тело (2,1,2)-(14,13,14), горло (4,13,4)-(12,16,12); auto-UV, top=urn_top, бока=urn_side_{meta}
- Meta 0..2 = уровень лута; текстуры urn_top + urn_side_{0..2}
- Drops: `Utils.generateLoot(meta, rand)` ×(1+meta+rand(3)) — **TODO Phase 3**; сейчас роняет себя
- Interactions: генерация лута из loot-таблиц TC4 (Phase 3), спавн в подземельях (worldgen Phase 4).

### blockLootCrate — loot_crate | 🟡 STUB (визуал+коллизия done, loot TODO)
- Material WOOD · Sound WOOD · Hardness 0.15 · Resistance 0
- BB (0.0625,0,0.0625)-(0.9375,0.875,0.9375) · isOpaque/Full false · **layer CUTOUT**
- Модель: **один короб** (1,0,1)-(15,14,15) (по BlockLootCrateRenderer); auto-UV, top=crate_top, бока=crate_side_{meta}
- Meta 0..2 = уровень лута; текстуры crate_top + crate_side_{0..2}
- Drops/Interactions: как urn (Phase 3).

### blockEldritch — eldritch | ✅ DONE (визуал+свет+прочность), 🟡 mechanics
- Material ROCK · **Sound STONE** (field_149769_e) · base Hardness 50 · base Resistance 20000 (→blockResistance 60000)
- Мета 0..10, полноразмерный куб (0,0,0-1,1,1), cube_all. Только **meta 4 (Glowing Crusted Stone)** в креативе/дропе (func_149666_a).
- Текстуры per-meta (func_149691_a): 0-3=obsidiantile, 4=es_i_1 (анимир.), 5=es_i_2, 6=deco_1, 7=deco_3, 8=deco_2, 9=**crust** (= cosmeticsolid meta14), 10=es_5
- **Light**: 4/5/7→12, 6/8→5, 9→4, 10→0, else→8. **Hardness**: 4/5→2, 6→4, 7/8→−1(unbreak), 9/10→15, else→50. **Explosion res**: 4/5/9/10→30, 6→100, 7/8→MAX, else→12000
- Lang: оригинал не даёт имя meta6 → добавил "Glyphed Stone" (как meta5, для дебага). Item-модель только meta4.
- TileEntity (Phase 3): altar0/obelisk1/cap3/lock8/crab9/trap10. TODO Phase 3: eldritch-eye interaction, lock unlock, chain-collapse+explosion (func_149749_a), portableHole blacklist, **connected-texture per-face** (meta8 lock=deco_2/deco_3 by facing; meta10 trap=random es_5..8), spark/rune FX. Phase 4: dungeon worldgen.

### blockManaPod — mana_pod | ✅ DONE (2× care: форма/текстура/цвет/свет/звук), 🟡 mechanics
- **Worldgen-only**: НЕТ ItemBlock, НЕТ creative-таба (TC4 тоже не задаёт). Рег.имя mana_pod, extends BlockTC(Material.PLANTS).
- **Форма**: render type 1 = cross/X (block/cross). Collision/BB свисает сверху (maxY=1.0), растёт вниз по стадии: meta0 y0.75-1.0 … meta7 y0.125-1.0 (minY=W12,W10,W8,W6,W5,W4,W3,W2), x/z 0.25-0.75. isOpaque/Full false, layer CUTOUT.
- **Текстура**: meta0=manapod_stem_0, meta1=manapod_stem_1, meta2..7=manapod_stem_2.
- **Цвет**: без tint (оригинал не регистрирует color handler).
- **Свет**: = стадия роста (getLightValue = meta, 0..7).
- **Звук**: Block-дефолт **STONE** — TC4 никогда не вызывал setStepSound для пода (НЕ plant-звук). Оставлено 1:1, не «чиню».
- Hardness = 0.5/(8−meta) (спелее=прочнее). Опора: магическое бревно (LOG/LOG2/blockMagicalLog) сверху в MAGICAL-биоме; иначе ломается (neighborChanged/updateTick).
- TileEntity TileManaPod (aspect, checkGrowth) — TODO Phase 3. Drops: itemManaBean с аспектом (meta≥2, meta7 ×2 ~66%) — **TODO Phase 3** (сейчас дроп пустой).

---

## Устройства — B2 | 🟡 STUB (визуал+идентичность done, механика TODO Phase 3)
Все 15 через **общий `BlockDeviceStub extends BlockTC`** — инертные, куб `cube_all` с
репрезентативной текстурой на каждую creative-мету. Порт: material/sound/hardness/resistance/light,
creative-меты (`getSubBlocks`), opaque/full/layer, no-collision (airy). **TODO Phase 3**: TileEntity,
кастомные TESR-формы (тигель/трубы/зеркало/алтарь), GUI, эссенция/vis, banner-NBT (wooden meta8).
Blockstate 0..15 с фолбэком на `_0` для не-creative мет.

| блок | рег.имя | Mat | Sound | Hard/Res | Light | creative-меты | заметки |
|---|---|---|---|---|---|---|---|
| MetalDevice | metal_device | IRON | METAL | 3/17 | — | 0,1,2,3,5,7,8,9,12,13,14 | тигель/алембик/лампы/конструкт |
| WoodenDevice | wooden_device | WOOD | WOOD | 2.5/10 | — | 0,1,2,4,5,6,7,8 | меха/ухо/плита/бур/доски/баннер(8) |
| StoneDevice | stone_device | ROCK | STONE | 3/25 | — | 0,1,2,5,8,9,10,11,12,13,14 | алх.печь/пьедестал/матрица/спа |
| Table | table | WOOD | WOOD | 2.5/— | — | 0,14,15 | стол/деконструкция/верстак |
| Jar | jar | GLASS | GLASS | 0.3/— | **0.66→9** | 0,1,3 | warded/brain/void, transparent |
| Tube | tube | IRON | METAL | 0.5/5 | — | 0..7 | эссенция-трубы, transparent |
| Mirror | mirror | GLASS | GLASS | 1/10 | — | 0,6 | зеркало/эссенция, transparent |
| ArcaneFurnace | arcane_furnace | ROCK | STONE | 10/500 | **0.2→3** | 0 (нет таба) | Infernal Furnace multiblock |
| AlchemyFurnace | alchemy_furnace | IRON | METAL | 3/17 | — | 0 (нет таба) | Adv. Alchemical Furnace |
| ChestHungry | chest_hungry | WOOD | WOOD | 2.5/— | — | 0 | Hungry Chest |
| Lifter | lifter | WOOD | WOOD | 2.5/15 | — | 0 | Arcane Levitator |
| MagicBox | magic_box | WOOD | WOOD | 2.5/— | — | нет (пустой getSubBlocks) | item есть, в креативе нет |
| EssentiaReservoir | essentia_reservoir | IRON | METAL | 2/17 | — | 0 | Essentia Reservoir |
| Airy | airy | **MaterialAiry** | CLOTH | 0/— | — | 0 | невидимый (blank), no-collision, ставит рандом-ноду |
| Warded | warded | ROCK | STONE | **unbreak/999** | — | нет item | ставится Warding-фокусом, маскирует блок |

- **MaterialAiry** (`common/config`): порт TC4 — isSolid/blocksLight/isOpaque = false (блок невидим).
- Furnaces/MagicBox: ItemBlock есть (для /give+рецептов), но в креативе не показываются (нет таба / пустой getSubBlocks).
- Warded: единственный без ItemBlock. TODO Phase 3: TileWarded маскировка + Warding focus.
- Текстуры-репрезентанты по `getIcon` оригинала (metalbase/crucible/lamp*, pedestal*/al_furnace*, pipe_*, jar_*, mirrorframe*, furnace0 и т.д.).

---

## Спец-блоки — B3 | ✅ DONE (визуал+идентичность), 🟡 mechanics TODO Phase 3
4 блока с нестандартным рендером/базой. Дверь — полноценная (32-вариантный blockstate,
проверен против ванильного алгоритма). 3 worldgen-блока сделаны с 2× кропотливостью
(форма=collision, текстура=blank/invisible, цвет=N/A, свет, звук).

| блок | рег.имя | база | Mat | Sound | Hard/Res | Light | item | заметки |
|---|---|---|---|---|---|---|---|---|
| ArcaneDoor | arcane_door | **BlockDoor** | IRON | METAL | 15/999 | — | ItemDoor (`ItemArcaneDoor`) | текстуры adoorbot/adoortop; override getItemDropped+getItem (ваниль хардкодит); StateMap.ignore(POWERED) |
| Hole | hole | BlockDeviceStub | ROCK | GLASS | **unbreak/6e6** | 0.7 | нет | portable hole; meta0=blank, meta15=empty; no-collision (airy=true) |
| EldritchNothing | eldritch_nothing | **BlockInvisibleTC** | ROCK | CLOTH | **unbreak/6e6** | 0.2 | нет | INVISIBLE render; collision AABB 0.125..0.875 (осязаем, но невидим) |
| EldritchPortal | eldritch_portal | **BlockInvisibleTC** | MaterialAiry | STONE | **unbreak/2e5** | **1.0** | нет | INVISIBLE render; collision=null (полностью проходим) |

- **BlockInvisibleTC** (новый): `getRenderType→INVISIBLE`, isOpaque/isFullCube=false, getCollisionBoundingBox возвращает переданный AABB (null=нематериален). Блоку всё равно нужны blockstate+model (заглушка cube_all/blank) для загрузчика.
- **BlockDoor gotcha**: `getItem()` приватный, хардкодит ванильные двери → override `getItemDropped(state,rnd,fortune)` (UPPER→AIR, иначе itemArcaneDoor) + `getItem(world,pos,state)`. Ставится через ванильный `net.minecraft.item.ItemDoor`.
- **Blockstate двери** (проверен 1:1 с ванилью): facing idx east=0/south=90/west=180/north=270; StateMap сортирует ключи алфавитно (facing,half,hinge,open); (left,closed)→base y=idx; (right,closed)→base_rh y=idx; (left,open)→base_rh y=(idx+90)%360; (right,open)→base y=(idx+270)%360. base=arcane_door_*; base_rh=arcane_door_*_rh.
- **TODO Phase 3**: hole = переносной портал/удаление блоков; eldritch_nothing/portal = логика измерения Эльдрич (спавн, телепорт). Модели дают только загрузчик, в мире не рендерятся (INVISIBLE).

## Флюиды — B4 | ✅ DONE (визуал+идентичность), 🟡 behaviors TODO Phase 3
4 флюида через Forge `Fluid` + `BlockFluidClassic/Finite`. Все с ItemBlock + creative-табом
(как в TC4) — можно /give и ставить в мире. Рендер — канонический `forge:fluid` model.

| флюид (Fluid name) | рег.имя блока | база | Mat | Gas | Light | Density | Viscosity | Rarity | Quanta | заметки |
|---|---|---|---|---|---|---|---|---|---|---|
| fluxGoo | fluxgoo | **BlockFluidFinite** | WATER¹ | нет | 7 | 8 | 6000 | — | 8 | звук GORE; вязкая жижа флюкса |
| fluxGas | fluxgas | BlockFluidFinite | WATER¹ | **да** | 7 | **−4** | 2500 | — | 8 | densityDir=1 (поднимается вверх) |
| fluidPure | fluidpure | **BlockFluidClassic** | WATER | нет | 10 | (0) | 1000 | RARE | — | самовыравнивается как вода |
| fluidDeath | fluiddeath | BlockFluidFinite | WATER | нет | 8 | (0) | 1500 | RARE | **4** | конечные лужи |

¹ TODO Phase 3: заменить WATER→MaterialTaint для goo/gas.

- **Fluid setup** (ConfigBlocks static): `new Fluid(name, still, flowing)` → both textures = `blocks/<tex>` (одна PNG на still+flowing); chainable setGaseous/Luminosity/Density/Viscosity/Rarity. Регистрация через `registerFluid()` helper (guard `isFluidRegistered`).
- **Рендер** (ModelHandler.registerFluidModel): blockstate `{"forge_marker":1,"variants":{"fluid":{"model":"forge:fluid","custom":{"fluid":"<camelCaseName>"}}}}`; **важно**: `custom.fluid`=имя Fluid (camelCase «fluxGoo»), а имя ФАЙЛА blockstate=рег.имя (lowercase «fluxgoo»). StateMapperBase схлопывает LEVEL→один MRL(`thaumcraft:<рег.имя>`,`fluid`), тот же MRL ставится на item.
- **Mappings gotcha**: `ResourceLocation.getResourcePath()` (НЕ `getPath()` — та из поздних маппингов).
- **TODO Phase 3**: goo=замедление/кормление слизней/Vis Exhaustion/спред taint/испарение в gas; gas=слепота/Vis Exhaustion, истончается; death=урон dissolve+партиклы; pure=warp-ward стоящему в источнике; вёдра; displacement Tainted Fibres.

---

# ПРЕДМЕТЫ
(заполняется в фазе айтемов; сейчас большинство — заглушки без функций)
- itemResource (meta 0..18) — материалы; TODO поведение.
- itemShard (meta 0..6) — primal shards, tint. 
- itemManaBean — дроп mana pod, хранит аспект (Phase 3).
- itemTripleMeatTreat — еда.
