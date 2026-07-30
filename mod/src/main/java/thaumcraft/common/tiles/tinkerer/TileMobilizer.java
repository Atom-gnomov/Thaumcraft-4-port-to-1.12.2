package thaumcraft.common.tiles.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Levitational Locomotive — ported from Thaumic Tinkerer's
 * {@code TileEntityMobilizer} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Runs back and forth along the track two paired relays define, carrying
 * whatever stands on it. Every hundred ticks it checks the way ahead and turns
 * round at the end; one tick later it takes a step. Redstone holds it.</p>
 *
 * <p>Upstream had three ways to move the passenger: plain blocks, Applied
 * Energistics' mover when that mod is present, and its fallback for movable or
 * vanilla tile entities. This port carries the first and the third; the AE
 * branch is left out because the mod is not here, and upstream's own code only
 * takes it when AE answers.</p>
 */
public class TileMobilizer extends TileTinkerer implements ITickable {

    /** The original's cadence: look ahead on 0, step on 1, every 100 ticks. */
    private static final int PERIOD = 100;

    public boolean linked;
    public int firstRelayX;
    public int firstRelayZ;
    public int secondRelayX;
    public int secondRelayZ;
    public EnumFacing movementDirection = EnumFacing.NORTH;
    /** Set when the block is being broken, so a ghost tile stops working. */
    public boolean dead;

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        tag.setBoolean("Linked", this.linked);
        tag.setInteger("FirstRelayX", this.firstRelayX);
        tag.setInteger("FirstRelayZ", this.firstRelayZ);
        tag.setInteger("SecondRelayX", this.secondRelayX);
        tag.setInteger("SecondRelayZ", this.secondRelayZ);
        tag.setInteger("Direction", this.movementDirection.ordinal());
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        super.readCustomNBT(tag);
        this.linked = tag.getBoolean("Linked");
        this.firstRelayX = tag.getInteger("FirstRelayX");
        this.firstRelayZ = tag.getInteger("FirstRelayZ");
        this.secondRelayX = tag.getInteger("SecondRelayX");
        this.secondRelayZ = tag.getInteger("SecondRelayZ");
        this.movementDirection = EnumFacing.values()[
                Math.max(0, Math.min(EnumFacing.values().length - 1, tag.getInteger("Direction")))];
    }

    /** The link dies with either relay, or if the pair no longer agrees. */
    public void verifyRelay() {
        TileEntity tile = this.world.getTileEntity(
                new BlockPos(this.firstRelayX, this.pos.getY(), this.firstRelayZ));
        if (tile instanceof TileMobilizerRelay) {
            ((TileMobilizerRelay) tile).verifyPartner();
        }
        if (!(this.linked && tile instanceof TileMobilizerRelay
                && ((TileMobilizerRelay) tile).partnerX == this.secondRelayX
                && ((TileMobilizerRelay) tile).partnerZ == this.secondRelayZ)) {
            this.linked = false;
        }
    }

    @Override
    public void update() {
        if (this.dead) {
            return;
        }
        verifyRelay();
        if (!this.linked || this.world.isBlockPowered(this.pos)) {
            return;
        }
        long phase = this.world.getTotalWorldTime() % PERIOD;
        if (phase == 0) {
            turnIfBlocked();
        } else if (phase == 1) {
            step();
        }
    }

    private BlockPos target() {
        return this.pos.offset(this.movementDirection);
    }

    private void turnIfBlocked() {
        BlockPos ahead = target();
        if (!this.world.isAirBlock(ahead) || !this.world.isAirBlock(ahead.up())) {
            this.movementDirection = this.movementDirection.getOpposite();
        }
    }

    private void step() {
        if (this.world.isRemote) {
            return;
        }
        // A tile left behind by a broken block must not drag anything along.
        if (this.world.getBlockState(this.pos).getBlock() != ConfigBlocks.blockMobilizer) {
            return;
        }
        BlockPos ahead = target();
        if (!this.world.isAirBlock(ahead)
                || !(this.world.isAirBlock(this.pos.up()) || this.world.isAirBlock(ahead.up()))) {
            return;
        }

        BlockPos seat = this.pos.up();
        BlockPos destination = ahead.up();
        IBlockState passengerState = this.world.getBlockState(seat);
        Block passengerBlock = passengerState.getBlock();
        TileEntity passenger = this.world.getTileEntity(seat);

        // Upstream drops stone in first so a passenger cannot fall through the
        // gap while the locomotive is mid-move. Never sent to clients.
        this.world.setBlockState(ahead, Blocks.STONE.getDefaultState(), 0);

        if (this.world.isAirBlock(seat) || passengerBlock.canPlaceBlockAt(this.world, destination)) {
            if (passenger == null) {
                movePlainBlock(passengerState, passengerBlock, seat, destination);
            } else {
                moveTileEntity(passengerState, passenger, seat, destination);
            }
        }
        moveSelf(ahead);
    }

    private void movePlainBlock(IBlockState state, Block block, BlockPos seat, BlockPos destination) {
        if (block == Blocks.BEDROCK) {
            return;
        }
        this.world.setBlockState(destination, state, 3);
        if (block != Blocks.AIR && block != Blocks.PISTON_HEAD) {
            this.world.setBlockState(seat, Blocks.AIR.getDefaultState(), 2);
        }
    }

    /**
     * Upstream's fallback path, used whenever Applied Energistics is absent —
     * which is always here. It carries the tile across by hand rather than
     * letting the world drop and recreate it, so its contents survive.
     */
    private void moveTileEntity(IBlockState state, TileEntity passenger, BlockPos seat, BlockPos destination) {
        NBTTagCompound saved = passenger.writeToNBT(new NBTTagCompound());
        this.world.setBlockState(destination, state, 3);
        passenger.invalidate();
        this.world.setBlockToAir(seat);

        TileEntity moved = TileEntity.create(this.world, saved);
        if (moved != null) {
            moved.setPos(destination);
            this.world.setTileEntity(destination, moved);
            this.world.notifyBlockUpdate(destination, state, state, 3);
        }
    }

    private void moveSelf(BlockPos ahead) {
        NBTTagCompound saved = writeToNBT(new NBTTagCompound());
        invalidate();
        this.world.removeTileEntity(this.pos);
        this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), 2);
        this.world.setBlockState(ahead, ConfigBlocks.blockMobilizer.getDefaultState(), 3);

        TileEntity moved = this.world.getTileEntity(ahead);
        if (moved instanceof TileMobilizer) {
            saved.setInteger("x", ahead.getX());
            saved.setInteger("y", ahead.getY());
            saved.setInteger("z", ahead.getZ());
            moved.readFromNBT(saved);
        }
    }
}
