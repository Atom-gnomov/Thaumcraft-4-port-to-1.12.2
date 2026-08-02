# Очередь адопции FOREVA

Составлена 2026-08-02 сравнением **деревьев целиком** (881 общий файл,
`diff --strip-trailing-cr` против `origin/master` референса на `1286a89`),
а не по заголовкам коммитов. Это важно: проверка по заголовкам дважды дала
ложные цели — «фиксы авто-печи», «кластеры кристаллов» и вся кластер роб
оказались **уже адоптированными** (файлы байт-идентичны или расходятся только
стилем импортов).

Всего реально расходятся **145** общих файлов; из них **123** FOREVA трогала в
последней волне (2026-07-16…29). Ниже — они, ранжированные по размеру диффа.

## Как адоптировать (правила из PORTING_HANDOFF, кратко)

1. Диффать файл (`git -C ../tc4-foreva-ref show origin/master:<путь>`), читать
   **их код**, не заголовок коммита.
2. Наши намеренные правки не затирать: у части файлов дифф — это **мы**
   (TT-хуки в `ClientProxy`/`ConfigItems`/`ConfigBlocks`, маска жерла в
   `BlockArcaneFurnace`, наш `ObsidianTotemBakedModel`). Адопция — слияние, не
   копирование.
3. `⛔ FX beam/particle классы FOREVA` — их не брать целиком (конфликт
   ITCParticle, см. PORTING_HANDOFF); только точечные фиксы.
4. Перед адопцией — проверить историю: часть волны уже взята (`5f8ebc5` —
   каскады worldgen; тултипы контейнеров — в 1.1.44.0).

## Верх очереди — по пересечению с открытыми багами KNOWN_ISSUES

| Дифф | Файл(ы) | Что закрывает |
| ---: | --- | --- |
| 522 | `client/lib/RenderEventHandler.java` | **три открытых пункта разом**: маркеры голема (`mark/home.png`), превью Архитектора, подсветка защищённых блоков |
| 498+ | `common/tiles/TileArcaneBore.java` + `FXBoreParticles` + `GuiArcaneBore` | паритет бура (2d7e917, be867e8, 9955409, 8570ebb) |
| 268+67 | `ModelHoverHarness` + `Hover` | артефакты ховер-ранца; **разблокирует кольца молний магнита** (`iconLightningRing`) |
| 229+49 | `TileNode` + `TileNodeRenderer` | узлы в банках: видимость сквозь блоки, рост, банка показывает узел |
| 214 | `common/blocks/BlockCosmeticSolid.java` | сверить с нашим: у них тоже тотемы (`e03bda2` charged parity) — **частично наш дифф**, мержить внимательно |
| 171+ | `ItemThaumometerRenderer` + `ThaumometerPerspectiveModel` + `WandRenderCalibration` + `ItemThaumometer` | вся серия поз таумометра (~10 коммитов 07-26/28) |
| 159+124 | `TileDeconstructionTable` + рендерер + контейнер + GUI | сверить с нашей независимой реализацией 1.1.40.3 (у них: `ac353e9`, `e8935b9`, `6e84d75`) |
| 140+105 | `FXBeamWand`, `FXLightningBolt`, `FXBeam*` | луч у кончика жезла (`655375f`, `4b04c2f`, `52ec9bc`) — ⛔ точечно |
| 135 | `GuiFocalManipulator` | контролы манипулятора (`a3f5dd3`, `0c3aa62`) |
| 39 | `BlockEldritch` (+ модели crust) | **чёрный декор Далёких земель**; рецепт слияния в PORTING_HANDOFF (наш `getLightValue` сохранить!) |
| 46 | `ResearchManager` | уведомления об аспектах (`de888aa`) — с открытым пунктом про попап исследования |

## Остальное (меньше или инфраструктурное)

`Thaumcraft.java`/`CommonProxy`/`ClientProxy`/`ClientModelRegistry` (частью наш
дифф), `ItemWandCasting` (169), `ComponentWizardTower`/`ComponentBankerHome`
(деревня — `cabc0a4`, `44ec3d7`), `EventHandlerEntity` (85), биомы Eerie/
MagicalForest (цвета узлов — `68e1d14`), `BlockAiry` (66, заряженный узел —
`e03bda2`), potions ×7 (иконки/визуал), `TileCrucible`+рендерер (жидкость —
серия 07-24/25), `TileEssentiaReservoir`+рендерер, `TileVisRelay` (203),
`GuiResearchRecipe` (JEI-серия), `EntityPech`/`EntityCultist`/
`EntityTaintacle`/`EntityEldritchGuardian`, `BlockLoot`/`BlockCustomOre`
(`48c3747`), `BlockTaintFibres` (`e7297ee`, `3d62e16`), `MazeGenerator`/
`GenPassage` (`0b908be`), api-хвост (`ThaumcraftApi`, visnet, research) и
TC6-компат (`8732e9a`, `7d836f3`).

Полные списки: `git -C ../tc4-foreva-ref log 15972f0..origin/master` и дифф
деревьев по рецепту из шапки.

## Уже взято из этой волны

- `5f8ebc5` (наш) — каскады worldgen (`5cf6e7a`, `c4acc6c`, `ece68fd`).
- 1.1.44.0 — тултипы предметов в слотах (`14c3e8a`, `7688ebc`): шесть наших
  экранов, включая стол исследований и все TT.
- Проверено идентичным (адоптировано ранее или сделано независимо):
  рендерер улучшенной авто-печи, `ItemCrystalRenderer`, робы (`ItemRobeArmor`,
  `ItemVoidRobeArmor`, `ModelRobe` — расходятся только стилем), слои жерла
  адской печи (наш порядок уже совпадает с их `a4dc903`).
