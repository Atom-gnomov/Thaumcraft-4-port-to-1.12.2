package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileFunnel;

/**
 * Funnel — ported from Thaumic Tinkerer's {@code BlockFunnel}
 * (pixlepix / nekosune / Vazkii). Right-click puts a filled jar in or takes it
 * back out; the tile then drips that jar's essentia into whatever the hopper
 * beneath it points at (see {@link TileFunnel}).
 */
public class BlockFunnel extends BlockContainer {

    /** The original's {@code setBlockBounds(0, 0, 0, 1, 1F / 8F, 1)}. */
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D / 8.0D, 1.0D);

    public BlockFunnel() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setSoundType(net.minecraft.block.SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFunnel();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    /**
     * The original's {@code getCollisionBoundingBoxFromPool} hands back the whole
     * cube, not {@link #SHAPE}: the funnel looks like a two-pixel plate but stops
     * you like a full block. Without this override 1.12 would collide against the
     * plate and let you stand inside the block.
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return FULL_BLOCK_AABB;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileFunnel)) {
            return false;
        }
        IItemHandler inventory = ((TileFunnel) tile).getInventory();
        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty()) {
            ItemStack remainder = inventory.insertItem(0, held.copy(), world.isRemote);
            if (remainder.getCount() != held.getCount()) {
                if (!world.isRemote) {
                    player.setHeldItem(hand, remainder);
                    tile.markDirty();
                }
                return true;
            }
            return false;
        }

        ItemStack stored = inventory.extractItem(0, 1, world.isRemote);
        if (!stored.isEmpty()) {
            if (!world.isRemote) {
                if (!player.inventory.addItemStackToInventory(stored)) {
                    player.dropItem(stored, false);
                }
                tile.markDirty();
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFunnel) {
            IItemHandler inventory = ((TileFunnel) tile).getInventory();
            ItemStack stored = inventory.extractItem(0, inventory.getSlotLimit(0), false);
            if (!stored.isEmpty()) {
                net.minecraft.block.Block.spawnAsEntity(world, pos, stored);
            }
        }
        super.breakBlock(world, pos, state);
    }
}
