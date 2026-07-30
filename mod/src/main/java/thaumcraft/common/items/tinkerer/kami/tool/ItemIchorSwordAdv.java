package thaumcraft.common.items.tinkerer.kami.tool;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.tinkerer.SoulHeartHandler;

/**
 * The awakened ichorium sword — the port of the original's
 * {@code ItemIchorSwordAdv}.
 *
 * <p>Three modes, cycled by sneak-right-click and kept in the item damage:
 * mode 0 strikes normally, mode 1 strikes every entity of the same kind within
 * three blocks, mode 2 lays resistance on the victim and grants the wielder a
 * soul heart.</p>
 */
public class ItemIchorSwordAdv extends ItemIchorSword implements IAdvancedTool {

    /** Mode 1 re-enters attack from inside attack; this stops the recursion. */
    private boolean ignoreLeftClick = false;

    /** Focused-strike bonus range, inclusive. A deviation, see onLeftClickEntity. */
    private static final int SINGLE_TARGET_MIN = 14;
    private static final int SINGLE_TARGET_MAX = 17;

    public ItemIchorSwordAdv() {
        super();
        // Modes live in the item damage, so the tool must not stack.
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!this.ignoreLeftClick && entity instanceof EntityLivingBase
                && ((EntityLivingBase) entity).hurtTime == 0 && !entity.isDead) {
            switch (KamiToolHandler.getMode(stack)) {
                case 0: {
                    // Owner's call, not the original's behaviour: upstream mode 0
                    // is a plain strike, so single-target and the mode-1 sweep hit
                    // for exactly the same amount and there was no reason to use
                    // it. Focusing on one target now rolls 14-17 on top of the
                    // usual hit. See THAUMIC_TINKERER_PLAN.md.
                    //
                    // The bonus is dealt here, before the vanilla hit, and the
                    // victim's immunity window is cleared afterwards. Without
                    // that clear the vanilla hit lands inside the window this
                    // one opened and only the larger of the two counts, so the
                    // player sees no change at all.
                    if (!player.world.isRemote) {
                        float bonus = SINGLE_TARGET_MIN
                                + player.world.rand.nextInt(SINGLE_TARGET_MAX - SINGLE_TARGET_MIN + 1);
                        EntityLivingBase victim = (EntityLivingBase) entity;
                        victim.attackEntityFrom(
                                net.minecraft.util.DamageSource.causePlayerDamage(player), bonus);
                        victim.hurtResistantTime = 0;
                        victim.hurtTime = 0;
                    }
                    break;
                }
                case 1: {
                    int range = 3;
                    List<Entity> entities = player.world.getEntitiesWithinAABB(entity.getClass(),
                            new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range,
                                    entity.posX + range, entity.posY + range, entity.posZ + range));
                    this.ignoreLeftClick = true;
                    for (Entity other : entities) {
                        player.attackTargetEntityWithCurrentItem(other);
                    }
                    this.ignoreLeftClick = false;
                    break;
                }
                case 2: {
                    EntityLivingBase living = (EntityLivingBase) entity;
                    living.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1, 1));
                    SoulHeartHandler.addHearts(player);
                    break;
                }
                default:
                    break;
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            KamiToolHandler.changeMode(stack);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public String getType() {
        return "sword";
    }

    /** Damage is the mode here, so the tool must never be treated as worn. */
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(KamiToolHandler.getToolModeStr(getType(), stack));
    }
}
