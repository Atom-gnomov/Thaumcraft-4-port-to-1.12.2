package thaumcraft.common.items.tinkerer.kami.tool;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Awakened Ichorium Pickaxe — ported from Thaumic Tinkerer's
 * {@code ItemIchorPickAdv} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Three modes, named as the original names them: <em>Block</em> breaks one
 * block, <em>Square</em> takes a five-by-five slab across the face struck, and
 * <em>Line</em> bores ten blocks along the line of sight. Sneak right-click
 * cycles them.</p>
 *
 * <p>The pickaxe is the only advanced tool that reacts to bedrock — striking it
 * at the bottom of a surface world opens the Bedrock dimension's portal.</p>
 */
public class ItemIchorPickAdv extends ItemIchorPick {

    public ItemIchorPickAdv() {
        super();
        // Modes live in the item damage, so the tool must not stack.
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        if (!KamiToolHandler.isRightMaterial(state.getMaterial(), KamiToolHandler.MATERIALS_PICK)) {
            return false;
        }
        RayTraceResult hit = rayTrace(world, player, false);
        if (hit == null || hit.sideHit == null) {
            return false;
        }
        EnumFacing face = hit.sideHit;
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;

        KamiToolHandler.handleBedrock(world, pos, state);

        switch (KamiToolHandler.getMode(stack)) {
            case 1: {
                boolean doX = face.getXOffset() == 0;
                boolean doY = face.getYOffset() == 0;
                boolean doZ = face.getZOffset() == 0;
                KamiToolHandler.removeBlocksInIteration(player, world, pos,
                        doX ? -2 : 0, doY ? -1 : 0, doZ ? -2 : 0,
                        doX ? 3 : 1, doY ? 4 : 1, doZ ? 3 : 1,
                        null, KamiToolHandler.MATERIALS_PICK, silk, fortune);
                break;
            }
            case 2: {
                int xo = -face.getXOffset();
                int yo = -face.getYOffset();
                int zo = -face.getZOffset();
                KamiToolHandler.removeBlocksInIteration(player, world, pos,
                        xo >= 0 ? 0 : -10, yo >= 0 ? 0 : -10, zo >= 0 ? 0 : -10,
                        xo > 0 ? 10 : 1, yo > 0 ? 10 : 1, zo > 0 ? 10 : 1,
                        null, KamiToolHandler.MATERIALS_PICK, silk, fortune);
                break;
            }
            default:
                break;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            KamiToolHandler.changeMode(stack);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    /** Damage is the mode here, so the tool must never be treated as worn. */
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(KamiToolHandler.getToolModeStr("pick", stack));
    }
}
