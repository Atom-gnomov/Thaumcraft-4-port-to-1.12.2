package thaumcraft.common.blocks.tinkerer;

import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockThaumcraftStairs;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Dark Quartz Stairs — ported from Thaumic Tinkerer's
 * {@code BlockDarkQuartzStairs} (pixlepix / nekosune / Vazkii), cut from plain
 * dark quartz exactly as the original was.
 */
public class BlockDarkQuartzStairs extends BlockThaumcraftStairs {

    public BlockDarkQuartzStairs() {
        super(ConfigBlocks.blockDarkQuartz.getStateFromMeta(0));
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.useNeighborBrightness = true;
    }
}
