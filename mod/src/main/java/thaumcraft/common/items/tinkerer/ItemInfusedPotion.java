package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabTinkerer;
import thaumcraft.common.lib.tinkerer.ModPotionsTinkerer;

/**
 * The infused potions — ported from Thaumic Tinkerer's
 * {@code ItemInfusedPotion} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>One per primal, brewed from the matching grain, each granting its own
 * primal effect for the full three minutes. Drunk like any potion and, as
 * upstream, leaving nothing behind.</p>
 */
public class ItemInfusedPotion extends Item {

    public ItemInfusedPotion() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    public PrimalCrop getCrop(ItemStack stack) {
        return PrimalCrop.byMeta(stack.getItemDamage());
    }

    /** The effect this bottle carries, in the enum's own order. */
    private static Potion effectFor(PrimalCrop crop) {
        switch (crop) {
            case FIRE:
                return ModPotionsTinkerer.potionFire;
            case EARTH:
                return ModPotionsTinkerer.potionEarth;
            case WATER:
                return ModPotionsTinkerer.potionWater;
            case AIR:
            default:
                return ModPotionsTinkerer.potionAir;
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal(
                "item.thaumcraft.infused_potion." + getCrop(stack).getAspect().getTag() + ".name").trim();
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (!world.isRemote) {
            entity.addPotionEffect(new PotionEffect(
                    effectFor(getCrop(stack)), ModPotionsTinkerer.DURATION));
        }
        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (PrimalCrop crop : PrimalCrop.values()) {
            items.add(new ItemStack(this, 1, crop.ordinal()));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(getCrop(stack).getAspect().getName());
    }
}
