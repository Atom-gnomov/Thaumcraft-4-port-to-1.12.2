package thaumcraft.common.tiles.tinkerer;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.blocks.tinkerer.BlockMagnet;

/**
 * Magnet tile — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2.
 *
 * <p>Faithful to the original behaviour: the block only works while it receives
 * redstone power, the reach is {@code signal / 2} blocks rounded down — a lever
 * beside it reaches seven, not seven and a half — items are moved at a fixed
 * 0.25 speed, and items already within one block of the centre are left alone
 * so an attracting magnet does not jitter its own pile.</p>
 *
 * <p>The original's research text says 7.5 blocks. The code does not: that
 * division is between two ints. The code is what runs.</p>
 */
public class TileMagnet extends TileTinkerer implements ITickable {

    private static final double SPEED = 0.25D;

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        int redstone = getIncomingPower();
        if (redstone <= 0) {
            return;
        }

        boolean pulling = isPulling();
        // Integer division upstream: signal 7 reaches 3 blocks, not 3.5.
        double range = redstone / 2;
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.5D;
        double cz = pos.getZ() + 0.5D;

        AxisAlignedBB box = new AxisAlignedBB(
                cx - range, pos.getY(), cz - range,
                cx + range, pos.getY() + 0.5D + range, cz + range);
        List<? extends Entity> items = world.getEntitiesWithinAABB(getTargetClass(), box);

        for (Entity item : items) {
            if (item == null || item.isDead || !isTarget(item)) {
                continue;
            }
            double dx = cx - item.posX;
            double dy = cy - item.posY;
            double dz = cz - item.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            // Repelling always applies; attracting stops once the item is basically here.
            if (pulling && distSq <= 1.0D) {
                continue;
            }
            double dist = Math.sqrt(distSq);
            if (dist < 1.0E-4D) {
                continue;
            }
            double sign = pulling ? 1.0D : -1.0D;
            item.motionX = dx / dist * SPEED * sign;
            item.motionY = dy / dist * SPEED * sign;
            item.motionZ = dz / dist * SPEED * sign;
            item.velocityChanged = true;
        }
    }

    /**
     * Entity class this magnet gathers. The item magnet takes dropped items;
     * the mob variant overrides it, mirroring the original's entity selector.
     */
    protected Class<? extends Entity> getTargetClass() {
        return EntityItem.class;
    }

    /** Extra filtering on top of {@link #getTargetClass()}. */
    protected boolean isTarget(Entity entity) {
        return true;
    }

    /** Strongest redstone signal reaching any face, as the original sampled it. */
    private int getIncomingPower() {
        int power = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighbour = pos.offset(facing);
            power = Math.max(power, world.getRedstonePower(neighbour, facing));
        }
        return Math.max(power, world.getStrongPower(pos));
    }

    private boolean isPulling() {
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        return !(state.getBlock() instanceof BlockMagnet) || state.getValue(BlockMagnet.PULLING);
    }
}
