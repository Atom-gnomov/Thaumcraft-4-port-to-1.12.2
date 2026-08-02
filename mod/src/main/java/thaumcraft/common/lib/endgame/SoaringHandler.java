package thaumcraft.common.lib.endgame;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Gives the Soaring and Ascension enchantments their behaviour — the End
 * Legacy module's flight (new content, no 1.7.10 original; numbers and design
 * in {@code END_LEGACY_PLAN.md}).
 *
 * <p>Runs in {@code PlayerTickEvent} on <b>both</b> sides: the client
 * predicts its own motion in 1.12, so a server-only push stutters. The maths
 * lives in {@link SoaringPhysics} as pure functions; this class is the glue —
 * find the enchant, read the state, apply the numbers.</p>
 *
 * <p>Thrust needs to know whether the jump key is held, which the server
 * cannot see — the client reports edges through {@code PacketSoaringThrust},
 * the same road the hover harness's fly packet takes. Thrust is paid in Aer
 * from any wand in the inventory: one point per
 * {@link SoaringPhysics#THRUST_TICKS_PER_VIS_POINT} ticks, and when the wands
 * run dry the thrust dies but the glide stays.</p>
 */
public class SoaringHandler {

    /** Jump-held state per player entity id, fed by the packet (server) or read directly (client). */
    private static final Map<Integer, Boolean> THRUSTING = new HashMap<>();
    /** Ticks of thrust since the last vis point was paid, per player entity id. */
    private static final Map<Integer, Integer> THRUST_TICKS = new HashMap<>();

    public static void setThrusting(int playerId, boolean thrusting) {
        THRUSTING.put(playerId, thrusting);
    }

    public static boolean isThrusting(int playerId) {
        Boolean thrusting = THRUSTING.get(playerId);
        return thrusting != null && thrusting;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            THRUSTING.remove(event.getEntity().getEntityId());
            THRUST_TICKS.remove(event.getEntity().getEntityId());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        EntityPlayer player = event.player;
        if (player.capabilities.isFlying || player.isRiding()) {
            return;
        }

        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            return;
        }
        boolean ascension = EnchantmentHelper.getEnchantmentLevel(Config.enchAscension, chest) > 0;
        boolean soaring = ascension
                || EnchantmentHelper.getEnchantmentLevel(Config.enchSoaring, chest) > 0;
        if (!soaring) {
            return;
        }

        boolean thrusting = ascension && isThrusting(player.getEntityId());

        // The launch: a held jump on the ground throws the player up — no
        // firework, no cliff. This is the owner's core request; the ordinary
        // jump impulse is replaced, not stacked.
        if (thrusting && player.onGround && payForThrust(player)) {
            player.motionY = SoaringPhysics.LAUNCH_IMPULSE;
            player.fallDistance = 0.0F;
            return;
        }

        if (thrusting && !player.onGround && payForThrust(player)) {
            Vec3d look = player.getLookVec().normalize();
            double[] motion = SoaringPhysics.thrust(
                    new double[]{player.motionX, player.motionY, player.motionZ},
                    new double[]{look.x, look.y, look.z});
            player.motionX = motion[0];
            player.motionY = motion[1];
            player.motionZ = motion[2];
            player.fallDistance = 0.0F;
            return;
        }

        if (SoaringPhysics.isGlidingFall(player.motionY, player.onGround,
                player.isSneaking(), player.isInWater() || player.isInLava())) {
            player.motionY = SoaringPhysics.glideMotionY(player.motionY);
            Vec3d look = player.getLookVec();
            double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
            if (horizontal > 1.0E-4D) {
                player.motionX += SoaringPhysics.glideDrift(look.x / horizontal);
                player.motionZ += SoaringPhysics.glideDrift(look.z / horizontal);
            }
            player.fallDistance = 0.0F;
        }
    }

    /**
     * A tick of thrust either rides the current vis point or buys the next one
     * from a wand in the inventory. Runs the payment server-side only; the
     * client trusts its own state until the server stops moving it.
     */
    private boolean payForThrust(EntityPlayer player) {
        if (player.world.isRemote) {
            return true;
        }
        int ticks = THRUST_TICKS.getOrDefault(player.getEntityId(), 0);
        if (ticks > 0) {
            THRUST_TICKS.put(player.getEntityId(), ticks - 1);
            return true;
        }
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                if (wand.consumeVis(stack, player, Aspect.AIR, 100, false)) {
                    THRUST_TICKS.put(player.getEntityId(),
                            SoaringPhysics.THRUST_TICKS_PER_VIS_POINT - 1);
                    return true;
                }
            }
        }
        return false;
    }
}
