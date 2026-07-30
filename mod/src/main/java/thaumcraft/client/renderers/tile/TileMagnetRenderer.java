package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelMagnet;
import thaumcraft.common.blocks.tinkerer.BlockMagnet;
import thaumcraft.common.tiles.tinkerer.TileMagnet;

/**
 * Draws the Magnet and the Mob Magnet — ported from Thaumic Tinkerer's
 * {@code RenderTileMagnet} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Upstream draws this block only here: its {@code renderWorldBlock} returns
 * false, so there is no static model at all. The port had it as a plain JSON
 * cube, which is why it never matched its own two-pixel collision box.</p>
 *
 * <p>Four textures, picked off the same two metadata bits the blockstate uses:
 * pulling or pushing, plain or mob.</p>
 *
 * <p><b>Not ported: the lightning rings.</b> Upstream stacks three pairs of
 * animated quads above the post, drawn from
 * {@code ItemHoverHarness.iconLightningRing} through
 * {@code UtilsFX.renderQuadCenteredFromIcon} and tinted by the indirect
 * redstone power. Neither the icon field nor that helper exists in this port,
 * so drawing them would mean inventing both. Recorded in
 * {@code KNOWN_ISSUES.md} rather than approximated.</p>
 */
public class TileMagnetRenderer extends TileEntitySpecialRenderer<TileMagnet> {

    private static final ResourceLocation MAGNET_PULL =
            new ResourceLocation("thaumcraft", "textures/models/magnet_s.png");
    private static final ResourceLocation MAGNET_PUSH =
            new ResourceLocation("thaumcraft", "textures/models/magnet_n.png");
    private static final ResourceLocation MOB_MAGNET_PULL =
            new ResourceLocation("thaumcraft", "textures/models/mob_magnet_s.png");
    private static final ResourceLocation MOB_MAGNET_PUSH =
            new ResourceLocation("thaumcraft", "textures/models/mob_magnet_n.png");

    private final ModelMagnet model = new ModelMagnet();

    @Override
    public void render(TileMagnet magnet, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        // Upstream reads the metadata bits directly; the blockstate carries the
        // same two, and falls back to the pulling plain magnet with no world —
        // which is the case the inventory render hits.
        boolean pulling = true;
        boolean mob = false;
        if (magnet != null && magnet.getWorld() != null) {
            IBlockState state = magnet.getWorld().getBlockState(magnet.getPos());
            if (state.getBlock() instanceof BlockMagnet) {
                pulling = state.getValue(BlockMagnet.PULLING);
                mob = state.getValue(BlockMagnet.MOB);
            }
        }

        bindTexture(mob
                ? (pulling ? MOB_MAGNET_PULL : MOB_MAGNET_PUSH)
                : (pulling ? MAGNET_PULL : MAGNET_PUSH));

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x, y, z);

        // The original's translate(0.5, 1.5, 0.5) then scale(1, -1, -1): the model
        // is built head-down in entity space, so it is flipped onto the block.
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.model.render();

        GlStateManager.popMatrix();
    }
}
