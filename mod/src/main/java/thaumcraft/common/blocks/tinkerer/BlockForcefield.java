package thaumcraft.common.blocks.tinkerer;

import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.tiles.tinkerer.TileForcefield;

/**
 * Forcefield — ported from Thaumic Tinkerer's {@code BlockForcefield}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A solid, invisible wall the Terra potion throws up between its drinker and
 * whoever they just hit. It is never crafted or held: it appears, stands for
 * three seconds, and goes.</p>
 */
public class BlockForcefield extends BlockContainer {

    public BlockForcefield() {
        super(Material.AIR);
        // Never in the creative tab — upstream's shouldDisplayInTab is false.
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileForcefield();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    /** Drops nothing, ever. */
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
    }

    @Override
    @Nullable
    public ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target,
                                  World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        return ItemStack.EMPTY;
    }
}
