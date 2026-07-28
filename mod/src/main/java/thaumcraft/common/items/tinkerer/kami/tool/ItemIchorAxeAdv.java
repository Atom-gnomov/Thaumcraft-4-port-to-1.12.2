package thaumcraft.common.items.tinkerer.kami.tool;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

/**
 * Awakened Ichorium Axe — ported from Thaumic Tinkerer's
 * {@code ItemIchorAxeAdv} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Three modes: <em>Block</em>, <em>Square</em> (a five-by-five slab across
 * the face struck) and <em>Tree</em>, which fells the whole trunk from the
 * furthest log inward and then draws every dropped item within ten blocks
 * across and sixty-four up back to the stump. Sneak right-click cycles them.</p>
 */
public class ItemIchorAxeAdv extends ItemIchorAxe implements IAdvancedTool {

    public ItemIchorAxeAdv() {
        super();
        // Modes live in the item damage, so the tool must not stack.
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        if (!KamiToolHandler.isRightMaterial(state.getMaterial(), KamiToolHandler.MATERIALS_AXE)) {
            return false;
        }
        RayTraceResult hit = rayTrace(world, player, false);
        if (hit == null || hit.sideHit == null) {
            return false;
        }
        EnumFacing face = hit.sideHit;
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;

        switch (KamiToolHandler.getMode(stack)) {
            case 1: {
                boolean doX = face.getXOffset() == 0;
                boolean doY = face.getYOffset() == 0;
                boolean doZ = face.getZOffset() == 0;
                KamiToolHandler.removeBlocksInIteration(player, world, pos,
                        doX ? -2 : 0, doY ? -1 : 0, doZ ? -2 : 0,
                        doX ? 3 : 1, doY ? 4 : 1, doZ ? 3 : 1,
                        null, KamiToolHandler.MATERIALS_AXE, silk, fortune);
                break;
            }
            case 2: {
                if (!Utils.isWoodLog(world, pos)) {
                    break;
                }
                // Fell the trunk furthest-first until nothing is left at the stump.
                while (!world.isAirBlock(pos)) {
                    if (!BlockUtils.breakFurthestBlock(world, pos, player)) {
                        break;
                    }
                }
                AxisAlignedBB pull = new AxisAlignedBB(
                        pos.getX() - 5, pos.getY() - 1, pos.getZ() - 5,
                        pos.getX() + 5, pos.getY() + 64, pos.getZ() + 5);
                for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, pull)) {
                    item.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    item.ticksExisted += 20;
                }
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

    @Override
    public String getType() {
        return "axe";
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
        tooltip.add(KamiToolHandler.getToolModeStr(getType(), stack));
    }
}
