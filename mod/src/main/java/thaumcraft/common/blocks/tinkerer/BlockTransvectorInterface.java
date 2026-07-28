package thaumcraft.common.blocks.tinkerer;

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
 * <p>Camouflaged as upstream is, so it can be hidden in a wall — right-click
 * with a block to take its face. See {@link BlockCamo}.</p>
 *
 * @see TileTransvectorInterface
 */
public class BlockTransvectorInterface extends BlockCamo {

    public BlockTransvectorInterface() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
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
