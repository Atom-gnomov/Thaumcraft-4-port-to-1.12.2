package thaumcraft.common.items.wands.foci;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;

/**
 * Focus of Deflection — ported 1:1 from Thaumic Tinkerer's ItemFocusDeflect
 * (pixlepix / nekosune / Vazkii). Held to channel a projectile shield: every
 * tick, projectiles near the caster are flung back out along their own motion,
 * scaled by twice their distance. The caster's own projectiles pass through.
 */
public class FocusDeflect extends ItemFocusBasic {

    /** The original's visUsage: charged every tick while channelling. */
    private static final AspectList COST = new AspectList().add(Aspect.ORDER, 8).add(Aspect.AIR, 4);

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
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "DF" + super.getSortingHelper(stack);
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
        if (wand.consumeAllVis(wandStack, player, COST, true, false)) {
            protectFromProjectiles(player);
        }
    }

    /** The original's protectFromProjectiles, box and displacement included. */
    public static void protectFromProjectiles(EntityPlayer player) {
        World world = player.world;
        AxisAlignedBB box = new AxisAlignedBB(
                player.posX - 4.0D, player.posY - 4.0D, player.posZ - 4.0D,
                player.posX + 3.0D, player.posY + 3.0D, player.posZ + 3.0D);
        List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, box);

        for (Entity e : projectiles) {
            if (!(e instanceof IProjectile) || isOwnedBy(e, player)) {
                continue;
            }
            double dx = e.posX - player.posX;
            double dy = e.posY - player.posY;
            double dz = e.posZ - player.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            Vec3d motion = new Vec3d(e.motionX, e.motionY, e.motionZ);
            if (motion.length() <= 0.0D) {
                continue;
            }
            motion = motion.normalize().scale(distance * 2.0D);

            for (int i = 0; i < 6; i++) {
                Thaumcraft.proxy.sparkle((float) e.posX, (float) e.posY, (float) e.posZ, 6);
            }

            e.posX += motion.x;
            e.posY += motion.y;
            e.posZ += motion.z;
            e.setPosition(e.posX, e.posY, e.posZ);
        }
    }

    /** The original skipped projectiles whose owner is the caster. */
    private static boolean isOwnedBy(Entity projectile, EntityPlayer player) {
        if (projectile instanceof EntityArrow) {
            return ((EntityArrow) projectile).shootingEntity == player;
        }
        if (projectile instanceof EntityThrowable) {
            return ((EntityThrowable) projectile).getThrower() == player;
        }
        return false;
    }
}
