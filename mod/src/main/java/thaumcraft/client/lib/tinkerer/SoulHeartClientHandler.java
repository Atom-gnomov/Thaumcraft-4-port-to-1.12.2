package thaumcraft.client.lib.tinkerer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Draws the soul hearts granted by the awakened ichorium sword — the port of
 * Thaumic Tinkerer's {@code SoulHeartClientHandler}.
 *
 * <p>They sit above the food bar, and the rest of the overlay is pushed down
 * ten pixels while any are held; the air bar gets the same shove so it does
 * not land on top of them.</p>
 */
@SideOnly(Side.CLIENT)
public final class SoulHeartClientHandler {

    private static final ResourceLocation ICONS =
            new ResourceLocation("textures/gui/icons.png");
    private static final ResourceLocation HEARTS =
            new ResourceLocation("thaumcraft", "textures/gui/soul_hearts.png");

    /** Kept in step by PacketSoulHearts; the pool itself lives server-side. */
    public static int clientPlayerHP = 0;

    @SubscribeEvent
    public void renderHealthBar(RenderGameOverlayEvent event) {
        if (event.getType() == ElementType.FOOD && clientPlayerHP > 0) {
            if (event instanceof RenderGameOverlayEvent.Post) {
                Minecraft mc = Minecraft.getMinecraft();
                int x = event.getResolution().getScaledWidth() / 2 + 10;
                int y = event.getResolution().getScaledHeight() - 39;
                GlStateManager.translate(0.0F, 10.0F, 0.0F);
                mc.getTextureManager().bindTexture(HEARTS);
                int drawn = 0;
                for (int i = 0; i < clientPlayerHP; i++) {
                    boolean half = i == clientPlayerHP - 1 && clientPlayerHP % 2 != 0;
                    if (half || i % 2 == 0) {
                        renderHeart(x + drawn * 8, y, !half);
                        drawn++;
                    }
                }
                mc.getTextureManager().bindTexture(ICONS);
            }
            GlStateManager.translate(0.0F, -10.0F, 0.0F);
        }
        if (event.getType() == ElementType.AIR
                && event instanceof RenderGameOverlayEvent.Post && clientPlayerHP > 0) {
            GlStateManager.translate(0.0F, 10.0F, 0.0F);
        }
    }

    /** A half heart is seven pixels wide and takes the right half of the sheet. */
    private static void renderHeart(int x, int y, boolean full) {
        float size = 1 / 16.0F;
        float startX = full ? 0.0F : 9 * size;
        float endX = full ? 9 * size : 1.0F;
        float endY = 9 * size;
        int width = full ? 9 : 7;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(x, y + 9, 0.0D).tex(startX, endY).endVertex();
        buf.pos(x + width, y + 9, 0.0D).tex(endX, endY).endVertex();
        buf.pos(x + width, y, 0.0D).tex(endX, 0.0D).endVertex();
        buf.pos(x, y, 0.0D).tex(startX, 0.0D).endVertex();
        tess.draw();
    }
}
