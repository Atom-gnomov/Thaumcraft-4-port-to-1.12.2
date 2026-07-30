package thaumcraft.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.ContainerEnchanter;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;

/**
 * Screen for the Osmotic Enchanter — mirrors Thaumic Tinkerer's
 * {@code GuiEnchanting} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Layout follows the original: the start button at (151, 33), a grid of up
 * to sixteen offered enchantments in two rows of eight starting at (34, 54),
 * each queued enchantment listed off the right edge with level down/up buttons,
 * and a bar per primal aspect showing paid-against-total with a tooltip.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiEnchanter extends GuiContainerScaled {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/gui/enchanter.png");

    private final TileEnchanter enchanter;
    private final ContainerEnchanter container;
    private List<String> hoverText = new ArrayList<>();
    private int left;
    private int top;

    public GuiEnchanter(InventoryPlayer playerInv, TileEnchanter enchanter) {
        super(new ContainerEnchanter(playerInv, enchanter));
        this.container = (ContainerEnchanter) this.inventorySlots;
        this.enchanter = enchanter;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.left = (this.width - this.xSize) / 2;
        this.top = (this.height - this.ySize) / 2;
        rebuildButtons();
    }

    private void rebuildButtons() {
        this.buttonList.clear();

        GuiButton start = new GuiButton(ContainerEnchanter.BUTTON_START,
                this.left + 151, this.top + 33, 18, 18, ">");
        start.enabled = !enchanter.getQueuedEnchantments().isEmpty() && !enchanter.isWorking();
        this.buttonList.add(start);

        // Offers: two rows of eight, second row only appears once the first fills.
        List<Enchantment> offers = container.getOffers();
        for (int i = 0; i < ContainerEnchanter.OFFER_BUTTONS && i < offers.size(); i++) {
            int row = i / 8;
            int col = i % 8;
            int y = this.top + 54 + (offers.size() > 8 ? (row == 0 ? -24 : 0) : 0);
            IconButton button = new IconButton(ContainerEnchanter.FIRST_OFFER_BUTTON + i,
                    this.left + 34 + col * 16, y, offers.get(i));
            button.enabled = !enchanter.isWorking();
            this.buttonList.add(button);
        }

        // Queue: one row per enchantment off the right edge, with level controls.
        List<Enchantment> queued = enchanter.getQueuedEnchantments();
        for (int i = 0; i < queued.size(); i++) {
            int id = ContainerEnchanter.FIRST_ROW_BUTTON + i * ContainerEnchanter.ROW_STRIDE;
            int y = this.top + i * 26;
            IconButton row = new IconButton(id, this.left + this.xSize + 4, y, queued.get(i));
            row.framed = true;
            row.enabled = !enchanter.isWorking();
            this.buttonList.add(row);

            GuiButton down = new GuiButton(id + 1, this.left + this.xSize + 24, y - 4, 8, 12, "-");
            GuiButton up = new GuiButton(id + 2, this.left + this.xSize + 33, y - 4, 8, 12, "+");
            down.enabled = up.enabled = !enchanter.isWorking();
            this.buttonList.add(down);
            this.buttonList.add(up);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        // The tile is authoritative; rebuild so the grid tracks the tool and queue.
        rebuildButtons();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);

        boolean twoRows = container.getOffers().size() > 8;
        if (!enchanter.getInventory().getStackInSlot(TileEnchanter.SLOT_TOOL).isEmpty()) {
            if (twoRows) {
                this.drawTexturedModalRect(this.left + 30, this.top + 26, 0, this.ySize, 147, 24);
            }
            this.drawTexturedModalRect(this.left + 30, this.top + 50, 0, this.ySize, 147, 24);
        }

        if (!enchanter.getQueuedEnchantments().isEmpty()) {
            GlStateManager.enableBlend();
            int barX = this.left + 40 + 15;
            int barY = this.top + (twoRows ? 26 : 50);
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                drawAspectBar(aspect, barX, barY, mouseX, mouseY);
                barX += 15;
            }
            GlStateManager.disableBlend();
        }
    }

    /** Vertical bar per primal: filled by paid/total, with a tooltip. */
    private void drawAspectBar(Aspect aspect, int x, int y, int mouseX, int mouseY) {
        int total = enchanter.getTotalCost().getAmount(aspect);
        int paid = enchanter.getPaid().getAmount(aspect);
        int size = total == 0 ? 11 : 59;

        this.mc.getTextureManager().bindTexture(TEXTURE);
        if (total == 0) {
            this.drawTexturedModalRect(x, y - size, 200, 0, 10, 4);
            this.drawTexturedModalRect(x, y - size + 4, 200, 52, 10, 10);
        } else {
            int pixels = (int) (48.0D * ((double) paid / (double) total));
            Color colour = new Color(aspect.getColor());
            GlStateManager.color(colour.getRed() / 255.0F, colour.getGreen() / 255.0F, colour.getBlue() / 255.0F);
            this.drawTexturedModalRect(x + 1, y - size + 4 + 48 - pixels, 210, 48 - pixels, 8, pixels);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(x, y - size, 200, 0, 10, size);
        }

        if (mouseX > x && mouseX <= x + 10 && mouseY > y - size && mouseY <= y) {
            List<String> text = new ArrayList<>();
            text.add(TextFormatting.RESET + aspect.getName());
            text.add(paid + "/" + total);
            this.hoverText = text;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (GuiButton button : this.buttonList) {
            if (button instanceof IconButton && button.isMouseOver()) {
                IconButton icon = (IconButton) button;
                List<String> text = new ArrayList<>();
                text.add(TextFormatting.AQUA + I18n.format(icon.enchantment.getName()));
                int level = enchanter.getQueuedLevel(icon.enchantment);
                if (level > 0) {
                    text.add(TextFormatting.GRAY + I18n.format("enchantment.level." + level));
                }
                this.hoverText = text;
            }
        }
        if (!this.hoverText.isEmpty()) {
            this.drawHoveringText(this.hoverText, mouseX - this.left, mouseY - this.top);
            this.hoverText.clear();
        }
    }

    /** Enchantment button drawing the icon sheet the original shipped. */
    private static class IconButton extends GuiButton {

        final Enchantment enchantment;
        boolean framed;

        IconButton(int id, int x, int y, Enchantment enchantment) {
            super(id, x, y, 16, 16, "");
            this.enchantment = enchantment;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            this.hovered = mouseX >= this.x && mouseY >= this.y
                    && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (this.framed) {
                drawRect(this.x - 1, this.y - 1, this.x + 17, this.y + 17,
                        this.hovered ? 0xFFFFFFAA : 0xFF6A6A6A);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, this.enabled ? 1.0F : 0.4F);
            String path = this.enchantment.getRegistryName() == null
                    ? "unknown" : this.enchantment.getRegistryName().getPath();
            mc.getTextureManager().bindTexture(
                    new ResourceLocation("thaumcraft", "textures/enchants/" + path + ".png"));
            // Icons are 32x32; draw them into a 16x16 slot.
            drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, 16, 16, 16, 16);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
