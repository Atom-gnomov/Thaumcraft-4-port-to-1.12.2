package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigSounds;
import thaumcraft.common.lib.block.BlockTCBush;

// Phase 1 port: taint fibres (0), taint grass 1 (1), taint grass 2 (2), spore stalk (3) as static cross plants.
// TODO Phase 3 (taint mechanic): spread onto taint soil, spore stalk growth, entity effects.
public class BlockTaintFibres extends BlockTCBush {
    public BlockTaintFibres() {
        super();
        this.setHardness(0.0f);
        this.setSoundType(ConfigSounds.SOUND_GORE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 3; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = getMetaFromState(state);
        if (meta == 2) {
            return 8;
        }
        if (meta == 4) {
            return 10;
        }
        return 0;
    }
}
