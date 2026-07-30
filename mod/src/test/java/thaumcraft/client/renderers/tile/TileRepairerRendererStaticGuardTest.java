package thaumcraft.client.renderers.tile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Like the magnet, the repairer has no static geometry upstream —
 * {@code RenderRepairer.renderWorldBlock} returns false and
 * {@code ModelRepairer} is the whole block. The port drew a JSON cube.
 */
public class TileRepairerRendererStaticGuardTest {

    private static final Path RENDERER =
            Paths.get("src/main/java/thaumcraft/client/renderers/tile/TileRepairerRenderer.java");
    private static final Path MODEL =
            Paths.get("src/main/java/thaumcraft/client/renderers/models/ModelRepairer.java");
    private static final Path BLOCK =
            Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockRepairer.java");

    @Test
    public void theBlockLeavesItsGeometryToTheRenderer() throws IOException {
        assertTrue("the repairer must be TESR-only, as upstream is",
                read(BLOCK).contains("EnumBlockRenderType.ENTITYBLOCK_ANIMATED"));
        assertTrue("and the renderer must be bound",
                read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"))
                        .contains("TileRepairerRenderer()"));
        assertTrue("the model sheet must exist",
                Files.exists(Paths.get(
                        "src/main/resources/assets/thaumcraft/textures/models/repairer.png")));
    }

    /**
     * The ordering is the whole reason upstream splits the model in two. Case,
     * then the item, then the glass over it — put the glass with the case and
     * the item is hidden behind it.
     */
    @Test
    public void theGlassIsASeparatePassDrawnAfterTheItem() throws IOException {
        String model = read(MODEL);
        assertTrue("the case pass", model.contains("public void render()"));
        assertTrue("the glass pass", model.contains("public void renderGlass()"));

        String renderer = read(RENDERER);
        int caseAt = renderer.indexOf("this.model.render();");
        int itemAt = renderer.indexOf("renderRepairedItem(");
        int glassAt = renderer.indexOf("this.model.renderGlass();");
        assertTrue("all three passes must be present",
                caseAt > 0 && itemAt > 0 && glassAt > 0);
        assertTrue("case, then item, then glass — in that order",
                caseAt < itemAt && itemAt < glassAt);
    }

    /** Eighteen parts, thirteen of case and five of glass, transcribed from upstream. */
    @Test
    public void theModelGeometryMatchesUpstream() throws IOException {
        String model = read(MODEL);
        assertTrue("the base plate", model.contains("box(0, 0, 16, 1, 16, -8.0F, 23.0F, -8.0F, 0.0F)"));
        assertTrue("a corner post", model.contains("box(0, 17, 2, 14, 2, -8.0F, 9.0F, 6.0F, 0.0F)"));
        assertTrue("the long rim piece", model.contains("box(11, 19, 16, 1, 2, -8.0F, 8.0F, -8.0F, 0.0F)"));
        assertTrue("the opening", model.contains("box(48, 24, 1, 3, 1, 1.0F, 14.5F, 7.0F, 0.0F)"));
        assertTrue("the glass lid, at upstream's negative U",
                model.contains("box(-11, 37, 12, 0, 12, -6.0F, 8.5F, -6.0F, 0.0F)"));
        assertTrue("a half-turned pane", model.contains("box(33, 50, 12, 14, 0, 6.0F, 9.0F, 7.5F, HALF_TURN)"));
        assertTrue("upstream renders at a sixteenth", model.contains("1.0F / 16.0F"));
    }

    /**
     * The tile's own tick counter stops on the client, so the renderer cannot
     * spin the item off it. Upstream's {@code ticksExisted} equivalent has to be
     * counted before the server-side early return.
     */
    @Test
    public void theSpinCounterAdvancesOnTheClient() throws IOException {
        String tile = read(Paths.get(
                "src/main/java/thaumcraft/common/tiles/tinkerer/TileRepairer.java"));
        int counted = tile.indexOf("this.renderTicks++;");
        int bailed = tile.indexOf("world.isRemote");
        assertTrue("renderTicks must exist", counted > 0);
        assertTrue("and must be counted before the server-only early return,"
                + " or the item hangs motionless for every client", counted < bailed);
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
