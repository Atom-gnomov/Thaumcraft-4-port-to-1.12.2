package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.ContainerMobMagnet;
import thaumcraft.common.tiles.tinkerer.TileMobMagnet;

/**
 * Screen for the Mob Magnet — mirrors Thaumic Tinkerer's {@code GuiMobMagnet}
 * (pixlepix/nekosune, originally Vazkii): the Soul Mould slot with an
 * adult/baby radio pair above it.
 */
@SideOnly(Side.CLIENT)
public class GuiMobMagnet extends GuiContainerScaled {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/gui/mob_magnet.png");

    private final TileMobMagnet magnet;
    private int left;
    private int top;

    public GuiMobMagnet(InventoryPlayer playerInv, TileMobMagnet magnet) {
        super(new ContainerMobMagnet(playerInv, magnet));
        this.magnet = magnet;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.left = (this.width - this.xSize) / 2;
        this.top = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        // Original layout: radios stacked at (100, 28) and (100, 48).
        this.buttonList.add(new RadioButton(ContainerMobMagnet.BUTTON_ADULT, this.left + 100, this.top + 28));
        this.buttonList.add(new RadioButton(ContainerMobMagnet.BUTTON_BABY, this.left + 100, this.top + 48));
        refresh();
    }

    private void refresh() {
        for (GuiButton button : this.buttonList) {
            if (button instanceof RadioButton) {
                ((RadioButton) button).on =
                        (button.id == ContainerMobMagnet.BUTTON_ADULT) == magnet.isAdult();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
        magnet.setAdult(button.id == ContainerMobMagnet.BUTTON_ADULT);
        refresh();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);

        String filter = I18n.format("tc.mobmagnet.filter");
        String adult = I18n.format("tc.mobmagnet.adult");
        String baby = I18n.format("tc.mobmagnet.baby");
        this.fontRenderer.drawString(filter,
                this.left + this.xSize / 2 - this.fontRenderer.getStringWidth(filter) / 2 - 26,
                this.top + 16, 0x999999);
        this.fontRenderer.drawString(adult, this.left + 120, this.top + 30, 0x999999);
        this.fontRenderer.drawString(baby, this.left + 120, this.top + 50, 0x999999);
    }

    private static class RadioButton extends GuiButton {

        boolean on;

        RadioButton(int id, int x, int y) {
            super(id, x, y, 14, 14, "");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            this.hovered = mouseX >= this.x && mouseY >= this.y
                    && mouseX < this.x + this.width && mouseY < this.y + this.height;
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height,
                    this.hovered ? 0xFFFFFFAA : 0xFF6A6A6A);
            drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFF2B2B2B);
            if (this.on) {
                drawRect(this.x + 3, this.y + 3, this.x + this.width - 3, this.y + this.height - 3, 0xFF9C7BD4);
            }
        }
    }

    @Override
    protected void drawScaledScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScaledScreen(mouseX, mouseY, partialTicks);
        // FOREVA fix: render the hovered item tooltip so slots show tooltips in this container GUI.
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
