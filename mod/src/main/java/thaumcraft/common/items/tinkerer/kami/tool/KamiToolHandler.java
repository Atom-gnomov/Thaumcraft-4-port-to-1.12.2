package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.dim.bedrock.WorldProviderBedrock;

/**
 * Shared behaviour of the advanced ichor tools — transcribed from Thaumic
 * Tinkerer's {@code ToolHandler} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Each advanced tool carries three modes in its item damage and only ever
 * breaks blocks whose material is on its own list, so a pickaxe cannot clear a
 * forest. Breaking bedrock is what opens the way to the Bedrock dimension.</p>
 *
 * <p>Two of the original's quirks are reproduced deliberately rather than
 * tidied away, because they are observable in game:</p>
 * <ul>
 *   <li>{@link #removeBlocksInIteration} skips a position when any one of the
 *       loop offsets happens to equal the corresponding <em>absolute</em>
 *       coordinate of the block struck. Near the world origin, or at
 *       y&nbsp;=&nbsp;1&nbsp;.. 7, that leaves holes in the dug shape.</li>
 *   <li>{@code silk} and {@code fortune} are threaded through the call chain
 *       and never read — drops come from {@code harvestBlock}, which reads the
 *       held tool's own enchantments.</li>
 * </ul>
 */
public final class KamiToolHandler {

    public static final Material[] MATERIALS_PICK = {
            Material.ROCK, Material.IRON, Material.ICE, Material.GLASS, Material.PISTON, Material.ANVIL};
    public static final Material[] MATERIALS_SHOVEL = {
            Material.GRASS, Material.GROUND, Material.SAND, Material.SNOW, Material.CRAFTED_SNOW, Material.CLAY};
    public static final Material[] MATERIALS_AXE = {
            Material.CORAL, Material.LEAVES, Material.PLANTS, Material.WOOD};

    private KamiToolHandler() {
    }

    public static int getMode(ItemStack tool) {
        return tool.getItemDamage();
    }

    /** Three modes, wrapping back to the first — the original's cycle. */
    public static int getNextMode(int mode) {
        return mode == 2 ? 0 : mode + 1;
    }

    public static void changeMode(ItemStack tool) {
        tool.setItemDamage(getNextMode(getMode(tool)));
    }

    public static boolean isRightMaterial(Material material, Material[] materialsListing) {
        for (Material candidate : materialsListing) {
            if (material == candidate) {
                return true;
            }
        }
        return false;
    }

    /** {@code ttmisc.mode.<type>.<mode>} in the original; the type names differ per tool. */
    public static String getToolModeStr(String type, ItemStack stack) {
        return I18n.translateToLocal("tc.kami.mode." + type + "." + getMode(stack));
    }

    /**
     * Breaks every block in the half-open box {@code [xs, xe) × [ys, ye) ×
     * [zs, ze)} offset from the block struck, restricted to {@code block} when
     * that is non-null.
     */
    public static void removeBlocksInIteration(EntityPlayer player, World world, BlockPos hit,
                                               int xs, int ys, int zs, int xe, int ye, int ze,
                                               Block block, Material[] materialsListing,
                                               boolean silk, int fortune) {
        for (int x1 = xs; x1 < xe; x1++) {
            for (int y1 = ys; y1 < ye; y1++) {
                for (int z1 = zs; z1 < ze; z1++) {
                    // The original's guard, verbatim: absolute coordinate vs. loop offset.
                    if (hit.getX() != x1 && hit.getY() != y1 && hit.getZ() != z1) {
                        removeBlockWithDrops(player, world, hit.add(x1, y1, z1),
                                block, materialsListing, silk, fortune);
                    }
                }
            }
        }
    }

    /**
     * Breaks one block as the player would have, and turns bedrock into the
     * portal at the bottom of a surface world or into air inside the Bedrock
     * dimension.
     */
    public static void removeBlockWithDrops(EntityPlayer player, World world, BlockPos pos,
                                            Block block, Material[] materialsListing,
                                            boolean silk, int fortune) {
        if (!world.isBlockLoaded(pos)) {
            return;
        }
        IBlockState state = world.getBlockState(pos);
        Block found = state.getBlock();
        if (block != null && found != block) {
            return;
        }
        boolean bedrockInDimension = found == Blocks.BEDROCK
                && pos.getY() <= 253 && world.provider instanceof WorldProviderBedrock;
        if (found.isAir(state, world, pos)
                || (state.getPlayerRelativeBlockHardness(player, world, pos) == 0.0F && !bedrockInDimension)) {
            return;
        }
        if (!found.canHarvestBlock(world, pos, player)
                || !isRightMaterial(state.getMaterial(), materialsListing)) {
            return;
        }
        // The original tests the *filter* block here, not the one it found.
        if (block == Blocks.BEDROCK
                && ((world.provider.isSurfaceWorld() && pos.getY() < 5)
                || (pos.getY() > 253 && world.provider instanceof WorldProviderBedrock))) {
            world.setBlockState(pos, ConfigBlocks.blockBedrockPortal.getDefaultState(), 3);
        }
        if (world.provider instanceof WorldProviderBedrock && found == Blocks.BEDROCK && pos.getY() <= 253) {
            world.setBlockToAir(pos);
        }
        if (!player.capabilities.isCreativeMode && found != Blocks.BEDROCK) {
            // The original's order: removedByPlayer, onBlockDestroyedByPlayer
            // (onPlayerDestroy here), harvestBlock, onBlockHarvested.
            net.minecraft.tileentity.TileEntity tile = world.getTileEntity(pos);
            if (found.removedByPlayer(state, world, pos, player, true)) {
                found.onPlayerDestroy(world, pos, state);
            }
            found.harvestBlock(world, player, pos, state, tile, player.getHeldItemMainhand());
            found.onBlockHarvested(world, pos, state, player);
        } else {
            world.setBlockToAir(pos);
        }
    }

    /**
     * Bedrock struck directly by an advanced pickaxe: at the bottom of a
     * surface world or the ceiling of the Bedrock dimension it becomes the
     * portal, and below that ceiling it simply goes away. Only the pickaxe does
     * this in the original — the axe and shovel do not touch bedrock.
     */
    public static void handleBedrock(World world, BlockPos pos, IBlockState state) {
        if (state.getBlock() != Blocks.BEDROCK) {
            return;
        }
        if ((world.provider.isSurfaceWorld() && pos.getY() < 5)
                || (pos.getY() > 253 && world.provider instanceof WorldProviderBedrock)) {
            world.setBlockState(pos, ConfigBlocks.blockBedrockPortal.getDefaultState(), 3);
        }
        if (pos.getY() <= 253 && world.provider instanceof WorldProviderBedrock) {
            world.setBlockToAir(pos);
        }
    }
}
