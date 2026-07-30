package thaumcraft.client.gui.tinkerer;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.tinkerer.ContainerWarpGate;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.tinkerer.PacketWarpGateLock;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;
import thaumcraft.client.gui.GuiContainerScaled;

/**
 * The warp gate's inventory screen — the port of Thaumic Tinkerer's
 * {@code GuiWarpGate}. Ten pearl slots and one toggle: a locked gate refuses
 * arrivals.
 */
@SideOnly(Side.CLIENT)
public class GuiWarpGate extends GuiContainerScaled {

    private static final ResourceLocation GUI =
            new ResourceLocation("thaumcraft", "textures/gui/gui_warp_gate.png");

    private final TileWarpGate warpGate;
    private int left;
    private int top;

    public GuiWarpGate(TileWarpGate warpGate, InventoryPlayer inv) {
        super(new ContainerWarpGate(warpGate, inv));
        this.warpGate = warpGate;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!(button instanceof GuiButtonWarpGateLock)) {
            return;
        }
        GuiButtonWarpGateLock lock = (GuiButtonWarpGateLock) button;
        lock.locked = !lock.locked;
        this.warpGate.locked = lock.locked;
        PacketHandler.INSTANCE.sendToServer(
                new PacketWarpGateLock(this.warpGate.getPos(), this.warpGate.locked));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.left = (this.width - this.xSize) / 2;
        this.top = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonWarpGateLock(
                0, this.left + 5, this.top + 5, this.warpGate.locked));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);
        this.fontRenderer.drawStringWithShadow(
                I18n.translateToLocal("ttmisc.lockedGate"), this.left + 20, this.top + 7, 0x999999);
    }
}
