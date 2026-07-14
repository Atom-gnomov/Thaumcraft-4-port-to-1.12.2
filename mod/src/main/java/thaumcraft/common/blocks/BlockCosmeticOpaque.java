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

// Phase 1 port: amber block (0), amber bricks (1), warded glass (2) as static drop-self blocks.
// TODO Phase 2+: warded glass ownership/unbreakable (TileOwned), connected texture.
public class BlockCosmeticOpaque extends BlockTC {
    private static final int GLASS = 2;

    public BlockCosmeticOpaque() {
        super(Material.ROCK);
        this.setResistance(5.0f);
        this.setHardness(1.5f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 2; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return getMetaFromState(state) != GLASS;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (getMetaFromState(state) == GLASS) {
            return layer == BlockRenderLayer.CUTOUT_MIPPED;
        }
        return layer == BlockRenderLayer.SOLID;
    }
}
