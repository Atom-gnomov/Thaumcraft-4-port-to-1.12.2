# -*- coding: utf-8 -*-
"""Regenerate TT_OBJECT_REFERENCE.md — the verbatim catalogue of every original
Thaumic Tinkerer object, so nothing ever has to be invented.

Run from anywhere:  py scripts/gen_tt_reference.py
"""
import io
import os
import re

import tt_common as tt


def fmt(body, consts, indent='  '):
    """Tidy a method body into something readable without changing any value."""
    if not body:
        return None
    body = tt.resolve(body, consts)
    body = re.sub(r'ThaumicTinkerer\.registry\.getFirst(?:Item|Block)FromClass\((\w+)\.class\)',
                  r'<\1>', body)
    body = re.sub(r'\bnew ItemStack\(', 'stack(', body)
    body = re.sub(r'\bnew AspectList\(\)', 'aspects', body)
    body = re.sub(r'\s+', ' ', body).strip()

    out, depth, buf = [], 0, ''
    for ch in body:
        if ch in '([':
            depth += 1
        elif ch in ')]':
            depth -= 1
        if ch == ',' and depth <= 1:
            out.append(buf + ',')
            buf = ''
            continue
        buf += ch
    out.append(buf)
    return ('\n' + indent).join(x.strip() for x in out if x.strip())


GROUPS = [
    ('Ресурсы и базовые блоки', lambda f: '/quartz/' in f or 'DarkQuartz' in f),
    ('Механизмы (блоки)', lambda f: f.startswith('common/block/') and '/kami/' not in f
        and '/quartz/' not in f and '/fire/' not in f),
    ('Элементальные костры', lambda f: '/fire/' in f),
    ('Трансвекторы', lambda f: 'transvector' in f or 'Connector' in f),
    ('Фокусы палочки', lambda f: '/foci/' in f and '/kami/' not in f),
    ('Предметы', lambda f: f.startswith('common/item/') and '/kami/' not in f
        and '/foci/' not in f and '/quartz/' not in f),
    ('KAMI — ресурсы и предметы', lambda f: '/kami/' in f and '/tool/' not in f
        and '/armor/' not in f and '/foci/' not in f),
    ('KAMI — броня', lambda f: '/kami/armor/' in f),
    ('KAMI — инструменты', lambda f: '/kami/tool/' in f),
    ('KAMI — фокусы', lambda f: '/kami/foci/' in f),
]

HEADER = '''# Каталог объектов Thaumic Tinkerer 1.7.10 — исходные значения

> **Это справочник, а не план.** Каждая строка ниже выписана дословно из
> оригинального исходника Thaumic Tinkerer (ветка `1.7.10`, локальная копия
> в `../tt-original-1.7.10`). Ничего здесь не придумано и не пересчитано.
> Файл генерируется скриптом `scripts/gen_tt_reference.py` — руками не править.

## Как этим пользоваться

1. **Перед тем как портировать объект — найди его здесь.** Рецепт, аспекты,
   твёрдость, сопротивление, координаты исследования, родители — всё это уже
   извлечено.
2. **Если объекта здесь нет — открой оригинал**, а не придумывай по аналогии.
   Локальная копия исходников лежит в `../tt-original-1.7.10` (относительно
   корня репозитория — работа идёт с нескольких машин, абсолютные пути не
   писать).
3. **Что портировать следующим** — смотри [`TT_PORT_QUEUE.md`](TT_PORT_QUEUE.md),
   он упорядочен по зависимостям.

Обозначения в выписках ниже:

| Запись | Значит |
| --- | --- |
| `stack(...)` | `new ItemStack(...)` |
| `aspects` | `new AspectList()` |
| `<ClassName>` | `ThaumicTinkerer.registry.getFirstItemFromClass(ClassName.class)` |
| `"KEY"` | раскрытая константа `LibResearch.KEY_*` / `LibItemNames.*` / `LibBlockNames.*` |

Типы рецептов оригинала:

| Класс | Разворачивается в |
| --- | --- |
| `ThaumicTinkererArcaneRecipe(name, research, out, aspects, ...)` | `ThaumcraftApi.addArcaneCraftingRecipe` |
| `ThaumicTinkererInfusionRecipe(name[, research], out, instability, aspects, input, ...)` | `ThaumcraftApi.addInfusionCraftingRecipe` |
| `ThaumicTinkererCrucibleRecipe(name, out, in, aspects)` | `ThaumcraftApi.addCrucibleRecipe` |
| `ThaumicTinkererCraftingBenchRecipe(name, out, ...)` | обычный верстак, `GameRegistry.addRecipe` |
| `ThaumicTinkererRecipeMulti(...)` | несколько рецептов на один объект |

Когда в `ThaumicTinkererInfusionRecipe` два имени (`name`, `research`) — первое
это ключ рецепта в `ConfigResearch.recipes`, второе — исследование, к которому
он привязан. С одним именем оба совпадают.

---
'''


def main():
    consts = tt.load_constants()
    data = tt.extract()

    L = HEADER.split('\n')
    w = L.append
    w('')

    used = set()
    for title, pred in GROUPS:
        members = [e for e in data if e['file'] not in used and pred(e['file'])]
        if not members:
            continue
        for e in members:
            used.add(e['file'])
        w('## %s' % title)
        w('')
        for e in members:
            w('### `%s`' % e['cls'])
            w('')
            w('`%s` — extends `%s`%s' % (
                e['file'], e['extends'],
                ('  implements `%s`' % e['implements']) if e['implements'] else ''))
            w('')
            name = fmt(e['name'], consts)
            if name:
                w('**Имя регистрации:** `%s`'
                  % name.replace('return ', '').rstrip(';').replace('\n', ' '))
                w('')
            if e['ctor']:
                w('**Конструктор:**')
                w('')
                w('```java')
                for ln in tt.resolve(e['ctor'], consts).splitlines():
                    w(ln)
                w('```')
                w('')
            if e['tile'] and e['tile'] != 'return null;':
                w('**Tile entity:** `%s`' % e['tile'].replace('return ', '').replace('.class;', ''))
                w('')
            if e['shouldRegister'] and 'true' not in e['shouldRegister']:
                w('**shouldRegister:** `%s`' % e['shouldRegister'].replace('\n', ' '))
                w('')
            if e['special'] and e['special'] != 'return null;':
                w('**Спецпараметры:** `%s`' % e['special'].replace('\n', ' '))
                w('')
            r = fmt(e['recipe'], consts)
            if r and r != 'return null;':
                w('**Рецепт:**')
                w('')
                w('```java')
                w(r)
                w('```')
                w('')
            elif r == 'return null;':
                w('**Рецепт:** нет (не крафтится).')
                w('')
            rs = fmt(e['research'], consts)
            if rs and rs != 'return null;':
                w('**Исследование:**')
                w('')
                w('```java')
                w(rs)
                w('```')
                w('')
            elif rs == 'return null;':
                w('**Исследование:** нет (входит в чужую страницу).')
                w('')
            if e['consts']:
                w('**Константы:**')
                w('')
                w('```java')
                for c in e['consts']:
                    w(tt.resolve(c, consts))
                w('```')
                w('')
        w('---')
        w('')

    leftover = [e for e in data if e['file'] not in used]
    if leftover:
        w('## Прочее')
        w('')
        for e in leftover:
            w('- `%s`' % e['file'])
        w('')

    out = os.path.join(tt.REPO, 'TT_OBJECT_REFERENCE.md')
    io.open(out, 'w', encoding='utf-8', newline='\n').write('\n'.join(L))
    print('wrote %s — %d objects' % (out, len(data)))


if __name__ == '__main__':
    main()
