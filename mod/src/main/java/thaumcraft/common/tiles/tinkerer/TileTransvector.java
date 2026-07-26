package thaumcraft.common.tiles.tinkerer;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;

/**
 * Shared base for the transvector devices — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Holds the position this device is linked to and resolves it safely. As in
 * the original, a link that has drifted out of range is dropped rather than
 * honoured, and the range check is per-axis (Chebyshev), not euclidean.</p>
 *
 * <p>Unlike the original this never touches an unloaded chunk: resolving a link
 * into a chunk that is not loaded returns {@code null} instead of forcing the
 * chunk to load, so a linked device cannot keep terrain resident or fire
 * world-gen from a tick.</p>
 */
public abstract class TileTransvector extends TileThaumcraft {

    private static final String TAG_TARGET = "target";
    private static final String TAG_LINKED = "linked";

    private BlockPos target = BlockPos.ORIGIN;
    private boolean linked;

    /** Maximum per-axis distance a link may span. */
    public abstract int getMaxDistance();

    /** Whether a link is only meaningful when the far end has a tile entity. */
    protected boolean requiresTileAtLink() {
        return true;
    }

    public boolean isLinked() {
        return linked;
    }

    @Nullable
    public BlockPos getTarget() {
        return linked ? target : null;
    }

    /** Links this device to {@code pos}; returns false when it is out of range. */
    public boolean link(BlockPos pos) {
        if (!withinRange(pos)) {
            return false;
        }
        this.target = pos.toImmutable();
        this.linked = true;
        markDirty();
        return true;
    }

    public void unlink() {
        this.linked = false;
        this.target = BlockPos.ORIGIN;
        markDirty();
    }

    public boolean withinRange(BlockPos other) {
        int max = getMaxDistance();
        return Math.abs(other.getX() - pos.getX()) <= max
                && Math.abs(other.getY() - pos.getY()) <= max
                && Math.abs(other.getZ() - pos.getZ()) <= max;
    }

    /**
     * The tile at the far end, or {@code null} when there is no live link.
     * Drops the link if the target moved out of range; returns null without
     * dropping it when the chunk simply is not loaded right now.
     */
    @Nullable
    public final TileEntity getLinkedTile() {
        if (!linked || world == null) {
            return null;
        }
        if (!withinRange(target)) {
            unlink();
            return null;
        }
        if (!world.isBlockLoaded(target)) {
            return null;
        }
        TileEntity tile = world.getTileEntity(target);
        if (tile == null && requiresTileAtLink()) {
            return null;
        }
        // Never resolve to ourselves — that would recurse forever.
        return tile == this ? null : tile;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        linked = nbt.getBoolean(TAG_LINKED);
        target = linked ? BlockPos.fromLong(nbt.getLong(TAG_TARGET)) : BlockPos.ORIGIN;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setBoolean(TAG_LINKED, linked);
        nbt.setLong(TAG_TARGET, target.toLong());
    }
}
