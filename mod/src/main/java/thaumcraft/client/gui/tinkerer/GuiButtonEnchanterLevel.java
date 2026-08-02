package thaumcraft.client.gui.tinkerer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The level up/down arrows beside a queued enchantment — the port of Thaumic
 * Tinkerer's {@code GuiButtonEnchanterLevel} (Vazkii): a 7×7 sprite from the
 * enchanter's atlas, minus at (218, 0) and plus one tile over at (225, 0).
 */
@SideOnly(Side.CLIENT)
public class GuiButtonEnchanterLevel extends GuiButton {

    private static final ResourceLocation GUI =
            new ResourceLocation("thaumcraft", "textures/gui/enchanter.png");

    private final boolean plus;

    public GuiButtonEnchanterLevel(int id, int x, int y, boolean plus) {
        super(id, x, y, 7, 7, "");
        this.plus = plus;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.enabled) {
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(this.x, this.y, 218 + (this.plus ? 7 : 0), 0, 7, 7);
    }
}
