package thaumcraft.common.lib.endgame;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.endgame.ItemWardDeflection;
import thaumcraft.common.items.endgame.ItemWardLastBreath;

/**
 * Gives the two wards their behaviour — End Legacy module, phase 2 (new
 * content, no 1.7.10 original; design in {@code END_LEGACY_PLAN.md} §2).
 *
 * <p><b>Deflection:</b> a projectile that would hit the wearer is refused
 * outright — the attack event is cancelled before damage is rolled, and the
 * charm wears by one. When its 250 points are gone the charm breaks in the
 * slot.</p>
 *
 * <p><b>Last Breath:</b> the vanilla totem, answered from the charm slot. On
 * a fatal blow: death is cancelled, three hearts, the totem's Resistance and
 * Fire Resistance — and <b>two points of temporary warp</b>, because refusing
 * death from inside a mortal frame leaves a mark. The ward shatters into its
 * cracked remnant in place, which re-infuses at a discount: the soul inside
 * is already broken in.</p>
 */
public class WardHandler {

    /** The totem's own numbers: 40 seconds of each. */
    private static final int EFFECT_TICKS = 800;
    private static final int WARP_TEMPORARY = 2;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onProjectile(LivingAttackEvent event) {
        if (event.isCanceled() || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        if (!event.getSource().isProjectile()) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) {
            return;
        }
        int slot = findCharm(player, ConfigItems.itemWardDeflection);
        if (slot < 0) {
            return;
        }
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        ItemStack charm = baubles.getStackInSlot(slot);
        event.setCanceled(true);
        charm.damageItem(1, player);
        if (charm.isEmpty()) {
            baubles.setStackInSlot(slot, ItemStack.EMPTY);
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.8F, 1.2F);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDeath(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) {
            return;
        }
        // The void is the one thing even a dragon's stubbornness cannot argue with.
        if (event.getSource().canHarmInCreative()) {
            return;
        }
        int slot = findCharm(player, ConfigItems.itemWardLastBreath);
        if (slot < 0) {
            return;
        }
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

        event.setCanceled(true);
        player.setHealth(6.0F);
        player.clearActivePotions();
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, EFFECT_TICKS, 1));
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, EFFECT_TICKS, 0));

        baubles.setStackInSlot(slot, new ItemStack(ConfigItems.itemWardLastBreathCracked));
        Thaumcraft.addWarpToPlayer(player, WARP_TEMPORARY, true);

        player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    /** The bauble slot holding the given charm, or -1. */
    private static int findCharm(EntityPlayer player, Item charm) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles == null) {
            return -1;
        }
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack stack = baubles.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == charm) {
                return slot;
            }
        }
        return -1;
    }
}
