package thaumcraft.client.renderers.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelElytra;
import thaumcraft.client.renderers.models.gear.ModelWings;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.tinkerer.kami.armor.ItemGemChest;
import thaumcraft.common.lib.endgame.SoaringHandler;

/**
 * Wings on the back of any chestplate carrying Soaring or Ascension — the End
 * Legacy module's visible half (new content, no 1.7.10 original; see
 * {@code END_LEGACY_PLAN.md}).
 *
 * <p>The Robes of the Stratosphere draw their wings through the armour-model
 * route ({@code ItemGemChest.getArmorModel}); an enchantment has no item class
 * to hang that on, so this is a player render layer instead, added to both
 * skin types at client init. The robe is excluded here — its wings already
 * come from the armour model, and drawing a second pair would double them.</p>
 *
 * <p>Texture: the robe's own sheet ({@code ichor_gem1.png}) — the wings cut
 * their shape from it at a negative V offset. Phase 1 shares it; a recolour
 * gets its own file once seen in game (plan §2).</p>
 */
@SideOnly(Side.CLIENT)
public class LayerSoaringWings implements LayerRenderer<AbstractClientPlayer> {

    private static final ResourceLocation WINGS_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/ichor_gem1.png");
    private static final ResourceLocation ELYTRA_TEXTURE =
            new ResourceLocation("textures/entity/elytra.png");

    private final RenderPlayer renderer;
    private final ModelWings wings = new ModelWings();
    /** Ascension wears the elytra's own silhouette — the owner's call: it flies like one, it looks like one. */
    private final ModelElytra elytra = new ModelElytra();

    public LayerSoaringWings(RenderPlayer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw,
                              float headPitch, float scale) {
        if (player.isInvisible()) {
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty() || chest.getItem() instanceof ItemGemChest) {
            return;
        }
        boolean ascension = EnchantmentHelper.getEnchantmentLevel(Config.enchAscension, chest) > 0;
        boolean soaring = EnchantmentHelper.getEnchantmentLevel(Config.enchSoaring, chest) > 0;
        if (!ascension && !soaring) {
            return;
        }
        // Switched-off wings are stowed wings: nothing on the back at all.
        if (SoaringHandler.getMode(chest) == SoaringHandler.MODE_OFF) {
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (ascension) {
            // Vanilla LayerElytra's own transform; the model reads the flight
            // state off the entity, so the wings spread when flying.
            this.renderer.bindTexture(ELYTRA_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.elytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks,
                    netHeadYaw, headPitch, scale, player);
            this.elytra.render(player, limbSwing, limbSwingAmount, ageInTicks,
                    netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
            return;
        }
        this.renderer.bindTexture(WINGS_TEXTURE);
        this.wings.renderWingsOnly(player, limbSwing, limbSwingAmount,
                ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
