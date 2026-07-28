package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Aer Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireAir}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Dries the world out: wood and dirt become sand, leaves sandstone, ice glass. Water becomes a cake, which is the original’s joke and is kept.</p>
 */
public class BlockFireAir extends BlockFireBase {

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(Blocks.LOG, Blocks.SAND);
        result.put(Blocks.LEAVES, Blocks.SANDSTONE);
        result.put(Blocks.LEAVES2, Blocks.SANDSTONE);
        result.put(Blocks.LOG2, Blocks.SAND);
        result.put(Blocks.ICE, Blocks.GLASS);
        result.put(Blocks.WATER, Blocks.CAKE);
        result.put(Blocks.DIRT, Blocks.SAND);
        return result;
    }
}
