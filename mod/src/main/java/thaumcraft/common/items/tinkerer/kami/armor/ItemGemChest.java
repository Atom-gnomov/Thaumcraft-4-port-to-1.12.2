package thaumcraft.common.items.tinkerer.kami.armor;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.foci.FocusDeflect;

/**
 * Robes of the Stratosphere — ported from Thaumic Tinkerer's
 * {@code ItemGemChest} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Grants free flight while worn and switched on, and turns aside arrows and
 * other projectiles with the same sweep the Wand Focus: Distortion uses.</p>
 *
 * <p>Flight is granted and revoked by hand, so the set of players it was given
 * to has to be remembered — take the robe off and the ability goes with it,
 * without stripping flight from someone in creative.</p>
 */
public class ItemGemChest extends ItemIchorclothArmorAdv {

    /** Players this robe handed flight to, so it can take it back again. */
    private static final Set<String> PLAYERS_WITH_FLIGHT = new HashSet<>();

    public ItemGemChest() {
        super(EntityEquipmentSlot.CHEST);
    }

    @Override
    protected boolean ticks() {
        return true;
    }

    @Override
    protected void tickPlayer(EntityPlayer player) {
        if (!isActive(player)) {
            return;
        }
        FocusDeflect.protectFromProjectiles(player);
    }

    private boolean shouldPlayerHaveFlight(EntityPlayer player) {
        return isActive(player) && Config.tinkererEnableFlight;
    }

    @SubscribeEvent
    public void updatePlayerFlyStatus(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        String name = player.getGameProfile().getName();

        if (PLAYERS_WITH_FLIGHT.contains(name)) {
            if (shouldPlayerHaveFlight(player)) {
                player.capabilities.allowFlying = true;
            } else {
                // Never strip flight from someone who has it for other reasons.
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.capabilities.disableDamage = false;
                }
                PLAYERS_WITH_FLIGHT.remove(name);
            }
        } else if (shouldPlayerHaveFlight(player)) {
            PLAYERS_WITH_FLIGHT.add(name);
            player.capabilities.allowFlying = true;
        }
    }
}
