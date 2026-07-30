# Кто что сейчас делает

Над репозиторием работает несколько агентов параллельно, на разных машинах и в
разных рабочих копиях. Этот файл — единственный способ не наступить друг другу
на руки. **Перед тем как взять задачу — посмотри сюда и допиши строку.**

Правила простые:

1. **Заявляй директории, а не задачи.** Столкновения происходят по файлам.
2. **Заявляй до начала работы**, не после. Заявка стоит одну строку и один пуш.
3. **Снимай заявку, когда закончил** — перечёркиванием, чтобы осталась история.
4. **Не коммить чужие файлы.** `git add -A` в общей рабочей копии затянет чужую
   незакоммиченную работу. Добавляй свои пути явно.
5. Если нужно тронуть чужую область — сначала допиши сюда, что и зачем.

---

## Активные заявки

### Ветка исследований TT — Claude (сессия аудита 2026-07-28)

**Заявлено:** 2026-07-28. Закрывает первый пункт `KNOWN_ISSUES` — без записей
Таумономикона бо́льшая часть рецептов модуля не срабатывает.

**Сделано в 1.1.25.0:** вкладка `TT_CATEGORY` и ветка семи фокусов. Заявка
остаётся активной — впереди поддеревья `DARK_QUARTZ`, `GASEOUS_LIGHT`,
`SPELL_CLOTH` и одиночки от ключей TC4 (порядок и дерево — в `KNOWN_ISSUES`).

Директории и файлы:

- `mod/src/main/java/thaumcraft/common/config/research/ConfigResearchTinkerer.java` (новый)
- `mod/src/main/java/thaumcraft/common/config/research/TinkererResearchItem.java` (новый)
- `mod/src/test/java/thaumcraft/common/config/research/TinkererResearchStaticGuardTest.java` (новый)
- `mod/src/main/java/thaumcraft/common/config/research/ConfigResearch.java` (только две строки вызова)
- `mod/src/main/resources/assets/thaumcraft/textures/misc/r_enchanting.png` (иконка вкладки)
- строки `ttresearch.*` в обоих `lang`

### Модуль Thaumic Tinkerer — Claude (сессия TT-порта)

**Заявлено:** 2026-07-28

Директории и файлы:

- `mod/src/main/java/thaumcraft/common/items/tinkerer/**`
- `mod/src/main/java/thaumcraft/common/blocks/tinkerer/**`
- `mod/src/main/java/thaumcraft/common/tiles/tinkerer/**`
- `mod/src/main/java/thaumcraft/common/lib/tinkerer/**`
- `mod/src/main/java/thaumcraft/common/lib/enchantment/tinkerer/**`
- `mod/src/main/java/thaumcraft/common/lib/world/dim/bedrock/**`
- `mod/src/main/java/thaumcraft/common/items/wands/foci/Focus*.java` (семь фокусов TT)
- `mod/src/main/java/thaumcraft/common/config/ConfigTinkerer.java`
- `mod/src/test/java/thaumcraft/common/items/wands/foci/ThaumicTinkererFociStaticGuardTest.java`
- `scripts/**` — генераторы документации TT
- `TT_OBJECT_REFERENCE.md`, `TT_PORT_QUEUE.md`, `TT_PORT_AUDIT.md`, `THAUMIC_TINKERER_PLAN.md`

Общие файлы, которые приходится трогать (правки точечные, конфликтуют редко —
но лучше предупредить перед своей правкой):

- `ConfigItems.java`, `ConfigBlocks.java` — только добавление своих полей в конец
- `CommonProxy.java`, `ClientProxy.java` — только свои GUI id и ветки `switch`
- `lang/en_us.lang`, `lang/ru_ru.lang` — только ключи `*.tinkerer.*`, `*.kami.*`, `ttmisc.*`
- `CHANGELOG.md`, `KNOWN_ISSUES.md` — своя запись сверху

**Что в работе сейчас:** свободен.

**Читать первым:** аудит 2026-07-28 нашёл, что рецепты модуля привязаны к
исследованиям, которых в порту нет, и поэтому не срабатывают в игре вовсе.
См. `KNOWN_ISSUES.md`, первый пункт. Ветка исследований TT — не «последняя
большая система», а то, без чего модуля фактически нет. Брать её раньше
оставшихся девяти объектов KAMI. После 1.1.25.0 достижимы 16 рецептов из 51,
висят ещё 35 на 30 ключах — список и дерево родителей там же.

**Состояние модуля:** 70 из 79 объектов перенесены, версия 1.1.25.0.

---

## Не моё — не трогаю

- **Генерация мира** (`common/lib/world/**`, `common/lib/events/EventHandlerWorld.java`,
  `common/lib/world/biomes/**`) — там правки другого агента, на 2026-07-28
  незакоммиченные в общей рабочей копии. Я их не коммичу и не откатываю.
- **Клиентский рендер TC4** (`client/renderers/**`, `client/fx/**`) — отдельное
  направление. (Пять guard-тестов оттуда, которые тут числились падающими, на
  2026-07-28 зелёные — пункт в `KNOWN_ISSUES` снят.)

---

## Снятые заявки

_(пока пусто)_
