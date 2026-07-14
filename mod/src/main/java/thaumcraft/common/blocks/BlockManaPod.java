package thaumcraft.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: mana pod — the "mana bean" crop that grows hanging under greatwood/silverwood in magical
// biomes. Worldgen-only: no creative entry and no ItemBlock (breaking it yields Mana Beans in Phase 3).
//
// Faithful reproduction of the original per the user's gen-block checklist:
//   * FORM    — render type 1 (cross/X billboard) via block/cross; collision box hangs from the top (y=1) and
//               reaches further down as the pod matures (meta 0 = y 0.75-1.0 … meta 7 = y 0.125-1.0), x/z 0.25-0.75.
//   * TEXTURE — meta 0 = manapod_stem_0, meta 1 = manapod_stem_1, meta 2..7 = manapod_stem_2 (func_149691_a).
//   * COLOUR  — no tint (original registers no colour handler).
//   * LIGHT   — light level == growth stage (getLightValue returns meta: 0 → 0 … 7 → 7).
//   * SOUND   — inherits Block's default STONE step sound: TC4's BlockManaPod never calls setStepSound, so it is
//               NOT a plant sound. Left unset here to match 1:1 (do not "fix" to PLANT).
// Hardness scales with age: 0.5 / (8 - meta) → tougher when riper (meta 0 ≈ 0.0625 … meta 7 = 0.5).
//
// TODO Phase 3: TileManaPod (aspect storage + checkGrowth), Mana Bean drops (meta ≥ 2, meta 7 doubles ~66%),
//   ItemManaBean pick-block (func_149694_d), aspect inheritance from adjacent pods.
public class BlockManaPod extends BlockTC {

    // Collision/selection box per growth stage — anchored at the top (maxY = 1.0), growing downward. From
    // func_149719_a: minY = W12,W10,W8,W6,W5,W4,W3,W2 (i.e. 12/16 … 2/16) for meta 0..7.
    private static final AxisAlignedBB[] POD_AABB = new AxisAlignedBB[8];
    static {
        final double[] minY16 = { 12.0, 10.0, 8.0, 6.0, 5.0, 4.0, 3.0, 2.0 };
        for (int i = 0; i < 8; i++) {
            POD_AABB[i] = new AxisAlignedBB(0.25, minY16[i] / 16.0, 0.25, 0.75, 1.0, 0.75);
        }
    }

    public BlockManaPod() {
        super(Material.PLANTS);
        this.setHardness(0.5f);       // base; scaled per-meta in getBlockHardness
        this.setTickRandomly(true);
        // No setSoundType (→ Block default STONE) and no setCreativeTab, matching the original 1:1.
    }

    private static int stage(IBlockState state) {
        int meta = state.getValue(META);
        return meta < 0 ? 0 : (meta > 7 ? 7 : meta);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return POD_AABB[stage(state)];
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Original func_149668_a returns the (non-null) per-stage bounds as the collision box.
        return POD_AABB[stage(state)];
    }

    @Override
    public int getLightValue(IBlockState state) {
        // Light level equals the growth stage (meta 0..7).
        return state.getValue(META);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        // 0.5 / (8 - meta): riper pods are tougher.
        return 0.5f / (8 - stage(state));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        // Cross billboard uses a texture with transparency.
        return BlockRenderLayer.CUTOUT;
    }

    // --- Placement / support: must hang below a magical log (vanilla oak/spruce or greatwood/silverwood) in a
    //     MAGICAL biome, exactly like the original func_149718_j / func_149707_d. ---

    private boolean canBlockStay(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);
        boolean magical = biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL);
        Block above = world.getBlockState(pos.up()).getBlock();
        boolean log = above == Blocks.LOG || above == Blocks.LOG2 || above == ConfigBlocks.blockMagicalLog;
        return magical && log;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && canBlockStay(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote && !canBlockStay(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        if (!canBlockStay(world, pos)) {
            world.setBlockToAir(pos);
        }
        // TODO Phase 3: TileManaPod.checkGrowth() (1-in-30 chance) to advance meta / set aspect.
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // Phase 1: drops nothing (original drops air here; Mana Beans come from getDrops in Phase 3).
        return Items.AIR;
    }
}
