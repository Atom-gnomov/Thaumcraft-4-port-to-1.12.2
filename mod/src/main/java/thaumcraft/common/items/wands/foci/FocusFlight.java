package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;

/**
 * Focus of Flight — ported 1:1 from Thaumic Tinkerer's ItemFocusFlight
 * (pixlepix / nekosune / Vazkii). Each cast throws the caster along their look
 * vector at 1/1.5, increased a fifth per potency level, clears fall damage and
 * resets the server's floating-tick counter so the flight is not rejected.
 */
public class FocusFlight extends ItemFocusBasic {

    /** The original's visUsage, charged per cast. */
    private static final AspectList COST = new AspectList().add(Aspect.AIR, 15);

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
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();

        if (wand.consumeAllVis(wandStack, player, COST, true, false)) {
            int potency = this.getUpgradeLevel(wand.getFocusItem(wandStack), FocusUpgradeType.potency);
            Vec3d look = player.getLookVec();
            double force = 1.0D / 1.5D * (1.0D + potency * 0.2D);
            player.motionX = look.x * force;
            player.motionY = look.y * force;
            player.motionZ = look.z * force;
            player.fallDistance = 0.0F;
            player.velocityChanged = true;
            if (player instanceof EntityPlayerMP) {
                // Without this the server's floating-player check kicks the flight.
                thaumcraft.common.lib.utils.Utils.resetFloatCounter((EntityPlayerMP) player);
            }
            for (int i = 0; i < 5; i++) {
                Thaumcraft.proxy.smokeSpiral(world, player.posX, player.posY - player.motionY, player.posZ,
                        2.0F, (int) (Math.random() * 360.0D), (int) player.posY, 0xFFFFFF);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    TCSounds.WIND, SoundCategory.PLAYERS, 0.4F, 1.0F);
        }

        if (world.isRemote) {
            player.swingArm(ItemWandCasting.getHandHoldingWand(player, wandStack));
        }
        return wandStack;
    }
}
