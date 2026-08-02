package thaumcraft.common.lib.endgame;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
 * reshaped twice by the owner: Ascension is <b>the elytra, on armour</b>, and
 * — after the first flight test — nothing engages by itself: the wings have an
 * explicit <b>mode</b>, cycled with a key.</p>
 *
 * <h3>Modes ({@code WingMode} NBT on the chestplate)</h3>
 * <ul>
 * <li><b>OFF</b> — the enchantment sleeps; no physics, no wings drawn.</li>
 * <li><b>GLIDE</b> — the paraglider: fall softened, drift toward the look,
 * soft landing. The default: harmless on the ground.</li>
 * <li><b>FLIGHT</b> (Ascension only) — the real elytra state. It never starts
 * on its own: a <em>fresh</em> jump press on the ground launches, a fresh
 * press in the air spreads the wings mid-fall; landing ends the flight and
 * walking or holding jump on the ground does nothing until pressed anew —
 * the owner's fix for "авто-пробел мешает просто ходить".</li>
 * </ul>
 *
 * <p>Forge 14.23.5.2847 has no {@code canElytraFly} hook; vanilla clears
 * entity flag 7 every server tick for anything that is not the elytra item —
 * server-side and <em>inside</em> the tick, while the metadata sync runs
 * <em>after</em> it. Re-raising the flag in {@code Phase.END} therefore wins
 * the sync, and the client runs genuine elytra physics.</p>
 *
 * <p><b>Climbing costs vis, honestly.</b> The server is the only payer
 * ({@link WandManager#consumeVisFromInventory} — the vis amulet in the
 * baubles first, then wands) and tells the client whether the tank is dry
 * through {@code PacketSoaringFuel}; the first flight test showed the client
 * happily boosting for free on its own authority otherwise.</p>
 */
public class SoaringHandler {

    public static final String TAG_WING_MODE = "WingMode";
    public static final int MODE_OFF = 0;
    public static final int MODE_GLIDE = 1;
    public static final int MODE_FLIGHT = 2;

    /** {@code Entity.setFlag} — protected; flag 7 is the elytra-flying state. */
    private static final Method SET_FLAG = ObfuscationReflectionHelper.findMethod(
            Entity.class, "func_70052_a", void.class, int.class, boolean.class);
    private static final int FLAG_ELYTRA = 7;

    /** One point of Aer, in the centivis units the consume helpers speak. */
    private static final AspectList BOOST_COST = new AspectList().add(Aspect.AIR, 100);

    /** Jump-held state per player entity id, fed by the packet (server) or the key (client). */
    private static final Map<Integer, Boolean> THRUSTING = new HashMap<>();
    /** Fresh, unconsumed jump presses — the only thing that may start a flight. */
    private static final Set<Integer> PENDING_LAUNCH = new HashSet<>();
    /** Ticks of paid ascent remaining, per player entity id (server only). */
    private static final Map<Integer, Integer> THRUST_TICKS = new HashMap<>();
    /** Client-side: the server's word on whether there is vis to climb with. */
    private static final Map<Integer, Boolean> FUEL_OK = new HashMap<>();

    public static void setThrusting(int playerId, boolean thrusting) {
        Boolean previous = THRUSTING.put(playerId, thrusting);
        boolean wasHeld = previous != null && previous;
        if (thrusting && !wasHeld) {
            PENDING_LAUNCH.add(playerId);
        }
        if (!thrusting) {
            PENDING_LAUNCH.remove(playerId);
        }
    }

    public static boolean isThrusting(int playerId) {
        Boolean thrusting = THRUSTING.get(playerId);
        return thrusting != null && thrusting;
    }

    public static void setFuelOk(int playerId, boolean ok) {
        FUEL_OK.put(playerId, ok);
    }

    private static boolean isFuelOk(int playerId) {
        Boolean ok = FUEL_OK.get(playerId);
        return ok == null || ok;
    }

    // ---- the mode on the chestplate ----

    public static int getMode(ItemStack chest) {
        if (chest.isEmpty()) {
            return MODE_OFF;
        }
        if (chest.getTagCompound() == null || !chest.getTagCompound().hasKey(TAG_WING_MODE)) {
            return MODE_GLIDE;   // default: the harmless one
        }
        return chest.getTagCompound().getByte(TAG_WING_MODE);
    }

    public static void setMode(ItemStack chest, int mode) {
        if (!chest.hasTagCompound()) {
            chest.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        chest.getTagCompound().setByte(TAG_WING_MODE, (byte) mode);
    }

    /** The next mode the key cycles to, given what the chestplate can do. */
    public static int cycleMode(int current, boolean ascension) {
        int next = current + 1;
        int top = ascension ? MODE_FLIGHT : MODE_GLIDE;
        return next > top ? MODE_OFF : next;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            int id = event.getEntity().getEntityId();
            THRUSTING.remove(id);
            THRUST_TICKS.remove(id);
            PENDING_LAUNCH.remove(id);
            FUEL_OK.remove(id);
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
        if (!ascension && !soaring) {
            return;
        }
        int mode = getMode(chest);

        if (event.phase == TickEvent.Phase.START) {
            if (mode == MODE_GLIDE) {
                glide(player);
            }
            return;
        }
        if (mode == MODE_FLIGHT && ascension) {
            fly(player);
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
     * carries our answer, not its. Only a fresh jump press ever <em>starts</em>
     * it; landing ends it and nothing restarts by itself.
     */
    private void fly(EntityPlayer player) {
        int id = player.getEntityId();
        boolean jumpHeld = isThrusting(id);
        boolean wet = player.isInWater() || player.isInLava();
        boolean flying = player.isElytraFlying();

        if (player.onGround) {
            // A fresh press launches; a jump merely held over from the flight
            // that just ended does not. Walking stays walking.
            if (PENDING_LAUNCH.remove(id) && !wet && payForAscent(player)) {
                player.motionY = SoaringPhysics.LAUNCH_IMPULSE;
                player.fallDistance = 0.0F;
                setElytraFlying(player, true);
            }
            return;
        }
        if (wet) {
            return;
        }
        if (!flying) {
            // Mid-air: a fresh press spreads the wings; plain falling stays falling.
            if (PENDING_LAUNCH.remove(id)) {
                setElytraFlying(player, true);
                player.fallDistance = 0.0F;
            }
            return;
        }
        if (player.isSneaking()) {
            return;   // the way down: stop maintaining, vanilla folds the wings
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
     * A tick of ascent either rides the current vis point or buys the next one
     * — the amulet in the baubles first, then any wand. The server is the only
     * honest payer; the client asks the last word the server sent it, because
     * with client-authoritative movement a client that "trusts itself" simply
     * flies for free — which is exactly what the first flight test caught.
     */
    private boolean payForAscent(EntityPlayer player) {
        if (player.world.isRemote) {
            return isFuelOk(player.getEntityId());
        }
        int id = player.getEntityId();
        int ticks = THRUST_TICKS.getOrDefault(id, 0);
        if (ticks > 0) {
            THRUST_TICKS.put(id, ticks - 1);
            return true;
        }
        boolean paid = WandManager.consumeVisFromInventory(player, BOOST_COST);
        if (paid) {
            THRUST_TICKS.put(id, SoaringPhysics.THRUST_TICKS_PER_VIS_POINT - 1);
        }
        reportFuel(player, paid);
        return paid;
    }

    /** Tells the client when the tank runs dry or fills again — edges only. */
    private static void reportFuel(EntityPlayer player, boolean ok) {
        Boolean known = FUEL_OK.get(player.getEntityId());
        if (known == null || known != ok) {
            FUEL_OK.put(player.getEntityId(), ok);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendTo(
                        new thaumcraft.common.lib.network.misc.PacketSoaringFuel(ok),
                        (net.minecraft.entity.player.EntityPlayerMP) player);
            }
        }
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
