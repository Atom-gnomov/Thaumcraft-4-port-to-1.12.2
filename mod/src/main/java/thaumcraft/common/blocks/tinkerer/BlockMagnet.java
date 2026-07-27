package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileMagnet;
import thaumcraft.common.tiles.tinkerer.TileMobMagnet;

/**
 * Magnet — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for 1.12.2.
 *
 * <p>While powered by redstone it drags dropped items toward itself; right-click
 * flips it into repelling mode, which pushes them away instead. The original
 * encoded that flag in metadata bit 0 and used a Techne model whose two poles
 * swapped; here it is the {@code pulling} blockstate property, shown by the top
 * face texture (converging core vs radiating ring).</p>
 */
public class BlockMagnet extends BlockContainer {

    public static final PropertyBool PULLING = PropertyBool.create("pulling");
    /** Second metadata bit in the original: 0 = item magnet, 1 = mob magnet. */
    public static final PropertyBool MOB = PropertyBool.create("mob");

    /** The original's {@code setBlockBounds(0.0625, 0, 0.0625, 0.9375, 2F / 16F, 0.9375)}. */
    private static final AxisAlignedBB SHAPE =
            new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 2.0D / 16.0D, 0.9375D);

    public BlockMagnet() {
        super(Material.IRON);
        this.setHardness(1.7F);
        this.setResistance(1.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(PULLING, true)
                .withProperty(MOB, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        // Bit 1 picks the variant, exactly as the original's `metadata & 2` did.
        return (meta & 2) == 2 ? new TileMobMagnet() : new TileMagnet();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public void getSubBlocks(net.minecraft.creativetab.CreativeTabs tab,
                             net.minecraft.util.NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /** Right-click toggles attract/repel, as in the original. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (state.getValue(MOB) && !player.isSneaking()) {
            player.openGui(thaumcraft.common.Thaumcraft.instance,
                    thaumcraft.common.CommonProxy.GUI_MOB_MAGNET,
                    world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        world.setBlockState(pos, state.cycleProperty(PULLING), 3);
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PULLING, MOB);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(PULLING, (meta & 1) == 0)
                .withProperty(MOB, (meta & 2) == 2);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(PULLING) ? 0 : 1) | (state.getValue(MOB) ? 2 : 0);
    }

    @Override
    public int damageDropped(IBlockState state) {
        // The original's table: block metas 0/1 drop item damage 0, metas 2/3
        // drop item damage 1. So the toggle is forgotten, the variant is kept.
        return state.getValue(MOB) ? 1 : 0;
    }
}
