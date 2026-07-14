package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
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
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: dungeon loot containers (urn / crate). Meta 0..2 = loot tier (texture side_{0..2}).
// TODO Phase 3: real loot on break via Utils.generateLoot(meta, rand) + TC4 loot tables;
//   worldgen placement is Phase 4. For now it drops itself so it is retrievable.
public class BlockLoot extends BlockTC {
    private static final AxisAlignedBB URN_AABB =
            new AxisAlignedBB(2 / 16.0, 1 / 16.0, 2 / 16.0, 14 / 16.0, 13 / 16.0, 14 / 16.0);
    private static final AxisAlignedBB CRATE_AABB =
            new AxisAlignedBB(1 / 16.0, 0.0, 1 / 16.0, 15 / 16.0, 14 / 16.0, 15 / 16.0);

    private final boolean urn;

    public BlockLoot(Material material, boolean urn, SoundType sound) {
        super(material);
        this.urn = urn;
        this.setHardness(0.15f);
        this.setResistance(0.0f);
        this.setSoundType(sound);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 2; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.urn ? URN_AABB : CRATE_AABB;
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
        // Urn/crate textures have transparent regions; CUTOUT stops them rendering as black.
        return BlockRenderLayer.CUTOUT;
    }
}
