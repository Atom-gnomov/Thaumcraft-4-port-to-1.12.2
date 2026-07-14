package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: decorative solid blocks exist as placeable/drop-self blocks with static textures.
// TODO Phase 2+: obsidian totem directional faces (meta 0/8), ancient stone random variants (meta 11/12),
//   golem fetter behavior (meta 9/10), paving stone of travel/warding effects (meta 2/3).
public class BlockCosmeticSolid extends BlockTC {
    // Metas shown in the creative tab (skips 10 = active golem fetter, 13 = ancient stone variant).
    private static final int[] CREATIVE_METAS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15 };

    public BlockCosmeticSolid() {
        super(Material.ROCK);
        this.setResistance(10.0f);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m : CREATIVE_METAS) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = getMetaFromState(state);
        if (meta == 2) {
            return 9;
        }
        if (meta == 14) {
            return 4;
        }
        return 0;
    }
}
