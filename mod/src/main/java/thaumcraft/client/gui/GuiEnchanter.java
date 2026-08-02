package thaumcraft.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.gui.tinkerer.GuiButtonEnchant;
import thaumcraft.client.gui.tinkerer.GuiButtonEnchanterLevel;
import thaumcraft.client.gui.tinkerer.GuiButtonEnchantment;
import thaumcraft.client.gui.tinkerer.GuiButtonFramedEnchantment;
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
 *
 * <p>The buttons are the original's own classes, ported one to one —
 * {@code GuiButtonEnchant}, {@code GuiButtonEnchantment},
 * {@code GuiButtonFramedEnchantment}, {@code GuiButtonEnchanterLevel} — drawing
 * their sprites from the enchanter's atlas. Nothing is dimmed while a run is
 * working: upstream keeps every button live and lets the server refuse, and
 * the start button's sprite switching to the busy variant is the only visible
 * change.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiEnchanter extends GuiContainerScaled {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/gui/enchanter.png");

    private final TileEnchanter enchanter;
    private final ContainerEnchanter container;
    /** Filled by whichever button is hovered this frame; drawn and cleared in the foreground pass. */
    public List<String> tooltip = new ArrayList<>();
    private int left;
    private int top;

    public GuiEnchanter(InventoryPlayer playerInv, TileEnchanter enchanter) {
        super(new ContainerEnchanter(playerInv, enchanter));
        this.container = (ContainerEnchanter) this.inventorySlots;
        this.enchanter = enchanter;
    }

    public TileEnchanter getEnchanter() {
        return enchanter;
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

        GuiButtonEnchant start = new GuiButtonEnchant(this, enchanter,
                ContainerEnchanter.BUTTON_START, this.left + 151, this.top + 33);
        start.enabled = !enchanter.getQueuedEnchantments().isEmpty();
        this.buttonList.add(start);

        // Offers: two rows of eight, second row only appears once the first fills.
        List<Enchantment> offers = container.getOffers();
        for (int i = 0; i < ContainerEnchanter.OFFER_BUTTONS && i < offers.size(); i++) {
            int row = i / 8;
            int col = i % 8;
            int y = this.top + 54 + (offers.size() > 8 ? (row == 0 ? -24 : 0) : 0);
            GuiButtonEnchantment button = new GuiButtonEnchantment(this,
                    ContainerEnchanter.FIRST_OFFER_BUTTON + i, this.left + 34 + col * 16, y);
            button.enchant = offers.get(i);
            this.buttonList.add(button);
        }

        // Queue: one row per enchantment off the right edge, with level controls.
        List<Enchantment> queued = enchanter.getQueuedEnchantments();
        for (int i = 0; i < queued.size(); i++) {
            int id = ContainerEnchanter.FIRST_ROW_BUTTON + i * ContainerEnchanter.ROW_STRIDE;
            int y = this.top + i * 26;
            GuiButtonFramedEnchantment row = new GuiButtonFramedEnchantment(
                    this, id, this.left + this.xSize + 4, y);
            row.enchant = queued.get(i);
            this.buttonList.add(row);

            this.buttonList.add(new GuiButtonEnchanterLevel(
                    id + 1, this.left + this.xSize + 24, y - 4, false));
            this.buttonList.add(new GuiButtonEnchanterLevel(
                    id + 2, this.left + this.xSize + 31, y - 4, true));
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
            this.tooltip = text;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (!this.tooltip.isEmpty()) {
            this.drawHoveringText(this.tooltip, mouseX - this.left, mouseY - this.top);
        }
        this.tooltip.clear();
    }

    @Override
    protected void drawScaledScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScaledScreen(mouseX, mouseY, partialTicks);
        // FOREVA fix: render the hovered item tooltip so slots show tooltips in this container GUI.
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
