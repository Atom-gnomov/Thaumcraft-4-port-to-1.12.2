package thaumcraft.common.lib.tinkerer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Whether a player's awakened ichorcloth armour is switched on — ported from
 * Thaumic Tinkerer's {@code KamiArmorHandler} (Katrina, for pixlepix /
 * nekosune).
 *
 * <p>Every one of the four awakened pieces ends its Thaumonomicon entry with
 * "pressing U will toggle this armor's effects", and this is the flag that
 * sentence refers to. It is stored on the player's persistent entity data, so
 * it survives relogging, and defaults to on for a player who has never pressed
 * the key.</p>
 *
 * <p>The server owns the value; the client keeps its own copy in
 * {@link #clientStatus} because the armour's per-tick code runs on both sides
 * and the client cannot read the server's entity data.</p>
 */
public final class KamiArmorHandler {

    private static final String COMPOUND = "thaumcraft";
    private static final String TAG_STATUS = "GemArmor";

    /** The client's copy, kept in step by {@code PacketToggleArmor}. */
    @SideOnly(Side.CLIENT)
    private static boolean clientStatus = true;

    private KamiArmorHandler() {
    }

    private static NBTTagCompound compound(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(COMPOUND)) {
            data.setTag(COMPOUND, new NBTTagCompound());
        }
        return data.getCompoundTag(COMPOUND);
    }

    /** Absent means on, so armour worn by a player who never pressed U works. */
    public static boolean getArmorStatus(EntityPlayer player) {
        if (player.world != null && player.world.isRemote) {
            return getClientStatus();
        }
        NBTTagCompound cmp = compound(player);
        return !cmp.hasKey(TAG_STATUS) || cmp.getBoolean(TAG_STATUS);
    }

    public static void setArmorStatus(EntityPlayer player, boolean status) {
        if (player.world != null && player.world.isRemote) {
            setClientStatus(status);
            return;
        }
        compound(player).setBoolean(TAG_STATUS, status);
    }

    @SideOnly(Side.CLIENT)
    public static boolean getClientStatus() {
        return clientStatus;
    }

    @SideOnly(Side.CLIENT)
    public static void setClientStatus(boolean status) {
        clientStatus = status;
    }
}
