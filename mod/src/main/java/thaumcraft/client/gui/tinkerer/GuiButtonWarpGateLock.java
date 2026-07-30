package thaumcraft.client.gui.tinkerer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The warp gate's lock toggle — the port of Thaumic Tinkerer's
 * {@code GuiButtonWG}.
 *
 * <p>Upstream declares its own {@code public boolean enabled}, which
 * <em>shadows</em> {@link GuiButton#enabled} rather than reusing it. That reads
 * like an accident and is load-bearing: vanilla's {@code enabled} decides
 * whether a button can be clicked at all, so a lock that stored its state there
 * would refuse to be clicked in exactly the state you need to click it from —
 * an unlocked gate could never be locked. The port did use vanilla's field, and
 * that is what it did.</p>
 *
 * <p>Here the state is a separate {@code locked} flag, so the button is always
 * clickable and the flag only decides what is drawn: the marker appears when
 * locked and nothing is drawn when not, as upstream draws it.</p>
 */
@SideOnly(Side.CLIENT)
public class GuiButtonWarpGateLock extends GuiButton {

    private static final ResourceLocation GUI =
            new ResourceLocation("thaumcraft", "textures/gui/gui_warp_gate.png");

    /** The lock's state. Deliberately not {@link GuiButton#enabled}. */
    public boolean locked;

    public GuiButtonWarpGateLock(int id, int x, int y, boolean locked) {
        super(id, x, y, 13, 13, "");
        this.locked = locked;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.locked) {
            return;   // upstream draws nothing for an open gate
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI);
        drawTexturedModalRect(this.x, this.y, 176, 0, this.width, this.height);
    }
}
