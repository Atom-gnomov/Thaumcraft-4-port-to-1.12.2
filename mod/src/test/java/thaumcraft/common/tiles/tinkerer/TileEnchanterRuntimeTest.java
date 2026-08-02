package thaumcraft.common.tiles.tinkerer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Enchantments;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Runs the Osmotic Enchanter for real, rather than reading the source and
 * reasoning about it.
 *
 * <p>What was broken was the sync: the tile never told the client anything, so
 * the screen — which reads its queue, its cost and its start button off the
 * client's copy — was inert. That is asserted here by queuing an actual
 * enchantment and watching for the push.</p>
 *
 * <p>The multiblock was <em>not</em> broken, and these tests exist partly to
 * say so. Reading the port's {@code BlockCosmeticSolid.types} array suggested
 * the pillar check wanted the wrong block, because that array had
 * {@code obsidianTile} and {@code obsidianTotem} the wrong way round. Stacking
 * a real ring of each settles it: totems count, tiles do not, and the code was
 * right all along. A guard that only quoted the source could not have told the
 * difference.</p>
 */
public class TileEnchanterRuntimeTest {

    private static final BlockPos ENCHANTER = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        if (ConfigBlocks.blockCosmeticSolid == null) {
            ConfigBlocks.blockCosmeticSolid = new BlockCosmeticSolid();
        }
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.blockAiry = new BlockAiry();
        }
    }

    /** Six pillars of Obsidian Totems capped with Nitor — the original's shape. */
    @Test
    public void aRingOfTotemPillarsSatisfiesTheMultiblock() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        raisePillars(world, 6, 3, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);

        assertEquals(6, enchanter.countPillars());
    }

    /**
     * The other half of the same claim. "Totems count" and "tiles do not" are
     * separate statements, and only the pair pins which metadata is which —
     * which is the whole point here, given that the port's own name table said
     * the opposite.
     */
    @Test
    public void pillarsOfObsidianTileDoNotCount() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        raisePillars(world, 6, 3, 1);   // obsidianTile

        assertEquals(0, enchanter.countPillars());
    }

    /** Upstream's bounds: shorter than two totems or taller than twelve is not a pillar. */
    @Test
    public void pillarsOutsideTwoToTwelveAreRejected() {
        for (int height : new int[]{1, 13}) {
            TestWorld world = new TestWorld();
            TileEnchanter enchanter = world.placeEnchanter();
            raisePillars(world, 6, height, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);
            assertEquals("a pillar of " + height + " totems must not count",
                    0, enchanter.countPillars());
        }
        for (int height : new int[]{2, 12}) {
            TestWorld world = new TestWorld();
            TileEnchanter enchanter = world.placeEnchanter();
            raisePillars(world, 6, height, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);
            assertEquals("a pillar of " + height + " totems must count",
                    6, enchanter.countPillars());
        }
    }

    /** A pillar left uncapped is not a pillar, however tall. */
    @Test
    public void pillarsWithoutNitorAreRejected() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        for (BlockPos base : pillarBases(6)) {
            for (int y = 0; y < 3; y++) {
                world.set(base.up(y), ConfigBlocks.blockCosmeticSolid.getDefaultState()
                        .withProperty(BlockCosmeticSolid.TYPE, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM));
            }
        }
        assertEquals(0, enchanter.countPillars());
    }

    /**
     * Queuing an enchantment has to reach the client, because the screen reads
     * its queue, its cost and its start button off the client's copy of the
     * tile. Without this the device is not glitchy — it is inert.
     */
    @Test
    public void queuingAnEnchantmentPushesTheTileToClients() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();

        enchanter.setEnchant(Enchantments.SHARPNESS, 0);

        assertTrue("the queue must have reached the tile",
                enchanter.getQueuedEnchantments().contains(Enchantments.SHARPNESS));
        assertTrue("and the tile must have been pushed to watching clients",
                world.notified.contains(ENCHANTER));
    }

    /** A run refuses without the structure, and takes it once the structure is there. */
    @Test
    public void startNeedsBothAQueueAndTheStructure() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        enchanter.getInventory().setStackInSlot(TileEnchanter.SLOT_TOOL,
                new ItemStack(Items.IRON_SWORD));
        enchanter.setEnchant(Enchantments.SHARPNESS, 0);

        assertFalse("no pillars, no run", enchanter.start());
        assertFalse(enchanter.isWorking());

        raisePillars(world, 6, 3, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);

        assertTrue("with the structure up, the run starts", enchanter.start());
        assertTrue(enchanter.isWorking());
        assertTrue("and it costs something", enchanter.getTotalCost().size() > 0);
    }

    /**
     * The whole device, end to end: tool in, wand in, pillars up, queue set,
     * start pressed — and the sword comes out sharp. Everything above tests one
     * joint; this tests that the joints are connected.
     */
    @Test
    public void aFullRunEnchantsTheTool() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        raisePillars(world, 6, 3, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);

        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        enchanter.getInventory().setStackInSlot(TileEnchanter.SLOT_TOOL, sword);
        enchanter.getInventory().setStackInSlot(TileEnchanter.SLOT_WAND, chargedWand());
        enchanter.setEnchant(Enchantments.SHARPNESS, 0);

        assertTrue(enchanter.start());
        for (int tick = 0; tick < 200 && enchanter.isWorking(); tick++) {
            enchanter.update();
        }

        assertFalse("the run must finish rather than stall", enchanter.isWorking());
        assertEquals("the sword must come out enchanted", 1,
                EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS,
                        enchanter.getInventory().getStackInSlot(TileEnchanter.SLOT_TOOL)));
        assertTrue("and the queue must be cleared behind it",
                enchanter.getQueuedEnchantments().isEmpty());
    }

    /** Paying for a run has to actually cost the wand its vis. */
    @Test
    public void theRunIsPaidForOutOfTheWand() {
        TestWorld world = new TestWorld();
        TileEnchanter enchanter = world.placeEnchanter();
        raisePillars(world, 6, 3, BlockCosmeticSolid.TYPE_OBSIDIAN_TOTEM);

        ItemStack wand = chargedWand();
        int before = ItemWandCasting.getVis(wand, Aspect.ORDER);
        enchanter.getInventory().setStackInSlot(TileEnchanter.SLOT_TOOL, new ItemStack(Items.IRON_SWORD));
        enchanter.getInventory().setStackInSlot(TileEnchanter.SLOT_WAND, wand);
        enchanter.setEnchant(Enchantments.SHARPNESS, 0);
        enchanter.start();
        // Read the price before the run: finishing clears it along with the queue.
        // Sharpness costs Ordo alone — base 10, and level 1 multiplies by 1.2.
        assertEquals(12, enchanter.getTotalCost().getAmount(Aspect.ORDER));

        for (int tick = 0; tick < 200 && enchanter.isWorking(); tick++) {
            enchanter.update();
        }

        assertEquals("twelve points at a hundred units each",
                before - 1200, ItemWandCasting.getVis(wand, Aspect.ORDER));
    }

    // ---- fixtures ----

    /**
     * A plain wand filled to the brim with every primal.
     *
     * <p>Filled to exactly its capacity, not past it: a rodless wand holds
     * 10000 units, and {@code addRealVis} silently clamps to that on the first
     * write. Seeding more than the wand can hold makes the very first point of
     * vis look as though it cost ninety thousand.</p>
     */
    private static ItemStack chargedWand() {
        ItemWandCasting item = new ItemWandCasting();
        ItemStack wand = new ItemStack(item);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            item.storeVis(wand, aspect, ItemWandCasting.getMaxVis(wand));
        }
        return wand;
    }

    /** Column bases spread around the enchanter, all inside the original's radius of four. */
    private static BlockPos[] pillarBases(int count) {
        BlockPos[] offsets = {
                ENCHANTER.add(4, 0, 0), ENCHANTER.add(-4, 0, 0),
                ENCHANTER.add(0, 0, 4), ENCHANTER.add(0, 0, -4),
                ENCHANTER.add(3, 0, 3), ENCHANTER.add(-3, 0, -3),
                ENCHANTER.add(3, 0, -3), ENCHANTER.add(-3, 0, 3),
        };
        BlockPos[] bases = new BlockPos[count];
        System.arraycopy(offsets, 0, bases, 0, count);
        return bases;
    }

    private static void raisePillars(TestWorld world, int count, int height, int totemMeta) {
        IBlockState totem = ConfigBlocks.blockCosmeticSolid.getDefaultState()
                .withProperty(BlockCosmeticSolid.TYPE, totemMeta);
        IBlockState nitor = ConfigBlocks.blockAiry.getDefaultState()
                .withProperty(BlockAiry.TYPE, BlockAiry.TYPE_NITOR);
        for (BlockPos base : pillarBases(count)) {
            for (int y = 0; y < height; y++) {
                world.set(base.up(y), totem);
            }
            world.set(base.up(height), nitor);
        }
    }

    /** A world that remembers what was placed in it and who was told about it. */
    private static class TestWorld extends World {

        private final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        final Set<BlockPos> notified = new HashSet<>();

        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "enchanter"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        TileEnchanter placeEnchanter() {
            TileEnchanter enchanter = new TileEnchanter();
            enchanter.setWorld(this);
            enchanter.setPos(ENCHANTER);
            this.tiles.put(ENCHANTER, enchanter);
            return enchanter;
        }

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
            return this.tiles.get(pos);
        }

        /** No listeners here, and no need for any: the fake world is mute. */
        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound,
                              SoundCategory category, float volume, float pitch) {
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.notified.add(pos.toImmutable());
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) {
                    return null;
                }

                @Override
                public Chunk provideChunk(int x, int z) {
                    return null;
                }

                @Override
                public boolean tick() {
                    return false;
                }

                @Override
                public String makeString() {
                    return "enchanter_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return false;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return false;
        }
    }
}
