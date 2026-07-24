package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus of Flight — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. Right-click for a dash impulse in the look direction and cancel fall
 * damage; a lightweight take on TT's flight focus.
 */
public class FocusFlight extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.FLIGHT, 10);

    public FocusFlight() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xCCEEFF;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "FL" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 15;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        if (!wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }

        Vec3d look = player.getLookVec();
        double power = 1.15D;
        player.motionX += look.x * power;
        player.motionY += look.y * power + 0.35D;
        player.motionZ += look.z * power;
        player.fallDistance = 0.0F;
        player.velocityChanged = true;
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 0.5F, 1.6F);
        player.swingArm(hand);
        return wandStack;
    }
}
