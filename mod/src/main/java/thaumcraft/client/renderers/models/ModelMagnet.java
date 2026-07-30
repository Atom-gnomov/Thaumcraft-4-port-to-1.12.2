package thaumcraft.client.renderers.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The Magnet and the Mob Magnet — ported from Thaumic Tinkerer's
 * {@code ModelMagnet} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A fourteen-wide plate with a post standing on it and four vanes around
 * the post. Upstream draws the block entirely from here — its
 * {@code renderWorldBlock} returns false — so nothing about this shape can come
 * from a blockstate model.</p>
 */
@SideOnly(Side.CLIENT)
public class ModelMagnet extends ModelBase {

    private final ModelRenderer panel;
    private final ModelRenderer magnet;
    private final ModelRenderer box1;
    private final ModelRenderer box2;
    private final ModelRenderer box3;
    private final ModelRenderer box4;
    private final ModelRenderer box5;

    public ModelMagnet() {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.panel = box(0, 0, 0.0F, 0.0F, 0.0F, 14, 2, 14, -7.0F, 22.0F, -7.0F, 0.0F);
        this.magnet = box(0, 16, 0.0F, 0.0F, 0.0F, 4, 13, 4, -2.0F, 9.0F, -2.0F, 0.0F);
        this.box1 = box(28, 19, 0.0F, -2.0F, 0.0F, 6, 14, 0, 3.0F, 10.0F, -3.0F, -1.570796F);
        this.box2 = box(28, 33, 0.0F, 0.0F, 0.0F, 6, 14, 0, -3.0F, 8.0F, 3.0F, 1.570796F);
        this.box3 = box(40, 19, 0.0F, 0.0F, 0.0F, 6, 14, 0, 3.0F, 8.0F, 3.0F, 3.141593F);
        this.box4 = box(40, 33, 0.0F, 0.0F, 0.0F, 6, 14, 0, -3.0F, 8.0F, -3.0F, 0.0F);
        this.box5 = box(28, 49, 0.0F, 0.0F, 0.0F, 6, 0, 6, -3.0F, 8.0F, -3.0F, 0.0F);
    }

    /** Every part upstream builds the same way; only the Y rotation ever differs. */
    private ModelRenderer box(int texU, int texV, float x, float y, float z,
                              int width, int height, int depth,
                              float pivotX, float pivotY, float pivotZ, float rotationY) {
        ModelRenderer part = new ModelRenderer(this, texU, texV);
        part.addBox(x, y, z, width, height, depth);
        part.setRotationPoint(pivotX, pivotY, pivotZ);
        part.setTextureSize(64, 64);
        part.rotateAngleX = 0.0F;
        part.rotateAngleY = rotationY;
        part.rotateAngleZ = 0.0F;
        return part;
    }

    public void render() {
        final float scale = 1.0F / 16.0F;

        this.panel.render(scale);
        this.magnet.render(scale);
        this.box1.render(scale);
        this.box2.render(scale);
        this.box3.render(scale);
        this.box4.render(scale);
        this.box5.render(scale);
    }
}
