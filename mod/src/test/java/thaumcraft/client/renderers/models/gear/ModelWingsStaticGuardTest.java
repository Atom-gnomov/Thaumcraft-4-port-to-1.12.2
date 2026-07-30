package thaumcraft.client.renderers.models.gear;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The Robes of the Stratosphere have wings, and they come from one hook: the
 * chestplate's {@code getArmorModel} returning {@code ModelWings}. Drop the
 * override and the robe silently falls back to the plain biped armour model —
 * no error, no missing texture, just no wings. That is how it shipped, and it
 * was reported from the game rather than caught here.
 */
public class ModelWingsStaticGuardTest {

    private static final Path MODEL =
            Paths.get("src/main/java/thaumcraft/client/renderers/models/gear/ModelWings.java");
    private static final Path CHEST =
            Paths.get("src/main/java/thaumcraft/common/items/tinkerer/kami/armor/ItemGemChest.java");

    @Test
    public void theRobeReturnsTheWingModel() throws IOException {
        String chest = read(CHEST);
        assertTrue("ItemGemChest must override getArmorModel — without it there are no wings",
                chest.contains("public ModelBiped getArmorModel("));
        assertTrue("and it must return the wings", chest.contains("new ModelWings()"));
        assertTrue("the override touches client-only classes and must be marked",
                chest.contains("@SideOnly(Side.CLIENT)"));
    }

    /**
     * The geometry, transcribed from Vazkii's original. Two zero-width boxes
     * seven tall and twelve deep, parented to the body and angled apart — one
     * at the resting angle, one mirrored short of it.
     */
    @Test
    public void theWingGeometryMatchesUpstream() throws IOException {
        String model = read(MODEL);
        assertTrue("both wings are cut at texture offset (16, -12)",
                model.contains("new ModelRenderer(this, 16, -12)"));
        assertTrue("first wing box", model.contains("addBox(0.0F, 0.0F, 0.0F, 0, 7, 12)"));
        assertTrue("second wing box", model.contains("addBox(0.1F, 0.0F, 0.0F, 0, 7, 12)"));
        assertTrue("first wing pivot", model.contains("setRotationPoint(-2.0F, 1.0F, 2.0F)"));
        assertTrue("second wing pivot", model.contains("setRotationPoint(2.0F, 1.0F, 2.0F)"));
        assertTrue("resting angle", model.contains("WING_REST_ANGLE = -0.6108652F"));
        assertTrue("second wing angle", model.contains("0.4468043F"));
        assertTrue("both wings hang off the body", model.contains("bipedBody.addChild"));
        assertTrue("the sheet is the 64x32 ichor gem armour texture",
                model.contains("textureWidth = 64") && model.contains("textureHeight = 32"));
    }

    /**
     * The wings ride on {@code ichor_gem1.png}. Upstream ships a {@code wings.png}
     * but nothing references it — not {@code LibResources}, not a single class —
     * so a port that goes looking for it is chasing a leftover. Recorded here
     * because the texture audit first read its absence as the defect.
     */
    @Test
    public void theWingsDoNotAskForATextureOfTheirOwn() throws IOException {
        String model = read(MODEL);
        // A string literal, not the word — the class javadoc explains the leftover
        // on purpose, and that explanation is the point of this guard, not a breach.
        assertFalse("upstream's wings.png is unreferenced dead weight; the wings are cut"
                + " from the chestplate sheet at a negative V offset",
                model.contains("\"wings.png\"") || model.contains("wings.png\""));
        assertTrue("the chestplate sheet must be the 64x32 one it samples",
                Files.exists(Paths.get(
                        "src/main/resources/assets/thaumcraft/textures/models/ichor_gem1.png")));
    }

    /** 1.7.10's heldItemRight/aimedBow became the arm pose enum; all three cases must survive. */
    @Test
    public void theArmPoseTranslationKeepsAllThreeCases() throws IOException {
        String model = read(MODEL);
        for (String pose : new String[]{"ArmPose.EMPTY", "ArmPose.ITEM",
                "ArmPose.BLOCK", "ArmPose.BOW_AND_ARROW"}) {
            assertTrue(pose + " must be handled", model.contains(pose));
        }
        assertTrue("the flap only runs while actually flying",
                model.contains("player.capabilities.isFlying"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
