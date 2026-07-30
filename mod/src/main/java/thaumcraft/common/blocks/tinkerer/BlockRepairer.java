package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileRepairer;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Repairer — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. Mends the tool placed inside it using essentia drawn through the
 * side it faces (see {@link TileRepairer}).
 *
 * <p>Placing it against a block points {@link #FACING} at that block, so
 * dropping it straight onto a tube connects it up.</p>
 */
public class BlockRepairer extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockRepairer() {
        super(Material.IRON);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRepairer();
    }

    /**
     * The whole block is drawn by {@code TileRepairerRenderer}. Upstream's
     * {@code RenderRepairer.renderWorldBlock} returns false — there is no static
     * geometry — so a blockstate model here would draw on top of the real one.
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * The original says false to both, and it matters for more than culling:
     * an opaque block stops light, so leaving this out left the repairer's own
     * space unlit and the renderer drew the whole case nearly black. It is a
     * glass case — light has to reach it.
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /** Right-click swaps the held item with whatever is being repaired. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileRepairer)) {
            return false;
        }
        ItemStackHandler inv = ((TileRepairer) te).getInventory();
        ItemStack held = player.getHeldItem(hand);
        ItemStack stored = inv.getStackInSlot(0);

        if (!stored.isEmpty()) {
            if (!player.inventory.addItemStackToInventory(stored)) {
                player.dropItem(stored, false);
            }
            inv.setStackInSlot(0, ItemStack.EMPTY);
        }
        if (!held.isEmpty() && held.isItemStackDamageable()) {
            ItemStack single = held.copy();
            single.setCount(1);
            inv.setStackInSlot(0, single);
            held.shrink(1);
        }
        return true;
    }

    /** Spill the held tool when broken, so it is never lost. */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileRepairer) {
            ItemStack stored = ((TileRepairer) te).getInventory().getStackInSlot(0);
            if (!stored.isEmpty()) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(
                        world, pos.getX(), pos.getY(), pos.getZ(), stored);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        // `side` is the face of the block that was clicked, so the neighbour we
        // were placed against lies in the opposite direction.
        return this.getDefaultState().withProperty(FACING, side.getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}
