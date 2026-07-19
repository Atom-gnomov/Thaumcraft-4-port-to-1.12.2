package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AmbientBiomeVisualParityStaticGuardTest {

    @Test
    public void taintAndEerieSkyColorRemainBiomeDriven() throws IOException {
        String taintBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java");
        String eerieBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java");
        String renderHandler = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String tickHandler = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");

        assertTrue(taintBiome.contains("public int getSkyColorByTemp(float temp)"));
        assertTrue(taintBiome.contains("return 0x7C44FF;"));
        assertTrue(eerieBiome.contains("public int getSkyColorByTemp(float temp)"));
        assertTrue(eerieBiome.contains("return 0x222299;"));

        assertTrue(renderHandler.contains("event.getType() == RenderGameOverlayEvent.ElementType.PORTAL"));
        assertTrue(renderHandler.contains("renderVignette(targetBrightness"));
        assertTrue(tickHandler.contains("if (warpVignette > 0)"));
        assertFalse(renderHandler.contains("biomeTaint"));
        assertFalse(renderHandler.contains("biomeEerie"));
        assertFalse(tickHandler.contains("biomeTaint"));
        assertFalse(tickHandler.contains("biomeEerie"));
    }

    @Test
    public void biomeGrassFoliageWaterColorsMatchTC4() throws IOException {
        String magicalForest = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java").replace("\r\n", "\n");
        String eerieBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java").replace("\r\n", "\n");
        String taintBiome = read("src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java").replace("\r\n", "\n");

        // TC4 BiomeGenMagicalForest: grass func_150558_b = 5635969 (0x55FF81),
        // foliage func_150571_c = 6750149 (0x66FFC5), water = 30702 (0x77EE)
        assertTrue(magicalForest.contains("public int getGrassColorAtPos(BlockPos pos) {\n        return Config.blueBiome ? 0x66AACC : 0x55FF81;"));
        assertTrue(magicalForest.contains("public int getFoliageColorAtPos(BlockPos pos) {\n        return Config.blueBiome ? 0x77CCEE : 0x66FFC5;"));
        assertTrue(magicalForest.contains(".setWaterColor(0x0077EE)"));

        // TC4 BiomeGenEerie: grass = 0x404840, foliage = 4215616 (0x405340), water = 3035999 (0x2E535F)
        assertTrue(eerieBiome.contains("public int getGrassColorAtPos(BlockPos pos) {\n        return 0x404840;"));
        assertTrue(eerieBiome.contains("public int getFoliageColorAtPos(BlockPos pos) {\n        return 0x405340;"));
        assertTrue(eerieBiome.contains("public int getWaterColorMultiplier() {\n        return 0x2E535F;"));

        // TC4 BiomeGenTaint: grass = 7160201 (0x6D4189), foliage = 8154503 (0x7C6D87), water = 0xCC1188
        assertTrue(taintBiome.contains("return 0x6D4189;"));
        assertTrue(taintBiome.contains("return 0x7C6D87;"));
        assertTrue(taintBiome.contains("return 0xCC1188;"));
    }

    @Test
    public void darkAndTaintedNodesMutateBiomesLikeTC4() throws IOException {
        String node = read("src/main/java/thaumcraft/common/tiles/TileNode.java");
        String utils = read("src/main/java/thaumcraft/common/lib/utils/Utils.java");

        assertTrue(node.contains("changed = handleTaintNode(changed);"));
        assertTrue(node.contains("changed = handleDarkNode(changed);"));

        assertTrue(node.contains("private boolean handleTaintNode(boolean changed)"));
        assertTrue(node.contains("this.getNodeType() == NodeType.TAINTED && this.count % 50 == 0"));
        assertTrue(node.contains("this.world.rand.nextInt(8) - this.world.rand.nextInt(8)"));
        assertTrue(node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);"));
        assertTrue(node.contains("Config.hardNode && this.world.rand.nextBoolean()"));
        assertTrue(node.contains("BlockTaintFibres.spreadFibres(this.world, new BlockPos(x, y, z));"));
        assertTrue(node.contains("this.world.rand.nextInt(500) != 0"));
        assertTrue(node.contains("this.setNodeType(NodeType.TAINTED);"));

        assertTrue(node.contains("private boolean handleDarkNode(boolean changed)"));
        assertTrue(node.contains("this.getNodeType() != NodeType.DARK || this.count % 50 != 0"));
        assertTrue(node.contains("this.world.rand.nextInt(12) - this.world.rand.nextInt(12)"));
        assertTrue(node.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeEerie);"));
        assertTrue(node.contains("new EntityGiantBrainyZombie(this.world)"));
        assertTrue(node.contains("this.world.getClosestPlayer("));
        assertTrue(node.contains("new AxisAlignedBB(this.pos).grow(10.0D, 6.0D, 10.0D)"));
        assertTrue(node.contains("this.world.playEvent(2004, this.pos, 0);"));
        assertTrue(node.contains("entity.spawnExplosionParticle();"));

        assertTrue(utils.contains("new PacketBiomeChange(x, z, (short) biomeId)"));
    }

    @Test
    public void airyFireAndEerieAmbientSparksMatchTC4Colors() throws IOException {
        String airy = read("src/main/java/thaumcraft/common/blocks/BlockAiry.java");

        assertTrue(airy.contains("public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)"));
        assertTrue(airy.contains("if (meta != 10 && meta != 11)"));
        assertTrue(airy.contains("float h = rand.nextFloat() * 0.33F;"));
        assertTrue(airy.contains("0.1515F + h / 2.0F"));
        assertTrue(airy.contains("0.33F + h"));
        assertTrue(airy.contains("0.65F + rand.nextFloat() * 0.1F, 1.0F, 1.0F, 0.8F"));
        assertTrue(airy.contains("0.3F - rand.nextFloat() * 0.1F, 0.0F"));
        assertTrue(airy.contains("0.5F + rand.nextFloat() * 0.2F, 1.0F"));
        assertTrue(airy.contains("Thaumcraft.proxy.spark("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
