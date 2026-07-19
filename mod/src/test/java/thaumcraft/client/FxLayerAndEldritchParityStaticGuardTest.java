package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FxLayerAndEldritchParityStaticGuardTest {

    /**
     * TC4 particle-sheet routing: smoke spiral cells live on thaumcraft
     * particles.png sheet 1 (normal blend). It must go through the TC
     * ParticleEngine (ITCParticle) and emit vertices into the engine's begun
     * buffer — never begin/draw its own.
     */
    @Test
    public void smokeSpiralStaysOnTCSheetOne() throws IOException {
        String fx = read("src/main/java/thaumcraft/client/fx/particles/FXSmokeSpiral.java");
        assertTrue(fx.contains("implements ITCParticle"));
        assertTrue(fx.contains("public int getTCParticleLayer() {"));
        assertTrue(fx.replace("\r\n", "\n").contains("return 1;"));
        assertFalse("engine owns begin/draw for TC particles", fx.contains("buffer.begin("));
        assertFalse("engine owns begin/draw for TC particles", fx.contains("tessellator.draw()"));
    }

    /**
     * Pickaxe of the Core scan overlay: nodes.png is a 32x32 grid and the
     * pulse must sample only strip (row) 0, with TC4 water/lava overlay colors
     * (3986684 = 0x3CD4FC, 16734721 = 0xFF5A01).
     */
    @Test
    public void oreScanOverlayMatchesTC4() throws IOException {
        String handler = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        assertTrue(handler.contains("0.0F, 1.0F / 32.0F"));
        assertTrue(handler.contains("0x3CD4FC"));
        assertTrue(handler.contains("0xFF5A01"));
        assertFalse(handler.contains("0x3CD4BC"));
        assertFalse(handler.contains("0xFF55C1"));
    }

    /**
     * TC4 BlockEldritchNothing (Outer Lands void filler) is solid: collision
     * cube inset 0.125 per side plus 8.0 contact damage; only the selection
     * box is zero-size.
     */
    @Test
    public void eldritchNothingKeepsTC4CollisionContract() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockEldritchNothing.java");
        assertTrue(block.contains("new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 0.875, 0.875)"));
        assertTrue(block.contains("return COLLISION_AABB;"));
        assertTrue(block.contains("public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)"));
        assertTrue(block.contains("attackEntityFrom(net.minecraft.util.DamageSource.OUT_OF_WORLD, 8.0f)"));
    }

    /**
     * Web-nest passages (maze feature 14) must point their spawner at the
     * registered entity path: legacyPath("MindSpider") == "mindspider".
     */
    @Test
    public void webNestSpawnerUsesRegisteredEntityId() throws IOException {
        String gen = read("src/main/java/thaumcraft/common/lib/world/dim/GenPassage.java");
        assertTrue(gen.contains("new net.minecraft.util.ResourceLocation(\"thaumcraft\", \"mindspider\")"));
        assertFalse(gen.contains("mind_spider"));
    }

    /** Warded glass model must resolve to a texture that ships in resources. */
    @Test
    public void wardedGlassTextureShips() throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/block/blockcosmeticopaque_2.json");
        assertTrue(model.contains("thaumcraft:blocks/wardedglass"));
        assertTrue(Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/wardedglass.png")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
