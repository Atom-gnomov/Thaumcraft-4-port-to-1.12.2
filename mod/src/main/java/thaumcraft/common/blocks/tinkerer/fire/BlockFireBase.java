package thaumcraft.common.blocks.tinkerer.fire;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Shared behaviour of the six imbued fires — transcribed from Thaumic
 * Tinkerer's {@code BlockFireBase} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Each fire carries a table of blocks it transmutes and what it turns them
 * into. It only survives while something it can work on is next to it, dies in
 * the rain, and otherwise behaves as vanilla fire: it ages, spreads, and treats
 * its own targets as if they were highly flammable.</p>
 *
 * <p>Two of the original's quirks are kept deliberately — see
 * {@link #setBlockWithTransmutationTarget} for the argument-order one, and
 * {@link #getChanceOfNeighborsEncouragingFire}, which answers only 0 or 100
 * where vanilla weighs the neighbours.</p>
 */
public abstract class BlockFireBase extends BlockFire {

    protected BlockFireBase() {
        super();
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    /** What this fire changes, and into what. */
    public abstract Map<Block, Block> getBlockTransformation();

    public boolean isTransmutationTarget(Block block) {
        return getBlockTransformation().containsKey(block);
    }

    public boolean isTransmutationResult(Block block) {
        return getBlockTransformation().containsValue(block);
    }

    /** True when any of the six neighbours is something this fire works on. */
    public boolean isNeighborTarget(World world, BlockPos pos) {
        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos side = pos.offset(face);
            if (world.isBlockLoaded(side) && isTransmutationTarget(world.getBlockState(side).getBlock())) {
                return true;
            }
        }
        return false;
    }

    /** One in this many transmutations is a burn-away instead. 1 = never. */
    public int getDecayChance() {
        return 1;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int tickRate(World world) {
        return 200;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return true;
    }

    /**
     * Places {@code block}, or transmutes what is already there instead.
     *
     * <p><b>The argument order is the original's and is not a typo here.</b>
     * Upstream this method took {@code (x, y, z)} and then read
     * {@code world.getBlock(x, z, y)} — the last two swapped. One of its two
     * callers passed them pre-swapped so the mistake cancelled out; the other
     * did not. Both call sites below pass exactly what upstream passed, so the
     * behaviour matches on both paths, quirk included.</p>
     */
    protected void setBlockWithTransmutationTarget(World world, int x, int a, int b, int age, Block block) {
        BlockPos pos = new BlockPos(x, b, a);
        Block found = world.getBlockState(pos).getBlock();
        if (isTransmutationTarget(found) && world.rand.nextInt(getDecayChance()) == 0) {
            world.setBlockState(pos, getBlockTransformation().get(found).getDefaultState(), 3);
        } else if (block == Blocks.AIR) {
            world.setBlockToAir(pos);
        } else {
            world.setBlockState(pos, block.getDefaultState()
                    .withProperty(AGE, Math.min(15, age)), 3);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.getGameRules().getBoolean("doFireTick")) {
            return;
        }
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (world.isRaining() && (world.isRainingAt(pos)
                || world.isRainingAt(pos.west()) || world.isRainingAt(pos.east())
                || world.isRainingAt(pos.north()) || world.isRainingAt(pos.south()))) {
            world.setBlockToAir(pos);
            return;
        }

        if (!isNeighborTarget(world, pos)) {
            world.setBlockToAir(pos);
        }
        if (rand.nextInt(20) == 0 && isNeighborTarget(world, pos)) {
            for (EnumFacing face : EnumFacing.VALUES) {
                BlockPos side = pos.offset(face);
                Block found = world.getBlockState(side).getBlock();
                if (!isTransmutationTarget(found)) {
                    continue;
                }
                if (rand.nextInt(getDecayChance()) == 0) {
                    world.setBlockState(side, getBlockTransformation().get(found).getDefaultState(), 3);
                } else {
                    world.setBlockToAir(side);
                }
            }
        }

        int age = state.getValue(AGE);
        if (age < 15) {
            world.setBlockState(pos, state.withProperty(AGE, age + rand.nextInt(3) / 2), 4);
        }
        world.scheduleUpdate(pos, this, tickRate(world) + rand.nextInt(3));

        boolean humid = world.isBlockinHighHumidity(pos);
        int humidity = humid ? -50 : 0;

        for (EnumFacing face : EnumFacing.VALUES) {
            tryCatchFire(world, pos.offset(face), 300 + humidity, rand, age, face.getOpposite());
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos target = new BlockPos(x + dx, y + dy, z + dz);
                    int encouragement = getChanceOfNeighborsEncouragingFire(world, target);
                    if (encouragement <= 0) {
                        continue;
                    }
                    int chance = (encouragement + 70) / (age + 30) + 70;
                    if (humid) {
                        chance /= 2;
                    }
                    if (chance > 0 && rand.nextInt(100) <= chance
                            && (!world.isRaining() || !world.isRainingAt(target))
                            && !world.isRainingAt(target.west()) && !world.isRainingAt(target.east())
                            && !world.isRainingAt(target.north()) && !world.isRainingAt(target.south())) {
                        int newAge = Math.min(15, age + rand.nextInt(5) / 4);
                        // Upstream order: (x, z, y) — see the note on the method.
                        setBlockWithTransmutationTarget(world, target.getX(), target.getZ(),
                                target.getY(), newAge, this);
                    }
                }
            }
        }
    }

    /** Targets burn like tinder, results not at all, everything else as usual. */
    public int getBlockFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        Block block = world.getBlockState(pos).getBlock();
        if (isTransmutationTarget(block)) {
            return 100;
        }
        if (isTransmutationResult(block)) {
            return 0;
        }
        return block.getFlammability(world, pos, face);
    }

    private void tryCatchFire(World world, BlockPos pos, int strength, Random rand, int age, EnumFacing face) {
        int flammability = getBlockFlammability(world, pos, face);
        if (rand.nextInt(strength) >= flammability) {
            return;
        }
        boolean tnt = world.getBlockState(pos).getBlock() == Blocks.TNT;
        if (rand.nextInt(age + 10) < 5 && !world.isRainingAt(pos)) {
            int newAge = Math.min(15, age + rand.nextInt(5) / 4);
            // Upstream order: (x, y, z) — unswapped here, which is the quirk.
            setBlockWithTransmutationTarget(world, pos.getX(), pos.getY(), pos.getZ(), newAge, this);
        } else {
            setBlockWithTransmutationTarget(world, pos.getX(), pos.getY(), pos.getZ(), 0, Blocks.AIR);
        }
        if (tnt) {
            Blocks.TNT.onPlayerDestroy(world, pos, Blocks.TNT.getDefaultState()
                    .withProperty(net.minecraft.block.BlockTNT.EXPLODE, true));
        }
    }

    private boolean canNeighborBurn(World world, BlockPos pos) {
        return isNeighborTarget(world, pos);
    }

    /**
     * Upstream reduced vanilla's weighted survey to a yes/no: an air block with
     * something this fire works on beside it scores 100, anything else 0.
     */
    private int getChanceOfNeighborsEncouragingFire(World world, BlockPos pos) {
        if (!world.isAirBlock(pos)) {
            return 0;
        }
        return isNeighborTarget(world, pos) ? 100 : 0;
    }

    @Override
    public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getBlockFlammability(world, pos, face) > 0;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        if (!world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)
                && !canNeighborBurn(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleUpdate(pos, this, tickRate(world) + world.rand.nextInt(10));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    net.minecraft.init.SoundEvents.BLOCK_FIRE_AMBIENT,
                    net.minecraft.util.SoundCategory.BLOCKS,
                    1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }
        super.randomDisplayTick(state, world, pos, rand);
    }
}
