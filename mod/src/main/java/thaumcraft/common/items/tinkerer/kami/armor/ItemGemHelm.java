package thaumcraft.common.items.tinkerer.kami.armor;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

/**
 * Cowl of the Abyssal Depths — ported from Thaumic Tinkerer's
 * {@code ItemGemHelm} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Underwater it keeps your breath full and your eyes open: air is topped
 * back to 300 and night vision is kept alive on a rolling 202-tick refresh, the
 * odd number being what stops the vision flickering as it expires.</p>
 *
 * <p>Submerged in <em>lava</em> it also keeps you breathing — and blinds you,
 * which is upstream's own joke: the depths it is named for are not meant to be
 * sightseeing.</p>
 *
 * <p>It feeds you as well: half a heart every four seconds while your hunger is
 * between one and seventeen. That runs whenever the cowl is worn, even switched
 * off, because upstream puts it outside both material checks.</p>
 *
 * <p>It reveals nodes like the goggles do.</p>
 */
public class ItemGemHelm extends ItemIchorclothArmorAdv implements IGoggles, IRevealer {

    /** The original's refresh: just over ten seconds, renewed every tick. */
    private static final int REFRESH_TICKS = 202;

    public ItemGemHelm() {
        super(EntityEquipmentSlot.HEAD);
    }

    @Override
    protected boolean ticks() {
        return true;
    }

    @Override
    protected void tickPlayer(EntityPlayer player) {
        if (isActive(player)) {
            if (player.isInsideOfMaterial(Material.WATER)) {
                player.setAir(300);
                refresh(player, MobEffects.NIGHT_VISION);
            }
            if (player.isInsideOfMaterial(Material.LAVA)) {
                player.setAir(300);
                refresh(player, MobEffects.BLINDNESS);
            }
        }
        // Outside the material checks and outside isActive, exactly as upstream:
        // the cowl feeds its wearer even when switched off.
        int food = player.getFoodStats().getFoodLevel();
        if (food > 0 && food < 18 && player.shouldHeal() && player.ticksExisted % 80 == 0) {
            player.heal(1.0F);
        }
    }

    /** Keeps an effect topped up at 202 ticks rather than letting it lapse. */
    private static void refresh(EntityPlayer player, Potion effect) {
        PotionEffect active = player.getActivePotionEffect(effect);
        if (active == null || active.getDuration() <= REFRESH_TICKS) {
            if (active != null) {
                player.removePotionEffect(effect);
            }
            player.addPotionEffect(new PotionEffect(effect, REFRESH_TICKS, 0, true, false));
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
