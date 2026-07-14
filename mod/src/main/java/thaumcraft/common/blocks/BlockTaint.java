package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigSounds;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: taint crust (0), taint soil (1), flesh block (2) as static drop-self blocks.
// TODO Phase 3 (taint mechanic): spread to neighbours, convert biome, damage entities.
public class BlockTaint extends BlockTC {
    public BlockTaint() {
        super(Material.ROCK);
        this.setResistance(2.0f);
        this.setHardness(0.5f);
        this.setSoundType(ConfigSounds.SOUND_GORE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 2; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }
}
