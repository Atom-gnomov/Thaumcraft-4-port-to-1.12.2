package thaumcraft.common.lib.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

// Base for cross-model plant blocks using the shared generic meta property (0..15). See BlockTC.
public class BlockTCBush extends BlockBush {
    public BlockTCBush() {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockTC.META, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockTC.META);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockTC.META, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockTC.META);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(BlockTC.META);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
