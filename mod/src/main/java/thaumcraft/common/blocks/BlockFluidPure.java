package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.common.Thaumcraft;

/**
 * Purifying Fluid — TC4's "pure" water (a {@link BlockFluidClassic}, i.e. infinite/self-levelling
 * like vanilla water). Luminosity 10, rare. Phase-1 port: correct fluid base + material + creative
 * tab so it flows and renders properly.
 *
 * <p><b>TODO Phase 3:</b> standing in a <i>source</i> block grants the Warp-Ward potion (scaled by
 * permanent warp) and consumes the block; ambient bubble particles + lavapop sfx.
 */
public class BlockFluidPure extends BlockFluidClassic {
    public BlockFluidPure(Fluid fluid) {
        super(fluid, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
    }
}
