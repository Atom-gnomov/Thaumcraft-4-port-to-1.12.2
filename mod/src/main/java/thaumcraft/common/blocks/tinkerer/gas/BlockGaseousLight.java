package thaumcraft.common.blocks.tinkerer.gas;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;

/**
 * Gaseous Illuminae — ported from Thaumic Tinkerer's {@code BlockGaseousLight}
 * (pixlepix / nekosune, originally Vazkii). A drifting glow that lights the
 * room it fills.
 */
public class BlockGaseousLight extends BlockGas {

    public BlockGaseousLight() {
        super();
        this.setLightLevel(0.85F);
    }

    @Override
    public void placeParticle(World world, BlockPos pos) {
        Thaumcraft.proxy.sparkle(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextFloat() < 0.0075F) {
            Thaumcraft.proxy.sparkle(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    1.0F, 1, rand.nextFloat() / 2.0F);
        }
    }
}
