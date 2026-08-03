package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.utils.EntityUtils;

/**
 * Focus: Life Drain («Иссушение») — the infernal branch of the End Legacy
 * module (new content, no 1.7.10 original; the owner's design:
 * «иссушение → лечение из моба»).
 *
 * <p>Withers the creature under the crosshair and pours a share of what it
 * loses into the caster: the wither skeletons' trick, taught to a wand. The
 * heal is the point — the drain is how the Nether pays a debt.</p>
 */
public class FocusLifedrain extends ItemFocusBasic {

    private static final AspectList COST = new AspectList()
            .add(Aspect.ENTROPY, 40).add(Aspect.WATER, 20);
    public static final double RANGE = 12.0D;
    public static final float DAMAGE = 4.0F;
    /** What the caster keeps — half the bite, «лечение из моба». */
    public static final float HEAL = 3.0F;
    public static final int WITHER_TICKS = 100;

    public FocusLifedrain() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x3B2F3B;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player,
                                       RayTraceResult movingobjectposition) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            return wandStack;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        if (!world.isRemote) {
            Entity pointed = EntityUtils.getPointedEntity(world, player, 1.0D, RANGE, 1.1F);
            if (pointed instanceof EntityLivingBase
                    && wand.consumeAllVis(wandStack, player, this.getVisCost(wandStack), true, false)) {
                EntityLivingBase victim = (EntityLivingBase) pointed;
                victim.addPotionEffect(new PotionEffect(MobEffects.WITHER, WITHER_TICKS, 1));
                victim.attackEntityFrom(
                        DamageSource.causePlayerDamage(player).setMagicDamage(), DAMAGE);
                player.heal(HEAL);
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXZap(victim.getEntityId(), player.getEntityId()),
                        new NetworkRegistry.TargetPoint(world.provider.getDimension(),
                                player.posX, player.posY, player.posZ, 32.0D));
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 0.4F, 1.6F);
            }
        }
        player.swingArm(hand);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 300;
    }
}
