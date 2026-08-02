package thaumcraft.client.renderers.models;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

/**
 * The ring of little cubes orbiting above a warp gate — the port of Thaumic
 * Tinkerer's {@code ModelSpinningCubes} (Vazkii).
 *
 * <p>Transcribed rather than reinterpreted; the strange parts are upstream's
 * and deliberate:</p>
 *
 * <ul>
 * <li>Texturing is disabled around the whole pass, so the cubes render as
 * flat colour — the texture offset on the box never matters.</li>
 * <li>The clock is the <em>client player's</em> {@code ticksExisted}, not the
 * tile's — every gate in view spins in phase.</li>
 * <li>The lightmap is forced to fullbright (upstream's {@code 15728880}), so
 * the ring glows at night.</li>
 * <li>The method recurses: each repeat re-renders the whole ring 0.75 ticks
 * back in time at alpha 0.2 — that is the ghost trail behind each cube.</li>
 * </ul>
 */
public class ModelSpinningCubes extends ModelBase {

    private final ModelRenderer spinningCube;

    public ModelSpinningCubes() {
        spinningCube = new ModelRenderer(this, 42, 0);
        spinningCube.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
        spinningCube.setRotationPoint(0.0F, 0.0F, 0.0F);
        spinningCube.setTextureSize(64, 64);
        spinningCube.mirror = true;
    }

    public void renderSpinningCubes(int cubes, int repeat, int origRepeat) {
        GlStateManager.disableTexture2D();

        final float modifier = 6.0F;
        final float rotationModifier = 0.25F;
        final float radiusBase = 0.7F;
        final float radiusMod = 0.1F;

        double ticks = Minecraft.getMinecraft().player.ticksExisted - 0.75D * (origRepeat - repeat);
        float offsetPerCube = 360 / cubes;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.025F, 0.85F, -0.025F);
        for (int i = 0; i < cubes; i++) {
            float offset = offsetPerCube * i;
            float deg = (int) (ticks / rotationModifier % 360.0F + offset);
            float rad = deg * (float) Math.PI / 180.0F;
            float radiusX = (float) (radiusBase + radiusMod * Math.sin(ticks / modifier));
            float radiusZ = (float) (radiusBase + radiusMod * Math.cos(ticks / modifier));
            float x = (float) (radiusX * Math.cos(rad));
            float z = (float) (radiusZ * Math.sin(rad));
            float y = (float) Math.cos((ticks + 50 * i) / 5.0F) / 10.0F;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            float xRotate = (float) Math.sin(ticks * rotationModifier) / 2.0F;
            float yRotate = (float) Math.max(0.6F, Math.sin(ticks * 0.1F) / 2.0F + 0.5F);
            float zRotate = (float) Math.cos(ticks * rotationModifier) / 2.0F;

            GlStateManager.rotate(deg, xRotate, yRotate, zRotate);
            if (repeat < origRepeat) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.2F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            } else {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            int light = 15728880;
            int lightmapX = light % 65536;
            int lightmapY = light / 65536;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
            spinningCube.render(1.0F / 16.0F);

            if (repeat < origRepeat) {
                GlStateManager.disableBlend();
            }

            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        GlStateManager.enableTexture2D();

        if (repeat != 0) {
            renderSpinningCubes(cubes, repeat - 1, origRepeat);
        }
    }
}
