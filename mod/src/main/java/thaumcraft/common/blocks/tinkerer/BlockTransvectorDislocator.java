package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileTransvectorDislocator;

/**
 * Transvector Dislocator — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Swaps the block it faces with the block it is linked to whenever it
 * receives a redstone pulse. Link it with the Transvector Connector.</p>
 *
 * @see TileTransvectorDislocator
 */
public class BlockTransvectorDislocator extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockTransvectorDislocator() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(12.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.UP)
                .withProperty(POWERED, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTransvectorDislocator();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /** Fires the swap on the rising edge only, as the original did via metadata. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block,
                                BlockPos fromPos) {
        if (world.isRemote) {
            return;
        }
        boolean powered = world.isBlockPowered(pos);
        boolean wasPowered = state.getValue(POWERED);
        if (powered == wasPowered) {
            return;
        }
        world.setBlockState(pos, state.withProperty(POWERED, powered), 2);
        if (powered) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileTransvectorDislocator) {
                ((TileTransvectorDislocator) te).onRedstonePulse();
            }
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        // Points away from the face that was clicked, i.e. at whatever it was set against.
        return this.getDefaultState()
                .withProperty(FACING, side.getOpposite())
                .withProperty(POWERED, false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(FACING, EnumFacing.byIndex(meta & 7))
                .withProperty(POWERED, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(POWERED) ? 8 : 0);
    }
}
