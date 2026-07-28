package thaumcraft.common.items.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.tinkerer.gas.BlockGas;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

/**
 * Fume Dissipator — ported from Thaumic Tinkerer's {@code ItemGasRemover}
 * (pixlepix / nekosune, originally Vazkii). Sneak-right-click and every gas
 * within three blocks puffs out of existence.
 *
 * <p>The sweep is upstream's, right down to its asymmetry: it runs from −3 up
 * to but not including +3, so the box is six blocks wide and sits a little off
 * centre. Left as written.</p>
 */
public class ItemGasRemover extends Item {

    private static final int RANGE = 3;

    public ItemGasRemover() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        int xs = (int) player.posX;
        int ys = (int) player.posY;
        int zs = (int) player.posZ;

        for (int x = xs - RANGE; x < xs + RANGE; x++) {
            for (int y = ys - RANGE; y < ys + RANGE; y++) {
                for (int z = zs - RANGE; z < zs + RANGE; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof BlockGas) {
                        ((BlockGas) block).placeParticle(world, pos);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }
        world.playSound(null, player.posX, player.posY, player.posZ,
                TCSounds.WAND, SoundCategory.PLAYERS, 0.2F, 1.0F);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
