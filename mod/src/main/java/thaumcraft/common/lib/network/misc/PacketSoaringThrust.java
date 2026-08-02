package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.endgame.SoaringHandler;
import thaumcraft.common.lib.network.PacketBase;

/**
 * Client → server: the jump key's held state, for Ascension's thrust.
 *
 * <p>The server cannot read the jump key itself — vanilla only syncs jump
 * <em>events</em>, not the held state — so the client reports edges: pressed,
 * released. Same shape as {@link PacketFlyToServer}, the hover harness's
 * precedent for exactly this problem.</p>
 *
 * <p>End Legacy module (new content, no 1.7.10 original).</p>
 */
public class PacketSoaringThrust extends PacketBase {

    private boolean thrusting;

    public PacketSoaringThrust() {
    }

    public PacketSoaringThrust(boolean thrusting) {
        this.thrusting = thrusting;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.thrusting = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.thrusting);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player ->
                SoaringHandler.setThrusting(player.getEntityId(), this.thrusting));
        return null;
    }
}
