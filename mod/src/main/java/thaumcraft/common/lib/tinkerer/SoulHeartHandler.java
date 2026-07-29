package thaumcraft.common.lib.tinkerer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Soul hearts — the port of Thaumic Tinkerer's {@code SoulHeartHandler}.
 *
 * <p>A pool of up to twenty points kept in the player's persistent NBT that
 * soaks incoming damage before their real health does. Only the awakened
 * ichorium sword grants them, one per hit in its third mode, which is why the
 * whole thing lives and dies with that weapon upstream too.</p>
 */
public class SoulHeartHandler {

    private static final String COMPOUND = "thaumcraft";
    private static final String TAG_HP = "soulHearts";
    private static final int MAX_HP = 20;

    /** Damage comes off the soul pool first; only the overflow reaches the player. */
    @SubscribeEvent
    public void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && event.getAmount() > 0.0F) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            event.setAmount(removeHP(player, (int) event.getAmount()));
        }
    }

    public static void addHearts(EntityPlayer player) {
        addHP(player, 1);
    }

    public static boolean addHP(EntityPlayer player, int hp) {
        int current = getHP(player);
        if (current >= MAX_HP) {
            return false;
        }
        setHP(player, Math.min(MAX_HP, current + hp));
        return true;
    }

    /** Returns the damage left over once the soul pool has taken what it can. */
    public static int removeHP(EntityPlayer player, int hp) {
        int current = getHP(player);
        int newHp = current - hp;
        setHP(player, Math.max(0, newHp));
        return Math.max(0, -newHp);
    }

    public static void setHP(EntityPlayer player, int hp) {
        getCompoundToSet(player).setInteger(TAG_HP, hp);
    }

    public static int getHP(EntityPlayer player) {
        NBTTagCompound cmp = getCompoundToSet(player);
        return cmp.hasKey(TAG_HP) ? cmp.getInteger(TAG_HP) : 0;
    }

    private static NBTTagCompound getCompoundToSet(EntityPlayer player) {
        NBTTagCompound cmp = player.getEntityData();
        if (!cmp.hasKey(COMPOUND)) {
            cmp.setTag(COMPOUND, new NBTTagCompound());
        }
        return cmp.getCompoundTag(COMPOUND);
    }
}
