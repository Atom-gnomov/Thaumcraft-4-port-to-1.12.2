package thaumcraft.common.blocks;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import thaumcraft.common.Thaumcraft;

// Phase 1 port: cosmetic stairs (arcane stone, ancient/eldritch stone, greatwood, silverwood).
// BlockStairs copies hardness/resistance/sound from the model state's block; texture comes from the blockstate JSON.
public class BlockStairsTC extends BlockStairs {
    public BlockStairsTC(IBlockState modelState) {
        super(modelState);
        this.setCreativeTab(Thaumcraft.tabTC);
    }
}
