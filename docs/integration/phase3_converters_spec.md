# Фаза 3а | Спека конвертеров: Таум-Генератор и Эфирный Двигатель
## «Переносится в код». Основана на фактах из исходников порта (разведка 2026-08).

---

## 0. Ключевые факты порта (проверено по коду)

1. **Ауры чанков НЕТ.** `thaumcraft.api.aura.AuraHelper` / `AuraHandler` /
   `AuraChunk` — мёртвый TC6-фасад: ничего не читает, не пишет, не
   сохраняется (0 использований вне самих файлов). **Вся «аура» = узлы.**
   → Эфирный Двигатель заряжает УЗЛЫ. Все упоминания «ауры чанка» в v1–v5
   доках читать как «узлы»; перегрузка оверклокера (фаза 3б) будет
   спавнить `blockFluxGoo`/`blockFluxGas`, а не «флюкс в ауру».
2. **Узел**: `TileNode implements INode` — ДВА AspectList:
   `aspects` (текущее) и `aspectsBase` (ёмкость ПО-АСПЕКТНО,
   `getNodeVisBase(Aspect)`). Регенерация: +1 вис случайного недостающего
   аспекта раз в ~600 тиков (BRIGHT 400, PALE 900, FADING — никогда).
3. **Мутаторы узла**: `takeFromContainer(Aspect,int)` — всё-или-ничего;
   `addToContainer(Aspect,int)` — клампит по ёмкости, возвращает излишек.
   Оба НЕ шлют синк — после изменения обязателен
   `world.notifyBlockUpdate(pos, state, state, 3)`.
4. **Поиск узлов**: хелпера нет. Образец — `TileWandPedestal.findNodes()`:
   куб ±8, кэш `List<BlockPos>`, пересканирование раз в 100 тиков и только
   если прошлая попытка неудачна. `TileJarNode` исключать
   (`te instanceof INode && !(te instanceof TileJarNode)`).
5. **Ignis = `Aspect.FIRE`, Potentia = `Aspect.ENERGY`** (составной!) —
   в обычных узлах Potentia редка; генератор работает в основном на Ignis.
6. **Шаблон блока-машины**: `BlockContainer` + `PropertyDirection FACING`
   (горизонталь) + свой `PropertyBool ACTIVE` (meta: `facing | (active?8:0)`);
   тайл наследует `thaumcraft.api.TileThaumcraft` (переопределять только
   `readCustomNBT`/`writeCustomNBT`). Регистрация — по образцу
   `BlockRepairer`/`ConfigBlocks.TILE_REGISTRATIONS`; blockstate — как
   `blockanimationtablet.json` (4 поворота модели).
7. **API-дыры**: у `TileNode` нет геттеров `wait`/`regeneration` (не нужны);
   `setNodeVisBase(Aspect, short)` — короткий int, кастовать явно.

## 1. Таум-Генератор (`thaum_generator`) — аура → EU

- **Блок**: FACING+ACTIVE, светится слабо при работе (light 7), звук как
  генератор IC2. Тайл `TileThaumGenerator extends TileThaumcraft
  implements ITickable` + IC2-источник энергии.
- **IC2-сторона**: `ic2.api.energy.prefab.BasicSource(te, capacity, tier)` —
  tier 1 (LV, 32 EU/t), буфер **20,000 EU**. Load/unload/invalidate — по
  контракту BasicSource (проброс в NBT read/write). [допущение API — сверить]
- **Дренаж** (зеркалим Пьедестал):
  - кэш узлов ±8 как §0.4; попытка раз в **20 тиков** и только если
    буфер ≤ 18,000 EU (место под 1 вис);
  - из первого подходящего узла: `takeFromContainer(aspect, 1)` для
    `Aspect.FIRE` или `Aspect.ENERGY`, где
    `node.getAspects().getAmount(a) - 1 >= ceil(getNodeVisBase(a) * 0.2)`
    — **пол 20% ёмкости**, узлы не убиваются;
  - успех → буфер += `EnergyCanon.EU_PER_AURA_SELL` (2,000), синк узла
    (§0.3), ACTIVE=true; неудача по всем узлам → ACTIVE=false, флаг recalc.
- **Темп**: 2,000 EU / 20 тиков = 100 EU/т потенциально; ограничитель —
  эмиссия tier LV (32 EU/t) и пол 20%: обычный узел (~50 Ignis, пол 10)
  отдаёт 40 вис = 80,000 EU, дальше — скорость регенерации узла
  (+1 вис/30 сек ⇒ ~66 EU/t долговременно с одного узла; BRIGHT ~100 EU/t).
  Числа v1 «32/128 EU/t» достигаются сами: краткосрочно упираемся в LV-тир,
  долговременно — в реген узла. Отдельного кода «пробуждённых» узлов нет.
- **Антиферма**: при постановке и раз в 100 тиков сканить ±16 на другой
  `TileThaumGenerator`; найден → ACTIVE=false + строка в WAILA/тултип
  «интерференция». (Проверка дешёвая: по кэшу BlockPos, не тайлам.)
- **GUI нет** (фаза 3а): буфер и статус — в тултип-оверлей гогглов позже.

## 2. Эфирный Двигатель (`aetheric_engine`) — EU → узлы

- **Блок**: FACING+ACTIVE. Тайл + `ic2.api.energy.prefab.BasicSink(te,
  capacity, tier)`: tier 2 (MV, до 128 EU/t приём), буфер **40,000 EU**.
- **Работа** (раз в 20 тиков):
  - кэш узлов ±8 (тот же паттерн);
  - выбрать узел с недостающим аспектом (`getAmount(a) < getNodeVisBase(a)`),
    случайный недостающий аспект (как `rechargeOneMissingAspect` узла);
  - если буфер ≥ `EnergyCanon.EU_PER_AURA_BUY` (8,000): `addToContainer(a, 1)`,
    излишек==0 → списать 8,000 EU, синк узла, ACTIVE=true.
  - Ёмкость узла (`aspectsBase`) НЕ трогаем — восстановление ёмкости
    (лечение FADING-узлов через `setNodeVisBase`) — отдельное исследование
    фазы 6+, не сейчас.
- **Скорость**: 1 вис/сек максимум (8,000 EU/20т = 400 EU/t потребление
  пиково > MV-приёма ⇒ реально ~1 вис / 2.5 сек на MV-подводе). Это в
  ~15–30 раз быстрее естественного регена — за это игрок и платит.
- Вместе генератор+двигатель на одном узле = насос в обе стороны:
  цикл 2,000/8,000 = 25% ✔ (закон 2), самоокупаемости нет.

## 3. Исследования (rt_1, вкладка UNBOUNDTECH)

```
THAUM_ORE_MACERATION → VIS_TO_EU_GENERATOR   [арк. верстак: рецепт блока]
THAUM_IC2_INTRO      → EU_TO_VIS_ENGINE      [требует VIS_TO_EU_GENERATOR]
LORE_RESONANCE_LIMITS [лорное, secondary]    — «почему не ближе 16 блоков»
```
Рецепты (арканный верстак, `ThaumcraftApi.addArcaneCraftingRecipe`):
- Генератор: генератор IC2 + 4× таум-сталь* + 2× осколок Ignis + котёл?
  → *таум-сталь — фаза 3в; ВРЕМЕННО: слитки таумия (пометить TODO в коде).
- Двигатель: МФЭ-корпус... упрощённо: продв. схема IC2 + 4× таумий +
  2× осколок любой + жезловой набалдашник железный. Аспекты: по 25
  Ordo/Ignis/Aqua (генератор), Ordo/Perditio/Aer (двигатель).
  Точные рецепты — на усмотрение реализации, канон только курсы EU.

## 4. Ассеты

- `blockstates/thaum_generator.json`, `aetheric_engine.json` — шаблон
  `blockanimationtablet.json` (+ вариант `active=true` с эмиссив-текстурой
  «морды»). Текстуры: арт-гайд §2.5 (корпус таум-стали серый с пурпурным
  отливом, морда — руна/катушка).
- Лэнг ru/en: `tc.research_name/text.VIS_TO_EU_GENERATOR|EU_TO_VIS_ENGINE|
  LORE_RESONANCE_LIMITS` + страницы `unboundtech.research_page.*`.

## 5. Дополнения в IC2_API_ASSUMPTIONS

`ic2.api.energy.prefab.BasicSource(TileEntity, double capacity, int tier)`,
`BasicSink(TileEntity, double, int)`: методы `onLoad/invalidate/onChunkUnload`
(проброс из тайла), `readFromNBT/writeToNBT`, `addEnergy/useEnergy/
canUseEnergy/getEnergyStored`. Сверить точные имена при сборке.

## 6. Приёмка фазы 3а

1. Генератор у узла: копит EU, кабель уносит; узел не падает ниже 20%
   по-аспектной ёмкости; после выработки «излишка» темп ≈ регену узла.
2. Второй генератор в 16 блоках — оба показывают интерференцию, работает 0.
3. Двигатель на MV: узел восстанавливает недостающие аспекты, ~1 вис/2.5 c;
   FADING-узел пополняется до своей (пониженной) ёмкости, ёмкость не растёт.
4. Цикл генератор↔двигатель на одном узле убыточен (расход EU > выработки).
5. NBT: буферы и ACTIVE переживают перезаход; клиент видит содержимое узла
   актуальным (нет «застывших» цифр таумометра).
