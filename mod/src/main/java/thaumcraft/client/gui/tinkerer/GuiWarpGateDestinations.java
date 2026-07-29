package thaumcraft.client.gui.tinkerer;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tinkerer.kami.ItemSkyPearl;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.tinkerer.PacketWarpGateTeleport;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * The warp gate's destination map — the port of Thaumic Tinkerer's
 * {@code GuiWarpGateDestinations}.
 *
 * <p>Not a list: destinations sit at their real world coordinates relative to
 * the gate, on a scrolling end-portal field. Drag with the left button to pan,
 * space recentres on the gate, number keys 1-0 jump the view to that slot, and
 * shift-clicking a pearl teleports.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiWarpGateDestinations extends GuiScreen {

    private static final ResourceLocation ENDER_FIELD =
            new ResourceLocation("textures/entity/end_portal.png");

    private final TileWarpGate warpGate;
    private final List<String> tooltip = new ArrayList<>();

    private int lastMouseX;
    private int lastMouseY;
    private int x;
    private int y;
    private int ticks;

    public GuiWarpGateDestinations(TileWarpGate warpGate) {
        this.warpGate = warpGate;
    }

    @Override
    public void initGui() {
        super.initGui();
        recentre();
    }

    private void recentre() {
        this.x = this.warpGate.getPos().getX() - this.width / 2;
        this.y = this.warpGate.getPos().getZ() - this.height / 2;
    }

    @Override
    public void updateScreen() {
        ++this.ticks;
        ScaledResolution res = new ScaledResolution(this.mc);
        int mx = Mouse.getX() * res.getScaledWidth() / this.mc.displayWidth;
        int my = res.getScaledHeight()
                - Mouse.getY() * res.getScaledHeight() / this.mc.displayHeight - 1;
        if (Mouse.isButtonDown(0)) {
            this.x -= mx - this.lastMouseX;
            this.y -= my - this.lastMouseY;
        }
        this.lastMouseX = mx;
        this.lastMouseY = my;
    }

    @Override
    protected void keyTyped(char typed, int code) throws IOException {
        super.keyTyped(typed, code);
        if (code == 57) { // space
            recentre();
            return;
        }
        if (code >= 2 && code < 12) {
            int slot = code - 2;
            ItemStack stack = this.warpGate.getStackInSlot(slot);
            if (!stack.isEmpty() && ItemSkyPearl.isAttuned(stack)
                    && ItemSkyPearl.getDim(stack) == this.warpGate.getWorld().provider.getDimension()) {
                this.x = ItemSkyPearl.getX(stack) - this.width / 2;
                this.y = ItemSkyPearl.getZ(stack) - this.height / 2;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltip.clear();

        int gateX = this.warpGate.getPos().getX() - this.x;
        int gateY = this.warpGate.getPos().getZ() - this.y;

        List<Object[]> coords = new ArrayList<>();
        for (int i = 0; i < this.warpGate.getSizeInventory(); i++) {
            ItemStack stack = this.warpGate.getStackInSlot(i);
            if (stack.isEmpty() || !ItemSkyPearl.isAttuned(stack)) {
                continue;
            }
            if (this.warpGate.getWorld().provider.getDimension() != ItemSkyPearl.getDim(stack)) {
                continue;
            }
            if (ItemSkyPearl.getY(stack) != -1) {
                coords.add(new Object[]{
                        ItemSkyPearl.getX(stack) - this.x, ItemSkyPearl.getZ(stack) - this.y, stack, i});
            }
        }

        // Threads from the gate to each destination, pulsing with the tick count.
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(2.0F);
        float alpha = (float) ((Math.sin(this.ticks / 10.0D) + 1.0F) / 4.0F + 0.25F);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        for (Object[] dest : coords) {
            buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(gateX, gateY, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            buf.pos((Integer) dest[0], (Integer) dest[1], 0.0D)
                    .color(1.0F, 1.0F, 1.0F, alpha).endVertex();
            tess.draw();
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        this.fontRenderer.drawStringWithShadow(
                TextFormatting.UNDERLINE + I18n.translateToLocal("ttmisc.destinations"),
                3, 40, 0xFFFFFF);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawPearlAt(0, ItemStack.EMPTY, gateX, gateY, mouseX, mouseY);
        for (Object[] dest : coords) {
            drawPearlAt((Integer) dest[3], (ItemStack) dest[2],
                    (Integer) dest[0], (Integer) dest[1], mouseX, mouseY);
        }

        if (!this.tooltip.isEmpty()) {
            drawHoveringText(this.tooltip, mouseX, mouseY);
        }
        drawCenteredString(this.fontRenderer, I18n.translateToLocal("ttmisc.numberKeys"),
                this.width / 2, 5, 0xFFFFFF);
        drawCenteredString(this.fontRenderer, I18n.translateToLocal("ttmisc.spaceToReset"),
                this.width / 2, 16, 0xFFFFFF);
    }

    /** The gate itself is drawn with an empty stack; every other point is a pearl. */
    private void drawPearlAt(int index, ItemStack stack, int px, int py, int mx, int my) {
        int worldX = px + this.x;
        int worldZ = py + this.y;

        GlStateManager.pushMatrix();
        GlStateManager.translate(px, py, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        this.mc.getRenderItem().renderItemIntoGUI(new ItemStack(ConfigItems.itemSkyPearl), -8, -8);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        String destName;
        if (!stack.isEmpty() && stack.hasDisplayName()) {
            destName = stack.getDisplayName();
        } else {
            destName = I18n.translateToLocal(
                    stack.isEmpty() ? "ttmisc.entrancePoint" : "ttmisc.destination");
        }
        if (!stack.isEmpty()) {
            this.fontRenderer.drawString((index + 1) + ": " + destName, 5, 54 + index * 11, 0xFFFFFF);
        }

        if (mx >= px - 4 && mx <= px + 4 && my >= py - 4 && my < py + 4) {
            String destNum = " " + TextFormatting.ITALIC + String.format(
                    I18n.translateToLocal("ttmisc.destinationInd"), index + 1);
            this.tooltip.add(TextFormatting.AQUA + destName + destNum);
            if (!stack.isEmpty()) {
                ItemSkyPearl.addInfo(stack, this.warpGate.getWorld().provider.getDimension(),
                        this.warpGate.getPos().getX() + 0.5D,
                        this.warpGate.getPos().getY() + 0.5D,
                        this.warpGate.getPos().getZ() + 0.5D,
                        this.tooltip, true);
                this.tooltip.add(I18n.translateToLocal("ttmisc.clickToTeleport"));
            } else {
                this.tooltip.add("X: " + worldX);
                this.tooltip.add("Z: " + worldZ);
            }
            if (Mouse.isButtonDown(0) && isShiftKeyDown() && !stack.isEmpty()) {
                PacketHandler.INSTANCE.sendToServer(
                        new PacketWarpGateTeleport(this.warpGate.getPos(), index));
                this.mc.displayGuiScreen(null);
            }
        }
    }

    /** A slowly hue-cycling end-portal field, drawn behind everything. */
    @Override
    public void drawDefaultBackground() {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        this.mc.getTextureManager().bindTexture(ENDER_FIELD);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 256.0F;
        float hue = (float) (Math.sin(this.ticks / 150.0D) + 1.0F / 2.0F);
        int rgb = Color.HSBtoRGB(hue, 0.5F, 0.4F);
        float r = (rgb >> 16 & 255) / 255.0F;
        float g = (rgb >> 8 & 255) / 255.0F;
        float b = (rgb & 255) / 255.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(0.0D, this.height, 0.0D).tex(0.0D, this.height / f).color(r, g, b, 1.0F).endVertex();
        buf.pos(this.width, this.height, 0.0D).tex(this.width / f, this.height / f)
                .color(r, g, b, 1.0F).endVertex();
        buf.pos(this.width, 0.0D, 0.0D).tex(this.width / f, 0.0D).color(r, g, b, 1.0F).endVertex();
        buf.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(r, g, b, 1.0F).endVertex();
        tess.draw();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
