package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: greatwood (0) and silverwood (1) leaves as static, non-opaque drop-self blocks.
// TODO Phase 2+: leaf decay, sapling drops (blockCustomPlant 0/1), shearing, fancy/fast graphics variants.
public class BlockMagicalLeaves extends BlockTC {
    public BlockMagicalLeaves() {
        super(Material.LEAVES);
        this.setResistance(1.0f);
        this.setHardness(0.2f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public int getLightValue(IBlockState state) {
        return getMetaFromState(state) == 1 ? 7 : 0;
    }
}
