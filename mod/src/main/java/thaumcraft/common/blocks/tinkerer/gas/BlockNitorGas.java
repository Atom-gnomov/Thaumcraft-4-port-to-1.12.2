package thaumcraft.common.blocks.tinkerer.gas;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;

/**
 * The light the Hyperenergetic Nitor leaves behind — ported from Thaumic
 * Tinkerer's {@code BlockNitorGas} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Unlike the other gases this one keeps checking whether it is still wanted:
 * every tick it looks for a player nearby who is carrying the nitor or wearing
 * the Leggings of the Burning Mantle, and vanishes if there is none. The search
 * radius is six when the gas came from the leggings and one otherwise, which is
 * what its metadata records.</p>
 */
public class BlockNitorGas extends BlockGas {

    /** Spread 1 means the leggings laid it: brighter, and it looks further. */
    private static final int FROM_LEGGINGS = 1;

    public BlockNitorGas() {
        super();
    }

    @Override
    public int tickRate(World world) {
        return world.provider.getDimension() == -1 ? 60 : 20;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        int dist = state.getValue(SPREAD) == FROM_LEGGINGS ? 6 : 1;
        AxisAlignedBB box = new AxisAlignedBB(
                pos.getX() - dist, pos.getY() - dist, pos.getZ() - dist,
                pos.getX() + dist, pos.getY() + dist, pos.getZ() + dist);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, box);

        boolean wanted = false;
        for (EntityPlayer player : players) {
            if (carriesNitor(player) || wearsBurningMantle(player)) {
                wanted = true;
                break;
            }
        }
        if (!wanted) {
            world.setBlockToAir(pos);
        }
        world.scheduleUpdate(pos, this, tickRate(world));
    }

    private static boolean carriesNitor(EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == ConfigItems.itemBrightNitor) {
                return true;
            }
        }
        return false;
    }

    private static boolean wearsBurningMantle(EntityPlayer player) {
        ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        return ConfigItems.itemIchorclothLegsGem != null
                && !legs.isEmpty() && legs.getItem() == ConfigItems.itemIchorclothLegsGem;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(SPREAD) == FROM_LEGGINGS ? 15 : 12;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            world.scheduleUpdate(pos, this, tickRate(world));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextFloat() < 0.03F) {
            Thaumcraft.proxy.sparkle(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    1.0F, 4, rand.nextFloat() / 2.0F);
        }
    }
}
