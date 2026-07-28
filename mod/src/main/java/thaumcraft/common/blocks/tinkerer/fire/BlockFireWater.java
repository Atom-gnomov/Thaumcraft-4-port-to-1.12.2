package thaumcraft.common.blocks.tinkerer.fire;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Aqua Imbued Fire — ported from Thaumic Tinkerer's {@code BlockFireWater}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Chills what it touches: sand and soul sand freeze to ice, netherrack to snow, and lava sets to obsidian.</p>
 */
public class BlockFireWater extends BlockFireBase {

    @Override
    public Map<Block, Block> getBlockTransformation() {
        Map<Block, Block> result = new HashMap<>();
        result.put(Blocks.SAND, Blocks.ICE);
        result.put(Blocks.NETHERRACK, Blocks.SNOW);
        result.put(Blocks.SOUL_SAND, Blocks.ICE);
        result.put(Blocks.GLOWSTONE, Blocks.ICE);
        result.put(Blocks.LAVA, Blocks.OBSIDIAN);
        result.put(Blocks.FLOWING_LAVA, Blocks.OBSIDIAN);
        return result;
    }
}
