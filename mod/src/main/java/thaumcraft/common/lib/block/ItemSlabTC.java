package thaumcraft.common.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

// ItemBlock for the two-variant cosmetic slabs: damage == variant, per-variant unlocalized name,
// vanilla ItemSlab handling for placing/combining into the paired double slab.
public class ItemSlabTC extends ItemSlab {
    public ItemSlabTC(Block block, BlockSlab singleSlab, BlockSlab doubleSlab) {
        super(block, singleSlab, doubleSlab);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return ((BlockSlab) this.getBlock()).getUnlocalizedName(stack.getMetadata());
    }
}
