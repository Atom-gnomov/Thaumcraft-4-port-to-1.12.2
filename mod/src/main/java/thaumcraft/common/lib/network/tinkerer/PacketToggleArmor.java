package thaumcraft.common.lib.network.tinkerer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.tinkerer.KamiArmorHandler;

/**
 * Carries the U-key toggle to the server — the port of Thaumic Tinkerer's
 * {@code PacketToggleArmor}.
 *
 * <p>The key is pressed on the client, but the armour's effects are applied
 * server-side, so the flag has to cross. The client keeps its own copy for the
 * tick code that runs on both sides; this is what keeps the server's copy in
 * step.</p>
 */
public class PacketToggleArmor extends PacketBase {

    private boolean enabled;

    public PacketToggleArmor() {
    }

    public PacketToggleArmor(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.enabled);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        if (player != null) {
            player.getServer().addScheduledTask(() ->
                    KamiArmorHandler.setArmorStatus(player, this.enabled));
        }
        return null;
    }
}
