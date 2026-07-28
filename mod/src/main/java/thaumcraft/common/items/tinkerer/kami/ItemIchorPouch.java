package thaumcraft.common.items.tinkerer.kami;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemFocusPouch;

/**
 * Bottomless Pouch — ported from Thaumic Tinkerer's {@code ItemIchorPouch}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>The focus pouch grown to thirteen by nine and no longer restricted to
 * foci: it holds anything except another pouch, sixty-four to a slot. Worn on
 * the belt, and opened by right-clicking as the plain pouch is.</p>
 *
 * <p>It extends {@link ItemFocusPouch} exactly as the original did, so it
 * shares the pouch's NBT format — only the slot count differs.</p>
 */
public class ItemIchorPouch extends ItemFocusPouch implements IBauble {

    /** The original's grid: {@code 13 * 9}. */
    public static final int SLOTS = 13 * 9;

    public ItemIchorPouch() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_ICHOR_POUCH, world,
                    MathHelper.floor(player.posX), MathHelper.floor(player.posY),
                    MathHelper.floor(player.posZ));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public ItemStack[] getInventory(ItemStack item) {
        ItemStack[] inventory = new ItemStack[SLOTS];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = ItemStack.EMPTY;
        }
        if (item.hasTagCompound()) {
            NBTTagList list = item.getTagCompound().getTagList("Inventory", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound slotTag = list.getCompoundTagAt(i);
                int slot = slotTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = new ItemStack(slotTag);
                }
            }
        }
        return inventory;
    }

    @Override
    public void setInventory(ItemStack item, ItemStack[] inventory) {
        NBTTagList list = new NBTTagList();
        if (inventory != null) {
            for (int i = 0; i < Math.min(inventory.length, SLOTS); i++) {
                ItemStack stack = inventory[i];
                if (stack == null || stack.isEmpty()) {
                    continue;
                }
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                stack.writeToNBT(slotTag);
                list.appendTag(slotTag);
            }
        }
        NBTTagCompound tag = item.hasTagCompound() ? item.getTagCompound() : new NBTTagCompound();
        tag.setTag("Inventory", list);
        item.setTagCompound(tag);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
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
