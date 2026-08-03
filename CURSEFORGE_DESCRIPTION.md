# Thaumcraft 4 Eternal

**The complete Thaumcraft 4.2.3.5 experience, ported to 1.12.2** — every system held to the
original's own numbers, not reimagined. Built on top of that: **Thaumic Tinkerer** rebuilt
natively from the ground up (transvectors, the osmotic enchanter, the full KAMI tier, the bedrock
dimension), and **End Legacy** — an original endgame module of new content in the same spirit.

This is not a rewrite-from-memory. The porting rule from day one has been **1:1 with the
decompiled 1.7.10 source**: block hardness, recipe costs, mob behaviour, even the original's own
bugs — read out of the real code, never invented, never "improved" without saying so.

---

## What you get

### Thaumcraft 4, whole
- Research and the Thaumonomicon — full tree, recipe pages, focus previews, particle rendering
- Alchemy and the crucible, arcane crafting, infusion with instability
- Essentia distillation, tubes, jars, and the full wand/focus/scanning loop
- Golems, warp and sanity, runic shielding
- The Outer Lands dimension and its worldgen — silverwood forests, aura nodes, the Taint
- Every armor, tool and wand from the original, numbers intact

### Thaumic Tinkerer, reimplemented
All 79 portable objects from the original addon (pixlepix / nekosune, originally Vazkii),
rebuilt natively for 1.12 rather than ported as a jar:
- Transvector interface, connector, dislocator — camouflage and all
- The osmotic enchanter (six-pillar multiblock, full-strength infusion enchanting)
- Dynamism tablet, magnets (item + mob), the essentia funnel, the thaumic restorer
- Nether and eldritch foci, the full TT research branch in its own Thaumonomicon tab
- **KAMI, complete**: ichorcloth gear (basic + awakened, three tool modes each), the warp gate and
  sky pearls, the bedrock dimension with its own portal, soul hearts
- Its own creative tab, so progression stays legible even with everything visible

### End Legacy — original content, clearly marked as such
Nothing here pretends to be from 1.7.10; every entry says so in its own research page.
- **Wings, infused into any chestplate** — not a wearable, an NBT property the armor carries. A
  gliding tier and a full-flight tier that behaves like a genuine elytra (real aerodynamics,
  vis-powered climb instead of fireworks); the armor's name changes to match («Soaring …», «… of
  Ascension»)
- **Wards** in the charm slot — one deflects projectiles, one answers death from around your neck
  (a totem-of-undying alternative that costs warp instead of an inventory slot)
- **Spires from Beyond** — eldritch obelisks on the End's outer islands, guarded, worth the climb
- **New foci**: the dragon's own lingering breath cloud (rolls one of six primal effects per cast),
  the ghast's fireball, and a life-drain that heals its caster

### Plays well with others
The mod advertises enough of the Thaumcraft 6 binary API surface that 1.12 addons built against
TC6 load and function against it — EnderIO, Fossils and Archeology, Magic Bees, JEID compatibility
is shim-covered and smoke-tested, not accidental.

---

## Status

Actively developed and actually played, not archived. 1.7.10 parity is essentially complete;
current work is rendering polish and End Legacy content. The codebase carries **700+ automated
tests** — static guards that pin every ported value against the original, and runtime tests that
assemble multiblocks and run devices end-to-end in a simulated world. The full suite runs before
every release.

Open bugs, deviations from the original, and the reasoning behind every owner decision are
tracked in the open — nothing here is swept under the rug.

---

## Requirements

- Minecraft **1.12.2**, Forge `14.23.5.2847` or compatible
- **Baubles** (amulet / ring / belt / charm slots) — required, most content depends on it

---

## Credits & lineage

- **Original mod:** Thaumcraft 4.2.3.5 © 2013–2015 **Azanor**
- **Base port:** [0FL01/Thaumcraft-4.2-FOREVA](https://github.com/0FL01/Thaumcraft-4.2-FOREVA) —
  this project continues from it and still adopts its fixes
- **Thaumic Tinkerer:** pixlepix, nekosune, originally **Vazkii** (CC BY-NC-SA 3.0) — reimplemented
  natively for 1.12.2, with per-class attribution in the source
- **This project:** further porting, fixes, and original content, under MIT

This is an unofficial community project, not affiliated with or endorsed by the original authors.
Full source, changelog, and issue tracker on GitHub.
