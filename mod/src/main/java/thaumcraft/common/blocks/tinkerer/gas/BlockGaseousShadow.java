package thaumcraft.common.blocks.tinkerer.gas;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;

/**
 * Gaseous Tenebrae — ported from Thaumic Tinkerer's {@code BlockGaseousShadow}
 * (pixlepix / nekosune, originally Vazkii). The light's opposite: it swallows
 * what light there is rather than adding any.
 *
 * <p>Upstream's {@code wispFX2} took one boolean where this version takes two —
 * shrink and no-clip. Shrink is upstream's; no-clip follows what the port's own
 * type-5 wisps use.</p>
 */
public class BlockGaseousShadow extends BlockGas {

    public BlockGaseousShadow() {
        super();
        this.setLightOpacity(215);
    }

    @Override
    public void placeParticle(World world, BlockPos pos) {
        Thaumcraft.proxy.wispFX2(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                0.125F, 5, true, true, -0.02F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextFloat() < 0.0075F) {
            Thaumcraft.proxy.wispFX2(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    0.125F, 5, true, true, -0.02F);
        }
    }
}
