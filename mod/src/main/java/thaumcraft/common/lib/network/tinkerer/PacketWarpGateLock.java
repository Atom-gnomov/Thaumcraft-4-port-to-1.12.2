package thaumcraft.common.lib.network.tinkerer;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * Carries the lock toggle from the gate's screen to the server — the port of
 * Thaumic Tinkerer's {@code PacketWarpGateButton}.
 */
public class PacketWarpGateLock extends PacketBase {

    private BlockPos pos;
    private boolean locked;

    public PacketWarpGateLock() {
    }

    public PacketWarpGateLock(BlockPos pos, boolean locked) {
        this.pos = pos;
        this.locked = locked;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeBoolean(this.locked);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.locked = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getDistanceSq(this.pos) > 64.0D) {
                return;
            }
            TileEntity tile = player.world.getTileEntity(this.pos);
            if (tile instanceof TileWarpGate) {
                ((TileWarpGate) tile).locked = this.locked;
                tile.markDirty();
            }
        });
        return null;
    }
}
