package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus: Fireball — the infernal branch of the End Legacy module (new
 * content, no 1.7.10 original; the owner's request: Nether-flavoured foci
 * beside the dragon's, built as part of what already exists).
 *
 * <p>Casts the ghast's own {@link EntityLargeFireball} along the look —
 * explosive, deflectable with a swat, unmistakably rude. The vanilla
 * projectile does all the work; the focus only pays for it.</p>
 */
public class FocusFireball extends ItemFocusBasic {

    private static final AspectList COST = new AspectList()
            .add(Aspect.FIRE, 40).add(Aspect.ENTROPY, 10);
    /** The ghast's explosion strength. */
    public static final int POWER = 1;

    public FocusFireball() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF7700;
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

        if (!world.isRemote && wand.consumeAllVis(wandStack, player, this.getVisCost(wandStack), true, false)) {
            Vec3d look = player.getLookVec();
            EntityLargeFireball fireball = new EntityLargeFireball(world, player,
                    look.x, look.y, look.z);
            fireball.explosionPower = POWER;
            Vec3d eyes = player.getPositionEyes(1.0F);
            fireball.setPosition(eyes.x + look.x, eyes.y + look.y, eyes.z + look.z);
            world.spawnEntity(fireball);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.7F,
                    1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
        }
        player.swingArm(hand);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 300;
    }
}
