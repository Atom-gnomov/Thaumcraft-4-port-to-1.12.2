package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.items.armor.Hover;
import thaumcraft.common.items.armor.ItemHoverHarness;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;

@SideOnly(Side.CLIENT)
public class KeyHandler {
    public final KeyBinding keyF = new KeyBinding("Change Wand Focus",
            KeyConflictContext.IN_GAME, Keyboard.KEY_F, "key.categories.misc");
    public final KeyBinding keyH = new KeyBinding("Activate Hover Harness",
            KeyConflictContext.IN_GAME, Keyboard.KEY_H, "key.categories.misc");
    public final KeyBinding keyG = new KeyBinding("Wand Focus Selector",
            KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories.misc");
    /** Every awakened ichorcloth entry ends "pressing U will toggle this armor's effects". */
    public final KeyBinding keyU = new KeyBinding("ttmisc.toggleArmor",
            KeyConflictContext.IN_GAME, Keyboard.KEY_U, "key.categories.misc");
    /** End Legacy: cycles the wings off / glide / flight on the worn chestplate. */
    public final KeyBinding keyK = new KeyBinding("endlegacy.key.wings",
            KeyConflictContext.IN_GAME, Keyboard.KEY_K, "key.categories.misc");

    public static boolean radialActive = false;
    public static boolean radialLock = false;
    public static long lastPressF = 0L;
    public static long lastPressH = 0L;
    public static long lastPressG = 0L;

    private boolean keyPressedF = false;
    private boolean keyPressedH = false;
    private boolean keyPressedG = false;
    private boolean keyPressedU = false;
    private boolean keyPressedK = false;

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(this.keyF);
        ClientRegistry.registerKeyBinding(this.keyH);
        ClientRegistry.registerKeyBinding(this.keyG);
        ClientRegistry.registerKeyBinding(this.keyU);
        ClientRegistry.registerKeyBinding(this.keyK);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.inGameHasFocus) {
            releaseAllKeys();
            return;
        }

        EntityPlayer player = minecraft.player;
        handleFocusKey(player);
        handleHoverKey(player);
        handleMiscKey(player);
        handleArmorToggleKey(player);
        handleWingModeKey(player);
    }

    /**
     * Flips every worn awakened ichorcloth piece on or off at once — the port of
     * upstream's {@code GemArmorKeyHandler}. It only fires while at least one
     * piece is worn, so the key stays free otherwise.
     */
    private void handleArmorToggleKey(EntityPlayer player) {
        if (!this.keyU.isKeyDown()) {
            this.keyPressedU = false;
            return;
        }
        if (this.keyPressedU) {
            return;   // held down; act on the press, not on every tick
        }
        this.keyPressedU = true;

        if (player == null || !wearsAwakenedIchorcloth(player)) {
            return;
        }
        boolean enabled = !thaumcraft.common.lib.tinkerer.KamiArmorHandler.getClientStatus();
        thaumcraft.common.lib.tinkerer.KamiArmorHandler.setClientStatus(enabled);
        PacketHandler.INSTANCE.sendToServer(
                new thaumcraft.common.lib.network.tinkerer.PacketToggleArmor(enabled));
        player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                enabled ? "ttmisc.enableAllArmor" : "ttmisc.disableAllArmor"), true);
    }

    /** One press — one cycle request; the server owns the NBT and answers on the action bar. */
    private void handleWingModeKey(EntityPlayer player) {
        if (!this.keyK.isKeyDown()) {
            this.keyPressedK = false;
            return;
        }
        if (this.keyPressedK) {
            return;
        }
        this.keyPressedK = true;
        if (player == null) {
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            return;
        }
        boolean enchanted = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(
                thaumcraft.common.config.Config.enchSoaring, chest) > 0
                || net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(
                thaumcraft.common.config.Config.enchAscension, chest) > 0;
        if (enchanted) {
            PacketHandler.INSTANCE.sendToServer(
                    new thaumcraft.common.lib.network.misc.PacketSoaringMode());
        }
    }

    private static boolean wearsAwakenedIchorcloth(EntityPlayer player) {
        for (net.minecraft.inventory.EntityEquipmentSlot slot
                : new net.minecraft.inventory.EntityEquipmentSlot[]{
                net.minecraft.inventory.EntityEquipmentSlot.HEAD,
                net.minecraft.inventory.EntityEquipmentSlot.CHEST,
                net.minecraft.inventory.EntityEquipmentSlot.LEGS,
                net.minecraft.inventory.EntityEquipmentSlot.FEET}) {
            ItemStack worn = player.getItemStackFromSlot(slot);
            if (!worn.isEmpty() && worn.getItem()
                    instanceof thaumcraft.common.items.tinkerer.kami.armor.ItemIchorclothArmorAdv) {
                return true;
            }
        }
        return false;
    }

    private void handleFocusKey(EntityPlayer player) {
        if (this.keyF.isKeyDown()) {
            boolean firstPress = !this.keyPressedF;
            if (firstPress) {
                lastPressF = System.currentTimeMillis();
                radialLock = false;
            }
            if (player != null) {
                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting && !ItemWandCasting.isSceptre(held)) {
                    if (player.isSneaking()) {
                        if (firstPress) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(player, "REMOVE"));
                        }
                    } else if (!radialLock) {
                        radialActive = true;
                    }
                } else if (!held.isEmpty() && held.getItem() instanceof ItemGolemBell && firstPress) {
                    PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(player, 0));
                }
            }
            this.keyPressedF = true;
        } else {
            radialActive = false;
            if (this.keyPressedF) {
                lastPressF = System.currentTimeMillis();
            }
            this.keyPressedF = false;
        }
    }

    private void handleHoverKey(EntityPlayer player) {
        if (this.keyH.isKeyDown()) {
            if (player != null && !this.keyPressedH) {
                lastPressH = System.currentTimeMillis();
                ItemStack harness = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!harness.isEmpty() && harness.getItem() instanceof ItemHoverHarness) {
                    Hover.toggleHover(player, player.getEntityId(), harness);
                }
            }
            this.keyPressedH = true;
        } else {
            if (this.keyPressedH) {
                lastPressH = System.currentTimeMillis();
            }
            this.keyPressedH = false;
        }
    }

    private void handleMiscKey(EntityPlayer player) {
        if (this.keyG.isKeyDown()) {
            boolean firstPress = !this.keyPressedG;
            if (firstPress) {
                lastPressG = System.currentTimeMillis();
                radialLock = false;
            }
            if (player != null) {
                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting && !ItemWandCasting.isSceptre(held)) {
                    if (player.isSneaking()) {
                        if (firstPress) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(player, "REMOVE"));
                        }
                    } else if (!radialLock) {
                        radialActive = true;
                    }
                }
            }
            this.keyPressedG = true;
        } else {
            radialActive = false;
            if (this.keyPressedG) {
                lastPressG = System.currentTimeMillis();
            }
            this.keyPressedG = false;
        }
    }

    private void releaseAllKeys() {
        long now = System.currentTimeMillis();
        if (this.keyPressedF) {
            lastPressF = now;
        }
        if (this.keyPressedH) {
            lastPressH = now;
        }
        if (this.keyPressedG) {
            lastPressG = now;
        }
        radialActive = false;
        this.keyPressedF = false;
        this.keyPressedH = false;
        this.keyPressedG = false;
    }
}
