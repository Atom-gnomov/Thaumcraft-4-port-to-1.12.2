package thaumcraft.common.blocks.tinkerer;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Four Thaumic Tinkerer blocks are flat in the original — the enchanter is a
 * twelve-pixel bench, the funnel, the dynamism tablet and the tablet of
 * necromancy are two-pixel plates — and they get that shape from
 * {@code setBlockBounds} plus {@code renderAsNormalBlock() == false}. In 1.7.10
 * that was enough: {@code RenderBlocks} drew the box straight off the bounds.
 * In 1.12 the bounds and the model are two separate declarations, so a block
 * can collide as a plate and still <em>render</em> as a full cube — which is
 * exactly how all four shipped up to 1.1.37.6.
 *
 * <p>This guard pins both halves together: the AABB in the Java and the element
 * in the JSON have to agree, and neither may quietly go back to a cube.</p>
 */
public class TinkererBlockShapeStaticGuardTest {

    private static final Path BLOCKS = Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer");
    private static final Path MODELS = Paths.get("src/main/resources/assets/thaumcraft/models/block");
    private static final Path TEXTURES = Paths.get("src/main/resources/assets/thaumcraft/textures/blocks");

    /**
     * Block class, model name, height in sixteenths, and the Java literal that
     * spells that height. The literal is kept in the same shape the original
     * wrote it in — the funnel says an eighth, the two tablets say two
     * sixteenths — so the transcription stays legible against the source.
     */
    private static final String[][] FLAT_BLOCKS = {
            // BlockEnchanter:       setBlockBounds(0F, 0F, 0F, 1F, 0.75F, 1F)
            {"BlockEnchanter", "blockenchanter", "12", "0.75D"},
            // BlockFunnel:          setBlockBounds(0F, 0F, 0F, 1F, 1F / 8F, 1F)
            {"BlockFunnel", "blockfunnel", "2", "1.0D / 8.0D"},
            // BlockAnimationTablet: setBlockBounds(0F, 0F, 0F, 1F, 1F / 16F * 2F, 1F)
            {"BlockAnimationTablet", "blockanimationtablet", "2", "2.0D / 16.0D"},
            // BlockSummon:          setBlockBounds(0F, 0F, 0F, 1F, 1F / 16F * 2F, 1F)
            {"BlockSummon", "blocksummon", "2", "2.0D / 16.0D"},
    };

    @Test
    public void everyFlatBlockDeclaresItsBoundsInJava() throws IOException {
        List<String> wrong = new ArrayList<>();
        for (String[] block : FLAT_BLOCKS) {
            String source = read(BLOCKS.resolve(block[0] + ".java"));
            String height = block[3];
            if (!squash(source).contains("new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, " + height + ", 1.0D)")) {
                wrong.add(block[0] + " (expected height " + height + ")");
            }
            if (!source.contains("public boolean isFullCube(IBlockState state) {")) {
                wrong.add(block[0] + " must override isFullCube");
            }
            if (!source.contains("public boolean isOpaqueCube(IBlockState state) {")) {
                wrong.add(block[0] + " must override isOpaqueCube");
            }
        }
        assertTrue("these blocks are flat in the original but their Java bounds do not say so: "
                + wrong, wrong.isEmpty());
    }

    @Test
    public void everyFlatBlockModelIsAnElementAndNotACube() throws IOException {
        List<String> wrong = new ArrayList<>();
        for (String[] block : FLAT_BLOCKS) {
            String model = squash(read(MODELS.resolve(block[1] + ".json")));

            if (model.contains("\"parent\": \"block/cube")) {
                wrong.add(block[1] + " still inherits a full cube");
            }
            if (!model.contains("\"elements\"")) {
                wrong.add(block[1] + " has no elements array");
            }
            if (!model.contains("\"to\": [16, " + block[2] + ", 16]")) {
                wrong.add(block[1] + " element must end at y=" + block[2]);
            }
            if (!model.contains("\"from\": [0, 0, 0]")) {
                wrong.add(block[1] + " element must start at the floor");
            }
            // Break particles come from #particle; a custom-element model has to set it
            // itself, because it no longer inherits one from block/cube_bottom_top.
            if (!model.contains("\"particle\"")) {
                wrong.add(block[1] + " must declare a particle texture");
            }
        }
        assertTrue("these models draw a full cube where the original draws a plate: "
                + wrong, wrong.isEmpty());
    }

    /**
     * UVs are deliberately left implicit. 1.12 derives them from the element
     * ({@code BlockPart.getFaceUvs}) with the same arithmetic 1.7.10 used to
     * derive them from the bounds ({@code getInterpolatedV(16 - maxY * 16)}), so
     * omitting them reproduces the original's cropping exactly. Writing them out
     * by hand can only drift from it.
     */
    @Test
    public void flatBlockModelsDoNotHardcodeUvs() throws IOException {
        for (String[] block : FLAT_BLOCKS) {
            String model = read(MODELS.resolve(block[1] + ".json"));
            assertFalse(block[1] + " must let 1.12 derive the UVs from the element,"
                    + " the way RenderBlocks derived them from the bounds",
                    model.contains("\"uv\""));
        }
    }

    /**
     * The funnel is the one that is <em>not</em> symmetric: it looks like a
     * two-pixel plate but its {@code getCollisionBoundingBoxFromPool} hands back
     * the whole cube, so it stops you like a solid block. Drop the override and
     * players start standing inside it.
     */
    @Test
    public void theFunnelKeepsItsFullCollisionBox() throws IOException {
        String source = read(BLOCKS.resolve("BlockFunnel.java"));
        assertTrue("BlockFunnel must override getCollisionBoundingBox",
                source.contains("public AxisAlignedBB getCollisionBoundingBox("));
        assertTrue("the original's collision box is the full cube, not the visible plate",
                source.contains("return FULL_BLOCK_AABB;"));
    }

    /**
     * {@code registerBlockIcons} numbers the dynamism tablet's icons 0 = bottom,
     * 1 = top, 2 = sides. The port copied only two of the three files and put
     * them on the wrong faces: the bottom texture was drawn on top, the top
     * texture on the sides and the bottom, and the real side texture was never
     * copied at all.
     */
    @Test
    public void theDynamismTabletsThreeFacesAreDistinctAndCorrectlyNamed() throws IOException {
        String[] faces = {"animation_tablet_bottom", "animation_tablet_top", "animation_tablet_side"};
        List<String> digests = new ArrayList<>();
        for (String face : faces) {
            Path png = TEXTURES.resolve(face + ".png");
            assertTrue(face + ".png is one of animationTablet0/1/2 in the original and must exist",
                    Files.exists(png));
            digests.add(digest(png));
        }
        assertEquals("animationTablet0, 1 and 2 are three different images; if two of these"
                + " match, a face is wearing another face's texture",
                3, new java.util.HashSet<>(digests).size());

        String model = squash(read(MODELS.resolve("blockanimationtablet.json")));
        assertTrue("bottom face must be animationTablet0",
                model.contains("\"bottom\": \"thaumcraft:blocks/animation_tablet_bottom\""));
        assertTrue("top face must be animationTablet1",
                model.contains("\"top\": \"thaumcraft:blocks/animation_tablet_top\""));
        assertTrue("side faces must be animationTablet2",
                model.contains("\"side\": \"thaumcraft:blocks/animation_tablet_side\""));
    }

    /**
     * Dark quartz is vanilla quartz with a different palette, and the original
     * was written that way: five metas, where 2/3/4 are the pillar standing on
     * Y, X and Z. The port had three, drew the chiseled block and the pillar as
     * {@code cube_all} — side texture on all six faces — and never copied the
     * two end textures the original registers.
     */
    @Test
    public void darkQuartzHasItsEndFacesAndAllThreePillarAxes() throws IOException {
        for (String end : new String[]{"dark_quartz_chiseled_top", "dark_quartz_pillar_top"}) {
            assertTrue(end + ".png is an end face the original registers and must exist",
                    Files.exists(TEXTURES.resolve(end + ".png")));
        }

        String chiseled = squash(read(MODELS.resolve("blockdarkquartz_1.json")));
        assertFalse("chiseled dark quartz has a distinct end face, so it is not cube_all",
                chiseled.contains("block/cube_all"));
        assertTrue("chiseled top must be chiseledDarkQuartz1",
                chiseled.contains("\"top\": \"thaumcraft:blocks/dark_quartz_chiseled_top\""));

        String pillar = squash(read(MODELS.resolve("blockdarkquartz_2.json")));
        assertTrue("the pillar is a column, not a uniform cube",
                pillar.contains("\"parent\": \"block/cube_column\""));
        assertTrue("pillar end must be pillarDarkQuartz1",
                pillar.contains("\"end\": \"thaumcraft:blocks/dark_quartz_pillar_top\""));

        String state = squash(read(Paths.get(
                "src/main/resources/assets/thaumcraft/blockstates/blockdarkquartz.json")));
        for (String variant : new String[]{"variant=0", "variant=1", "variant=2", "variant=3", "variant=4"}) {
            assertTrue("the original has five metas; " + variant + " is missing",
                    state.contains("\"" + variant + "\""));
        }

        String source = read(BLOCKS.resolve("BlockDarkQuartz.java"));
        assertTrue("VARIANT must span all five metas",
                source.contains("PropertyInteger.create(\"variant\", 0, 4)"));
        assertTrue("placing the pillar must pick its axis, as the original's onBlockPlaced does",
                source.contains("public IBlockState getStateForPlacement("));
        assertTrue("a lying pillar drops the upright one",
                source.contains("public int damageDropped(IBlockState state)")
                        && source.contains("uprightIfPillar"));
        assertTrue("pick-block on a lying pillar gives the upright one",
                source.contains("public ItemStack getItem(World world, BlockPos pos, IBlockState state)"));
        assertTrue("only the first three variants belong in creative, as upstream",
                source.contains("VARIANTS = {\"plain\", \"chiseled\", \"pillar\"}"));
    }

    /** Collapse runs of whitespace so the assertions do not depend on formatting. */
    private static String squash(String text) {
        return text.replaceAll("\\s+", " ");
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static String digest(Path path) throws IOException {
        try {
            byte[] hash = java.security.MessageDigest.getInstance("MD5")
                    .digest(Files.readAllBytes(path));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new AssertionError(impossible);
        }
    }
}
