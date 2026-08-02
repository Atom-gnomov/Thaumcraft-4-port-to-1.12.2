package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
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
import thaumcraft.common.lib.endgame.DragonbreathFog;

/**
 * Focus: Dragonbreath — End Legacy module (new content, no 1.7.10 original;
 * the owner's design: a focus like Fire or Excavation, slotting into any
 * finished wand, that breathes the Ender Dragon's own lingering fog).
 *
 * <p>Each cast rolls one die across the six primals — at cast start, once —
 * and lays a dragon-breath cloud where the caster points: at the struck block,
 * or four blocks out along the look when aiming at air. The fog's temper for
 * that cast is whatever the die said; the table lives in
 * {@link DragonbreathFog}.</p>
 *
 * <p>Costs a sliver of <em>every</em> primal — the die owns all six faces —
 * and cools down like the heavier single-shot foci.</p>
 */
public class FocusDragonbreath extends ItemFocusBasic {

    /** All six, because the die may land on any of them. 0.15 vis apiece. */
    private static final AspectList COST = new AspectList()
            .add(Aspect.AIR, 15).add(Aspect.FIRE, 15).add(Aspect.WATER, 15)
            .add(Aspect.EARTH, 15).add(Aspect.ORDER, 15).add(Aspect.ENTROPY, 15);

    /** How far the breath carries when aiming at open air. */
    private static final double BREATH_REACH = 4.0D;

    public FocusDragonbreath() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xB324BF;   // the dragon breath particle's own purple
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
            Vec3d at = movingobjectposition != null
                    && movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK
                    ? movingobjectposition.hitVec
                    : player.getPositionEyes(1.0F).add(player.getLookVec().scale(BREATH_REACH));
            DragonbreathFog.breathe(world, player, at);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ENDERDRAGON_SHOOT, SoundCategory.PLAYERS, 0.6F,
                    1.2F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F);
        }
        player.swingArm(hand);
        return wandStack;
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 500;   // a heavy single shot, like the chained lightning
    }
}
