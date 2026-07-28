package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.common.container.ContainerIchorPouch;

/**
 * Bottomless Pouch screen — ported from Thaumic Tinkerer's
 * {@code GuiIchorPouch} (pixlepix / nekosune, originally Vazkii), on the
 * original's own 256×256 texture.
 */
public class GuiIchorPouch extends GuiContainer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/gui/ichorpouch.png");

    public GuiIchorPouch(InventoryPlayer playerInv, World world) {
        super(new ContainerIchorPouch(playerInv, world));
        this.xSize = 256;
        this.ySize = 256;
    }

    /**
     * The original blocks the hotbar key for the slot the pouch itself is in —
     * without it the pouch can be swapped out from under its own screen and
     * duplicated (upstream issue 367).
     */
    @Override
    protected boolean checkHotbarKeys(int keyCode) {
        if (this.mc.gameSettings.keyBindsHotbar[this.mc.player.inventory.currentItem]
                .getKeyCode() != keyCode) {
            super.checkHotbarKeys(keyCode);
        }
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.disableBlend();
    }

    /** The original draws no labels over this one. */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }
}
