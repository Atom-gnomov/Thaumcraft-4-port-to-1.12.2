package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Thaumic Tinkerer's foci wear a second layer on the <em>wand</em> — an
 * ornament or a depth layer, drawn by {@code ModelWand}, never on the focus's
 * own icon. Thaumcraft's foci mostly do not, so the port carried the machinery
 * for one focus (the pech's depth layer) and dropped all eight of Tinkerer's.
 *
 * <p>Two things have to line up and neither shows up as a broken texture in the
 * log: the focus has to override {@code getOrnament} or
 * {@code getFocusDepthLayerIcon}, and the sprite has to reach the atlas. No item
 * model references these files, so the only thing that stitches them is
 * {@code ClientModelRegistry}; miss that and {@code getAtlasSprite} quietly
 * returns the missing-texture placeholder instead of failing.</p>
 */
public class TinkererFocusLayerStaticGuardTest {

    private static final Path FOCI = Paths.get("src/main/java/thaumcraft/common/items/wands/foci");
    private static final Path TEXTURES = Paths.get("src/main/resources/assets/thaumcraft/textures/items");
    private static final Path REGISTRY = Paths.get("src/main/java/thaumcraft/client/ClientModelRegistry.java");

    /**
     * Focus class, the accessor upstream overrides, and the sprite. Taken from
     * {@code hasOrnament()}/{@code hasDepth()} in each upstream focus — the
     * Tinkerer base class {@code ItemModFocus} declares both and each focus
     * turns exactly one of them on.
     */
    private static final String[][] LAYERS = {
            {"FocusSmelt", "getOrnament", "focus_smelt_orn"},
            {"FocusFlight", "getOrnament", "focus_flight_orn"},
            {"FocusTelekinesis", "getOrnament", "focus_telekinesis_orn"},
            {"FocusDislocation", "getOrnament", "focus_dislocation_orn"},
            {"FocusShadowbeam", "getOrnament", "focus_shadowbeam_orn"},
            {"FocusHeal", "getFocusDepthLayerIcon", "focus_heal_depth"},
            {"FocusEnderChest", "getFocusDepthLayerIcon", "focus_ender_chest_depth"},
            {"FocusRecall", "getFocusDepthLayerIcon", "focus_recall_depth"},
    };

    @Test
    public void everyTinkererFocusLayerHasItsTexture() {
        List<String> missing = new ArrayList<>();
        for (String[] layer : LAYERS) {
            if (!Files.exists(TEXTURES.resolve(layer[2] + ".png"))) {
                missing.add(layer[2] + ".png (" + layer[0] + ")");
            }
        }
        assertTrue("upstream registers these layers for its foci and they must be copied: "
                + missing, missing.isEmpty());
    }

    @Test
    public void everyTinkererFocusOverridesItsAccessor() throws IOException {
        List<String> wrong = new ArrayList<>();
        for (String[] layer : LAYERS) {
            String source = read(FOCI.resolve(layer[0] + ".java"));
            if (!source.contains("public TextureAtlasSprite " + layer[1] + "(ItemStack stack)")) {
                wrong.add(layer[0] + " must override " + layer[1]);
            }
            if (!source.contains("\"thaumcraft:items/" + layer[2] + "\"")) {
                wrong.add(layer[0] + " must point at " + layer[2]);
            }
            // Without @SideOnly the class drags Minecraft.class onto the server.
            if (!source.contains("@SideOnly(Side.CLIENT)")) {
                wrong.add(layer[0] + " must mark the accessor client-only");
            }
        }
        assertTrue("Tinkerer focus layers not wired: " + wrong, wrong.isEmpty());
    }

    /**
     * The half that fails silently. A sprite nothing stitches is not an error at
     * load — it is a magenta square at render, and only on the wand, which is
     * why it survived every asset audit that came before.
     */
    @Test
    public void everyTinkererFocusLayerIsStitchedIntoTheAtlas() throws IOException {
        String registry = read(REGISTRY);
        List<String> unstitched = new ArrayList<>();
        for (String[] layer : LAYERS) {
            if (!registry.contains("\"items/" + layer[2] + "\"")) {
                unstitched.add(layer[2]);
            }
        }
        assertTrue("no item model references these, so ClientModelRegistry is the only thing"
                + " that can put them in the atlas: " + unstitched, unstitched.isEmpty());
        assertTrue("the stitch loop must actually run over the list",
                registry.contains("for (String sprite : TINKERER_FOCUS_LAYER_SPRITES)"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
