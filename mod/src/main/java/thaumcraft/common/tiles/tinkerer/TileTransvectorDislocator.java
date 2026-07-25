package thaumcraft.common.tiles.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.blocks.tinkerer.BlockTransvectorDislocator;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Transvector Dislocator — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>On a redstone pulse it swaps the block it faces with the block it is
 * linked to, up to {@value #MAX_DISTANCE} away, carrying tile entities and any
 * creatures standing in either spot along with them. As in the original a pulse
 * arriving during the {@value #COOLDOWN}-tick cooldown is remembered and fires
 * when the cooldown expires, so rapid clock signals do not get dropped.</p>
 */
public class TileTransvectorDislocator extends TileTransvector implements ITickable {

    /** Per-axis reach, as in the original (LibFeatures.DISLOCATOR_DISTANCE). */
    public static final int MAX_DISTANCE = 16;
    private static final int COOLDOWN = 10;

    private int cooldown;
    private boolean pulseStored;

    @Override
    public int getMaxDistance() {
        return MAX_DISTANCE;
    }

    /** A dislocator may move a plain block, so the far end needs no tile. */
    @Override
    protected boolean requiresTileAtLink() {
        return false;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
        }
        if (cooldown == 0 && pulseStored) {
            pulseStored = false;
            swap();
        }
    }

    /** Called by the block on a rising redstone edge. */
    public void onRedstonePulse() {
        if (cooldown > 0) {
            pulseStored = true;
            return;
        }
        swap();
    }

    private void swap() {
        BlockPos target = getTarget();
        if (target == null || !world.isBlockLoaded(target)) {
            return;
        }
        BlockPos front = pos.offset(getFacing());
        if (front.equals(target) || front.equals(pos)) {
            return;
        }
        if (!canMove(front) || !canMove(target)) {
            return;
        }

        swapBlocks(front, target);
        swapEntities(front, target);
        cooldown = COOLDOWN;
    }

    /**
     * Whether a position may take part in a swap. Mirrors the original's
     * refusals — aura nodes, anything on the portable-hole blacklist, and
     * unbreakable blocks such as bedrock or portals.
     */
    private boolean canMove(BlockPos at) {
        IBlockState state = world.getBlockState(at);
        Block block = state.getBlock();
        if (block == Blocks.AIR) {
            return true;
        }
        if (block == ConfigBlocks.blockAiry && block.getMetaFromState(state) == 0) {
            return false;
        }
        if (ThaumcraftApi.portableHoleBlackList.contains(block)) {
            return false;
        }
        return state.getBlockHardness(world, at) >= 0.0F;
    }

    private void swapBlocks(BlockPos a, BlockPos b) {
        IBlockState stateA = world.getBlockState(a);
        IBlockState stateB = world.getBlockState(b);
        NBTTagCompound nbtA = detachTile(a);
        NBTTagCompound nbtB = detachTile(b);

        world.setBlockState(a, stateB, 3);
        world.setBlockState(b, stateA, 3);

        attachTile(a, nbtB);
        attachTile(b, nbtA);
    }

    /** Lifts the tile at {@code at} out of the world, returning its data. */
    @Nullable
    private NBTTagCompound detachTile(BlockPos at) {
        TileEntity tile = world.getTileEntity(at);
        if (tile == null) {
            return null;
        }
        NBTTagCompound nbt = tile.writeToNBT(new NBTTagCompound());
        // Drop it first so vanilla does not invalidate the tile we are moving.
        world.removeTileEntity(at);
        return nbt;
    }

    private void attachTile(BlockPos at, @Nullable NBTTagCompound nbt) {
        if (nbt == null) {
            return;
        }
        nbt.setInteger("x", at.getX());
        nbt.setInteger("y", at.getY());
        nbt.setInteger("z", at.getZ());
        TileEntity tile = TileEntity.create(world, nbt);
        if (tile != null) {
            world.setTileEntity(at, tile);
            tile.markDirty();
        }
    }

    private void swapEntities(BlockPos a, BlockPos b) {
        List<Entity> atA = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(a));
        List<Entity> atB = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(b));
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();
        int dz = b.getZ() - a.getZ();
        for (Entity entity : atA) {
            move(entity, dx, dy, dz);
        }
        for (Entity entity : atB) {
            move(entity, -dx, -dy, -dz);
        }
    }

    private static void move(Entity entity, int dx, int dy, int dz) {
        entity.setPositionAndUpdate(entity.posX + dx, entity.posY + dy, entity.posZ + dz);
    }

    private EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockTransvectorDislocator
                ? state.getValue(BlockTransvectorDislocator.FACING)
                : EnumFacing.UP;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        cooldown = nbt.getInteger("cooldown");
        pulseStored = nbt.getBoolean("pulseStored");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setInteger("cooldown", cooldown);
        nbt.setBoolean("pulseStored", pulseStored);
    }
}
