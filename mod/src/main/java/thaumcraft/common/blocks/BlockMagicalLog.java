package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: greatwood (0) and silverwood (1) logs as static vertical blocks. Meta 2 = silverwood knot (world-gen only).
// TODO Phase 2+: axis rotation (BlockRotatedPillar), silverwood wisp essence drop on break.
public class BlockMagicalLog extends BlockTC {
    public BlockMagicalLog() {
        super(Material.WOOD);
        this.setResistance(5.0f);
        this.setHardness(2.0f);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = getMetaFromState(state);
        return (meta == 1 || meta == 2) ? 7 : 0;
    }
}
