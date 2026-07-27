package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Metadata ItemBlock for {@link thaumcraft.common.blocks.tinkerer.BlockMagnet} —
 * ported from Thaumic Tinkerer's {@code ItemBlockMagnet}.
 *
 * <p>The original kept two numbering schemes and this reproduces both: the
 * <em>item</em> carries damage 0 for the item magnet and 1 for the mob magnet,
 * while the <em>block</em> uses bit 1 for the variant and bit 0 for the
 * attract/repel toggle. Hence {@code getMetadata}'s {@code 0 -> 0, else -> 2},
 * which is the original's mapping verbatim.</p>
 */
public class BlockMagnetItem extends ItemBlock {

    public BlockMagnetItem(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage == 0 ? 0 : 2;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return "tile." + (stack.getItemDamage() == 0 ? "thaumcraft.magnet" : "thaumcraft.mobMagnet");
    }
}
