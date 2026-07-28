package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Perditio Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireChaos}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Eats the other five, leaving ordinary fire behind. It ticks every tick rather than every two hundred, so it catches them quickly.</p>
 */
public class BlockFireChaos extends BlockFireBase {

    @Override
    public int tickRate(net.minecraft.world.World world) {
        return 1;
    }

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(ConfigBlocks.blockFireAir, Blocks.FIRE);
        result.put(ConfigBlocks.blockFireWater, Blocks.FIRE);
        result.put(ConfigBlocks.blockFireEarth, Blocks.FIRE);
        result.put(ConfigBlocks.blockFireIgnis, Blocks.FIRE);
        result.put(ConfigBlocks.blockFireOrder, Blocks.FIRE);
        return result;
    }
}
