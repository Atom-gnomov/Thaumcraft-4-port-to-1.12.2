package thaumcraft.common.items.wands.foci;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;

/**
 * Focus of Telekinesis — ported 1:1 from Thaumic Tinkerer's
 * ItemFocusTelekinesis (pixlepix / nekosune / Vazkii). Held to channel: items
 * around a point ahead of the caster are drawn to it. Sneaking gathers them to
 * the caster instead. Potency widens the radius.
 */
public class FocusTelekinesis extends ItemFocusBasic {

    /** The original's visUsage: charged every tick while channelling. */
    private static final AspectList COST = new AspectList().add(Aspect.AIR, 5).add(Aspect.ENTROPY, 5);

    public FocusTelekinesis() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x66CCFF;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "TK" + super.getSortingHelper(stack);
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        player.setActiveHand(ItemWandCasting.getHandHoldingWand(player, wandStack));
        WandManager.setCooldown(player, -1);
        return wandStack;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        World world = player.world;

        // Target starts at the caster's centre; unless sneaking it is pushed
        // (range - 1) blocks along the look vector, then lifted half a block.
        int range = 6 + this.getUpgradeLevel(wand.getFocusItem(wandStack), FocusUpgradeType.potency);
        double distance = range - 1;
        Vec3d target = new Vec3d(player.posX, player.posY + player.height / 2.0D, player.posZ);
        if (!player.isSneaking()) {
            Vec3d look = player.getLookVec();
            target = target.add(look.x * distance, look.y * distance, look.z * distance);
        }
        target = target.add(0.0D, 0.5D, 0.0D);

        AxisAlignedBB box = new AxisAlignedBB(
                target.x - range, target.y - range, target.z - range,
                target.x + range, target.y + range, target.z + range);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);

        if (!items.isEmpty() && wand.consumeAllVis(wandStack, player, COST, true, false)) {
            for (EntityItem item : items) {
                setMotionFromVector(item, target, 0.3333F);
                Thaumcraft.proxy.sparkle((float) item.posX, (float) item.posY, (float) item.posZ, 0);
            }
        }
    }

    /** The original's MiscHelper.setEntityMotionFromVector. */
    private static void setMotionFromVector(EntityItem item, Vec3d target, float speed) {
        Vec3d delta = new Vec3d(target.x - item.posX, target.y - item.posY, target.z - item.posZ);
        double length = delta.length();
        if (length <= 1.0E-4D) {
            item.motionX = 0.0D;
            item.motionY = 0.0D;
            item.motionZ = 0.0D;
            return;
        }
        Vec3d motion = delta.normalize().scale(speed);
        item.motionX = motion.x;
        item.motionY = motion.y;
        item.motionZ = motion.z;
    }
}
