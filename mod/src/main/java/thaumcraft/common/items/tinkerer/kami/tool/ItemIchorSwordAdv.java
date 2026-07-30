package thaumcraft.common.items.tinkerer.kami.tool;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
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

    /**
     * Extra attack damage while the sword is in focused-strike mode. Owner's
     * call, not upstream: mode 0 is a plain strike there, so focusing on one
     * target hit for exactly what the mode-1 sweep did and there was no reason
     * to use it.
     *
     * <p>This started as a bonus hit dealt alongside the vanilla one, which
     * fought the damage-immunity window and had to clear it by hand. It is a
     * plain attribute now, on the owner's instruction: the number shows in the
     * tooltip, enchantments and potions scale it the way they scale any weapon,
     * and the sweep and lifesteal modes keep the stock value.</p>
     */
    private static final double FOCUSED_STRIKE_BONUS_DAMAGE = 6.0D;

    /** Upstream's plain strike, and the only mode that gets the bonus. */
    private static final int MODE_FOCUSED_STRIKE = 0;

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
                case MODE_FOCUSED_STRIKE:
                    // Nothing to do on the hit itself. The mode's extra damage is
                    // an attribute now — see getAttributeModifiers.
                    break;
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

    /**
     * Focused-strike mode hits harder; the sweep and lifesteal modes keep the
     * stock ichor numbers. Only the damage is touched — attack speed stays
     * where {@code ItemSword} puts it, so the weapon still feels like a sword.
     */
    @Override
    public com.google.common.collect.Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier>
            getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        com.google.common.collect.Multimap<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> base =
                super.getAttributeModifiers(slot, stack);

        if (slot != EntityEquipmentSlot.MAINHAND
                || KamiToolHandler.getMode(stack) != MODE_FOCUSED_STRIKE) {
            return base;
        }

        String damage = net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE.getName();
        com.google.common.collect.Multimap<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> tuned =
                com.google.common.collect.HashMultimap.create();
        for (java.util.Map.Entry<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> entry : base.entries()) {
            net.minecraft.entity.ai.attributes.AttributeModifier modifier = entry.getValue();
            if (damage.equals(entry.getKey())) {
                tuned.put(damage, new net.minecraft.entity.ai.attributes.AttributeModifier(
                        modifier.getID(), modifier.getName(),
                        modifier.getAmount() + FOCUSED_STRIKE_BONUS_DAMAGE,
                        modifier.getOperation()));
            } else {
                tuned.put(entry.getKey(), modifier);
            }
        }
        return tuned;
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
