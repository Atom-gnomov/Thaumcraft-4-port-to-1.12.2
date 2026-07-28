package thaumcraft.common.items.tinkerer.kami.armor;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

/**
 * Cowl of the Abyssal Depths — ported from Thaumic Tinkerer's
 * {@code ItemGemHelm} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Underwater it keeps your breath full and your eyes open: air is topped
 * back to 300 and night vision is kept alive on a rolling 202-tick refresh, the
 * odd number being what stops the vision flickering as it expires. It reveals
 * nodes like the goggles do.</p>
 */
public class ItemGemHelm extends ItemIchorclothArmorAdv implements IGoggles, IRevealer {

    /** The original's refresh: just over ten seconds, renewed every tick. */
    private static final int NIGHT_VISION_TICKS = 202;

    public ItemGemHelm() {
        super(EntityEquipmentSlot.HEAD);
    }

    @Override
    protected boolean ticks() {
        return true;
    }

    @Override
    protected void tickPlayer(EntityPlayer player) {
        if (!isActive(player) || !player.isInsideOfMaterial(Material.WATER)) {
            return;
        }
        player.setAir(300);
        PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect != null && effect.getDuration() <= NIGHT_VISION_TICKS) {
            player.removePotionEffect(MobEffects.NIGHT_VISION);
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, true, false));
        } else if (effect == null) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, 0, true, false));
        }
    }

    @Override
    public boolean showNodes(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
