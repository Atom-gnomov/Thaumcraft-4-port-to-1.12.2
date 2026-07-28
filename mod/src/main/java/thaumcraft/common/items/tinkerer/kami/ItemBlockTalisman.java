package thaumcraft.common.items.tinkerer.kami;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Black Hole Ring — ported from Thaumic Tinkerer's {@code ItemBlockTalisman}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Right-clicking a block keys the ring to that block, after which it stores
 * an unbounded number of them. Sneak-right-click switches it on and off, shown
 * by item damage 0/1 exactly as the original did it. While worn and switched
 * on, it sweeps every stack of its block out of the inventory into itself,
 * leaving one slot topped up; when the inventory holds none it hands back a
 * stack of 64. Right-clicking a container fills it from the ring instead.</p>
 *
 * <p>1.7.10 spoke to inventories through {@code ISidedInventory}; here that is
 * the item-handler capability, which is the same conversation.</p>
 */
public class ItemBlockTalisman extends Item implements IBauble {

    private static final String TAG_BLOCK_NAME = "blockName";
    private static final String TAG_BLOCK_META = "blockMeta";
    private static final String TAG_BLOCK_COUNT = "blockCount";

    public ItemBlockTalisman() {
        this.setMaxStackSize(1);
        // Damage 0/1 is the original's on/off switch, not wear.
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getBlock(stack) != Blocks.AIR && player.isSneaking()) {
            stack.setItemDamage(~stack.getItemDamage() & 1);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        Block struck = world.getBlockState(pos).getBlock();
        int meta = struck.getMetaFromState(world.getBlockState(pos));

        // An unkeyed (or emptied) ring keys itself to whatever was clicked.
        if (setBlock(stack, struck, meta)) {
            return EnumActionResult.SUCCESS;
        }

        Block held = getBlock(stack);
        if (held == Blocks.AIR) {
            return EnumActionResult.PASS;
        }
        int heldMeta = getBlockMeta(stack);

        TileEntity tile = world.getTileEntity(pos);
        IItemHandler inventory = tile == null ? null
                : tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
        if (inventory != null) {
            return fill(stack, inventory, held, heldMeta) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
        }

        // No inventory: place one block from the ring, as the original did.
        if (remove(stack, 1) > 0) {
            Item asItem = Item.getItemFromBlock(held);
            return asItem.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        return EnumActionResult.PASS;
    }

    /** Tops up every slot of {@code inventory} that will take the ring's block. */
    private static boolean fill(ItemStack ring, IItemHandler inventory, Block block, int meta) {
        boolean any = false;
        ItemStack template = new ItemStack(block, 1, meta);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            int room;
            if (inSlot.isEmpty()) {
                room = Math.min(template.getMaxStackSize(), inventory.getSlotLimit(slot));
            } else if (ItemHandlerHelper.canItemStacksStack(inSlot, template)) {
                room = Math.min(inSlot.getMaxStackSize(), inventory.getSlotLimit(slot)) - inSlot.getCount();
            } else {
                continue;
            }
            if (room <= 0) {
                continue;
            }
            int taken = remove(ring, room);
            if (taken == 0) {
                break;
            }
            ItemStack offered = new ItemStack(block, taken, meta);
            ItemStack rejected = inventory.insertItem(slot, offered, false);
            if (!rejected.isEmpty()) {
                // Put back whatever the inventory refused.
                add(ring, rejected.getCount());
            }
            if (rejected.getCount() != taken) {
                any = true;
            }
        }
        return any;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack ring, EntityLivingBase entity) {
        Block block = getBlock(ring);
        if (entity.world.isRemote || ring.getItemDamage() != 1
                || block == Blocks.AIR || !(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        int meta = getBlockMeta(ring);
        Item asItem = Item.getItemFromBlock(block);

        // Main inventory only — the original skipped the armour slots.
        int slots = player.inventory.mainInventory.size();
        int[] counts = new int[slots];
        int highest = -1;
        boolean hasFreeSlot = false;

        for (int i = 0; i < slots; i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (slot.isEmpty()) {
                hasFreeSlot = true;
                continue;
            }
            if (slot.getItem() == asItem && slot.getItemDamage() == meta) {
                counts[i] = slot.getCount();
                if (highest == -1) {
                    highest = i;
                } else {
                    // The original's rule: prefer a bigger stack, but only once
                    // we are past the hotbar.
                    highest = counts[i] > counts[highest] && highest > 8 ? i : highest;
                }
            }
        }

        if (highest == -1) {
            // Nothing of that block carried: hand a stack back if there is room.
            ItemStack onCursor = player.inventory.getItemStack();
            if (hasFreeSlot && (onCursor.isEmpty() || asItem == onCursor.getItem()
                    || onCursor.getItemDamage() != meta)) {
                int taken = remove(ring, 64);
                if (taken != 0) {
                    player.inventory.addItemStackToInventory(new ItemStack(block, taken, meta));
                }
            }
            return;
        }

        // Swallow every other stack, then top the surviving one back up.
        for (int i = 0; i < slots; i++) {
            if (i == highest || counts[i] == 0) {
                continue;
            }
            add(ring, counts[i]);
            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        ItemStack survivor = player.inventory.getStackInSlot(highest);
        int missing = survivor.getMaxStackSize() - counts[highest];
        if (missing > 0) {
            survivor.grow(remove(ring, missing));
        }
    }

    // --- stored block ----------------------------------------------------

    /** Keys the ring, but only while it is unkeyed or empty — the original's guard. */
    private static boolean setBlock(ItemStack stack, Block block, int meta) {
        if (getBlock(stack) != Blocks.AIR && getBlockCount(stack) != 0) {
            return false;
        }
        ResourceLocation name = Block.REGISTRY.getNameForObject(block);
        if (name == null) {
            return false;
        }
        NBTTagCompound tag = tag(stack);
        tag.setString(TAG_BLOCK_NAME, name.toString());
        tag.setInteger(TAG_BLOCK_META, meta);
        return true;
    }

    public static Block getBlock(ItemStack stack) {
        String name = stack.hasTagCompound() ? stack.getTagCompound().getString(TAG_BLOCK_NAME) : "";
        if (name.isEmpty()) {
            return Blocks.AIR;
        }
        Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
        return block == null ? Blocks.AIR : block;
    }

    public static int getBlockMeta(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_BLOCK_META) : 0;
    }

    public static int getBlockCount(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_BLOCK_COUNT) : 0;
    }

    private static void setCount(ItemStack stack, int count) {
        tag(stack).setInteger(TAG_BLOCK_COUNT, count);
    }

    private static void add(ItemStack stack, int count) {
        setCount(stack, getBlockCount(stack) + count);
    }

    /** Takes up to {@code count} out, returning how many were actually there. */
    public static int remove(ItemStack stack, int count) {
        int current = getBlockCount(stack);
        setCount(stack, Math.max(current - count, 0));
        return Math.min(current, count);
    }

    private static NBTTagCompound tag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    // --- display ---------------------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        Block block = getBlock(stack);
        if (block != Blocks.AIR) {
            ItemStack shown = new ItemStack(block, 1, getBlockMeta(stack));
            tooltip.add(shown.getDisplayName() + " (x" + getBlockCount(stack) + ")");
        }
        tooltip.add(new TextComponentTranslation(
                stack.getItemDamage() == 1 ? "ttmisc.active" : "ttmisc.inactive").getFormattedText());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
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
