package thaumcraft.common.container;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;

/**
 * Container for the Osmotic Enchanter — mirrors Thaumic Tinkerer's
 * {@code ContainerEnchanter} (pixlepix/nekosune, originally Vazkii): the tool
 * and wand slots plus the player inventory.
 *
 * <p>The screen's buttons arrive through {@link #enchantItem(EntityPlayer, int)}
 * using the same id scheme as the original's packets: {@code 0} starts the run,
 * {@code 1..16} add the offered enchantment at that grid position, and each
 * queued row occupies three ids from {@link #FIRST_ROW_BUTTON} — remove,
 * level down, level up.</p>
 */
public class ContainerEnchanter extends Container {

    public static final int BUTTON_START = 0;
    public static final int FIRST_OFFER_BUTTON = 1;
    public static final int OFFER_BUTTONS = 16;
    public static final int FIRST_ROW_BUTTON = 17;
    public static final int ROW_STRIDE = 3;

    private final TileEnchanter enchanter;

    public ContainerEnchanter(InventoryPlayer playerInv, TileEnchanter enchanter) {
        this.enchanter = enchanter;
        this.addSlotToContainer(new SlotItemHandler(enchanter.getInventory(), TileEnchanter.SLOT_TOOL, 15, 33));
        this.addSlotToContainer(new SlotItemHandler(enchanter.getInventory(), TileEnchanter.SLOT_WAND, 15, 57) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof ItemWandCasting;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public TileEnchanter getEnchanter() {
        return enchanter;
    }

    /** Enchantments currently offered for the tool inside, in grid order. */
    public List<Enchantment> getOffers() {
        return enchanter.getOffers(OFFER_BUTTONS);
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == BUTTON_START) {
            enchanter.start();
            return true;
        }
        if (id >= FIRST_OFFER_BUTTON && id < FIRST_OFFER_BUTTON + OFFER_BUTTONS) {
            List<Enchantment> offers = getOffers();
            int index = id - FIRST_OFFER_BUTTON;
            if (index < offers.size()) {
                enchanter.setEnchant(offers.get(index), 0);
            }
            return true;
        }
        if (id >= FIRST_ROW_BUTTON) {
            int offset = id - FIRST_ROW_BUTTON;
            int index = offset / ROW_STRIDE;
            int action = offset % ROW_STRIDE;
            List<Enchantment> queued = enchanter.getQueuedEnchantments();
            if (index >= queued.size()) {
                return true;
            }
            Enchantment enchantment = queued.get(index);
            int level = enchanter.getQueuedLevel(enchantment);
            switch (action) {
                case 0:
                    enchanter.setEnchant(enchantment, -1);
                    break;
                case 1:
                    // Dropping below one removes the row, as in the original.
                    enchanter.setEnchant(enchantment, level <= 1 ? -1 : level - 1);
                    break;
                default:
                    enchanter.setEnchant(enchantment, level + 1);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return enchanter.getWorld().getTileEntity(enchanter.getPos()) == enchanter
                && player.getDistanceSq(enchanter.getPos()) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return result;
        }
        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (index < 2) {
            if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            int target = stack.getItem() instanceof ItemWandCasting ? 1 : 0;
            if (!this.mergeItemStack(stack, target, target + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return result;
    }
}
