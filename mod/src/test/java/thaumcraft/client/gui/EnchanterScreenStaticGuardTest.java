package thaumcraft.client.gui;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The Osmotic Enchanter's buttons, pinned to the original's sprites and
 * coordinates.
 *
 * <p>The port shipped for a long while with placeholders — a vanilla
 * {@code GuiButton} with a "&gt;" for start, {@code drawRect} frames, vanilla
 * "-"/"+" for the levels — while the atlas the sprites live in
 * ({@code gui/enchanter.png}) was already carried over, sprites and all.
 * These assertions transcribe the numbers straight out of the original's four
 * button classes (Vazkii's {@code GuiButtonEnchant},
 * {@code GuiButtonEnchantment}, {@code GuiButtonFramedEnchantment},
 * {@code GuiButtonEnchanterLevel}), so the sprites cannot quietly turn back
 * into rectangles.</p>
 */
public class EnchanterScreenStaticGuardTest {

    private static final String BUTTONS_DIR = "src/main/java/thaumcraft/client/gui/tinkerer/";
    private static final String SCREEN = "src/main/java/thaumcraft/client/gui/GuiEnchanter.java";

    /** Start: 15×15 at u=176, v=24 idle / v=39 working — the sprite is the busy indicator. */
    @Test
    public void theStartButtonDrawsTheOriginalsSprite() throws IOException {
        String button = read(BUTTONS_DIR + "GuiButtonEnchant.java");
        assertTrue(button.contains("super(id, x, y, 15, 15, \"\")"));
        assertTrue(button.contains("final int u = 176;"));
        assertTrue("working switches the sprite one tile down",
                button.contains("isWorking() ? 39 : 24"));
        assertTrue("the tooltip is suppressed while working, as upstream does",
                button.contains("&& !this.enchanter.isWorking()"));
    }

    /** Level arrows: 7×7, minus at (218, 0), plus one tile over at (225, 0). */
    @Test
    public void theLevelArrowsDrawTheOriginalsSprites() throws IOException {
        String button = read(BUTTONS_DIR + "GuiButtonEnchanterLevel.java");
        assertTrue(button.contains("super(id, x, y, 7, 7, \"\")"));
        assertTrue(button.contains("218 + (this.plus ? 7 : 0)"));
    }

    /** The frame: 24×24 from (176, 0), drawn four pixels out, level text at +26/+8. */
    @Test
    public void theQueuedRowDrawsTheOriginalsFrame() throws IOException {
        String button = read(BUTTONS_DIR + "GuiButtonFramedEnchantment.java");
        assertTrue(button.contains("drawTexturedModalRect(this.x - 4, this.y - 4, 176, 0, 24, 24)"));
        assertTrue(button.contains("this.x + 26, this.y + 8"));
        assertTrue("the icon and tooltip come from the plain button on top",
                button.contains("super.drawButton(mc, mouseX, mouseY, partialTicks)"));
    }

    /**
     * The offer tooltip lists the base cost per aspect — the hand-tuned level-1
     * numbers, not the multiplied run price — and the framed variant appends
     * the click-to-remove hint outside a working run.
     */
    @Test
    public void theOfferTooltipQuotesBaseCosts() throws IOException {
        String button = read(BUTTONS_DIR + "GuiButtonEnchantment.java");
        assertTrue(button.contains("EnchantmentCosts.baseCostFor(this.enchant)"));
        assertTrue(button.contains("ttmisc.baseCost"));
        assertTrue(button.contains("aspect.getChatcolor()"));
        assertTrue(button.contains("ttmisc.clickToRemove"));

        for (String lang : new String[]{"en_us", "ru_ru"}) {
            String file = read("src/main/resources/assets/thaumcraft/lang/" + lang + ".lang");
            for (String key : new String[]{
                    "ttmisc.startEnchant=", "ttmisc.baseCost=", "ttmisc.clickToRemove="}) {
                assertTrue(key + " must exist in " + lang, file.contains(key));
            }
        }
    }

    /** The screen places them where the original does, and dims nothing while working. */
    @Test
    public void theScreenKeepsTheOriginalsLayout() throws IOException {
        String screen = read(SCREEN);
        assertTrue("start at (151, 33)",
                screen.contains("this.left + 151, this.top + 33"));
        assertTrue("offer grid from (34, 54), sixteen wide per step",
                screen.contains("this.left + 34 + col * 16"));
        assertTrue("minus at xSize+24, four up",
                screen.contains("id + 1, this.left + this.xSize + 24, y - 4, false"));
        assertTrue("plus at xSize+31 — seven over, the arrow's own width",
                screen.contains("id + 2, this.left + this.xSize + 31, y - 4, true"));
        assertFalse("no placeholder rectangles may return",
                screen.contains("drawRect("));
        assertFalse("upstream keeps every button live during a run; the server refuses instead",
                screen.contains("enabled = !enchanter.isWorking()"));
        assertTrue("the start button is gated on the queue alone",
                screen.contains("start.enabled = !enchanter.getQueuedEnchantments().isEmpty();"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
