package thaumcraft.common.lib.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * ItemBlock for metadata blocks: meta == damage, one unlocalized name per meta,
 * creative subtypes delegated to the block's {@link Block#getSubBlocks}.
 */
public class ItemBlockTC extends ItemBlock {
    public ItemBlockTC(Block block) {
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(block.getCreativeTabToDisplayOn());
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            this.block.getSubBlocks(tab, items);
        }
    }
}
