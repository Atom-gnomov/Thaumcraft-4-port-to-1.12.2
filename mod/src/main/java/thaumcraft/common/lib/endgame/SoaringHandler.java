package thaumcraft.common.lib.endgame;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.WandManager;

/**
 * Gives Soaring and Ascension their behaviour — the End Legacy module's flight
 * (new content, no 1.7.10 original; design in {@code END_LEGACY_PLAN.md} §2,
 * reshaped 2026-08-02 by the owner: Ascension is <b>the elytra, on armour</b>,
 * not a hand-rolled thruster).
 *
 * <p><b>Soaring</b> stays a paraglider: fall softened, drift toward the look,
 * soft landing. Runs in {@code Phase.START}, pure functions from
 * {@link SoaringPhysics}.</p>
 *
 * <p><b>Ascension</b> puts the player into the <em>real</em> elytra flight
 * state — vanilla's aerodynamics, the tilted pose, dive-to-accelerate — on any
 * chestplate. Forge 14.23.5.2847 has no {@code canElytraFly} hook, and vanilla
 * clears entity flag 7 every server tick for anything that is not the elytra
 * item ({@code EntityLivingBase.updateElytra}, server-side only). The way
 * through is sequencing, not a coremod: {@code updateElytra} runs
 * <em>inside</em> the tick and the metadata sync happens <em>after</em> it, so
 * re-raising the flag in {@code Phase.END} means the value the client ever
 * sees is {@code true} — and the client, whose flag vanilla never touches,
 * runs the genuine elytra physics on it.</p>
 *
 * <p><b>Climbing costs vis.</b> A held jump while flying applies the firework
 * rocket's own boost formula and burns one point of Aer per
 * {@link SoaringPhysics#THRUST_TICKS_PER_VIS_POINT} ticks through
 * {@link WandManager#consumeVisFromInventory} — which drains the <b>vis
 * amulet in the baubles first</b>, then any wand, exactly the owner's
 * "за вис в броне/бижутерии". A held jump on the ground is the launch.
 * Sneaking drops out of the flight state — the emergency exit.</p>
 */
public class SoaringHandler {

    /** {@code Entity.setFlag} — protected; flag 7 is the elytra-flying state. */
    private static final Method SET_FLAG = ObfuscationReflectionHelper.findMethod(
            Entity.class, "func_70052_a", void.class, int.class, boolean.class);
    private static final int FLAG_ELYTRA = 7;

    /** One point of Aer, in the centivis units the consume helpers speak. */
    private static final AspectList BOOST_COST = new AspectList().add(Aspect.AIR, 100);

    /** Jump-held state per player entity id, fed by the packet (server) or read directly (client). */
    private static final Map<Integer, Boolean> THRUSTING = new HashMap<>();
    /** Ticks of paid ascent remaining, per player entity id (server only). */
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
        EntityPlayer player = event.player;
        if (player.capabilities.isFlying || player.isRiding()) {
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            return;
        }
        boolean ascension = EnchantmentHelper.getEnchantmentLevel(Config.enchAscension, chest) > 0;
        boolean soaring = EnchantmentHelper.getEnchantmentLevel(Config.enchSoaring, chest) > 0;

        if (event.phase == TickEvent.Phase.START) {
            // The paraglider — only when the full flight state is not in play.
            if (soaring && !ascension) {
                glide(player);
            }
            return;
        }
        if (ascension) {
            ascend(player);
        }
    }

    private void glide(EntityPlayer player) {
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
     * The elytra state, maintained per tick from {@code Phase.END} — after
     * vanilla's {@code updateElytra} has had its say, so the metadata sync
     * carries our answer, not its.
     */
    private void ascend(EntityPlayer player) {
        boolean jumpHeld = isThrusting(player.getEntityId());
        boolean wet = player.isInWater() || player.isInLava();

        if (player.onGround) {
            // The launch: a held jump on the ground throws the player up and
            // straight into the flight state — no firework, no cliff.
            if (jumpHeld && !wet && payForAscent(player)) {
                player.motionY = SoaringPhysics.LAUNCH_IMPULSE;
                player.fallDistance = 0.0F;
                setElytraFlying(player, true);
            }
            return;
        }
        if (wet || player.isSneaking()) {
            // Sneak is the way down; water ends the argument on its own.
            return;
        }

        setElytraFlying(player, true);
        player.fallDistance = 0.0F;

        if (jumpHeld && payForAscent(player)) {
            Vec3d look = player.getLookVec();
            double[] motion = SoaringPhysics.boost(
                    new double[]{player.motionX, player.motionY, player.motionZ},
                    new double[]{look.x, look.y, look.z});
            player.motionX = motion[0];
            player.motionY = motion[1];
            player.motionZ = motion[2];
        }
    }

    /**
     * A tick of ascent either rides the current vis point or buys the next
     * one — the amulet in the baubles first, then any wand
     * ({@link WandManager#consumeVisFromInventory}'s order). Payment is
     * server-side; the client trusts its reported jump state and lets the
     * server's motion win when the vis runs out.
     */
    private boolean payForAscent(EntityPlayer player) {
        if (player.world.isRemote) {
            return true;
        }
        int ticks = THRUST_TICKS.getOrDefault(player.getEntityId(), 0);
        if (ticks > 0) {
            THRUST_TICKS.put(player.getEntityId(), ticks - 1);
            return true;
        }
        if (WandManager.consumeVisFromInventory(player, BOOST_COST)) {
            THRUST_TICKS.put(player.getEntityId(), SoaringPhysics.THRUST_TICKS_PER_VIS_POINT - 1);
            return true;
        }
        return false;
    }

    /** Both sides: the server's write is what the metadata sync broadcasts. */
    private static void setElytraFlying(EntityPlayer player, boolean flying) {
        try {
            SET_FLAG.invoke(player, FLAG_ELYTRA, flying);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Entity.setFlag is unreachable — the mappings moved", e);
        }
    }
}
