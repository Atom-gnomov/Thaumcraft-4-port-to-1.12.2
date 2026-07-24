package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileFunnel;

/**
 * Funnel — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for 1.12.2.
 * A full-cube block that vacuums dropped items above it into the inventory
 * beneath it (see {@link TileFunnel}).
 */
public class BlockFunnel extends BlockContainer {

    public BlockFunnel() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setSoundType(net.minecraft.block.SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFunnel();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
