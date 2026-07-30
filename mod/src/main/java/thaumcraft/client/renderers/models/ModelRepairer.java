package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The Repairer — ported from Thaumic Tinkerer's {@code ModelRepairer}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A base plate, four corner posts, a four-piece rim, a small opening on the
 * front face, and five panes of glass. Upstream draws the block entirely from
 * here — its {@code renderWorldBlock} returns false — so none of this shape can
 * come from a blockstate model.</p>
 *
 * <p>The glass is a <b>separate pass</b>: upstream keeps {@code render()} and
 * {@code renderGlass()} apart so the item inside can be drawn between them,
 * with the panes blended over it. Calling them together would put the item
 * behind the glass in the depth buffer instead of inside the case.</p>
 */
@SideOnly(Side.CLIENT)
public class ModelRepairer extends ModelBase {

    private static final float HALF_TURN = 3.141593F;

    private final ModelRenderer[] body;
    private final ModelRenderer[] glass;

    public ModelRepairer() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.body = new ModelRenderer[]{
                box(0, 0, 16, 1, 16, -8.0F, 23.0F, -8.0F, 0.0F),        // base
                box(0, 17, 2, 14, 2, -8.0F, 9.0F, 6.0F, 0.0F),          // support 1
                box(0, 17, 2, 14, 2, 6.0F, 9.0F, 6.0F, 0.0F),           // support 2
                box(0, 17, 2, 14, 2, -8.0F, 9.0F, -8.0F, 0.0F),         // support 3
                box(0, 17, 2, 14, 2, 6.0F, 9.0F, -8.0F, 0.0F),          // support 4
                box(11, 19, 16, 1, 2, -8.0F, 8.0F, -8.0F, 0.0F),        // top 1
                box(11, 19, 16, 1, 2, -8.0F, 8.0F, 6.0F, 0.0F),         // top 2
                box(11, 23, 2, 1, 12, 6.0F, 8.0F, -6.0F, 0.0F),         // top 3
                box(11, 23, 2, 1, 12, -8.0F, 8.0F, -6.0F, 0.0F),        // top 4
                box(48, 30, 5, 1, 1, -2.5F, 14.0F, 7.0F, 0.0F),         // opening 1
                box(48, 30, 5, 1, 1, -2.5F, 17.0F, 7.0F, 0.0F),         // opening 2
                box(48, 24, 1, 3, 1, -2.0F, 14.5F, 7.0F, 0.0F),         // opening 3
                box(48, 24, 1, 3, 1, 1.0F, 14.5F, 7.0F, 0.0F),          // opening 4
        };

        this.glass = new ModelRenderer[]{
                box(-11, 37, 12, 0, 12, -6.0F, 8.5F, -6.0F, 0.0F),      // lid
                box(1, 38, 0, 14, 12, 7.5F, 9.0F, -6.0F, 0.0F),
                box(1, 38, 0, 14, 12, -7.5F, 9.0F, 6.0F, HALF_TURN),
                box(40, 34, 12, 14, 0, -6.0F, 9.0F, -7.5F, 0.0F),
                box(33, 50, 12, 14, 0, 6.0F, 9.0F, 7.5F, HALF_TURN),
        };
    }

    /** Every part upstream builds the same way; only the Y rotation ever differs. */
    private ModelRenderer box(int texU, int texV, int width, int height, int depth,
                              float pivotX, float pivotY, float pivotZ, float rotationY) {
        ModelRenderer part = new ModelRenderer(this, texU, texV);
        part.addBox(0.0F, 0.0F, 0.0F, width, height, depth);
        part.setRotationPoint(pivotX, pivotY, pivotZ);
        part.setTextureSize(64, 64);
        part.rotateAngleX = 0.0F;
        part.rotateAngleY = rotationY;
        part.rotateAngleZ = 0.0F;
        return part;
    }

    /** The case, without its glass. */
    public void render() {
        final float scale = 1.0F / 16.0F;
        for (ModelRenderer part : this.body) {
            part.render(scale);
        }
    }

    /** The five panes, drawn after whatever is inside. */
    public void renderGlass() {
        final float scale = 1.0F / 16.0F;
        for (ModelRenderer pane : this.glass) {
            pane.render(scale);
        }
    }
}
