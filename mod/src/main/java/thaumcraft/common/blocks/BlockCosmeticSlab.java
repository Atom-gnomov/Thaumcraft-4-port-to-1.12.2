package thaumcraft.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

// Phase 1 port: cosmetic slabs. VARIANT (0..1) selects the two types within a family
// (wood: greatwood/silverwood, stone: arcane/eldritch); vanilla HALF handles top/bottom/double.
public class BlockCosmeticSlab extends BlockSlab {
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 1);

    private final boolean doubleSlab;
    private final String[] names;
    private final Block halfSlab;

    public BlockCosmeticSlab(Material material, boolean doubleSlab, String[] names, Block halfRef, SoundType sound) {
        super(material);
        this.doubleSlab = doubleSlab;
        this.names = names;
        this.halfSlab = halfRef != null ? halfRef : this;
        IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, 0);
        if (!doubleSlab) {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(state);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setSoundType(sound);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
    }

    @Override
    public boolean isDouble() {
        return this.doubleSlab;
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return stack.getMetadata() & 1;
    }

    @Override
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName() + "." + names[meta & 1];
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(VARIANT, meta & 1);
        if (!this.doubleSlab) {
            state = state.withProperty(HALF, (meta & 8) != 0 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(VARIANT);
        if (!this.doubleSlab && state.getValue(HALF) == EnumBlockHalf.TOP) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.doubleSlab
                ? new BlockStateContainer(this, VARIANT)
                : new BlockStateContainer(this, HALF, VARIANT);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this.halfSlab);
    }

    @Override
    public int quantityDropped(Random random) {
        return this.doubleSlab ? 2 : 1;
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this.halfSlab, 1, state.getValue(VARIANT));
    }
}
