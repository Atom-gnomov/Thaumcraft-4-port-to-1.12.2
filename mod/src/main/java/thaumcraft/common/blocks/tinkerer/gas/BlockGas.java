package thaumcraft.common.blocks.tinkerer.gas;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Base of the gases — ported from Thaumic Tinkerer's {@code BlockGas}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A gas has no shape, no collision, no drops and counts as air to anything
 * that asks. What it does have is a spread counter in its metadata: a block
 * placed with counter <em>n</em> seeds its six neighbours with <em>n-1</em> and
 * then zeroes itself, so a single placement blooms outward and stops.</p>
 */
public abstract class BlockGas extends Block {

    /** The original's metadata: how much further this gas may still spread. */
    public static final PropertyInteger SPREAD = PropertyInteger.create("spread", 0, 15);

    protected BlockGas() {
        super(Material.AIR);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SPREAD, 0));
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int spread = state.getValue(SPREAD);
        if (spread == 0) {
            return;
        }
        for (EnumFacing face : EnumFacing.VALUES) {
            setAt(world, pos.offset(face), spread - 1);
        }
        // Zero it afterwards, "just in case" — upstream's own words.
        world.setBlockState(pos, state.withProperty(SPREAD, 0), 2);
        placeParticle(world, pos);
    }

    /** Spawned when the gas arrives somewhere. Nothing by default. */
    public void placeParticle(World world, BlockPos pos) {
    }

    private void setAt(World world, BlockPos pos, int spread) {
        if (world.isAirBlock(pos) && world.getBlockState(pos).getBlock() != this) {
            if (!world.isRemote) {
                world.setBlockState(pos, this.getDefaultState().withProperty(SPREAD, spread), 2);
            }
            world.scheduleUpdate(pos, this, 10);
        }
    }

    // --- a gas is not really there -----------------------------------------

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    // --- state plumbing ----------------------------------------------------

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SPREAD);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SPREAD, meta & 15);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SPREAD);
    }
}
