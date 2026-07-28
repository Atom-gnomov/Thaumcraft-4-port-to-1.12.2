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

**Что в работе сейчас:** свободен. Газовая семья закрыта целиком (1.1.18.0).

**Состояние модуля:** 58 из 84 объектов каталога, версия 1.1.18.0.

---

## Не моё — не трогаю

- **Генерация мира** (`common/lib/world/**`, `common/lib/events/EventHandlerWorld.java`,
  `common/lib/world/biomes/**`) — там правки другого агента, на 2026-07-28
  незакоммиченные в общей рабочей копии. Я их не коммичу и не откатываю.
- **Клиентский рендер TC4** (`client/renderers/**`, `client/fx/**`) — 5 падающих
  guard-тестов оттуда, см. `KNOWN_ISSUES.md`. Это отдельное направление.

---

## Снятые заявки

_(пока пусто)_
