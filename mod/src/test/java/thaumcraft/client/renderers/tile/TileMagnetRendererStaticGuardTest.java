package thaumcraft.client.renderers.tile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * The magnet has no static geometry upstream: {@code RenderMagnet.renderWorldBlock}
 * returns false and {@code ModelMagnet} is the entire block. The port drew a
 * JSON cube instead, which is why the block never matched the two-pixel
 * collision box it already declared — the bounds were right and the picture
 * was not.
 */
public class TileMagnetRendererStaticGuardTest {

    private static final Path RENDERER =
            Paths.get("src/main/java/thaumcraft/client/renderers/tile/TileMagnetRenderer.java");
    private static final Path MODEL =
            Paths.get("src/main/java/thaumcraft/client/renderers/models/ModelMagnet.java");
    private static final Path BLOCK =
            Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockMagnet.java");
    private static final Path TEXTURES =
            Paths.get("src/main/resources/assets/thaumcraft/textures/models");

    /** All four sheets: pulling or pushing, plain or mob. */
    @Test
    public void allFourMagnetSheetsArePresentAndBound() throws IOException {
        String renderer = read(RENDERER);
        for (String sheet : new String[]{"magnet_s", "magnet_n", "mob_magnet_s", "mob_magnet_n"}) {
            assertTrue(sheet + ".png must exist", Files.exists(TEXTURES.resolve(sheet + ".png")));
            assertTrue(sheet + " must be bound by the renderer",
                    renderer.contains("textures/models/" + sheet + ".png"));
        }
        assertTrue("the sheet is picked off the same two bits the blockstate carries",
                renderer.contains("BlockMagnet.PULLING") && renderer.contains("BlockMagnet.MOB"));
    }

    /**
     * If the block keeps {@code MODEL}, the blockstate cube draws on top of the
     * renderer and you see both.
     */
    @Test
    public void theBlockLeavesItsGeometryToTheRenderer() throws IOException {
        String block = read(BLOCK);
        assertTrue("the magnet must be TESR-only, as upstream is",
                block.contains("EnumBlockRenderType.ENTITYBLOCK_ANIMATED"));

        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        assertTrue("TileMagnet must be bound", proxy.contains("TileMagnet.class, magnetRenderer"));
        assertTrue("TileMobMagnet extends TileMagnet but still binds separately",
                proxy.contains("TileMobMagnet.class, magnetRenderer"));
    }

    /** The seven parts, transcribed from Vazkii's original. */
    @Test
    public void theModelGeometryMatchesUpstream() throws IOException {
        String model = read(MODEL);
        assertTrue("the plate", model.contains("box(0, 0, 0.0F, 0.0F, 0.0F, 14, 2, 14, -7.0F, 22.0F, -7.0F, 0.0F)"));
        assertTrue("the post", model.contains("box(0, 16, 0.0F, 0.0F, 0.0F, 4, 13, 4, -2.0F, 9.0F, -2.0F, 0.0F)"));
        assertTrue("first vane, the one with the -2 Y offset",
                model.contains("box(28, 19, 0.0F, -2.0F, 0.0F, 6, 14, 0, 3.0F, 10.0F, -3.0F, -1.570796F)"));
        assertTrue("the cap", model.contains("box(28, 49, 0.0F, 0.0F, 0.0F, 6, 0, 6, -3.0F, 8.0F, -3.0F, 0.0F)"));
        assertTrue("upstream renders at a sixteenth", model.contains("1.0F / 16.0F"));
        assertTrue("the model is flipped onto the block, as upstream flips it",
                read(RENDERER).contains("scale(1.0F, -1.0F, -1.0F)"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
