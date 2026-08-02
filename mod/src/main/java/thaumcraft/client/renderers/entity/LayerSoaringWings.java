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
import thaumcraft.client.renderers.models.gear.ModelWings;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.tinkerer.kami.armor.ItemGemChest;

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

    private final RenderPlayer renderer;
    private final ModelWings wings = new ModelWings();

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
        if (EnchantmentHelper.getEnchantmentLevel(Config.enchSoaring, chest) <= 0
                && EnchantmentHelper.getEnchantmentLevel(Config.enchAscension, chest) <= 0) {
            return;
        }

        this.renderer.bindTexture(WINGS_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.wings.renderWingsOnly(player, limbSwing, limbSwingAmount,
                ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
