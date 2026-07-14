package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTCBush;

// Phase 1 port: greatwood sapling (0), silverwood sapling (1), shimmerleaf (2), cinderpearl (3),
//   silverwood purifier seed (4), manashroom (5) as static cross-model bushes that drop themselves.
// TODO Phase 2+: sapling growth into trees, purifier multiblock + TileEntity (meta 4),
//   cinderpearl/manashroom placement rules, shimmerleaf vis regen.
public class BlockCustomPlant extends BlockTCBush {
    public BlockCustomPlant() {
        super();
        this.setHardness(0.0f);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 5; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = getMetaFromState(state);
        if (meta == 4) {
            return 15;
        }
        if (meta == 1 || meta == 2 || meta == 3 || meta == 5) {
            return 8;
        }
        return 0;
    }
}
