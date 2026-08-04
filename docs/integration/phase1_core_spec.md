# Фаза 1 | Спецификация каркаса Unbound Technology (переносится в код)
## Цель фазы: мод запускается, вкладка открывается сканированием, аспекты на IC2-предметах, рецепты дробителя, алюментум-топливо. Релиз 0.1

---

## 1. Проект и сборка

- Отдельный репозиторий `Unbound-Technology`, ForgeGradle как в `mod/build.gradle`
  порта (тот же mappings/forge версии — скопировать блок из TC4U).
- Зависимости:
  - TC4 Unbound: deobf-jar в `libs/` (позже — maven через JitPack).
  - IC2 Experimental: maven `net.industrial-craft:industrialcraft-2:2.8.+:api`
    для компиляции, полный jar в `run/mods` для теста.
- `mcmod.info`: name «Unbound Technology», авторы, ссылка на TC4U.

```java
@Mod(modid = UnboundTech.MODID, name = "Unbound Technology",
     version = "0.1.0",
     dependencies = "required-after:thaumcraft;required-after:ic2;"
                  + "after:mets;after:advanced_solar_panels")
public class UnboundTech {
    public static final String MODID = "unboundtech";
    @SidedProxy(...) public static CommonProxy proxy;
    // preInit: UTConfig.load(cfg) → ModuleManager.init()
    // init:    ModuleManager.registerContent() (аспекты, рецепты, исследования)
    // postInit: ModuleManager.postInit() (рецепты, зависящие от чужого postInit)
}
```

## 2. Модули и конфиг

```java
public enum UTModule {
    CORE(null), PRODCHAINS(null), ENTITIES(null), WEAPONS(null),
    ARMOR(null), DUNGEON(null), RADIATION(null),
    METS("mets"), ASP("advanced_solar_panels"); // modid сверить по jar!

    private final String requiredMod;
    public boolean isEnabled() {
        return UTConfig.moduleEnabled(this)
            && (requiredMod == null || Loader.isModLoaded(requiredMod));
    }
}
```
- `CompatIds`: все modid-строки констант (единственное место правки).
- Конфиг `unboundtech.cfg`: категории `[modules]`, `[energy]`, `[overclock]`
  (см. skeleton §6). В фазе 1 читаются все, используются modules+energy.
- SRP: модуль отсутствует в enum (заморожен, decisions §7).

## 3. EnergyCanon (все курсы v5 — константы + конфиг-переопределение)

```java
public final class EnergyCanon {
    public static int EU_PER_AURA_SELL   = 2_000;   // аура → EU
    public static int EU_PER_AURA_BUY    = 8_000;   // EU → аура
    public static int EU_PER_VIS         = 20_000;  // EU → вис в жезле
    public static int EU_ALUMENTUM       = 40_000;  // топливо
    public static int EU_ESSENTIA_IGNIS  = 2_000;   // горелка (фаза 4)
    public static int EU_ESSENTIA_ARBOR  = 500;
    public static int EU_ESSENTIA_PERDITIO = 1_250;
    public static int EU_PERMUTATIO_AMP  = 5_000;   // массфаб (фаза 10)
    // sanity-check при загрузке: assert EU_PER_AURA_BUY >= 4*EU_PER_AURA_SELL
    // («второй закон»: цикл ≤ 25%) — лог WARN, если конфиг сломал баланс
}
```

## 4. Аспекты IC2-предметов

Доступ к предметам IC2 — только через `ic2.api.item.IC2Items.getItem(name, variant)`
(метаданные IC2 нестабильны; строки вариантов сверить по классу `ItemName`
конкретной версии при реализации — таблица ниже даёт целевые пары).

```java
private static void tag(String name, String variant, AspectList aspects) {
    ItemStack s = IC2Items.getItem(name, variant);
    if (s != null) ThaumcraftApi.registerObjectTag(s, aspects);
    else UTLog.warn("IC2 item not found: {}:{}", name, variant); // не крашим
}
```

| IC2Items (name / variant)* | Аспекты (v3 machines §1) |
|---|---|
| `cable` / `type:copper,insulation:0` | Metallum 2, Potentia 1 |
| `crafting` / `rubber` | Motus 1, Aqua 1 |
| `crafting` / `circuit` | Machina 4, Cognitio 2, Potentia 2 |
| `crafting` / `advanced_circuit` | Machina 6, Cognitio 4, Lux 2 |
| `te` / `generator` (блок) | Machina 5, Ignis 3, Potentia 3 |
| `te` / `solar_generator` | Machina 4, Lux 4, Potentia 2 |
| `re_battery` / — | Potentia 4, Metallum 2 |
| `energy_crystal` / — | Potentia 8, Vitreus 4 |
| `lapotron_crystal` / — | Potentia 12, Vitreus 4, Auram 2 |
| `ingot` / `refined_iron`** | Metallum 4, Ordo 2 |
| `crafting` / `carbon_fibre` | Pannus 2, Ordo 2, Ignis 1 |
| `nuclear` / `uranium_238` | Metallum 3, Potentia 2, Venenum 2 |
| `misc_resource` / `matter` (UU) | Alienis 8, Permutatio 8, Vacuos 4 |
| `misc_resource` / `iridium_ore` | Metallum 8, Alienis 4 |
| `crafting` / `scrap` | Perditio 2, Permutatio 1 |
| `jetpack` / — | Volatus 6, Machina 4, Potentia 3 |
| `drill` / — | Perfodio 4, Machina 3, Potentia 2 |
| `chainsaw` / — | Telum 4, Machina 3, Potentia 2 |
| `te` / `teleporter` | Iter 8, Alienis 6, Machina 4 |
| броня nano/quantum (4+4 шт.) | по v3 machines §1 |

\* точные строки — сверка по `ItemName`/`BlockName.te` на этапе кода.
\** в 2.8.x «refined iron» = steel — проверить наличие.

## 5. Вкладка и первые исследования

```java
ResearchCategories.registerCategory("UNBOUNDTECH",
    new ResourceLocation(MODID, "textures/misc/tab_icon.png"),
    new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
```
- `THAUM_IC2_INTRO`: скрытое; триггер — сканирование любого предмета с
  аспектом Machina ≥ 4 из нашей таблицы (hook: событие сканирования TC4U;
  если хука нет — добавить его в TC4U API, см. §7).
- Лорные: `LORE_EU_AND_VIS`, `LORE_ELAN_VITAL` (только страницы текста).
- `THAUM_ORE_MACERATION`, `ALUMENTUM_FUEL` — обычные исследования за очки.
- Тексты страниц: брать формулировки из v1 `part1`/`part2` (лор готов).

## 6. Рецепты фазы 1

```java
// Дробитель: руды ТК → x2 (v3 machines §2)
Recipes.macerator.addRecipe(Recipes.inputFactory.forStack(cinnabarOre),
    null, false, new ItemStack(cinnabarDust, 2));
// + янтарь, кристальные руды (8 рецептов), кластер → 4 осколка
// Компрессор: 9 осколков → кластер; янтарь → блок
// Экстрактор: таинт-тендрил → резина; серебролист → 2 живичные смолы
```
- Алюментум-топливо: генератор IC2 берёт furnace burn time →
  подобрать burn time так, чтобы выход = `EU_ALUMENTUM` (замерить фактическую
  формулу генератора 2.8.x на стенде; приёмка: 40,000 ± 5%).
- Рецепты регистрируются в postInit (после IC2).

## 6.1. Серебряная руда — РЕАЛИЗОВАНО в TC4U 1.2.8.0 (не в сабмоде)

> Ревизия: руда и слиток добавлены в сам TC4 Unbound (см. CHANGELOG 1.2.8.0
> и `ic2_v5_decisions.md` §8). Сабмоду из этого раздела остаётся ТОЛЬКО:
> рецепт дробителя `oreSilver` → 2× пыль серебра IC2 (+ промыватель в фазе 4).
> Остальное ниже — историческая спека, по которой делалась реализация.

**Факты из кода** (проверено):
- TC4U уже имеет: самородок серебра (`itemNugget:19`), оредикт-хуки
  `oreSilver`/`ingotSilver`/`nuggetSilver` (`Config.java:424-446`), аспекты
  (`ConfigAspects.java:471-494`), бонус плавки самородком
  (`ConfigRecipesSmeltingSlice`), тигель, и **серебряный наконечник жезла**
  `WandCapSilverInert` за флагом `Config.foundSilverIngot`
  (`ConfigRecipesArcaneSlice.java:287`) — мёртвый без источника серебра.
- IC2 Exp имеет слиток/пыль серебра (стекловолоконный кабель), но руды нет.
- Ни один из модов руду не генерирует → серебро недобываемо без Thermal/IE.

**Спецификация `silver_ore` (модуль core):**
- Блок `unboundtech:silver_ore`, регистрируется в оредикт как `oreSilver` —
  весь компат TC4U (наконечник, самородки, аспекты Earth 2/Metal 4)
  включается сам, без единой правки основного мода.
- Дроп: сам блок. Кирка: железная+ (harvestLevel 2).
- **Переработка:** печь → слиток серебра IC2 (`IC2Items ingot/silver`);
  дробитель → 2× пыль серебра IC2; Эссент-Промыватель (фаза 4) → +шанс
  самородка. Без IC2 сабмод не грузится, так что выход слитка гарантирован.
- **Worldgen:** y 6–32, жила 4–8 блоков, 4 жилы/чанк (между железом и золотом
  по редкости; числа в конфиг `[worldgen]`).
- **Анти-дубликат:** конфиг `B:addSilverOre=true` + автоотключение генерации,
  если на старте в оредикте `oreSilver` уже есть чужой блок (Thermal, IE…) —
  наш блок тогда регистрируется, но не генерируется (крафты сборок не ломаем).
- Текстура: камень + светлые металлические вкрапления с холодным голубоватым
  отблеском (палитра «машинные серые», арт-гайд §2).
- Приёмка: в мире с TC4U+IC2+UT добываются руда → слиток IC2 → самородки →
  крафтится серебряный наконечник жезла TC4U.

## 7. Хуки, которые нужно добавить в TC4 Unbound (PR в основной мод)

| Хук | Для чего |
|---|---|
| Событие «предмет отсканирован» (Forge event) | триггер THAUM_IC2_INTRO |
| Геттер burn-предметов алхимии | алюментум-топливо без хардкода |
| (фаза 3+) API ауры чанка: get/add/drain + flux | конвертеры, скруббер |
| (фаза 3+) регистрация cap/rod жезлов извне | компоненты жезлов |

Фаза 1 требует только первые два — остальные заявить в API заранее.

## 8. Приёмочный чек-лист фазы 1

1. Клиент+сервер стартуют с TC4U+IC2; без IC2 — мод отключается с логом (или креш с понятным сообщением — required-after).
2. Сканирование генератора IC2 таумометром: аспекты видны, вкладка UNBOUNDTECH открылась.
2а. Серебро: руда генерируется, цепочка руда→слиток→самородок→серебряный
    наконечник жезла проходит (§6.1); с Thermal-подобным модом дубль-генерации нет.
3. Все строки таблицы §4 дают аспекты (авто-тест: пройтись по таблице, лог WARN = 0).
4. Руды ТК дробятся ×2; кластер → 4 осколка; компрессор/экстрактор работают.
5. Алюментум в генераторе даёт 40,000 ± 5% EU.
6. `[modules] core=false` в конфиге отключает всё содержимое.
7. METS/ASP отсутствуют → лог «module disabled», нет крешей.
