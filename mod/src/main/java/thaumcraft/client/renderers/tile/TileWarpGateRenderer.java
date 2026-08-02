package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.client.renderers.models.ModelSpinningCubes;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * Draws the ring of cubes above a warp gate — the port of Thaumic Tinkerer's
 * {@code RenderTileWarpGate} (Vazkii): twelve cubes, five ghost repeats, hung
 * two and a half blocks up. The gate block itself is an ordinary baked model;
 * this is decoration on top, which is why the port ran without it — a working
 * gate, sitting there dead still.
 */
public class TileWarpGateRenderer extends TileEntitySpecialRenderer<TileWarpGate> {

    private final ModelSpinningCubes cubes = new ModelSpinningCubes();

    @Override
    public void render(TileWarpGate gate, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x + 0.5D, y + 2.5D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 1.0F);
        int repeat = 5;
        cubes.renderSpinningCubes(12, repeat, repeat);
        GlStateManager.popMatrix();
    }
}
