package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigSounds;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: aura crystal cluster (metas 0..5 = primal aspects, 6 = mixed) as a non-solid, aspect-tinted
//   crystal-cluster decoration. Glows (original light 0.5f) and has a cluster-sized collision box.
// TODO Phase 3 (aura/vis system): TileCrystal (aspect storage, vis node link), torch-like face attachment
//   (func_149742_c orientation), TESR renderer, spark FX, shard drops, growth/formation on nodes.
public class BlockCrystal extends BlockTC {
    // Cluster footprint — matches the visible cross model so the crystal is solid to walk into / selectable.
    private static final AxisAlignedBB CLUSTER_AABB = new AxisAlignedBB(0.15, 0.0, 0.15, 0.85, 0.95, 0.85);

    public BlockCrystal() {
        super(Material.GLASS);
        this.setResistance(2.0f);
        this.setHardness(0.3f);
        this.setSoundType(ConfigSounds.SOUND_CRYSTAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 6; m++) {
            items.add(new ItemStack(this, 1, m));
        }
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
    public int getLightValue(IBlockState state) {
        return 7;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CLUSTER_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CLUSTER_AABB;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
