package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.endgame.SoaringHandler;
import thaumcraft.common.lib.network.PacketBase;

/**
 * Server → client: whether there is vis left to climb with.
 *
 * <p>Movement is client-authoritative in 1.12, so a client that decides for
 * itself whether the boost is affordable simply flies for free — which is
 * what the owner's first flight test caught. The server, the only party that
 * actually pays, reports the tank's state on edges; the client's boost checks
 * the last word received. End Legacy module.</p>
 */
public class PacketSoaringFuel extends PacketBase {

    private boolean fuelOk;

    public PacketSoaringFuel() {
    }

    public PacketSoaringFuel(boolean fuelOk) {
        this.fuelOk = fuelOk;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fuelOk = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.fuelOk);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        boolean ok = this.fuelOk;
        thaumcraft.common.Thaumcraft.proxy.scheduleClientTask(() -> {
            net.minecraft.entity.player.EntityPlayer self =
                    thaumcraft.common.Thaumcraft.proxy.getClientPlayer();
            if (self != null) {
                SoaringHandler.setFuelOk(self.getEntityId(), ok);
            }
        });
        return null;
    }
}
