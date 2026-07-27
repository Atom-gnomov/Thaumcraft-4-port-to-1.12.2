package thaumcraft.common.lib.world.dim.bedrock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.Config;

/**
 * The Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code WorldProviderBedrock} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>A world made entirely of bedrock, reached by breaking bedrock with an
 * advanced ichor tool. It keeps a sky and stars, has no fog, and its ground
 * level sits at 1, as in the original.</p>
 */
public class WorldProviderBedrock extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.getById(Config.dimensionBedrockId);
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorBedrock(this.world);
    }

    @Override
    public int getAverageGroundLevel() {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z) {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return false;
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return new BlockPos(0, 255, 0);
    }
}
