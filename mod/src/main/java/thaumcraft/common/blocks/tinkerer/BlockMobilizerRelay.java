package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileMobilizerRelay;

/**
 * Levitational Locomotive Relay — ported from Thaumic Tinkerer's
 * {@code BlockMobilizerRelay} (pixlepix / nekosune, originally Vazkii).
 * Two of these facing each other define the track a Locomotive runs on.
 *
 * @see TileMobilizerRelay
 */
public class BlockMobilizerRelay extends BlockContainer {

    public BlockMobilizerRelay() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMobilizerRelay();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
