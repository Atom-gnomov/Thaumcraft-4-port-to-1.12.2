package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus of Healing — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. Right-click to heal the looked-at living entity, or the caster when no
 * target is in view.
 */
public class FocusHeal extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.HEAL, 12);
    private static final float HEAL_AMOUNT = 4.0F;

    public FocusHeal() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF88AA;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "HL" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 40;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        EntityLivingBase target = player;
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.ENTITY
                && mop.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mop.entityHit;
        }
        if (target.getHealth() >= target.getMaxHealth()) {
            return wandStack;
        }
        if (!wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }
        if (!world.isRemote) {
            target.heal(HEAL_AMOUNT);
        }
        Thaumcraft.proxy.blockSparkle(world, (int) target.posX, (int) (target.posY + target.getEyeHeight()),
                (int) target.posZ, 0xFF88AA, 8);
        player.swingArm(hand);
        return wandStack;
    }
}
