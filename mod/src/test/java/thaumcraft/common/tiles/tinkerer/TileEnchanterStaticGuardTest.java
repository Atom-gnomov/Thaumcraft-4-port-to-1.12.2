package thaumcraft.common.tiles.tinkerer;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The Osmotic Enchanter did nothing at all, and the cause was the sync.
 *
 * <p>{@code GuiEnchanter} reads the queue, the cost and the paid-so-far
 * straight off the <em>client's</em> copy of the tile. Nothing ever sent that
 * copy an update, so the queue stayed empty no matter what was clicked: no rows
 * appeared, no aspect bars appeared, and the start button — enabled on "the
 * queue is not empty" — could never be pressed. Upstream's {@code TileMod}
 * pushes the tile on every {@code markDirty}; the port lost that override when
 * it flattened the module onto {@code TileThaumcraft}.</p>
 *
 * <p>The metadata assertions below guard a near miss rather than a fix. The
 * pillar check reads {@code blockCosmeticSolid} meta 0, and the port's own
 * {@code types} array called meta 0 the Obsidian <em>Tile</em> — so the check
 * looked wrong, and was very nearly "corrected" into being wrong. Meta 0 is the
 * Totem: the original's language file says so, this port's says so, the model
 * files say so, and the original's side-icon routine gives the totem column
 * treatment to metas 0 and 8. The array was the thing that was wrong. Both
 * values are named now, and pinned here against the table they name.</p>
 */
public class TileEnchanterStaticGuardTest {

    private static final String TILE =
            "src/main/java/thaumcraft/common/tiles/tinkerer/TileEnchanter.java";
    private static final String BASE =
            "src/main/java/thaumcraft/common/tiles/tinkerer/TileTinkerer.java";

    // ---- the sync ----

    /**
     * The module's shared base must carry upstream's {@code TileMod#markDirty},
     * because that is the single line the whole module's client state hangs on.
     */
    @Test
    public void theTinkererBaseResyncsOnMarkDirty() throws IOException {
        String base = read(BASE);
        assertTrue("TileTinkerer must extend Thaumcraft's tile base",
                base.contains("class TileTinkerer extends TileThaumcraft"));
        assertTrue("markDirty must still mark the chunk for saving",
                base.contains("public void markDirty()") && base.contains("super.markDirty()"));
        assertTrue("and then push the tile to watching clients",
                base.contains("notifyBlockUpdate(this.pos, state, state, 3)"));
        assertTrue("from the server only, or the client would fight its own copy",
                base.contains("!this.world.isRemote"));
    }

    /**
     * The split belongs on the Tinkerer base and nowhere else. Thaumcraft's own
     * tiles — jars, tubes, crucibles — tick constantly and sync by hand where
     * they mean to; giving them an unconditional resync per {@code markDirty}
     * would be a very expensive way to fix one screen.
     */
    @Test
    public void thaumcraftsOwnBaseIsLeftAlone() throws IOException {
        String tc = read("src/main/java/thaumcraft/api/TileThaumcraft.java");
        assertFalse("TileThaumcraft must not resync on markDirty",
                tc.contains("markDirty"));
    }

    /** Every tile in the module inherits the sync rather than re-deriving it. */
    @Test
    public void theModuleTilesSitOnThatBase() throws IOException {
        String[] tiles = {
                "TileAnimationTablet", "TileCamo", "TileEnchanter", "TileForcefield",
                "TileFunnel", "TileMagnet", "TileMobilizer", "TileMobilizerRelay",
                "TileRepairer", "TileSummon",
        };
        for (String name : tiles) {
            String src = read("src/main/java/thaumcraft/common/tiles/tinkerer/" + name + ".java");
            assertTrue(name + " must extend TileTinkerer, not TileThaumcraft directly",
                    src.contains("class " + name + " extends TileTinkerer"));
            assertFalse(name + " must not keep a hand-rolled copy of the resync",
                    src.contains("public void markDirty()"));
        }
    }

    /** The screen reads the client tile directly — which is why the sync matters. */
    @Test
    public void theScreenReadsTheClientTile() throws IOException {
        String gui = read("src/main/java/thaumcraft/client/gui/GuiEnchanter.java");
        assertTrue("the start button is gated on the tile's queue",
                gui.contains("start.enabled = !enchanter.getQueuedEnchantments().isEmpty()"));
        assertTrue("the aspect bars are filled from the tile's paid-against-total",
                gui.contains("enchanter.getTotalCost().getAmount(aspect)")
                        && gui.contains("enchanter.getPaid().getAmount(aspect)"));
    }

    // ---- the multiblock ----

    /**
     * Obsidian Totems, not Obsidian Tiles. Pinned through the named constants and
     * cross-checked against the metadata table itself — the same table that was
     * mislabelled — so the next reader gets the truth from the assertion instead
     * of from the array.
     */
    @Test
    public void thePillarsAreBuiltOfObsidianTotemsCappedWithNitor() throws IOException {
        String tile = read(TILE);
        assertTrue("the totem must be addressed by name",
                tile.contains("BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM"));
        assertTrue("and so must the nitor cap",
                tile.contains("BlockAiry.TYPE_NITOR"));
        assertFalse("no bare metadata literals may creep back in",
                tile.contains("getMetaFromState(state) == 0")
                        || tile.contains("getMetaFromState(state) == 1"));

        assertEquals("blockCosmeticSolid meta 1 is the Obsidian Totem",
                "obsidianTotem",
                metaName("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java",
                        "types", constant("src/main/java/thaumcraft/common/blocks/BlockCosmeticSolid.java",
                                "TYPE_OBSIDIAN_TOTEM")));
        assertEquals("blockAiry meta 1 is Nitor",
                "nitor",
                metaName("src/main/java/thaumcraft/common/blocks/BlockAiry.java",
                        "airyTypes", constant("src/main/java/thaumcraft/common/blocks/BlockAiry.java",
                                "TYPE_NITOR")));
    }

    /** Upstream's shape: six pillars within four blocks, each two to twelve tall. */
    @Test
    public void theMultiblockKeepsTheOriginalsShape() throws IOException {
        String tile = read(TILE);
        assertTrue(tile.contains("PILLARS_REQUIRED = 6"));
        assertTrue(tile.contains("SEARCH_RADIUS = 4"));
        assertTrue(tile.contains("MIN_PILLAR = 2"));
        assertTrue(tile.contains("MAX_PILLAR = 12"));
        assertTrue("a run must refuse to start without the structure",
                tile.contains("countPillars() < PILLARS_REQUIRED"));
    }

    // ---- helpers ----

    /** The value of a {@code static final int} declared in the given source. */
    private static int constant(String path, String name) throws IOException {
        Matcher m = Pattern.compile("int\\s+" + name + "\\s*=\\s*(\\d+)").matcher(read(path));
        assertTrue(name + " must be declared in " + path, m.find());
        return Integer.parseInt(m.group(1));
    }

    /** Entry {@code index} of a {@code String[]} array literal in the given source. */
    private static String metaName(String path, String field, int index) throws IOException {
        Matcher m = Pattern.compile("String\\[\\]\\s+" + field + "\\s*=\\s*\\{([^}]*)}")
                .matcher(read(path));
        assertTrue(field + " must be declared in " + path, m.find());
        String[] entries = m.group(1).split(",");
        return entries[index].trim().replace("\"", "");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
