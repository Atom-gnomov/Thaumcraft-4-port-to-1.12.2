package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.Thaumcraft;

/**
 * Dark Quartz — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. A decorative quartz-like block with three variants:
 * 0 = plain, 1 = chiseled, 2 = pillar.
 */
public class BlockDarkQuartz extends Block {

    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 2);
    public static final String[] VARIANTS = {"plain", "chiseled", "pillar"};

    public BlockDarkQuartz() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
        this.setHardness(0.8F);
        this.setResistance(6.0F);
        this.setSoundType(net.minecraft.block.SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < VARIANTS.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, MathHelper.clamp(meta, 0, 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT);
    }
}
