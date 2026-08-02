package thaumcraft.client.gui.tinkerer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiEnchanter;
import thaumcraft.common.lib.tinkerer.EnchantmentCosts;

/**
 * One offered enchantment in the Osmotic Enchanter's grid — the port of
 * Thaumic Tinkerer's {@code GuiButtonEnchantment} (Vazkii).
 *
 * <p>Upstream draws the enchantment's own 16×16 icon, blended, and puts the
 * name and the per-aspect <em>base</em> cost in the tooltip — the hand-tuned
 * level-1 numbers, coloured with each aspect's chat colour, not the multiplied
 * price a run will actually charge. An unassigned or disabled button draws
 * nothing.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiButtonEnchantment extends GuiButton {

    public Enchantment enchant;
    protected final GuiEnchanter parent;

    public GuiButtonEnchantment(GuiEnchanter parent, int id, int x, int y) {
        super(id, x, y, 16, 16, "");
        this.parent = parent;
    }

    protected boolean dontRender() {
        return this.enchant == null || !this.enabled || !EnchantmentCosts.isSupported(this.enchant);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (dontRender()) {
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(iconTexture());
        GlStateManager.enableBlend();
        drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, 16, 16, 16, 16);
        GlStateManager.disableBlend();

        if (mouseX >= this.x && mouseX < this.x + 16 && mouseY >= this.y && mouseY < this.y + 16) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(TextFormatting.AQUA + I18n.translateToLocal(this.enchant.getName()));
            AspectList base = EnchantmentCosts.baseCostFor(this.enchant);
            if (base != null) {
                for (Aspect aspect : base.getAspectsSorted()) {
                    tooltip.add(" §" + aspect.getChatcolor() + aspect.getName()
                            + TextFormatting.RESET + " x " + base.getAmount(aspect)
                            + " " + I18n.translateToLocal("ttmisc.baseCost"));
                }
            }
            if (this instanceof GuiButtonFramedEnchantment && !this.parent.getEnchanter().isWorking()) {
                tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + " "
                        + I18n.translateToLocal("ttmisc.clickToRemove"));
            }
            this.parent.tooltip = tooltip;
        }
    }

    /** The 16×16 sheets carried over from the original's assets, one per enchantment. */
    private ResourceLocation iconTexture() {
        String path = this.enchant.getRegistryName() == null
                ? "unknown" : this.enchant.getRegistryName().getPath();
        return new ResourceLocation("thaumcraft", "textures/enchants/" + path + ".png");
    }
}
