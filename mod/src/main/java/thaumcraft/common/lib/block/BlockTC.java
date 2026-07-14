package thaumcraft.common.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

/**
 * Base block for the TC4 port. Uses a single generic {@code meta} PropertyInteger (0..15)
 * so ported blocks keep their original metadata-driven behavior 1:1 instead of per-block
 * PropertyEnum states. Blockstate JSONs map {@code meta=N} to a model.
 */
public class BlockTC extends Block {
    public static final PropertyInteger META = PropertyInteger.create("meta", 0, 15);

    public BlockTC(Material material) {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(META, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, META);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(META, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(META);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(META);
    }
}
