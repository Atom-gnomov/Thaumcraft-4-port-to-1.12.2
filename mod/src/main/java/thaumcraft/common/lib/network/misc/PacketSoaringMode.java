package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.endgame.SoaringHandler;
import thaumcraft.common.lib.network.PacketBase;

/**
 * Client → server: cycle the wing mode on the worn chestplate.
 *
 * <p>The server owns the item NBT, validates what the chestplate can actually
 * do (FLIGHT needs Ascension), writes the new mode and answers with the
 * action-bar line. End Legacy module — the owner's fix for flight engaging
 * while simply walking: nothing flies unless the wings are switched to.</p>
 */
public class PacketSoaringMode extends PacketBase {

    public PacketSoaringMode() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (chest.isEmpty()) {
                return;
            }
            boolean ascension = EnchantmentHelper.getEnchantmentLevel(Config.enchAscension, chest) > 0;
            boolean soaring = EnchantmentHelper.getEnchantmentLevel(Config.enchSoaring, chest) > 0;
            if (!ascension && !soaring) {
                return;
            }
            int mode = SoaringHandler.cycleMode(SoaringHandler.getMode(chest), ascension);
            SoaringHandler.setMode(chest, mode);
            String key = mode == SoaringHandler.MODE_OFF ? "endlegacy.wings.off"
                    : mode == SoaringHandler.MODE_GLIDE ? "endlegacy.wings.glide"
                    : "endlegacy.wings.flight";
            player.sendStatusMessage(new TextComponentTranslation(key), true);
        });
        return null;
    }
}
