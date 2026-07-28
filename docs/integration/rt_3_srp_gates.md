# RT-3: Вкладка «Внеземная Биология» — Стопперы Фаз и Практика (SRP)
## Гейты по фазам паразитов + все практические исследования.

---

## Стопперы (Phase Gates)

Каждый гейт требует **и** уровень фазы паразитов в мире, **и** соответствующую сводную запись Бестиария.
Гейт открывает как практические рецепты, так и контрмеру для снижения фазы.

```
PHASE_GATE_1
    Условие: фаза паразитов ≥ 1 В МИРЕ + SRP_BESTIARY_BASIC
    Открывает:
    ├── ALCHEMY_VACCINE                      ← [Алхимия] Магическая вакцина/прививка
    ├── ALCHEMY_INFECTION_ROLLBACK           ← [Алхимия] Откат заражения мобов на ранней стадии
    ├── INVENTION_COMPASS                    ← [Изобретения] Рунная Компас-Сфера
    └── PHASE_COUNTER_1                      ← [Ритуал] Контрмера: Очищающий Костёр
            Снижает очки фазы в чанке. Требует Нитор + Ignis.

PHASE_GATE_2
    Условие: фаза ≥ 2 + SRP_BESTIARY_ADVANCED
    Открывает:
    ├── INVENTION_WARD_OF_PURITY             ← [Изобретения] Оберег от Инфекции (баблс)
    ├── INVENTION_LIVING_TOOLS               ← [Изобретения] Живые Инструменты
    ├── THAUMATURGY_PURIFIED_BIOMASS         ← [Тауматургия] Очищенная Биомасса
    └── PHASE_COUNTER_2                      ← [Ритуал] Контрмера: Руны Отторжения
            Создаёт зону с замедленным ростом фазы в радиусе 32 блоков.
            Требует Магические Камни + Ordo.

PHASE_GATE_3
    Условие: фаза ≥ 3 + SRP_BESTIARY_ADVANCED
    Открывает:
    ├── THAUMATURGY_SENTIENT_CORE            ← [Тауматургия] Разумное Ядро
    ├── THAUMATURGY_VOID_LIVING_BLADE        ← [Тауматургия] Живое Оружие Пустоты
    ├── THAUMATURGY_FLESH_GOLEM              ← [Тауматургия] Голем-Мутант из Плоти
    └── PHASE_COUNTER_3                      ← [Ритуал] Контрмера: Ритуал Чистоты
            Масштабный ритуал (5×5 рун). Обнуляет фазу в чанке.
            Требует Сердце Маяка (дроп редкий) + Auram.

PHASE_GATE_ELITE
    Условие: фаза ≥ 4 или убит Босс + SRP_BESTIARY_ELITE
    Открывает:
    ├── → ANTI_ADAPT_BATTERY                 ← [Синтез] Антиадаптатор (эндгейм баблс)
    ├── → ARMOR_ICHOR_QUANTUM                ← [Синтез] Ихоровый Квант (финальная броня)
    └── PHASE_COUNTER_ELITE                  ← [Ритуал] Контрмера: Эндгейм-ритуал
            Уничтожает Маяки паразитов в радиусе 64 блоков.
            Требует: Антиадаптатор активированный + Разумное Ядро + огромные затраты Вис.
```

---

## Практические исследования (полная карта)

### Алхимия
```
PHASE_GATE_1  →  ALCHEMY_VACCINE
PHASE_GATE_1  →  ALCHEMY_INFECTION_ROLLBACK
```

### Изобретения
```
PHASE_GATE_1  →  INVENTION_COMPASS
PHASE_GATE_2  →  INVENTION_WARD_OF_PURITY
PHASE_GATE_2  →  INVENTION_LIVING_TOOLS
```

### Тауматургия
```
PHASE_GATE_2  →  THAUMATURGY_PURIFIED_BIOMASS
                     └── THAUMATURGY_ELDRITCH_CHITIN
                               └── THAUMATURGY_SENTIENT_CORE  [требует PHASE_GATE_3]
                                         ├── THAUMATURGY_VOID_LIVING_BLADE
                                         └── THAUMATURGY_FLESH_GOLEM
```

### Переходы в другие вкладки
```
THAUMATURGY_PURIFIED_BIOMASS  → разблокирует FOCUS_PURIFICATION  (в RT-1: IC2)
THAUMATURGY_SENTIENT_CORE     → разблокирует ANTI_ADAPT_BATTERY  (в RT-4: Синтез)
PHASE_GATE_ELITE              → разблокирует ARMOR_ICHOR_QUANTUM  (в RT-4: Синтез)
```

---
*Индекс: [rt_index.md](rt_index.md) | Предыдущий: [rt_2_srp_lore.md](rt_2_srp_lore.md) | Следующий: [rt_4_synthesis.md](rt_4_synthesis.md)*
