package thaumcraft.common.tiles.tinkerer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import thaumcraft.common.Thaumcraft;

/**
 * Counts a {@link thaumcraft.common.blocks.tinkerer.BlockForcefield} down —
 * ported from Thaumic Tinkerer's {@code TileForcefield} (pixlepix / nekosune,
 * originally Vazkii).
 *
 * <p>Sixty ticks, sparkling every one of them, then the wall goes away.</p>
 */
public class TileForcefield extends TileTinkerer implements ITickable {

    private static final String TAG_TICKS = "ticks";

    /** The original's lifetime: three seconds. */
    private int ticks = 60;

    @Override
    public void update() {
        if (this.ticks < 0) {
            this.world.setBlockToAir(this.pos);
        }
        this.ticks--;
        Thaumcraft.proxy.blockSparkle(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 255, 1);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        tag.setInteger(TAG_TICKS, this.ticks);
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        super.readCustomNBT(tag);
        if (tag.hasKey(TAG_TICKS)) {
            this.ticks = tag.getInteger(TAG_TICKS);
        }
    }
}
