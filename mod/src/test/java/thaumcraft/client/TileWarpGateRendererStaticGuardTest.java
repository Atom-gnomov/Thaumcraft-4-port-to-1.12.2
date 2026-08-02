package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * The warp gate's ring of spinning cubes, pinned to the original.
 *
 * <p>The gate worked but sat dead still: the block is an ordinary baked model
 * and the TESR that draws the ring above it was never carried over, so nothing
 * even referenced a renderer. The port is a transcription of Vazkii's
 * {@code ModelSpinningCubes} + {@code RenderTileWarpGate}, and its oddities
 * are the point — losing any of them changes the look:</p>
 *
 * <ul>
 * <li>texturing off for the whole pass (flat-colour cubes);</li>
 * <li>the clock is the client player's {@code ticksExisted} — all gates spin
 * in phase;</li>
 * <li>fullbright lightmap, upstream's literal {@code 15728880};</li>
 * <li>recursion: each repeat redraws the ring 0.75 ticks back at alpha 0.2 —
 * the ghost trail.</li>
 * </ul>
 */
public class TileWarpGateRendererStaticGuardTest {

    private static final String MODEL =
            "src/main/java/thaumcraft/client/renderers/models/ModelSpinningCubes.java";
    private static final String RENDERER =
            "src/main/java/thaumcraft/client/renderers/tile/TileWarpGateRenderer.java";

    @Test
    public void theModelKeepsTheOriginalsConstants() throws IOException {
        String model = read(MODEL);
        for (String constant : new String[]{
                "modifier = 6.0F", "rotationModifier = 0.25F",
                "radiusBase = 0.7F", "radiusMod = 0.1F"}) {
            assertTrue(constant, model.contains(constant));
        }
        assertTrue("the clock is the client player's tick count",
                model.contains("Minecraft.getMinecraft().player.ticksExisted"));
        assertTrue("each repeat steps 0.75 ticks back",
                model.contains("- 0.75D * (origRepeat - repeat)"));
        assertTrue("fullbright, upstream's literal",
                model.contains("int light = 15728880;"));
        assertTrue("the ghost repeats blend at 0.2",
                model.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 0.2F)"));
        assertTrue("the trail is drawn by recursion",
                model.contains("renderSpinningCubes(cubes, repeat - 1, origRepeat)"));
        assertTrue("texturing is off for the pass — the cubes are flat colour",
                model.contains("disableTexture2D()") && model.contains("enableTexture2D()"));
    }

    @Test
    public void theRendererHangsTheRingWhereTheOriginalDoes() throws IOException {
        String renderer = read(RENDERER);
        assertTrue("two and a half blocks up, centred",
                renderer.contains("translate(x + 0.5D, y + 2.5D, z + 0.5D)"));
        assertTrue("upstream's flip about (1, 0, 1)",
                renderer.contains("rotate(180.0F, 1.0F, 0.0F, 1.0F)"));
        assertTrue("twelve cubes, five ghost repeats",
                renderer.contains("renderSpinningCubes(12, repeat, repeat)")
                        && renderer.contains("int repeat = 5;"));
    }

    /** A renderer nothing binds is decoration for nobody. */
    @Test
    public void theRendererIsBoundToTheTile() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        assertTrue(proxy.contains("TileWarpGate.class")
                && proxy.contains("TileWarpGateRenderer()"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
