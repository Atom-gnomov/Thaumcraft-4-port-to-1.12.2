package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.tinkerer.TileTransvectorDislocator;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Transvector Dislocator — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Swaps the block it faces with the block it is linked to whenever it
 * receives a redstone pulse. Link it with the Transvector Connector.</p>
 *
 * @see TileTransvectorDislocator
 */
public class BlockTransvectorDislocator extends BlockCamo {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockTransvectorDislocator() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.UP)
                .withProperty(POWERED, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTransvectorDislocator();
    }

    /**
     * A wand re-aims it at the face clicked; anything else falls through to
     * BlockCamo, which is exactly the original's order.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
            world.setBlockState(pos, state.withProperty(FACING, side), 3);
            world.playSound(null, pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.6F, 1.0F);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
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

    /**
     * Facing and powered are this block's own; BlockCamo adds the unlisted
     * disguise property around them.
     */
    @Override
    protected net.minecraft.block.properties.IProperty<?>[] listedProperties() {
        return new net.minecraft.block.properties.IProperty<?>[]{FACING, POWERED};
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
