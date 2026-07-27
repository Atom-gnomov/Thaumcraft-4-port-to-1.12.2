package thaumcraft.common.lib.world.dim.bedrock;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

/**
 * Chunk generator for the Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code ChunkProviderBedrock} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The world is solid bedrock, all 256 layers of it, exactly as the original
 * built it from {@code new FlatLayerInfo(256, Blocks.bedrock)}. Everything worth
 * having is placed inside that solid mass afterwards by
 * {@link OreClusterGenerator}.</p>
 */
public class ChunkGeneratorBedrock implements IChunkGenerator {

    private final World world;

    public ChunkGeneratorBedrock(World world) {
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer primer = new ChunkPrimer();
        for (int y = 0; y < 256; y++) {
            for (int bx = 0; bx < 16; bx++) {
                for (int bz = 0; bz < 16; bz++) {
                    primer.setBlockState(bx, y, bz, Blocks.BEDROCK.getDefaultState());
                }
            }
        }
        Chunk chunk = new Chunk(this.world, primer, x, z);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        return this.world.getBiome(pos).getSpawnableList(type);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World world, String name, BlockPos pos, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
    }

    @Override
    public boolean isInsideStructure(World world, String name, BlockPos pos) {
        return false;
    }
}
