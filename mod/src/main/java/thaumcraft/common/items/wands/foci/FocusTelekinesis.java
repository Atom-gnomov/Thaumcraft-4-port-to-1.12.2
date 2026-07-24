package thaumcraft.common.items.wands.foci;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;

/**
 * Focus of Telekinesis — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. Right-click to draw nearby dropped items and XP orbs to the caster.
 */
public class FocusTelekinesis extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.MOTION, 8);
    private static final double RANGE = 12.0D;

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
    public String getSortingHelper(ItemStack stack) {
        return "TK" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 20;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        List<Entity> items = EntityUtils.getEntitiesInRange(world, player.posX, player.posY, player.posZ,
                player, Entity.class, RANGE);
        boolean pulled = false;
        if (items != null) {
            for (Entity e : items) {
                if (e == null || e.isDead || !(e instanceof EntityItem || e instanceof EntityXPOrb)) continue;
                double dx = player.posX - e.posX;
                double dy = player.posY + player.getEyeHeight() - e.posY;
                double dz = player.posZ - e.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < 1.0E-4D) continue;
                double pull = 0.35D;
                e.motionX = dx / dist * pull;
                e.motionY = dy / dist * pull;
                e.motionZ = dz / dist * pull;
                if (e instanceof EntityItem) ((EntityItem) e).setPickupDelay(0);
                pulled = true;
            }
        }
        if (pulled && !wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }
        player.swingArm(hand);
        return wandStack;
    }
}
