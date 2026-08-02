package thaumcraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.Config;

/**
 * Draws Thaumcraft's screens smaller than Minecraft would, by the factor set
 * in the mod's config ({@code gui_scale}).
 *
 * <p><b>Owner's decision.</b> Not upstream behaviour and not a defect being
 * fixed — the panels were judged too large at the game's own GUI scale
 * (2026-07-30), and on 2026-08-02 the owner chose a config knob over a
 * hard-wired constant: default 1.0 (off), settable down to 0.5. Recorded here
 * so it is never "corrected" back.</p>
 *
 * <h3>How it works, and why it is done this way</h3>
 *
 * <p>The obvious approach — wrap the drawing in a scale and then convert every
 * mouse coordinate back by hand — needs the conversion applied at every entry
 * point, including inside subclasses that do their own hit-testing. Miss one
 * and the clicks land somewhere other than the picture.</p>
 *
 * <p>So the screen is enlarged instead of the drawing being shrunk. A screen
 * that reports itself {@code 1 / factor} times larger and is then drawn at
 * {@code factor} lands on exactly the same pixels — but every coordinate
 * Minecraft computes for it, mouse included, is already in the enlarged space,
 * because {@code GuiScreen.handleMouseInput} derives the pointer from
 * {@code this.width} and {@code this.height}. Nothing downstream has to know
 * about the scale.</p>
 *
 * <p>The one exception is {@code drawScreen}, whose mouse position comes from
 * {@code EntityRenderer} against the real resolution rather than from the
 * screen — that pair has to be divided across. {@link GuiContainerScaled} does
 * it in one place.</p>
 *
 * <h3>History: the hard-wired 0.9 and what was ruled out</h3>
 *
 * <p>A fixed {@code 0.9F} shipped in 1.1.39.1 and the owner reported the
 * Thaumic Tinkerer screens stopped behaving properly; the cause was never
 * reproduced statically. Checked and ruled out, so it is not re-checked from
 * scratch: every container screen calls {@code super.initGui()} before laying
 * itself out; none calls {@code initGui()} again by hand, so the enlargement
 * cannot compound; {@code GuiScreen.handleMouseInput} derives clicks from
 * {@code width}/{@code height} and {@code GuiContainer} does not override it,
 * so clicks and layout share one space; and the rounding of the two paths
 * agrees. The config default of 1.0 keeps everyone on the identity transform
 * until they opt in; if a reduced scale misbehaves, what is needed is the
 * concrete symptom — which screen, and clicks-off-target versus
 * picture-misplaced.</p>
 */
@SideOnly(Side.CLIENT)
public final class GuiScale {

    private GuiScale() {
    }

    /** The factor screens are drawn at. From config; {@code Config} clamps it to 0.5..1.0. */
    public static float factor() {
        return Config.guiScale;
    }

    /** The logical size a screen must claim so that drawing it at {@link #factor()} fills the real one. */
    public static int enlarge(int realSize) {
        return MathHelper.ceil(realSize / factor());
    }

    /**
     * A coordinate handed in against the real resolution, in the enlarged space
     * the screen thinks in.
     */
    public static int toGuiSpace(int realCoord) {
        return Math.round(realCoord / factor());
    }

    public static void push() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(factor(), factor(), 1.0F);
    }

    public static void pop() {
        GlStateManager.popMatrix();
    }
}
