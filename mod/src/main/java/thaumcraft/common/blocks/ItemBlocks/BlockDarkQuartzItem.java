package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Metadata ItemBlock for {@link thaumcraft.common.blocks.tinkerer.BlockDarkQuartz}
 * so its three variants keep distinct items and names.
 */
public class BlockDarkQuartzItem extends ItemBlock {

    public BlockDarkQuartzItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }
}
