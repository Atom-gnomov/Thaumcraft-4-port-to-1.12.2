package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import thaumcraft.common.Thaumcraft;

/**
 * Liquid Death — a finite ({@link BlockFluidFinite}) pool that dissolves living things. Luminosity 8,
 * rare, 4 quanta per block. Phase-1 port: correct fluid base + material + creative tab.
 *
 * <p><b>TODO Phase 3:</b> deals {@code DamageSourceThaumcraft.dissolve} (amount scales with level) to
 * living entities inside it; red slimy-bubble particles + occasional lavapop sfx.
 */
public class BlockFluidDeath extends BlockFluidFinite {
    public BlockFluidDeath(Fluid fluid) {
        super(fluid, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setQuantaPerBlock(4);
    }
}
