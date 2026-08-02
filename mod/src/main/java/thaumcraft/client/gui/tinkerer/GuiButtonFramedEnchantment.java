package thaumcraft.client.gui.tinkerer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.gui.GuiEnchanter;

/**
 * A queued enchantment off the screen's right edge — the port of Thaumic
 * Tinkerer's {@code GuiButtonFramedEnchantment} (Vazkii).
 *
 * <p>Upstream draws the 24×24 frame from the atlas at (176, 0), four pixels
 * out from the icon on every side, writes the level
 * ({@code enchantment.level.N}) at (+26, +8), and then lets the plain
 * enchantment button draw the icon and the tooltip on top. Clicking it removes
 * the enchantment from the queue, which is what the tooltip's last line
 * says.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiButtonFramedEnchantment extends GuiButtonEnchantment {

    private static final ResourceLocation GUI =
            new ResourceLocation("thaumcraft", "textures/gui/enchanter.png");

    public GuiButtonFramedEnchantment(GuiEnchanter parent, int id, int x, int y) {
        super(parent, id, x, y);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (dontRender() || this.parent.getEnchanter().getQueuedEnchantments().isEmpty()) {
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(this.x - 4, this.y - 4, 176, 0, 24, 24);

        int level = this.parent.getEnchanter().getQueuedLevel(this.enchant);
        if (level > 0) {
            mc.fontRenderer.drawStringWithShadow(
                    I18n.translateToLocal("enchantment.level." + level),
                    this.x + 26, this.y + 8, 0xFFFFFF);
        }

        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }
}
