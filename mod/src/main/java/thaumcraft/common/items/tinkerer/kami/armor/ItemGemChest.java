package thaumcraft.common.items.tinkerer.kami.armor;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.gear.ModelWings;
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

    /** Built once and reused; a new model per frame is what the original did, but it churns. */
    @SideOnly(Side.CLIENT)
    private static ModelWings wings;

    public ItemGemChest() {
        super(EntityEquipmentSlot.CHEST);
    }

    /**
     * The original's {@code getArmorModel} — this is the whole reason the robe
     * has wings. Without it the chestplate falls back to the plain biped armour
     * model and the wings never exist.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack,
                                    EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        if (wings == null) {
            wings = new ModelWings();
        }
        return wings;
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
