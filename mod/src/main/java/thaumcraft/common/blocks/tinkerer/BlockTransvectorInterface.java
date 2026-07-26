package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileTransvectorInterface;

/**
 * Transvector Interface — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2. Stands in for another
 * block nearby; link it with the Transvector Connector.
 *
 * @see TileTransvectorInterface
 */
public class BlockTransvectorInterface extends BlockContainer {

    public BlockTransvectorInterface() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(12.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTransvectorInterface();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
