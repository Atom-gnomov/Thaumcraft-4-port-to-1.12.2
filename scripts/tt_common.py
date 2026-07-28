# -*- coding: utf-8 -*-
"""Shared extraction for the Thaumic Tinkerer documentation generators.

Reads the original mod's source and returns one record per registerable object
(anything implementing ITTinkererItem / ITTinkererBlock, i.e. anything with a
getRecipeItem()). Nothing here interprets or rounds a value -- method bodies
come back verbatim so the generated docs can be trusted as a source of truth.

The original is expected next to the repository:

    <repo>/../tt-original-1.7.10

Clone it with:

    git clone -b 1.7.10 https://github.com/Thaumic-Tinkerer/ThaumicTinkerer ../tt-original-1.7.10
"""
import io
import os
import re

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TT_ROOT = os.path.join(os.path.dirname(REPO), 'tt-original-1.7.10',
                       'src', 'main', 'java', 'thaumic', 'tinkerer')
PORT_SRC = os.path.join(REPO, 'mod', 'src', 'main', 'java')


def require_original():
    if not os.path.isdir(TT_ROOT):
        raise SystemExit(
            'Original Thaumic Tinkerer source not found at %s\n'
            'Clone it with:\n'
            '  git clone -b 1.7.10 https://github.com/Thaumic-Tinkerer/ThaumicTinkerer %s'
            % (TT_ROOT, os.path.join(os.path.dirname(REPO), 'tt-original-1.7.10')))


# --- java text helpers ----------------------------------------------------

def method_body(src, signature_re):
    """Brace-balanced body of the first method matching the regex, or None."""
    m = re.search(signature_re, src)
    if not m:
        return None
    i = src.index('{', m.end() - 1) if '{' not in m.group(0) else m.start() + m.group(0).index('{')
    depth, start = 0, i
    while i < len(src):
        if src[i] == '{':
            depth += 1
        elif src[i] == '}':
            depth -= 1
            if depth == 0:
                return src[start + 1:i].strip()
        i += 1
    return None


def strip_comments(s):
    s = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    return re.sub(r'^\s*//.*$', '', s, flags=re.M)


def collapse(s):
    if s is None:
        return None
    lines = [ln.strip() for ln in strip_comments(s).splitlines() if ln.strip()]
    return '\n'.join(lines)


# --- Lib* constant tables -------------------------------------------------

LIBS = ('LibResearch', 'LibItemNames', 'LibBlockNames', 'LibFeatures',
        'LibGuiIDs', 'LibEnchantNames', 'LibEnchantIDs', 'LibBlockIDs', 'LibMisc')


def load_constants():
    consts = {}
    for lib in LIBS:
        path = os.path.join(TT_ROOT, 'common', 'lib', lib + '.java')
        if not os.path.exists(path):
            continue
        src = io.open(path, encoding='utf-8').read()
        for m in re.finditer(r'static\s+final\s+\w+\s+(\w+)\s*=\s*("([^"]*)"|[^;]+);', src):
            consts['%s.%s' % (lib, m.group(1))] = (
                m.group(3) if m.group(3) is not None else m.group(2).strip())
    return consts


_CONST_RE = re.compile(r'Lib(?:%s)\.\w+' % '|'.join(l[3:] for l in LIBS))


def resolve(text, consts):
    if not text:
        return text

    def sub(m):
        v = consts.get(m.group(0))
        if v is None:
            return m.group(0)
        return '"%s"' % v if not v.startswith('new ') else v

    return _CONST_RE.sub(sub, text)


# --- object extraction ----------------------------------------------------

def extract():
    """One dict per registerable object, sorted by original file path."""
    require_original()
    results = []
    for dirpath, _dirs, files in os.walk(TT_ROOT):
        for fn in sorted(files):
            if not fn.endswith('.java'):
                continue
            path = os.path.join(dirpath, fn)
            src = io.open(path, encoding='utf-8', errors='replace').read()
            if 'getRecipeItem' not in src:
                continue
            rel = os.path.relpath(path, TT_ROOT).replace('\\', '/')
            if rel.startswith('common/registry/'):
                continue  # the interfaces and base classes themselves

            cls = fn[:-5]
            entry = {'file': rel, 'cls': cls}

            decl = re.search(
                r'public\s+(?:abstract\s+)?class\s+(\w+)\s+extends\s+([\w<>, .]+?)'
                r'\s+implements\s+([\w<>, .]+?)\s*\{', src)
            if decl:
                entry['extends'] = decl.group(2).strip()
                entry['implements'] = decl.group(3).strip()
            else:
                decl = re.search(
                    r'public\s+(?:abstract\s+)?class\s+(\w+)\s+extends\s+([\w<>, .]+?)\s*\{', src)
                entry['extends'] = decl.group(2).strip() if decl else ''
                entry['implements'] = ''

            entry['name'] = collapse(method_body(
                src, r'public\s+String\s+get(?:Item|Block)Name\s*\([^)]*\)\s*\{'))
            entry['recipe'] = collapse(method_body(
                src, r'public\s+ThaumicTinkererRecipe\s+getRecipeItem\s*\([^)]*\)\s*\{'))
            entry['research'] = collapse(method_body(
                src, r'public\s+IRegisterableResearch\s+getResearchItem\s*\([^)]*\)\s*\{'))
            entry['shouldRegister'] = collapse(method_body(
                src, r'public\s+boolean\s+shouldRegister\s*\([^)]*\)\s*\{'))
            entry['tile'] = collapse(method_body(
                src, r'public\s+Class<\?\s+extends\s+TileEntity>\s+getTileEntity\s*\([^)]*\)\s*\{'))
            entry['special'] = collapse(method_body(
                src, r'public\s+ArrayList<Object>\s+getSpecialParameters\s*\([^)]*\)\s*\{'))
            entry['ctor'] = collapse(method_body(
                src, r'public\s+' + cls + r'\s*\([^)]*\)\s*\{'))
            entry['consts'] = [
                ' '.join(c.split()) for c in re.findall(
                    r'^\s*(?:public|private|protected)?\s*static\s+final\s+'
                    r'[\w<>\[\], .]+\s+\w+\s*=\s*[^;]+;', src, flags=re.M)]
            results.append(entry)

    results.sort(key=lambda e: e['file'])
    return results


# --- recipe classification ------------------------------------------------

KINDS = [
    ('ThaumicTinkererInfusionRecipe', 'инфузия'),
    ('ThaumicTinkererArcaneRecipe', 'аркан'),
    ('ThaumicTinkererCrucibleRecipe', 'тигель'),
    ('ThaumicTinkererCraftingBenchRecipe', 'верстак'),
]


def recipe_kind(recipe):
    if not recipe or recipe == 'return null;':
        return 'нет рецепта'
    kinds = [label for token, label in KINDS if token in recipe]
    if not kinds:
        return 'нет рецепта'
    return ' + '.join(sorted(set(kinds), key=kinds.index))


def instability(recipe):
    """Infusion instability is the argument right after the output stack."""
    if not recipe or 'ThaumicTinkererInfusionRecipe' not in recipe:
        return None
    m = re.search(r'ThaumicTinkererInfusionRecipe\([^,]+,\s*new ItemStack\(this[^)]*\),\s*(\d+)', recipe)
    return m.group(1) if m else None


def aspects(recipe):
    """The recipe's aspect list, in the order the original wrote it."""
    if not recipe:
        return []
    m = re.search(r'new AspectList\(\)((?:\s*\.add\(Aspect\.\w+,\s*\d+\))+)', recipe)
    if not m:
        return []
    return ['%s %s' % (a, n) for a, n in re.findall(r'\.add\(Aspect\.(\w+),\s*(\d+)\)', m.group(1))]


def research_parent(research, consts):
    if not research:
        return None
    m = re.search(r'\.setParents\(([^)]*)\)', resolve(research, consts))
    if not m:
        return None
    first = m.group(1).split(',')[0].strip()
    return first.strip('"') or None


def gate(entry):
    """A short label for a shouldRegister() that is not plain `return true`.

    Returns None when the object is registered unconditionally.
    """
    body = entry.get('shouldRegister')
    if not body:
        return None
    body = ' '.join(body.split())
    if body == 'return true;':
        return None
    if body == 'return false;':
        return 'НЕ РЕГИСТРИРУЕТСЯ'
    m = re.search(r'isModLoaded\("(\w+)"\)', body)
    if m:
        return 'только с %s' % m.group(1)
    m = re.search(r'ConfigHandler\.(\w+)', body)
    if m:
        return 'по конфигу %s' % m.group(1)
    return body


def components(entry):
    """TT classes this object's recipe consumes, itself excluded.

    The original writes its own output as new ItemStack(this), so a naive scan
    reports every object as its own dependency -- that is dropped here.
    """
    if not entry['recipe']:
        return []
    found = re.findall(
        r'getFirst(?:Item|Block)FromClass\((\w+)\.class\)|'
        r'getBlockFromClass\((\w+)\.class\)', entry['recipe'])
    names = {a or b for a, b in found}
    names.discard(entry['cls'])
    return sorted(names)


# --- how the objects are grouped for human reading ------------------------

GROUPS = [
    (u'Ресурсы и базовые блоки', lambda f: '/quartz/' in f or 'DarkQuartz' in f),
    (u'Механизмы (блоки)', lambda f: f.startswith('common/block/') and '/kami/' not in f
        and '/quartz/' not in f and '/fire/' not in f),
    (u'Элементальные костры', lambda f: '/fire/' in f),
    (u'Трансвекторы', lambda f: 'transvector' in f or 'Connector' in f),
    (u'Фокусы палочки', lambda f: '/foci/' in f and '/kami/' not in f),
    (u'Предметы', lambda f: f.startswith('common/item/') and '/kami/' not in f
        and '/foci/' not in f and '/quartz/' not in f),
    (u'KAMI — ресурсы и предметы', lambda f: '/kami/' in f and '/tool/' not in f
        and '/armor/' not in f and '/foci/' not in f),
    (u'KAMI — броня', lambda f: '/kami/armor/' in f),
    (u'KAMI — инструменты', lambda f: '/kami/tool/' in f),
    (u'KAMI — фокусы', lambda f: '/kami/foci/' in f),
]


# --- which objects this port already has ----------------------------------

# Original class name -> the class this port implements it as, where the two
# differ. Anything not listed is looked up under its own name.
RENAMES = {
    'ItemConnector': 'ItemTransvectorConnector',
    'ItemXPTalisman': 'ItemXpTalisman',
    'ItemFocusSmelt': 'FocusSmelt',
    'ItemFocusTelekinesis': 'FocusTelekinesis',
    'ItemFocusFlight': 'FocusFlight',
    'ItemFocusHeal': 'FocusHeal',
    'ItemFocusDeflect': 'FocusDeflect',
    'ItemFocusDislocation': 'FocusDislocation',
    'ItemFocusEnderChest': 'FocusEnderChest',
    'ItemDarkQuartzBlock': 'BlockDarkQuartzItem',
    'ItemDarkQuartzSlab': 'BlockDarkQuartzSlab',
    'ItemBlockMagnet': 'BlockMagnetItem',
}


def ported_classes():
    """Every class name present in this port's source tree."""
    have = set()
    for dirpath, _dirs, files in os.walk(PORT_SRC):
        for fn in files:
            if fn.endswith('.java'):
                have.add(fn[:-5])
    return have


def is_ported(cls, have):
    return RENAMES.get(cls, cls) in have
