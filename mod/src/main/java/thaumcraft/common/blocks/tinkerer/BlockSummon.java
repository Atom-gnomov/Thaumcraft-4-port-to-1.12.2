package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileSummon;

/**
 * Tablet of Necromancy — ported from Thaumic Tinkerer's {@code BlockSummon}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Ring it with pedestals holding Soul Aspects and it puts creatures back
 * together out of them — see {@link TileSummon}. Redstone stops it.</p>
 */
public class BlockSummon extends BlockContainer {

    /** The original's {@code setBlockBounds(0, 0, 0, 1, 1F / 16F * 2F, 1)} — a two-pixel tablet. */
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D / 16.0D, 1.0D);

    public BlockSummon() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSummon();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
