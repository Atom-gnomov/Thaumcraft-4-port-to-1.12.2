# v5 | Инфо-каркас Unbound Technology для переноса в код
## Реестры, классы, ключи исследований, фазы работ. Источник истины для имплементации.

Modid: `unboundtech` | Пакет: `unboundtech` | Мод: отдельный репозиторий/Gradle-модуль
Зависимости Forge 1.12.2: `required-after:thaumcraft; required-after:ic2;
after:mets; after:advanced_solar_panels; after:srparasites` (id аддонов сверить по jar).

---

## 1. Скелет классов

```
unboundtech/
├── UnboundTech.java            @Mod, прокси, ModuleManager
├── CompatIds.java              все modid-строки в одном месте
├── module/                     Module (isEnabled = isModLoaded && config)
│   ├── ModuleCore, ModuleArmor, ModuleEntities, ModuleProdChains,
│   ├── ModuleWeapons, ModuleDungeon, ModuleRadiation,
│   └── compat: ModuleMets, ModuleAsp, ModuleSrp   (kami — в Core, TC4U всегда есть)
├── energy/  EnergyCanon.java   ВСЕ курсы EU/вис/аура константами (v5 §2 decisions)
├── block/  item/  entity/  tile/  research/  worldgen/  client/
```

Хуки, которые нужны от TC4 Unbound (API-запросы к основному моду):
аура чанка (чтение/запись/флюкс), регистрация компонентов жезлов (caps/rods),
события инфузии (только чтение — авто-вмешательство запрещено, decisions §4),
API големов (новый тип + аксессуары), регистрация вкладок Таумономикона.

---

## 2. Реестр блоков (registry name → класс → модуль/тир)

| Registry name | Блок | Модуль | Тир |
|---|---|---|---|
| `silver_ore` | Серебряная руда (oreSilver; см. phase1 §6.1) | core | 1 |
| `thaum_generator` | Таум-Генератор (аура→EU) | core | 3 |
| `aetheric_engine` | Эфирный Двигатель (EU→аура) | core | 3 |
| `thaumic_overclocker` | Таум-Оверклокер | core | 3 |
| `infernal_furnace_ind_controller` / `_casing` | Индустр. Адская Печь 3×3×3 | core | 3 |
| `essentia_ore_washer` | Эссент-Промыватель | prodchains | 2 |
| `powered_crucible` | Электротигель | prodchains | 3 |
| `flux_scrubber` | Флюкс-Скруббер | prodchains | 3 |
| `essentia_burner` | Эссент. Горелка | prodchains | 2 |
| `spirit_house` | Дом Духа | entities | 2 |
| `gauss_mortar` | Гаусс-Мортира | weapons | 4 |
| `awakening_pedestal` | Пьедестал Пробуждения | dungeon | — |
| `mechanist_pylon` | Энерго-пилон (арена) | dungeon | — |
| `pylon_shunt` | Шунт Механистов | dungeon | 3 |
| `mechanist_brick` (+step/pillar), `brass_vein_block`, `dead_machine`, `mechanist_core_trophy` | руинный декор | dungeon | — |
| `flux_condensate_block` | блок конденсата | prodchains | — |

## 3. Реестр предметов

| Registry name | Предмет | Модуль |
|---|---|---|
| `thaum_steel_ingot`, `thaum_carbon_mesh`, `vis_crystal`, `void_iridium` | материалы v3 | core |
| `electric_scribing_tools` | электрочернильница | core |
| `wand_cap_steel`, `wand_cap_iridium`, `wand_rod_carbon`, `wand_rod_iridium` | компоненты жезлов | core |
| `singulator`, `harness_electro`, `lead_thaum_lining` | баблсы/апгрейды | core |
| `focus_tesla`, `focus_purification`* | фокусы (*srp) | core |
| `aspect_sludge`, `flux_condensate` | пайплайн-предметы | prodchains |
| `charged_spark`, `faraday_phial` | техно-духи | entities |
| `volt_pike`, `storm_pike`, `flux_arquebus`, `long_arquebus` | оружие | weapons |
| `ammo_alumentum`, `ammo_nitor`, `ammo_vis`, `ammo_rad`*, `ammo_vaccine`** | патроны (*radiation, **srp) | weapons |
| `colossus_heart`, `archive_key` | босс-дроп | dungeon |
| `glowing_flesh`, `mutagen`, `dirty_alumentum`, `geiger_slime_bucket` | рад-контент | radiation |
| `nano_thaum_{helmet,chest,legs,boots}` и аналогично `void_quantum_*`, `ichor_quantum_*`, гогглы `goggles_{electric,nano,quantum,solar*}` | броня (*asp) | armor |

## 4. Реестр сущностей

| Registry name | Сущность | Модуль |
|---|---|---|
| `techno_spirit` | техно-дух | entities |
| `industrial_golem` | индустриальный голем | entities |
| `servitor` | сервитор (паук данжа) | dungeon |
| `flux_colossus` | босс | dungeon |
| `rad_zombie`, `rad_tentacle`, `geiger_slime` | рад-фауна | radiation |
| профессия `technomage` | житель | entities |
| снаряды: `thrown_pike`, `arquebus_bolt`, `flux_blob` | — | weapons/dungeon |

## 5. Ключи исследований (вкладка `UNBOUNDTECH` в Таумономиконе)

Канонический список = объединение: rt_1 (с правками аудита v3 §2) +
дельты `ic2_v4_entities.md` §5, `ic2_v4_prodchains.md` §7, `ic2_v5_weapons.md` §4,
`ic2_v5_dungeon_boss.md` §5, `ic2_v5_radiation.md` §5, минус вырезанное
(`MATRIX_STABILIZER`, `NITOR_THERMAL_GENERATOR`, `IC2_THAUM_CANNON`,
`GOLEM_DRILL_KIT`, `GOLEM_NANO_SHIELD`; `INFERNAL_FURNACE_5x5` → `INFERNAL_FURNACE_IND`).
Вкладки SRP/Синтез — по rt_2..rt_4 (модуль srp).

## 6. Конфиг (`config/unboundtech.cfg`)

```
[modules]     core/armor/entities/prodchains/weapons/dungeon/radiation/mets/asp/srp = true
[energy]      все курсы EnergyCanon (переопределяемы для сборок)
[dungeon]     rarity=2500, bossRespawnDays=7, arquebusBlockDamage=false
[overclock]   chunkLimit=4, perMachineLimit=2
```

## 7. Фазы реализации (обновляет чек-лист v3 items §7)

1. **Каркас**: мод, модули, конфиг, EnergyCanon, таблица аспектов IC2.
2. **Рецепты без блоков**: дробитель/компрессор/экстрактор, алюментум-топливо,
   нитор-тепло, аспекты. → *первый играбельный релиз 0.1*
3. **Core-блоки**: конвертеры, оверклокер+перегрузка, электрочернильница,
   компоненты жезлов. → *0.2*
4. **Prodchains**: промыватель, электротигель, скруббер, горелка, печь 3×3×3. → *0.3*
5. **Entities**: техно-духи, дом духа, житель, индустриальный голем. → *0.4*
6. **Weapons**: пика, аркебуза, патроны (без рад/вакцины). → *0.5*
7. **Armor**: гибридные сеты, гогглы, текстурный пайплайн. → *0.6*
8. **Radiation**: зоны, фауна, кратер, рад-патрон. → *0.7*
9. **Dungeon+Boss**: генерация, ловушки, пазл, арена, Колосс. → *0.8*
10. **Compat**: mets/asp рецепты, полировка, Теории. → *1.0*

**SRP заморожен** (decisions §7): `ModuleSrp` не пишется, srp-помеченные
предметы/режимы (`ammo_vaccine`, `focus_purification`, охранный режим
мортиры, Антиадаптатор) исключены из фаз до разморозки.

Правило: каждая фаза — отдельный дизайн-док уровня «переносится в код»
(структуры данных, формулы, состояния) по образцу этого файла, пишется
непосредственно перед фазой.
