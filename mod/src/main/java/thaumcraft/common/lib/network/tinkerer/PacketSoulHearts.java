package thaumcraft.common.lib.network.tinkerer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.network.PacketBase;

/**
 * Tells the client how many soul hearts it has — the port of Thaumic
 * Tinkerer's {@code PacketSoulHearts}. The pool lives in server-side NBT, so
 * without this the HUD would have nothing to draw.
 */
public class PacketSoulHearts extends PacketBase {

    private int hearts;

    public PacketSoulHearts() {
    }

    public PacketSoulHearts(int hearts) {
        this.hearts = hearts;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.hearts);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.hearts = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        thaumcraft.common.Thaumcraft.proxy.scheduleClientTask(() ->
                thaumcraft.client.lib.tinkerer.SoulHeartClientHandler.clientPlayerHP = this.hearts);
        return null;
    }
}
