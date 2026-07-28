package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Ignis Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireIgnis}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Drags the Nether up: grass and dirt become netherrack, sand and gravel soul sand, every ore quartz ore, crops nether wart, and lava turns to water. The yellow flower maps to itself, as upstream.</p>
 */
public class BlockFireIgnis extends BlockFireBase {

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(Blocks.GRASS, Blocks.NETHERRACK);
        result.put(Blocks.DIRT, Blocks.NETHERRACK);
        result.put(Blocks.SAND, Blocks.SOUL_SAND);
        result.put(Blocks.GRAVEL, Blocks.SOUL_SAND);
        result.put(Blocks.CLAY, Blocks.GLOWSTONE);
        result.put(Blocks.COAL_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.IRON_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.DIAMOND_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.EMERALD_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.GOLD_BLOCK, Blocks.QUARTZ_ORE);
        result.put(Blocks.LAPIS_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.REDSTONE_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.LIT_REDSTONE_ORE, Blocks.QUARTZ_ORE);
        result.put(Blocks.LAVA, Blocks.WATER);
        result.put(Blocks.WHEAT, Blocks.NETHER_WART);
        result.put(Blocks.POTATOES, Blocks.NETHER_WART);
        result.put(Blocks.CARROTS, Blocks.NETHER_WART);
        result.put(Blocks.RED_FLOWER, Blocks.BROWN_MUSHROOM);
        result.put(Blocks.YELLOW_FLOWER, Blocks.YELLOW_FLOWER);
        return result;
    }
}
