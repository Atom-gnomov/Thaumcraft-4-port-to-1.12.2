package thaumcraft.common.lib.network.tinkerer;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * Asks the server to send the player through a gate's slot — the port of
 * Thaumic Tinkerer's {@code PacketWarpGateTeleport}.
 *
 * <p>The gate does the checking, not this: the slot may be empty, its pearl
 * unattuned, or the far gate locked, and {@code teleportPlayer} handles each.
 * The only thing added here is a range check, so a client cannot drive a gate
 * it is nowhere near.</p>
 */
public class PacketWarpGateTeleport extends PacketBase {

    private BlockPos pos;
    private int index;

    public PacketWarpGateTeleport() {
    }

    public PacketWarpGateTeleport(BlockPos pos, int index) {
        this.pos = pos;
        this.index = index;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.index);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.index = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getDistanceSq(this.pos) > 64.0D) {
                return;
            }
            TileEntity tile = player.world.getTileEntity(this.pos);
            if (tile instanceof TileWarpGate) {
                ((TileWarpGate) tile).teleportPlayer(player, this.index);
            }
        });
        return null;
    }
}
