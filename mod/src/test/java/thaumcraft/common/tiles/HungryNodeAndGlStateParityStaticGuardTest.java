package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class HungryNodeAndGlStateParityStaticGuardTest {

    /**
     * TC4 hungry node behavior must stay wired into the node tick:
     * part 1 every tick (FX + hardNode pull/consume), part 2 every 50 ticks
     * (destroys a random raycast block with hardness in [0,5)).
     */
    @Test
    public void hungryNodeKeepsTC4Behavior() throws IOException {
        String node = read("src/main/java/thaumcraft/common/tiles/TileNode.java");
        assertTrue(node.contains("changed = handleHungryNodeFirst(changed);"));
        assertTrue(node.contains("changed = handleHungryNodeSecond(changed);"));
        assertTrue(node.contains("hungryNodeFX(this.world, target, this.pos, state)"));
        assertTrue("hardNode gate like TC4", node.contains("if (!Config.hardNode)"));
        assertTrue("50-tick block eating", node.contains("this.count % 50 != 0"));
        assertTrue("hardness window [0,5)", node.contains("hardness >= 0.0f && hardness < 5.0f"));
        assertTrue("pull strength constants like TC4",
                node.contains("* strength * 0.15") && node.contains("* strength * 0.25"));

        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        assertTrue(proxy.contains("public void hungryNodeFX(World world"));
    }

    /**
     * Item/tile renderers that override the lightmap must restore it, otherwise
     * following GUI slots or TESRs render with a leaked brightness (black slots).
     */
    @Test
    public void lightmapOverridesAreRestored() throws IOException {
        String thaumometer = read("src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java");
        assertTrue(thaumometer.contains("float prevLightX = OpenGlHelper.lastBrightnessX;"));
        assertTrue(thaumometer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);"));

        String crystalizer = read("src/main/java/thaumcraft/client/renderers/tile/TileEssentiaCrystalizerRenderer.java");
        assertTrue(crystalizer.contains("float prevLightX = OpenGlHelper.lastBrightnessX;"));
        assertTrue(crystalizer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
