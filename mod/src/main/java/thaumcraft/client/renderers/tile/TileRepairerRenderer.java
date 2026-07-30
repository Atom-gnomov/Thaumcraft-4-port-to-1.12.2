package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelRepairer;
import thaumcraft.common.blocks.tinkerer.BlockRepairer;
import thaumcraft.common.tiles.tinkerer.TileRepairer;

/**
 * Draws the Repairer — ported from Thaumic Tinkerer's {@code RenderTileRepairer}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Upstream draws this block only here; {@code renderWorldBlock} returns false
 * and there is no static geometry. The port had a JSON cube in its place.</p>
 *
 * <p>Three passes in upstream's order, and the order is the point: the case,
 * then whatever is being repaired, then the glass blended over it. Draw the
 * glass with the case and the item ends up behind it.</p>
 *
 * <p><b>Not ported: the repair overlay.</b> Upstream spins a quad under the
 * block showing {@code repair.png} or {@code repairOff.png} depending on
 * {@code tookLastTick} — whether essentia was actually drawn that tick. Our
 * {@link TileRepairer} has no such flag and it is server-side knowledge, so
 * showing it means adding the field and a sync path, not just a draw call.
 * Both textures are copied and waiting; recorded in {@code KNOWN_ISSUES.md}.</p>
 */
public class TileRepairerRenderer extends TileEntitySpecialRenderer<TileRepairer> {

    private static final ResourceLocation REPAIRER =
            new ResourceLocation("thaumcraft", "textures/models/repairer.png");

    private final ModelRepairer model = new ModelRepairer();

    @Override
    public void render(TileRepairer repairer, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (repairer == null) {
            return;
        }

        float rotation = rotationFor(repairer);
        float spin = (repairer.renderTicks + partialTicks) * 0.75F % 360.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x, y, z);

        // Upstream's translate(0, 2, 1) then scale(1, -1, -1): the model is built
        // head-down in entity space and flipped onto the block.
        GlStateManager.translate(0.0F, 2.0F, 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(REPAIRER);
        this.model.render();
        GlStateManager.disableBlend();

        renderRepairedItem(repairer, spin);

        // The glass last, so it blends over the item rather than hiding it.
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(REPAIRER);
        this.model.renderGlass();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * The item hovers inside the case, bobbing and turning. Upstream drew it by
     * hand through {@code ItemRenderer.renderItemIn2D} across the item's render
     * passes; 1.12's baked item models cover all of that, so this is the
     * equivalent call rather than a transcription.
     */
    private void renderRepairedItem(TileRepairer repairer, float spin) {
        ItemStack stack = repairer.getInventory().getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        // Undo the flip so the item is not drawn upside down.
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        final float scale = 0.5F;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-0.5F,
                -2.5F + Math.sin(repairer.renderTicks / 10.0F) * 0.1F, 0.0F);
        GlStateManager.translate(0.5F, 0.5F, 1.0F / 32.0F);
        GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -1.0F / 32.0F);

        Minecraft.getMinecraft().getRenderItem()
                .renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
    }

    /** Upstream reads the facing metadata: 2 is 0 degrees, 3 is 180, 4 is 270, else 90. */
    private float rotationFor(TileRepairer repairer) {
        if (repairer.getWorld() == null) {
            return 180.0F;
        }
        IBlockState state = repairer.getWorld().getBlockState(repairer.getPos());
        if (!(state.getBlock() instanceof BlockRepairer)) {
            return 180.0F;
        }
        EnumFacing facing = state.getValue(BlockRepairer.FACING);
        switch (facing) {
            case NORTH:
                return 0.0F;
            case SOUTH:
                return 180.0F;
            case WEST:
                return 270.0F;
            default:
                return 90.0F;
        }
    }
}
