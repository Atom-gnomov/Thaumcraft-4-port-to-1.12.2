package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Ordo Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireOrder}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Perfects ore into solid blocks of the metal. Beyond the vanilla nine it walks the ore dictionary and pairs every <code>oreFoo</code> with the matching <code>blockFoo</code>, so modded ores are covered too — the original does the same, and caches the result. Only one transmutation in three succeeds; the rest burn away.</p>
 */
public class BlockFireOrder extends BlockFireBase {

    /** The original's cache: the sweep is expensive and the result never changes. */
    private Map<Block, Block> oreDictionaryOresCache;

    @Override
    public int getDecayChance() {
        return 3;
    }

    /**
     * Pairs every {@code oreFoo} in the ore dictionary with {@code blockFoo}.
     * The ten characters compared after the prefixes are the original's
     * {@code regionMatches(5, ore, 3, 10)}, kept as-is.
     */
    public Map<Block, Block> getOreDictionaryOres() {
        if (this.oreDictionaryOresCache != null) {
            return this.oreDictionaryOresCache;
        }
        Map<Block, Block> result = new HashMap<>();
        String[] names = OreDictionary.getOreNames();
        for (String ore : names) {
            if (!ore.startsWith("ore")) {
                continue;
            }
            for (String block : names) {
                if (!block.startsWith("block") || !block.regionMatches(5, ore, 3, 10)) {
                    continue;
                }
                java.util.List<ItemStack> ores = OreDictionary.getOres(ore);
                java.util.List<ItemStack> blocks = OreDictionary.getOres(block);
                if (ores.isEmpty() || blocks.isEmpty()) {
                    continue;
                }
                if (ores.get(0).getItem() instanceof ItemBlock
                        && blocks.get(0).getItem() instanceof ItemBlock) {
                    result.put(((ItemBlock) ores.get(0).getItem()).getBlock(),
                            ((ItemBlock) blocks.get(0).getItem()).getBlock());
                }
            }
        }
        this.oreDictionaryOresCache = result;
        return result;
    }

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_BLOCK);
        result.put(Blocks.REDSTONE_ORE, Blocks.REDSTONE_BLOCK);
        result.put(Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK);
        result.put(Blocks.IRON_ORE, Blocks.IRON_BLOCK);
        result.put(Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK);
        result.put(Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK);
        result.put(Blocks.COAL_ORE, Blocks.COAL_BLOCK);
        result.put(Blocks.GOLD_ORE, Blocks.GOLD_BLOCK);
        result.put(Blocks.QUARTZ_ORE, Blocks.QUARTZ_BLOCK);
        result.putAll(getOreDictionaryOres());
        return result;
    }
}
