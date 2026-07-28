# -*- coding: utf-8 -*-
"""Regenerate TT_PORT_QUEUE.md.

Run from anywhere:  py scripts/gen_tt_queue.py

The queue is ordered by dependency, so an object only appears once everything
its recipe needs is above it. That makes the next pick a fact rather than a
judgement call.
"""
import io
import os

import tt_common as tt


def order(entries, have):
    """Dependency order: an object follows everything its recipe consumes."""
    remaining = {e['cls']: e for e in entries}
    deps = {}
    for cls, e in remaining.items():
        deps[cls] = {c for c in tt.components(e) if c in remaining}

    ordered, placed = [], set()
    while remaining:
        ready = sorted(cls for cls, d in deps.items()
                       if cls in remaining and not (d - placed))
        if not ready:
            # A cycle: break it on the object with the fewest open dependencies.
            ready = [min(remaining, key=lambda c: len(deps[c] - placed))]
        for cls in ready:
            ordered.append(remaining.pop(cls))
            placed.add(cls)
    return ordered


def main():
    consts = tt.load_constants()
    entries = tt.extract()
    have = tt.ported_classes()

    todo = [e for e in entries if not tt.is_ported(e['cls'], have)]
    done = len(entries) - len(todo)
    todo = order(todo, have)

    L = []
    w = L.append
    w('# Очередь портирования Thaumic Tinkerer')
    w('Сгенерировано скриптом `scripts/gen_tt_queue.py` из исходников оригинала')
    w('(`../tt-original-1.7.10`) и дерева этого порта — руками не заполнять,')
    w('перегенерировать после каждого захода.')
    w('')
    w('**Состояние: портировано %d из %d объектов каталога, осталось %d.**'
      % (done, len(entries), len(todo)))
    w('')
    w('Порядок — по зависимостям: объект появляется после всего, что ему нужно.')
    w('«Связи» — компоненты рецепта, которые сами являются объектами TT.')
    w('Точные значения любого объекта — в [`TT_OBJECT_REFERENCE.md`](TT_OBJECT_REFERENCE.md).')
    w('')
    w('> **Колонка «Регистрация» — читать первой.** `НЕ РЕГИСТРИРУЕТСЯ` значит, что')
    w('> `shouldRegister()` в оригинале возвращает `false` и объекта в игре нет')
    w('> вообще (`BlockRPlacer`). «Только с X» — он появляется лишь при')
    w('> установленном моде X (аспектализатор и голем-соединитель просят')
    w('> ComputerCraft). Портировать такое «просто по таблице» значит добавить в')
    w('> игру то, чего в оригинале нет.')
    w('')
    w('> **Осторожно: таблица знает только про зависимости по рецепту.**')
    w('> Поведенческие связи она не видит, и их надо проверять глазами по')
    w('> исходнику. Пример: `ItemSkyPearl` числится свободным, но настраивается')
    w('> только кликом по `BlockWarpGate` — без портала это мёртвый предмет.')
    w('> Перед тем как брать объект, прочитай его класс целиком.')
    w('')
    w('| # | Объект | Рецепт | Неста­бильность | Регистрация | Зависит от (ещё нет) | Использует (уже есть) |')
    w('|---|---|---|---|---|---|---|')

    rows = []
    for i, e in enumerate(todo, 1):
        comps = tt.components(e)
        missing = [c for c in comps if not tt.is_ported(c, have)]
        present = [c for c in comps if tt.is_ported(c, have)]
        inst = tt.instability(e['recipe'])
        rows.append((i, e, missing, present, inst))
        w('| %d | `%s` | %s | %s | %s | %s | %s |' % (
            i, e['cls'], tt.recipe_kind(e['recipe']), inst or '—',
            tt.gate(e) or 'всегда',
            ', '.join('`%s`' % c for c in missing) or '—',
            ', '.join('`%s`' % c for c in present) or '—'))

    w('')
    w('---')
    w('')
    w('## Свойства каждого объекта')
    w('')
    for i, e, missing, present, inst in rows:
        w('### %d. `%s`' % (i, e['cls']))
        w('')
        w('- **Файл оригинала:** `%s`' % e['file'])
        inherit = '`%s`' % e['extends'] if e['extends'] else '—'
        if e['implements']:
            inherit += ', реализует `%s`' % e['implements']
        w('- **Наследует:** %s' % inherit)
        name = tt.resolve(e['name'] or '', consts).replace('return ', '').rstrip(';').replace('\n', ' ')
        w('- **Имя регистрации:** %s' % ('`%s`' % name if name else '—'))
        w('- **Рецепт:** %s' % tt.recipe_kind(e['recipe']))
        if inst:
            w('- **Нестабильность:** %s' % inst)
        asp = tt.aspects(e['recipe'])
        if asp:
            w('- **Аспекты:** %s' % ', '.join(asp))
        parent = tt.research_parent(e['research'], consts)
        if parent:
            w('- **Родитель в дереве исследований:** `%s`' % parent)
        if missing:
            w('- **Блокируется:** %s' % ', '.join('`%s`' % c for c in missing))
        if tt.gate(e):
            w('- **Регистрация:** %s' % tt.gate(e))
        if present:
            w('- **Использует уже портированное:** %s' % ', '.join('`%s`' % c for c in present))
        w('- **Точные значения:** см. `TT_OBJECT_REFERENCE.md` → `%s`' % e['cls'])
        w('')

    out = os.path.join(tt.REPO, 'TT_PORT_QUEUE.md')
    io.open(out, 'w', encoding='utf-8', newline='\n').write('\n'.join(L))
    print('wrote %s — %d ported, %d remaining' % (out, done, len(todo)))


if __name__ == '__main__':
    main()
