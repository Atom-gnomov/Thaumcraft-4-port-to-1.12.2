package thaumcraft.common.items.tinkerer;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

/**
 * Experience Talisman — ported 1:1 from Thaumic Tinkerer's
 * {@code ItemXPTalisman} (pixlepix / nekosune / Vazkii). Worn in the amulet
 * slot; while absorbing it swallows nearby experience orbs up to its cap, and
 * right-clicking trades stored experience and a glass bottle for a bottle o'
 * enchanting. Sneak-right-click switches absorbing on and off.
 */
public class ItemXpTalisman extends Item implements IBauble {

    /** The original's LibFeatures values. */
    public static final int RANGE = 3;
    public static final int MAX_XP = 1500;
    public static final int BOTTLE_COST = 10;

    private static final String TAG_XP = "xp";

    public ItemXpTalisman() {
        this.setMaxStackSize(1);
        // Damage 0/1 is the original's absorbing switch, not wear.
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (getXP(stack) < MAX_XP) {
                stack.setItemDamage(~stack.getItemDamage() & 1);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
            }
        } else if (getXP(stack) >= BOTTLE_COST && consumeGlassBottle(player)) {
            if (!player.inventory.addItemStackToInventory(new ItemStack(Items.EXPERIENCE_BOTTLE, 1))
                    && !world.isRemote) {
                player.dropItem(Items.EXPERIENCE_BOTTLE, 1);
            }
            setXP(stack, getXP(stack) - BOTTLE_COST);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS,
                    0.1F, (float) (0.1F + Math.random() / 2.0F));
            for (int i = 0; world.isRemote && i < 6; i++) {
                Thaumcraft.proxy.sparkle(
                        (float) (player.posX + (Math.random() - 0.5D)),
                        (float) (player.posY + Math.random() - 0.5D),
                        (float) (player.posZ + (Math.random() - 0.5D)), 3);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static boolean consumeGlassBottle(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (!slot.isEmpty() && slot.getItem() == Items.GLASS_BOTTLE) {
                player.inventory.decrStackSize(i, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        World world = player.world;
        if (stack.getItemDamage() != 1 || world.isRemote) {
            return;
        }
        int currentXP = getXP(stack);
        int maxXP = MAX_XP - currentXP;
        if (maxXP <= 0) {
            stack.setItemDamage(0);
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB(
                player.posX - RANGE, player.posY - RANGE, player.posZ - RANGE,
                player.posX + RANGE, player.posY + RANGE, player.posZ + RANGE);
        List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
        int xpToAdd = 0;
        for (EntityXPOrb orb : orbs) {
            if (orb.isDead) {
                continue;
            }
            int xp = orb.getXpValue();
            if (xpToAdd + xp <= maxXP) {
                xpToAdd += xp;
                consumeXPOrb(orb);
            }
            maxXP -= xpToAdd;
            if (maxXP <= 0) {
                break;
            }
        }
        if (xpToAdd > 0) {
            setXP(stack, currentXP + xpToAdd);
        }
    }

    private static void consumeXPOrb(EntityXPOrb orb) {
        orb.setDead();
        orb.world.playSound(null, orb.posX, orb.posY, orb.posZ,
                TCSounds.ZAP, SoundCategory.PLAYERS, orb.getXpValue() / 10.0F, 1.0F);
        Thaumcraft.proxy.wispFX(orb.world, orb.posX, orb.posY, orb.posZ,
                orb.getXpValue() / 5.0F, 0.1F, 0.9F, 0.1F);
    }

    public static int getXP(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_XP) : 0;
    }

    public static void setXP(ItemStack stack, int xp) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(TAG_XP, xp);
        stack.setTagCompound(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add("XP: " + getXP(stack));
        if (getXP(stack) >= MAX_XP) {
            tooltip.add(new TextComponentTranslation("ttmisc.full").getFormattedText());
        } else if (stack.getItemDamage() == 0) {
            tooltip.add(new TextComponentTranslation("ttmisc.notAbsorbing").getFormattedText());
        } else {
            tooltip.add(new TextComponentTranslation("ttmisc.absorbing").getFormattedText());
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
