package thaumcraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Draws Thaumcraft's screens a tenth smaller than Minecraft would.
 *
 * <p><b>Owner's decision, 2026-07-30.</b> Not upstream behaviour and not a
 * defect being fixed — the panels were judged too large at the game's own GUI
 * scale. Recorded here so it is never "corrected" back.</p>
 *
 * <h3>How it works, and why it is done this way</h3>
 *
 * <p>The obvious approach — wrap the drawing in a scale and then convert every
 * mouse coordinate back by hand — needs the conversion applied at every entry
 * point, including inside subclasses that do their own hit-testing. Miss one
 * and the clicks land somewhere other than the picture.</p>
 *
 * <p>So the screen is enlarged instead of the drawing being shrunk. A screen
 * that reports itself {@code 1 / 0.9} times larger and is then drawn at
 * {@code 0.9} lands on exactly the same pixels — but every coordinate
 * Minecraft computes for it, mouse included, is already in the enlarged space,
 * because {@code GuiScreen.handleMouseInput} derives the pointer from
 * {@code this.width} and {@code this.height}. Nothing downstream has to know
 * about the scale.</p>
 *
 * <p>The one exception is {@code drawScreen}, whose mouse position comes from
 * {@code EntityRenderer} against the real resolution rather than from the
 * screen — that pair has to be divided across. {@link GuiContainerScaled} does
 * it in one place.</p>
 */
@SideOnly(Side.CLIENT)
public final class GuiScale {

    /**
     * <b>Currently 1.0 — the reduction is switched off.</b>
     *
     * <p>It shipped at {@code 0.9F} in 1.1.39.1 and the owner reported that the
     * Thaumic Tinkerer screens stopped behaving properly, so it is disabled
     * until the cause is known. Everything below still runs; at 1.0 it is an
     * identity transform, so the screens behave exactly as they did before the
     * reduction was introduced.</p>
     *
     * <p><b>The owner still wants ten percent off</b> — see {@code CHANGELOG.md}
     * for 1.1.39.1. This is a suspension, not a reversal of that decision. Do
     * not delete the machinery; put the cause right and set this back to
     * {@code 0.9F}.</p>
     *
     * <p>What was checked and ruled out, so it is not re-checked from scratch:
     * every container screen calls {@code super.initGui()} before laying itself
     * out; none calls {@code initGui()} again by hand, so the enlargement cannot
     * compound; {@code GuiScreen.handleMouseInput} derives clicks from
     * {@code width}/{@code height} and {@code GuiContainer} does not override
     * it, so clicks and layout share one space; and the rounding of the two
     * paths agrees. The fault is somewhere this reading did not reach.</p>
     */
    public static final float FACTOR = 1.0F;

    private GuiScale() {
    }

    /** The logical size a screen must claim so that drawing it at {@link #FACTOR} fills the real one. */
    public static int enlarge(int realSize) {
        return MathHelper.ceil(realSize / FACTOR);
    }

    /**
     * A coordinate handed in against the real resolution, in the enlarged space
     * the screen thinks in.
     */
    public static int toGuiSpace(int realCoord) {
        return Math.round(realCoord / FACTOR);
    }

    public static void push() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(FACTOR, FACTOR, 1.0F);
    }

    public static void pop() {
        GlStateManager.popMatrix();
    }
}
