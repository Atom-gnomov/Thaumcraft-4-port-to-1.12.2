package thaumcraft.common.blocks.tinkerer;

import java.util.Random;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Dark Quartz Slab — ported from Thaumic Tinkerer's
 * {@code BlockDarkQuartzSlab} (pixlepix / nekosune / Vazkii): a single-variant
 * slab cut from plain dark quartz, at the original's hardness and resistance.
 */
public abstract class BlockDarkQuartzSlab extends BlockSlab {

    /** Vanilla slabs need a variant property even when there is only one. */
    public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");

    protected BlockDarkQuartzSlab() {
        super(Material.ROCK);
        IBlockState state = this.blockState.getBaseState();
        if (this.isDouble()) {
            state = state.withProperty(SEAMLESS, false);
        } else {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
            this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        }
        this.setDefaultState(state);
        this.setHardness(0.8F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 0);
        if (!this.isDouble()) {
            this.setLightOpacity(0);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ConfigBlocks.blockSlabDarkQuartz);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ConfigBlocks.blockSlabDarkQuartz);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return SEAMLESS;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        if (this.isDouble()) {
            return state.withProperty(SEAMLESS, (meta & 8) != 0);
        }
        return state.withProperty(HALF,
                (meta & 8) != 0 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (this.isDouble()) {
            return state.getValue(SEAMLESS) ? 8 : 0;
        }
        return state.getValue(HALF) == EnumBlockHalf.TOP ? 8 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble()
                ? new BlockStateContainer(this, SEAMLESS)
                : new BlockStateContainer(this, HALF, SEAMLESS);
    }

    public static final class Half extends BlockDarkQuartzSlab {
        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static final class Double extends BlockDarkQuartzSlab {
        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
