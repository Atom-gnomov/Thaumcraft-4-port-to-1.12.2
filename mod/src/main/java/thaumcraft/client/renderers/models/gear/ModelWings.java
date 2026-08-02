package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The wings on the Robes of the Stratosphere — ported from Thaumic Tinkerer's
 * {@code ModelWings} (Vazkii).
 *
 * <p>Two flat quads parented to the body, angled apart. They ride on the ichor
 * gem chestplate's own texture, not a texture of their own: the original's
 * {@code wings.png} sits in the assets unreferenced, and the boxes are cut from
 * {@code ichorGem1.png} at a <em>negative</em> V offset, which wraps around the
 * 32-pixel-tall sheet. That offset is copied as-is — it is what draws the
 * wings.</p>
 *
 * <p>While the wearer is actually flying the two wings sweep in opposite
 * directions off {@code ticksExisted}.</p>
 */
@SideOnly(Side.CLIENT)
public class ModelWings extends ModelBiped {

    /** The original's resting angle for the left wing; the sweep swings around it. */
    private static final float WING_REST_ANGLE = -0.6108652F;

    private final ModelRenderer wing1;
    private final ModelRenderer wing2;

    public ModelWings() {
        super(1.0F);
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.wing1 = new ModelRenderer(this, 16, -12);
        this.wing1.addBox(0.0F, 0.0F, 0.0F, 0, 7, 12);
        this.wing1.setRotationPoint(-2.0F, 1.0F, 2.0F);
        setRotation(this.wing1, 0.0F, WING_REST_ANGLE, 0.0F);
        this.bipedBody.addChild(this.wing1);

        this.wing2 = new ModelRenderer(this, 16, -12);
        this.wing2.addBox(0.1F, 0.0F, 0.0F, 0, 7, 12);
        this.wing2.setRotationPoint(2.0F, 1.0F, 2.0F);
        setRotation(this.wing2, 0.0F, 0.4468043F, 0.0F);
        this.bipedBody.addChild(this.wing2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        this.bipedHead.showModel = false;
        this.bipedHeadwear.showModel = false;
        this.bipedLeftLeg.showModel = false;
        this.bipedRightLeg.showModel = false;

        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scale, Entity entity) {
        EntityLivingBase living = entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
        this.isSneak = living != null && living.isSneaking();

        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            // 1.7.10 said heldItemRight = 0/1/3 and aimedBow; 1.12 folds all of
            // that into the arm pose enum, so the three cases map across directly.
            ItemStack held = player.inventory.getCurrentItem();
            this.rightArmPose = held.isEmpty() ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;

            if (!held.isEmpty() && player.getItemInUseCount() > 0) {
                EnumAction action = held.getItemUseAction();
                if (action == EnumAction.BLOCK) {
                    this.rightArmPose = ModelBiped.ArmPose.BLOCK;
                } else if (action == EnumAction.BOW) {
                    this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }

            if (player.capabilities.isFlying) {
                this.wing1.rotateAngleY = (float) ((Math.sin(entity.ticksExisted) + 1)
                        * (Math.PI / 180.0F) * 15 + WING_REST_ANGLE);
                this.wing2.rotateAngleY = -this.wing1.rotateAngleY;
            }
        }

        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
    }

    /**
     * Draws the wings alone — no body, no arms — for the End Legacy render
     * layer, which puts these wings on any chestplate carrying Soaring or
     * Ascension (new content, no 1.7.10 original; the armor-model route above
     * stays exactly as the Robes of the Stratosphere use it).
     *
     * <p>The body box itself must not draw (the player is already wearing
     * whatever armour they wear), but the wings hang off {@code bipedBody}'s
     * transform, so its rotation is applied by hand before the two wing parts
     * render.</p>
     */
    public void renderWingsOnly(Entity entity, float limbSwing, float limbSwingAmount,
                                float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        net.minecraft.client.renderer.GlStateManager.pushMatrix();
        this.bipedBody.postRender(scale);
        this.wing1.render(scale);
        this.wing2.render(scale);
        net.minecraft.client.renderer.GlStateManager.popMatrix();
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
