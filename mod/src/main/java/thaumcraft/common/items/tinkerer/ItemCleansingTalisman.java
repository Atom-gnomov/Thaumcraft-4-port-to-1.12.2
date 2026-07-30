package thaumcraft.common.items.tinkerer;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.Collection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabTinkerer;
import thaumcraft.common.lib.TCSounds;

/**
 * Cleansing Talisman — ported 1:1 from Thaumic Tinkerer's
 * {@code ItemCleansingTalisman} (pixlepix / nekosune / Vazkii). Worn in the
 * amulet slot; while switched on it puts out fires and strips one harmful
 * effect a second, spending a charge each time it does.
 */
public class ItemCleansingTalisman extends Item implements IBauble {

    /** The original's CLEANSING_TALISMAN_USES. */
    public static final int USES = 100;

    private static final String TAG_ENABLED = "enabled";

    public ItemCleansingTalisman() {
        this.setMaxStackSize(1);
        this.setMaxDamage(USES);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            flipEnabled(stack);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static boolean isEnabled(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_ENABLED);
    }

    public static void flipEnabled(ItemStack stack) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setBoolean(TAG_ENABLED, !isEnabled(stack));
        stack.setTagCompound(tag);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        World world = player.world;
        if (!isEnabled(stack) || world.isRemote || player.ticksExisted % 20 != 0
                || !(player instanceof EntityPlayer)) {
            return;
        }
        boolean removed = false;
        if (player.isBurning()) {
            player.extinguish();
            removed = true;
        } else {
            Collection<PotionEffect> potions = player.getActivePotionEffects();
            for (PotionEffect effect : potions) {
                Potion potion = effect.getPotion();
                if (potion.isBadEffect()) {
                    player.removePotionEffect(potion);
                    removed = true;
                    break;
                }
            }
        }
        if (removed) {
            stack.damageItem(1, player);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    TCSounds.WAND, SoundCategory.PLAYERS, 0.3F, 0.1F);
        }
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
