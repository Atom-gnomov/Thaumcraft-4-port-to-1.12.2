package thaumcraft.common.items.wands.foci;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;

/**
 * Focus of Deflection — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. Right-click for a shockwave that knocks nearby hostile-range
 * living entities away from the caster.
 */
public class FocusDeflect extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.PROTECT, 12).add(Aspect.MOTION, 6);
    private static final double RANGE = 5.0D;

    public FocusDeflect() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xDDDD55;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "DF" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 30;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        List<EntityLivingBase> mobs = EntityUtils.getEntitiesInRange(world, player.posX, player.posY, player.posZ,
                player, EntityLivingBase.class, RANGE);
        boolean any = mobs != null && !mobs.isEmpty();
        if (any && !wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }
        if (any) {
            for (EntityLivingBase mob : mobs) {
                if (mob == null || mob == player || mob.isDead) continue;
                double dx = mob.posX - player.posX;
                double dz = mob.posZ - player.posZ;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist < 1.0E-4D) { dx = 1.0D; dist = 1.0D; }
                mob.knockBack(player, 1.6F, -dx / dist, -dz / dist);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.3F, 1.8F);
        }
        player.swingArm(hand);
        return wandStack;
    }
}
