package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.ContainerAnimationTablet;
import thaumcraft.common.tiles.tinkerer.TileAnimationTablet;

/**
 * Screen for the Tool Dynamism Tablet — mirrors Thaumic Tinkerer's
 * {@code GuiAnimationTablet} (pixlepix/nekosune, originally Vazkii): a strike /
 * use radio pair flanking the tool slot and a redstone-control toggle beneath
 * it, at the original's coordinates.
 */
@SideOnly(Side.CLIENT)
public class GuiAnimationTablet extends GuiContainer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/gui/animation_tablet.png");

    private final TileAnimationTablet tablet;
    private int left;
    private int top;

    public GuiAnimationTablet(InventoryPlayer playerInv, TileAnimationTablet tablet) {
        super(new ContainerAnimationTablet(playerInv, tablet));
        this.tablet = tablet;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.left = (this.width - this.xSize) / 2;
        this.top = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        // Original layout: radios at x+52 / x+111, y+15; redstone toggle centred at y+60.
        this.buttonList.add(new ToggleButton(ContainerAnimationTablet.BUTTON_STRIKE,
                this.left + 52, this.top + 15));
        this.buttonList.add(new ToggleButton(ContainerAnimationTablet.BUTTON_USE,
                this.left + 111, this.top + 15));
        this.buttonList.add(new ToggleButton(ContainerAnimationTablet.BUTTON_REDSTONE,
                this.left + this.xSize / 2 - 7, this.top + 60));
        refreshButtons();
    }

    private void refreshButtons() {
        for (GuiButton button : this.buttonList) {
            if (!(button instanceof ToggleButton)) {
                continue;
            }
            ToggleButton toggle = (ToggleButton) button;
            switch (toggle.id) {
                case ContainerAnimationTablet.BUTTON_STRIKE:
                    toggle.on = tablet.isStrikeMode();
                    break;
                case ContainerAnimationTablet.BUTTON_USE:
                    toggle.on = !tablet.isStrikeMode();
                    break;
                case ContainerAnimationTablet.BUTTON_REDSTONE:
                    toggle.on = tablet.isRedstoneMode();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
        // Optimistic local update; the tile is authoritative and re-syncs.
        if (button.id == ContainerAnimationTablet.BUTTON_REDSTONE) {
            tablet.toggleRedstoneMode();
        } else {
            tablet.setStrikeMode(button.id == ContainerAnimationTablet.BUTTON_STRIKE);
        }
        refreshButtons();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);

        String strike = I18n.format("tc.tablet.mode.strike.short");
        String use = I18n.format("tc.tablet.mode.use.short");
        String redstone = I18n.format("tc.tablet.redstone.label");
        this.fontRenderer.drawString(strike,
                this.left + 48 - this.fontRenderer.getStringWidth(strike), this.top + 18, 0x999999);
        this.fontRenderer.drawString(use, this.left + 128, this.top + 18, 0x999999);
        this.fontRenderer.drawString(redstone,
                this.left + this.xSize / 2 - this.fontRenderer.getStringWidth(redstone) / 2,
                this.top + 50, 0x999999);
    }

    /** Small on/off box; the original used its own sprite sheet for these. */
    private static class ToggleButton extends GuiButton {

        boolean on;

        ToggleButton(int id, int x, int y) {
            super(id, x, y, 14, 14, "");
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            this.hovered = mouseX >= this.x && mouseY >= this.y
                    && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int border = this.hovered ? 0xFFFFFFAA : 0xFF6A6A6A;
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, border);
            drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFF2B2B2B);
            if (this.on) {
                drawRect(this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, 0xFF9C7BD4);
            }
        }
    }
}
