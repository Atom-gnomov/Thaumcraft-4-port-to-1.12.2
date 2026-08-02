package thaumcraft.common.lib.endgame;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import thaumcraft.common.blocks.BlockEldritch;
import thaumcraft.common.config.ConfigBlocks;

/**
 * The Spires from Beyond — eldritch obelisks on the End's outer islands.
 * End Legacy module, phase 3 (new content, no 1.7.10 original — owner's
 * decision, {@code END_LEGACY_PLAN.md} §3).
 *
 * <p>Built entirely from blocks the port already ships: ancient stone
 * ({@code blockCosmeticSolid} 11–13), an eldritch crab spawner
 * ({@code blockEldritch} meta 9) sealed in the crown, and loot urns at the
 * base. Deliberately <em>not</em> prismarine-and-guardians and deliberately
 * not an end city: the End here reads as the Outer Lands' second face.</p>
 *
 * <p>Placement rules: outer islands only (past the vanilla end-city ring, so
 * the central island and the dragon stay vanilla), far apart, on a flat patch
 * of end stone.</p>
 */
public final class WorldGenEndSpires {

    /** Chunks this far from zero are outer-island territory (vanilla cities start ~64). */
    public static final int OUTER_ISLAND_CHUNKS = 64;
    /** One roll in this many chunks even tries to place a spire. */
    public static final int RARITY = 28;
    public static final int MIN_HEIGHT = 8;
    public static final int MAX_HEIGHT = 14;

    private static final int ANCIENT_STONE = 11;
    private static final int ANCIENT_PATTERN = 12;
    private static final int ANCIENT_STONE_B = 13;
    private static final int CRAB_SPAWNER = 9;

    private WorldGenEndSpires() {
    }

    public static void generate(World world, Random random, int chunkX, int chunkZ) {
        if (Math.abs(chunkX) < OUTER_ISLAND_CHUNKS && Math.abs(chunkZ) < OUTER_ISLAND_CHUNKS) {
            return;
        }
        if (random.nextInt(RARITY) != 0) {
            return;
        }
        int x = chunkX * 16 + 4 + random.nextInt(8);
        int z = chunkZ * 16 + 4 + random.nextInt(8);
        BlockPos base = findEndStoneSurface(world, x, z);
        if (base == null) {
            return;
        }
        buildSpire(world, random, base);
    }

    /** The highest end-stone column at (x, z) with enough flat ground around it. */
    private static BlockPos findEndStoneSurface(World world, int x, int z) {
        for (int y = 80; y > 20; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos).getBlock() != Blocks.END_STONE) {
                continue;
            }
            if (!world.isAirBlock(pos.up())) {
                return null;
            }
            // A spire needs footing: all four diagonal corners of the 3×3 base.
            for (BlockPos corner : new BlockPos[]{
                    pos.add(1, 0, 1), pos.add(-1, 0, 1), pos.add(1, 0, -1), pos.add(-1, 0, -1)}) {
                if (world.getBlockState(corner).getBlock() != Blocks.END_STONE) {
                    return null;
                }
            }
            return pos;
        }
        return null;
    }

    private static void buildSpire(World world, Random random, BlockPos ground) {
        int height = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        IBlockState stone = ancient(ANCIENT_STONE);
        IBlockState pattern = ancient(ANCIENT_PATTERN);
        IBlockState stoneB = ancient(ANCIENT_STONE_B);

        // The 3×3 plinth, two layers, patterned band on top.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlockState(ground.add(dx, 1, dz), stone, 2);
                world.setBlockState(ground.add(dx, 2, dz), (dx == 0 && dz == 0) ? stone : pattern, 2);
            }
        }

        // The shaft, alternating textures the way the Outer Lands' own ruins do.
        for (int y = 3; y < height; y++) {
            world.setBlockState(ground.up(y), (y % 3 == 0) ? stoneB : stone, 2);
        }

        // The crown: the spawner sealed in a cage of patterned stone.
        BlockPos crown = ground.up(height);
        world.setBlockState(crown, ConfigBlocks.blockEldritch.getDefaultState()
                .withProperty(BlockEldritch.TYPE, CRAB_SPAWNER), 2);
        world.setBlockState(crown.up(), pattern, 2);
        for (net.minecraft.util.EnumFacing side : net.minecraft.util.EnumFacing.HORIZONTALS) {
            world.setBlockState(crown.offset(side), pattern, 2);
        }

        // Urns at the base — the reason to climb down again.
        int urns = 2 + random.nextInt(2);
        for (int i = 0; i < urns; i++) {
            BlockPos spot = ground.add(random.nextInt(5) - 2, 1, random.nextInt(5) - 2);
            if (world.isAirBlock(spot) && world.getBlockState(spot.down()).isFullBlock()) {
                world.setBlockState(spot, ConfigBlocks.blockLootUrn.getDefaultState()
                        .withProperty(thaumcraft.common.blocks.BlockLoot.TYPE,
                                random.nextInt(6) == 0 ? 2 : 1), 2);
            }
        }
    }

    private static IBlockState ancient(int meta) {
        return ConfigBlocks.blockCosmeticSolid.getDefaultState()
                .withProperty(BlockCosmeticSolid.TYPE, meta);
    }
}
