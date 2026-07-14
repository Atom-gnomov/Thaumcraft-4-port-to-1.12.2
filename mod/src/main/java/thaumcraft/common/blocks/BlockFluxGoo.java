package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigSounds;

/**
 * Flux Goo — the viscous residue left by flux pollution (a finite {@link BlockFluidFinite}).
 * Luminosity 7, density 8, very viscous (6000), "gore" step sound. Phase-1 port: correct fluid
 * base + sound + creative tab so it pools/flows and renders.
 *
 * <p><b>TODO Phase 3:</b> slows entities and feeds/grows Thaumic Slimes; inflicts Vis Exhaustion on
 * the living; deep pools spawn slimes and (with {@code taintFromFlux}) spread Tainted Fibres;
 * evaporates upward into Flux Gas. Also uses {@code MaterialTaint} (currently {@link Material#WATER})
 * and registers a fluid displacement for Tainted Fibres.
 */
public class BlockFluxGoo extends BlockFluidFinite {
    public BlockFluxGoo(Fluid fluid) {
        // TODO Phase 3: Config.fluxGoomaterial (MaterialTaint) instead of WATER.
        super(fluid, Material.WATER);
        this.setSoundType(ConfigSounds.SOUND_GORE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }
}
