package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Terra Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireEarth}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Turns the world back to soil — stone, wood, glass and cobble all become dirt — while nether brick reverts to planks and a spawner to a block of iron.</p>
 */
public class BlockFireEarth extends BlockFireBase {

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(Blocks.SAND, Blocks.DIRT);
        result.put(Blocks.GRAVEL, Blocks.CLAY);
        result.put(Blocks.NETHER_BRICK, Blocks.PLANKS);
        result.put(Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE);
        result.put(Blocks.NETHER_BRICK_STAIRS, Blocks.OAK_STAIRS);
        result.put(Blocks.CACTUS, Blocks.LOG);
        result.put(Blocks.SNOW_LAYER, Blocks.TALLGRASS);
        result.put(Blocks.STONE, Blocks.DIRT);
        result.put(Blocks.MOB_SPAWNER, Blocks.IRON_BLOCK);
        result.put(Blocks.LOG, Blocks.DIRT);
        result.put(Blocks.LOG2, Blocks.DIRT);
        result.put(Blocks.LEAVES, Blocks.DIRT);
        result.put(Blocks.LEAVES2, Blocks.DIRT);
        result.put(Blocks.COBBLESTONE, Blocks.DIRT);
        result.put(Blocks.PLANKS, Blocks.DIRT);
        result.put(Blocks.GLASS, Blocks.DIRT);
        return result;
    }
}
