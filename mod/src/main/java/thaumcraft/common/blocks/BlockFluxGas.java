package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.common.Thaumcraft;

/**
 * Flux Gas — the airborne form of flux pollution (a finite {@link BlockFluidFinite}). Luminosity 7,
 * <i>negative</i> density (-4) so it rises instead of falling (the base class sets
 * {@code densityDir = 1} from the fluid's negative density). Phase-1 port: correct fluid base +
 * gaseous behaviour + creative tab.
 *
 * <p><b>TODO Phase 3:</b> randomly inflicts Vis Exhaustion or Blindness on non-tainted living
 * entities and thins out as it does; uses {@code MaterialTaint} (currently {@link Material#WATER}).
 */
public class BlockFluxGas extends BlockFluidFinite {
    public BlockFluxGas(Fluid fluid) {
        // TODO Phase 3: Config.fluxGoomaterial (MaterialTaint) instead of WATER.
        super(fluid, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
    }
}
