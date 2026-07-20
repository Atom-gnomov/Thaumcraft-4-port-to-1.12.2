
package thaumcraft.common.lib.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils {

    public static boolean isBlockBreakable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().getBlockHardness(state, world, pos) >= 0.0f;
    }

    public static void setBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 3);
    }

    public static ItemStack createStackedBlock(Block block, int meta) {
        if (block == null) return ItemStack.EMPTY;
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(item, 1, meta);
    }

    public static boolean isBlockExposed(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) return true;
        }
        return false;
    }

    public static void dropBlockAsItem(World world, int x, int y, int z, ItemStack stack, Block block) {
        if (world == null || world.isRemote || stack == null || stack.isEmpty()) return;
        Block.spawnAsEntity(world, new BlockPos(x, y, z), stack);
    }

    public static void dropBlockAsItemWithChance(World world, Block block, int x, int y, int z, int meta, float chance, int fortune, EntityPlayer player) {
        if (world == null || world.isRemote || block == null) return;
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        NonNullList<ItemStack> drops = NonNullList.create();
        block.getDrops(drops, world, pos, state, fortune);
        float actualChance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, chance, false, player);
        for (ItemStack drop : drops) {
            if (drop.isEmpty() || world.rand.nextFloat() > actualChance) continue;
            EntityItem entity = new EntityItem(world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, drop);
            entity.setDefaultPickupDelay();
            world.spawnEntity(entity);
        }
    }

    /** Count how many sides of a block are exposed to air. */
    public static int countExposedSides(World world, int x, int y, int z) {
        int count = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) count++;
        }
        return count;
    }

    /** Check if a block has at least `count` adjacent blocks of the given type. */
    public static boolean isBlockAdjacentToAtleast(IBlockAccess world, int x, int y, int z, Block block, int maxMeta, int count) {
        int found = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == block) {
                found++;
                if (found >= count) return true;
            }
        }
        return false;
    }

    /** Check if a block position is adjacent to any solid (full-cube) block. */
    public static boolean isAdjacentToSolidBlock(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos sidePos = pos.offset(facing);
            IBlockState state = world.getBlockState(sidePos);
            if (state.isSideSolid(world, sidePos, facing.getOpposite())) return true;
        }
        return false;
    }

    /** Break the furthest connected block of the given type from the start position. Used by lumber/harvest golems. */
    public static boolean breakFurthestBlock(World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        return breakFurthestBlock(world, pos, player, false, 0);
    }

    /**
     * TC4 tree-felling: greedily walk connected logs of the same block away from
     * the origin (±2 cube per step, y scanned top-down; walk limits ±24/±48/±24)
     * and harvest the FURTHEST one, so trees fall top-down. Schedules random
     * updates in a ±3 cube around the broken log so leaves start decaying.
     */
    public static boolean breakFurthestBlock(World world, BlockPos origin, EntityPlayer player, boolean followItem, int color) {
        Block block = world.getBlockState(origin).getBlock();
        BlockPos last = origin;
        double lastDistance = 0.0;

        boolean moved = true;
        while (moved) {
            moved = false;
            search:
            for (int xx = -2; xx <= 2; ++xx) {
                for (int yy = 2; yy >= -2; --yy) {
                    for (int zz = -2; zz <= 2; ++zz) {
                        BlockPos candidate = last.add(xx, yy, zz);
                        if (Math.abs(candidate.getX() - origin.getX()) > 24
                                || Math.abs(candidate.getY() - origin.getY()) > 48
                                || Math.abs(candidate.getZ() - origin.getZ()) > 24) {
                            continue;
                        }
                        IBlockState cState = world.getBlockState(candidate);
                        if (cState.getBlock() != block
                                || !Utils.isWoodLog(world, candidate)
                                || cState.getBlockHardness(world, candidate) < 0.0f) {
                            continue;
                        }
                        double d = candidate.distanceSq(origin);
                        if (d > lastDistance) {
                            lastDistance = d;
                            last = candidate;
                            moved = true;
                            break search;
                        }
                    }
                }
            }
        }

        boolean worked = harvestBlock(world, player, last, followItem, color);
        IBlockState originState = world.getBlockState(origin);
        world.notifyBlockUpdate(origin, originState, originState, 3);
        if (worked) {
            for (int xx = -3; xx <= 3; ++xx) {
                for (int yy = -3; yy <= 3; ++yy) {
                    for (int zz = -3; zz <= 3; ++zz) {
                        BlockPos around = last.add(xx, yy, zz);
                        world.scheduleUpdate(around, world.getBlockState(around).getBlock(),
                                150 + world.rand.nextInt(150));
                    }
                }
            }
        }
        return worked;
    }

    /** TC4 harvest: break FX + removedByPlayer + drops; optionally converts fresh drops into items flying to the player. */
    public static boolean harvestBlock(World world, EntityPlayer player, BlockPos pos, boolean followItem, int color) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlockHardness(world, pos) < 0.0f) {
            return false;
        }
        world.playEvent(2001, pos, Block.getStateId(state));
        if (player != null && player.capabilities.isCreativeMode) {
            return removeBlock(world, pos, player);
        }
        boolean canHarvest = player != null && state.getBlock().canHarvestBlock(world, pos, player);
        net.minecraft.tileentity.TileEntity tile = world.getTileEntity(pos);
        boolean removed = removeBlock(world, pos, player);
        if (removed && canHarvest) {
            state.getBlock().harvestBlock(world, player, pos, state, tile, player.getHeldItemMainhand());
            if (followItem) {
                java.util.List<EntityItem> drops = thaumcraft.common.lib.utils.EntityUtils.getEntitiesInRange(
                        world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, player, EntityItem.class, 2.0);
                if (drops != null) {
                    for (EntityItem drop : drops) {
                        if (drop.isDead || drop.ticksExisted != 0
                                || drop instanceof thaumcraft.common.entities.EntityFollowingItem) {
                            continue;
                        }
                        thaumcraft.common.entities.EntityFollowingItem following =
                                new thaumcraft.common.entities.EntityFollowingItem(
                                        world, drop.posX, drop.posY, drop.posZ,
                                        drop.getItem().copy(), player, color);
                        following.motionX = drop.motionX;
                        following.motionY = drop.motionY;
                        following.motionZ = drop.motionZ;
                        world.spawnEntity(following);
                        drop.setDead();
                    }
                }
            }
        }
        return removed;
    }

    private static boolean removeBlock(World world, BlockPos pos, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        boolean removed = state.getBlock().removedByPlayer(state, world, pos, player, true);
        if (removed) {
            state.getBlock().onPlayerDestroy(world, pos, state);
        }
        return removed;
    }
}
