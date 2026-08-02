package thaumcraft.client.gui.tinkerer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.gui.GuiEnchanter;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;

/**
 * The Osmotic Enchanter's start button — the port of Thaumic Tinkerer's
 * {@code GuiButtonEnchant} (Vazkii).
 *
 * <p>Upstream: a 15×15 sprite from the enchanter's own atlas, at (176, 24)
 * while idle and (176, 39) while a run is in progress. The button stays
 * enabled during the run — the sprite change <em>is</em> the busy state, and
 * a click while working is refused server-side rather than swallowed here.
 * Disabled (an empty queue) draws nothing at all.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiButtonEnchant extends GuiButton {

    private static final ResourceLocation GUI =
            new ResourceLocation("thaumcraft", "textures/gui/enchanter.png");

    private final GuiEnchanter parent;
    private final TileEnchanter enchanter;

    public GuiButtonEnchant(GuiEnchanter parent, TileEnchanter enchanter, int id, int x, int y) {
        super(id, x, y, 15, 15, "");
        this.parent = parent;
        this.enchanter = enchanter;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.enabled) {
            return;
        }

        final int u = 176;
        final int v = this.enchanter.isWorking() ? 39 : 24;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(this.x, this.y, u, v, 15, 15);

        if (mouseX >= this.x && mouseX < this.x + 15 && mouseY >= this.y && mouseY < this.y + 15
                && !this.enchanter.isWorking()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("ttmisc.startEnchant"));
            this.parent.tooltip = tooltip;
        }
    }
}
