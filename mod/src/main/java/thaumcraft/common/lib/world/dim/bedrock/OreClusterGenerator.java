package thaumcraft.common.lib.world.dim.bedrock;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import thaumcraft.common.config.Config;

/**
 * Ore clusters inside the Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code OreClusterGenerator} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Per chunk it seeds {@code density} clusters. Each picks one ore by weight
 * from {@link OreFrequency}, then makes two hundred attempts to place a vein of
 * up to twenty blocks between y=6 and y=250, replacing bedrock — the original's
 * numbers, unchanged.</p>
 */
public class OreClusterGenerator implements IWorldGenerator {

    private static final int ATTEMPTS = 200;
    private static final int MAX_VEIN = 20;
    private static final int MIN_Y = 6;
    private static final int Y_RANGE = 245;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!(world.provider instanceof WorldProviderBedrock)) {
            return;
        }
        for (int cluster = 0; cluster < Config.bedrockOreDensity; cluster++) {
            int x = 16 * chunkX + random.nextInt(16);
            int z = 16 * chunkZ + random.nextInt(16);
            ItemStack ore = OreFrequency.getRandomOre(random);
            if (ore == null || ore.isEmpty()) {
                continue;
            }
            Block block = Block.getBlockFromItem(ore.getItem());
            if (block == Blocks.AIR) {
                continue;
            }
            for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
                int y = random.nextInt(Y_RANGE) + MIN_Y;
                new WorldGenMinable(
                        block.getStateFromMeta(ore.getItemDamage()),
                        random.nextInt(MAX_VEIN),
                        input -> input != null && input.getBlock() == Blocks.BEDROCK)
                        .generate(world, random, new BlockPos(x, y, z));
            }
        }
    }
}
