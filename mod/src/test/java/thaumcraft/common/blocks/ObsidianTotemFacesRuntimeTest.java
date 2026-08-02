package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Obsidian Totem's side textures, checked against the original's own
 * expression rather than against a retelling of it.
 *
 * <p>In 1.7.10 the block answered {@code getIcon} per position and per face, so
 * a stack of totems came out patterned. The port had a plain
 * {@code cube_column} and every totem in the world looked identical — four of
 * the six textures were shipped and referenced by nothing.</p>
 *
 * <p>{@link #theBodyPatternMatchesTheOriginalsArithmetic()} is the load-bearing
 * one. It transcribes the original's line
 * {@code icon[2 + Math.abs((side + x % 4 + z % 4 + y % 4) % 4)]} into the test
 * and compares, position by position, over a range that crosses zero on all
 * three axes. Java's {@code %} keeps the sign of the dividend, and the
 * {@code Math.abs} is applied <em>after</em> the second remainder, not to the
 * coordinates — so a "tidied" floor-modulus version agrees on the positive
 * octant and diverges everywhere else. Testing only near the origin would have
 * missed it.</p>
 */
public class ObsidianTotemFacesRuntimeTest {

    private static BlockCosmeticSolid block;

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        block = new BlockCosmeticSolid();
    }

    /** A totem with another totem on top is drawn shaded, on every side. */
    @Test
    public void aTotemUnderAnotherIsShaded() {
        TestAccess world = new TestAccess();
        BlockPos pos = new BlockPos(3, 70, 5);
        world.set(pos, totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
        world.set(pos.up(), totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
        world.set(pos.down(), totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));

        assertAllSides(world, pos, BlockCosmeticSolid.TOTEM_SHADED);
    }

    /** A totem standing on something that is not a totem is a base. */
    @Test
    public void aTotemOnTheGroundIsABase() {
        TestAccess world = new TestAccess();
        BlockPos pos = new BlockPos(3, 70, 5);
        world.set(pos, totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
        world.set(pos.up(), Blocks.AIR.getDefaultState());
        world.set(pos.down(), Blocks.STONE.getDefaultState());

        assertAllSides(world, pos, BlockCosmeticSolid.TOTEM_BASE);
    }

    /**
     * The charged totem counts as a totem on both sides of the test — the
     * original checks metadata 0 and 8 together everywhere it checks either, so
     * a mixed column still reads as one continuous pillar.
     */
    @Test
    public void theChargedTotemStacksWithThePlainOne() {
        TestAccess world = new TestAccess();
        BlockPos pos = new BlockPos(3, 70, 5);
        world.set(pos, totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
        world.set(pos.up(), totem(BlockCosmeticSolid.TYPE_CHARGED_OBSIDIAN_TOTEM));
        assertAllSides(world, pos, BlockCosmeticSolid.TOTEM_SHADED);

        TestAccess onCharged = new TestAccess();
        onCharged.set(pos, totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
        onCharged.set(pos.up(), Blocks.AIR.getDefaultState());
        onCharged.set(pos.down(), totem(BlockCosmeticSolid.TYPE_CHARGED_OBSIDIAN_TOTEM));
        // Standing on a charged totem is standing on a totem: body, not base.
        assertTrue(sideTexture(onCharged, pos, EnumFacing.NORTH) >= BlockCosmeticSolid.TOTEM_BODY_FIRST);
    }

    /** The middle of a column picks one of four bodies — by the original's arithmetic. */
    @Test
    public void theBodyPatternMatchesTheOriginalsArithmetic() {
        int compared = 0;
        for (int x = -9; x <= 9; x++) {
            for (int y = 60; y <= 78; y++) {
                for (int z = -9; z <= 9; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    TestAccess world = new TestAccess();
                    world.set(pos, totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
                    world.set(pos.up(), Blocks.AIR.getDefaultState());
                    world.set(pos.down(), totem(BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));

                    for (EnumFacing side : new EnumFacing[]{
                            EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST}) {
                        assertEquals("side " + side + " at " + pos,
                                expectedBody(side.getIndex(), x, y, z),
                                sideTexture(world, pos, side));
                        compared++;
                    }
                }
            }
        }
        assertTrue("the sweep must actually have crossed the origin", compared > 20000);
    }

    /**
     * The original's line, transcribed:
     * {@code this.icon[2 + Math.abs((side + x % 4 + z % 4 + y % 4) % 4)]}.
     *
     * <p>The port numbers the same six textures from zero where the original
     * numbered from {@code icon[1]}, so the expected index is one lower.</p>
     */
    private static int expectedBody(int side, int x, int y, int z) {
        int originalIcon = 2 + Math.abs((side + x % 4 + z % 4 + y % 4) % 4);
        return originalIcon - 1;
    }

    /** Metadata that is not a totem is left with no unlisted values at all. */
    @Test
    public void otherSubtypesAreUntouched() {
        TestAccess world = new TestAccess();
        BlockPos pos = new BlockPos(0, 70, 0);
        IBlockState tile = totem(1);   // obsidianTile
        world.set(pos, tile);

        IBlockState extended = block.getExtendedState(tile, world, pos);
        assertEquals("a non-totem must come back as the plain state", tile, extended);
    }

    // ---- helpers ----

    private static IBlockState totem(int meta) {
        return block.getDefaultState().withProperty(BlockCosmeticSolid.TYPE, meta);
    }

    private static void assertAllSides(TestAccess world, BlockPos pos, int expected) {
        for (EnumFacing side : new EnumFacing[]{
                EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST}) {
            assertEquals("side " + side, expected, sideTexture(world, pos, side));
        }
    }

    private static int sideTexture(TestAccess world, BlockPos pos, EnumFacing side) {
        IBlockState extended = block.getExtendedState(world.getBlockState(pos), world, pos);
        assertTrue("a totem must produce an extended state", extended instanceof IExtendedBlockState);
        IUnlistedProperty<Integer> property;
        switch (side) {
            case NORTH:
                property = BlockCosmeticSolid.TOTEM_NORTH;
                break;
            case SOUTH:
                property = BlockCosmeticSolid.TOTEM_SOUTH;
                break;
            case WEST:
                property = BlockCosmeticSolid.TOTEM_WEST;
                break;
            default:
                property = BlockCosmeticSolid.TOTEM_EAST;
                break;
        }
        Integer value = ((IExtendedBlockState) extended).getValue(property);
        assertTrue("side " + side + " must have been assigned a texture", value != null);
        return value;
    }

    /** The smallest thing {@code getExtendedState} will accept. */
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
