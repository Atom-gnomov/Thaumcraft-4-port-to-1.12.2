package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: coloured candle (meta 0..15 = dye colour). Light source (14), no collision,
// small centred box; must sit on a solid top surface and pops off when its support is removed.
// TODO Phase 3: IInfusionStabiliser (stabilises infusion altar), smoke/flame particles.
public class BlockCandle extends BlockTC {
    // 16 dye colours (from TC4 Utils.colors) — used by the block/item colour handler for tinting.
    public static final int[] COLORS = {
            0xF0F0F0, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 0x434343,
            0xA0A0A0, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 0x1E1B1B
    };

    private static final AxisAlignedBB CANDLE_AABB =
            new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 0.5, 0.625);

    public BlockCandle() {
        super(Material.CIRCUITS);
        this.setHardness(0.1f);
        this.setSoundType(SoundType.CLOTH);
        this.setLightLevel(0.95f); // -> light value 14
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m < 16; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CANDLE_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
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
        return BlockRenderLayer.CUTOUT;
    }

    // --- Placement / support ---------------------------------------------------------------

    private boolean hasSolidSupport(World world, BlockPos pos) {
        BlockPos down = pos.down();
        return world.getBlockState(down).isSideSolid(world, down, EnumFacing.UP);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && hasSolidSupport(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!hasSolidSupport(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }
}
