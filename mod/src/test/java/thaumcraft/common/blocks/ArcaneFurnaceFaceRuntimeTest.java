package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.lib.utils.BlockUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * The Infernal Furnace's face, run against a real 3×3×3 multiblock.
 *
 * <p>The player's report was "the face is gone again", with a hunch about a
 * direction dependency — and that hunch was exactly right. The maw is drawn by
 * the <em>eight</em> wall blocks ringing the nozzle, each shifting three
 * columns into the texture atlas on its outward face. Upstream decides that
 * with {@code isBlockTouchingOnSide}, which for a horizontal face checks all
 * eight in-plane neighbours, diagonals included. The port had rewritten it as
 * "which direct neighbour is the nozzle, and does its direction equal the face
 * being drawn" — two conditions that are never both true for a visible face:
 * outward faces point at air, and the four diagonal blocks do not touch the
 * nozzle at all. Result: no bit ever set where it mattered, no maw anywhere,
 * on any side of the furnace.</p>
 *
 * <p>Here the standard furnace is assembled exactly as
 * {@code WandManager.replaceArcaneFurnace} builds it — layer metas 1..9, lava
 * core 0, one wall-centre nozzle 10 — and the mask is asserted per block.</p>
 */
public class ArcaneFurnaceFaceRuntimeTest {

    private static BlockArcaneFurnace furnace;

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        furnace = new BlockArcaneFurnace();
    }

    /**
     * The furnace as the wand builds it, nozzle in the north wall's centre:
     * per layer, metas step 1..9 west-to-east then north-to-south; the centre
     * of the cube is the lava core (0); the middle-layer north-centre block is
     * the nozzle (10).
     */
    private static TestAccess standardFurnace() {
        TestAccess world = new TestAccess();
        for (int y = 0; y < 3; y++) {
            int step = 1;
            for (int z = 0; z < 3; z++) {
                for (int x = 0; x < 3; x++) {
                    int meta = step;
                    if (x == 1 && y == 1 && z == 1) {
                        meta = 0;
                    } else if (x == 1 && y == 1 && z == 0) {
                        meta = 10;
                    }
                    world.set(new BlockPos(x, y, z), furnace.getStateFromMeta(meta));
                    step++;
                }
            }
        }
        return world;
    }

    /** Every one of the eight blocks ringing the nozzle wears the maw bit on its outward face. */
    @Test
    public void allEightBlocksAroundTheNozzleDrawTheMaw() {
        TestAccess world = standardFurnace();
        int north = EnumFacing.NORTH.getIndex();

        BlockPos[] ring = {
                new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(2, 0, 0),
                new BlockPos(0, 1, 0), new BlockPos(2, 1, 0),
                new BlockPos(0, 2, 0), new BlockPos(1, 2, 0), new BlockPos(2, 2, 0),
        };
        for (BlockPos pos : ring) {
            assertEquals("north face at " + pos + " must carry the maw",
                    1, nozzleMask(world, pos) >> north & 1);
        }
    }

    /**
     * The regression's sharpest edge: the four diagonal blocks. They are not
     * direct neighbours of the nozzle in any direction, so any 6-adjacency
     * rewrite silently drops exactly these four corners of the maw.
     */
    @Test
    public void theDiagonalCornersAreNotForgotten() {
        TestAccess world = standardFurnace();
        int north = EnumFacing.NORTH.getIndex();
        for (BlockPos corner : new BlockPos[]{
                new BlockPos(0, 0, 0), new BlockPos(2, 0, 0),
                new BlockPos(0, 2, 0), new BlockPos(2, 2, 0)}) {
            assertEquals("diagonal corner " + corner,
                    1, nozzleMask(world, corner) >> north & 1);
        }
    }

    /** Walls without the nozzle stay plain — the maw must not smear around corners. */
    @Test
    public void otherWallsStayPlain() {
        TestAccess world = standardFurnace();
        int south = EnumFacing.SOUTH.getIndex();
        int west = EnumFacing.WEST.getIndex();

        assertEquals("south wall centre, south face",
                0, nozzleMask(world, new BlockPos(1, 1, 2)) >> south & 1);
        assertEquals("west wall centre, west face",
                0, nozzleMask(world, new BlockPos(0, 1, 1)) >> west & 1);
        // The north-west edge block does carry the maw on its north face
        // (it rings the nozzle), but not on its west face — a different plane.
        assertEquals("ring block, but the wrong plane",
                0, nozzleMask(world, new BlockPos(0, 1, 0)) >> west & 1);
    }

    /** Vertical faces keep upstream's one-block rule: only directly above/below. */
    @Test
    public void verticalFacesUseTheDirectNeighbourOnly() {
        TestAccess world = standardFurnace();
        int down = EnumFacing.DOWN.getIndex();
        int up = EnumFacing.UP.getIndex();

        assertEquals("block directly above the nozzle, down face",
                1, nozzleMask(world, new BlockPos(1, 2, 0)) >> down & 1);
        assertEquals("block directly below the nozzle, up face",
                1, nozzleMask(world, new BlockPos(1, 0, 0)) >> up & 1);
        assertEquals("diagonal block, down face — not a direct neighbour",
                0, nozzleMask(world, new BlockPos(0, 2, 0)) >> down & 1);
    }

    // ---- helpers ----

    /** The same computation the block hangs on its extended state. */
    private static int nozzleMask(TestAccess world, BlockPos pos) {
        int mask = 0;
        for (int side = 0; side < 6; side++) {
            if (BlockUtils.isBlockTouchingOnSide(
                    world, pos.getX(), pos.getY(), pos.getZ(), furnace, 10, side)) {
                mask |= 1 << side;
            }
        }
        return mask;
    }

    private static final class TestAccess implements IBlockAccess {

        private final Map<BlockPos, IBlockState> blocks = new HashMap<>();

        void set(BlockPos pos, IBlockState state) {
            this.blocks.put(pos.toImmutable(), state);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.blocks.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return getBlockState(pos).getBlock() == Blocks.AIR;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return null;
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Override
        public WorldType getWorldType() {
            return WorldType.DEFAULT;
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean def) {
            return def;
        }
    }
}
