package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileMobilizer;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Levitational Locomotive — ported from Thaumic Tinkerer's
 * {@code BlockMobilizer} (pixlepix / nekosune, originally Vazkii).
 *
 * @see TileMobilizer
 */
public class BlockMobilizer extends BlockContainer {

    public BlockMobilizer() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMobilizer();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * A locomotive moves by removing and replacing itself, so the tile can
     * outlive its block for a tick. Marking it dead on the way out is what
     * stops that ghost from carrying on.
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMobilizer) {
            ((TileMobilizer) tile).dead = true;
        }
        super.breakBlock(world, pos, state);
    }
}
