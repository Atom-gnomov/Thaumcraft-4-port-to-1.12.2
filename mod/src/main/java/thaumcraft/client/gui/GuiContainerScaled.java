package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Every Thaumcraft container screen draws through this, which is the whole of
 * the ten-percent reduction the owner asked for — see {@link GuiScale}.
 *
 * <p>Subclasses need to know nothing about it. They lay themselves out against
 * {@code width} and {@code height} exactly as before; those numbers are simply
 * a tenth larger than the real screen by the time {@code initGui} hands back,
 * and the drawing is scaled to match.</p>
 */
@SideOnly(Side.CLIENT)
public abstract class GuiContainerScaled extends GuiContainer {

    protected GuiContainerScaled(Container container) {
        super(container);
    }

    /**
     * Claim the enlarged screen and re-centre against it. This runs before the
     * subclass's own {@code initGui} body, so anything it positions off
     * {@code width} or {@code guiLeft} is already working in the right space.
     */
    @Override
    public void initGui() {
        super.initGui();
        this.width = GuiScale.enlarge(this.width);
        this.height = GuiScale.enlarge(this.height);
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    /**
     * The only place the pointer has to be converted. Clicks arrive already in
     * the enlarged space because {@code handleMouseInput} derives them from
     * {@code width}/{@code height}, but this pair comes from
     * {@code EntityRenderer} against the real resolution.
     *
     * <p><b>Final on purpose.</b> Most of these screens have their own
     * {@code drawScreen} — tooltips, gauges, hover text drawn after the panel.
     * If a subclass could override this one, its body would run outside the
     * scale and against unconverted coordinates: the panel would shrink and its
     * tooltips would not, and they would sit in the wrong place. Subclasses
     * override {@link #drawScaledScreen} instead and are inside the transform
     * for their whole body.</p>
     */
    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GuiScale.push();
        this.drawScaledScreen(GuiScale.toGuiSpace(mouseX), GuiScale.toGuiSpace(mouseY), partialTicks);
        GuiScale.pop();
    }

    /**
     * What {@code drawScreen} would have been. The coordinates are already in
     * the screen's own enlarged space and the scale is already applied, so an
     * implementation looks exactly like an ordinary {@code drawScreen} and needs
     * no awareness of either.
     */
    protected void drawScaledScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
