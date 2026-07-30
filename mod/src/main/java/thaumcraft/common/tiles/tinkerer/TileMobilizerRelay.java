package thaumcraft.common.tiles.tinkerer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;

/**
 * Levitational Locomotive Relay — ported from Thaumic Tinkerer's
 * {@code TileEntityRelay} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Relays pair up along a straight line within thirty-two blocks and the
 * track between them is what a Locomotive runs on. Every two seconds a relay
 * walks that line, hands any Locomotive standing on it the pair's coordinates,
 * and points it along the axis they share.</p>
 */
public class TileMobilizerRelay extends TileThaumcraft implements ITickable {

    /** How far a relay looks for its partner, in blocks. */
    private static final int SEARCH = 32;
    /** Partner checks and track sweeps both run every 40 ticks. */
    private static final int CADENCE = 40;

    public boolean hasPartner;
    public int partnerX;
    public int partnerZ;

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        tag.setBoolean("HasPartner", this.hasPartner);
        tag.setInteger("PartnerX", this.partnerX);
        tag.setInteger("PartnerZ", this.partnerZ);
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        super.readCustomNBT(tag);
        this.hasPartner = tag.getBoolean("HasPartner");
        this.partnerX = tag.getInteger("PartnerX");
        this.partnerZ = tag.getInteger("PartnerZ");
    }

    /** A pairing only counts while the other end still points back. */
    public void verifyPartner() {
        TileEntity other = this.world.getTileEntity(new BlockPos(this.partnerX, this.pos.getY(), this.partnerZ));
        if (!(this.hasPartner && other instanceof TileMobilizerRelay
                && ((TileMobilizerRelay) other).partnerX == this.pos.getX()
                && ((TileMobilizerRelay) other).partnerZ == this.pos.getZ())) {
            this.hasPartner = false;
        }
    }

    @Override
    public void update() {
        verifyPartner();

        if (this.hasPartner && this.world.isRemote) {
            drawTrack();
        }
        if (this.world.getTotalWorldTime() % CADENCE == 0) {
            checkForPartner();
            if (this.hasPartner) {
                claimLocomotives();
            }
        }
    }

    /** Sparkles crawling from this relay towards its partner. */
    private void drawTrack() {
        long time = this.world.getTotalWorldTime() % 20;
        for (BlockPos step : track()) {
            float xInc = this.partnerX == this.pos.getX() ? 0.0F
                    : (float) Math.copySign(0.05D, this.partnerX - this.pos.getX()) * time;
            float zInc = this.partnerZ == this.pos.getZ() ? 0.0F
                    : (float) Math.copySign(0.05D, this.partnerZ - this.pos.getZ()) * time;
            Thaumcraft.proxy.sparkle(step.getX() + 0.5F + xInc, this.pos.getY() + 0.5F,
                    step.getZ() + 0.5F + zInc,
                    this.pos.getX() < this.partnerX || this.pos.getZ() > this.partnerX ? 2 : 14);
        }
    }

    private void claimLocomotives() {
        for (BlockPos step : track()) {
            TileEntity tile = this.world.getTileEntity(step);
            if (!(tile instanceof TileMobilizer)) {
                continue;
            }
            TileMobilizer locomotive = (TileMobilizer) tile;
            locomotive.verifyRelay();
            if (locomotive.linked) {
                continue;
            }
            locomotive.firstRelayX = this.pos.getX();
            locomotive.firstRelayZ = this.pos.getZ();
            locomotive.secondRelayX = this.partnerX;
            locomotive.secondRelayZ = this.partnerZ;
            locomotive.linked = true;
            locomotive.movementDirection =
                    this.pos.getX() != this.partnerX ? EnumFacing.EAST : EnumFacing.NORTH;
        }
    }

    /** Every block between this relay and its partner, this one included. */
    private java.util.List<BlockPos> track() {
        java.util.List<BlockPos> line = new java.util.ArrayList<>();
        int y = this.pos.getY();
        if (this.pos.getX() == this.partnerX) {
            int step = this.partnerZ > this.pos.getZ() ? 1 : -1;
            for (int z = this.pos.getZ(); z != this.partnerZ; z += step) {
                line.add(new BlockPos(this.pos.getX(), y, z));
            }
        } else if (this.pos.getZ() == this.partnerZ) {
            int step = this.partnerX > this.pos.getX() ? 1 : -1;
            for (int x = this.pos.getX(); x != this.partnerX; x += step) {
                line.add(new BlockPos(x, y, this.pos.getZ()));
            }
        }
        return line;
    }

    /**
     * Looks along both axes for another unpaired relay. Upstream's loop runs
     * from -32 to +32 and skips zero, so a relay never finds itself.
     */
    public void checkForPartner() {
        if (this.hasPartner) {
            return;
        }
        for (int i = -SEARCH; i < SEARCH; i++) {
            if (i == 0) {
                continue;
            }
            pairWith(this.world.getTileEntity(this.pos.add(0, 0, i)));
            pairWith(this.world.getTileEntity(this.pos.add(i, 0, 0)));
        }
    }

    private void pairWith(TileEntity other) {
        if (!(other instanceof TileMobilizerRelay) || other == this) {
            return;
        }
        TileMobilizerRelay relay = (TileMobilizerRelay) other;
        if (relay.hasPartner || this.hasPartner) {
            return;
        }
        relay.partnerX = this.pos.getX();
        relay.partnerZ = this.pos.getZ();
        relay.hasPartner = true;
        this.partnerX = relay.getPos().getX();
        this.partnerZ = relay.getPos().getZ();
        this.hasPartner = true;
    }
}
