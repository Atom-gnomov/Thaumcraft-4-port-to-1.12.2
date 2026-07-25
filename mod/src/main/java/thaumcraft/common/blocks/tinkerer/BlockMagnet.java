package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileMagnet;

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

    public BlockMagnet() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PULLING, true));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMagnet();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /** Right-click toggles attract/repel, as in the original. */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            world.setBlockState(pos, state.cycleProperty(PULLING), 3);
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PULLING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PULLING, (meta & 1) == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PULLING) ? 0 : 1;
    }

    @Override
    public int damageDropped(IBlockState state) {
        // Always drops the default (attracting) variant, like the original.
        return 0;
    }
}
