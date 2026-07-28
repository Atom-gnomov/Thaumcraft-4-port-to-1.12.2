package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.items.tinkerer.kami.ItemIchorPouch;
import thaumcraft.common.items.wands.ItemFocusPouch;

/**
 * Bottomless Pouch screen backing — ported from Thaumic Tinkerer's
 * {@code ContainerIchorPouch} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Thirteen columns by nine rows at {@code (12 + x*18, 8 + y*18)}, with the
 * player inventory starting at {@code (48, 177)} — the original's numbers. A
 * pouch cannot be put inside a pouch, and the slot holding the open pouch is
 * dead so it cannot be moved out from under the screen.</p>
 */
public class ContainerIchorPouch extends Container {

    private static final int SLOTS = ItemIchorPouch.SLOTS;
    private static final int INV_X = 48;
    private static final int INV_Y = 177;

    private final EntityPlayer player;
    private final World worldObj;
    private final ItemStack pouchStack;
    private final ItemIchorPouch pouchItem;
    private final InventoryIchorPouch pouchInventory;
    private final int pouchPlayerSlot;
    private final int blockedContainerSlot;

    public ContainerIchorPouch() {
        this(null, null);
    }

    public ContainerIchorPouch(InventoryPlayer playerInventory, World world) {
        this.player = playerInventory != null ? playerInventory.player : null;
        this.worldObj = world;
        this.pouchPlayerSlot = findPouchSlot(playerInventory);
        this.blockedContainerSlot = toContainerSlot(this.pouchPlayerSlot);
        this.pouchStack = findPouch(playerInventory, this.pouchPlayerSlot);
        this.pouchItem = !this.pouchStack.isEmpty() && this.pouchStack.getItem() instanceof ItemIchorPouch
                ? (ItemIchorPouch) this.pouchStack.getItem() : null;
        this.pouchInventory = new InventoryIchorPouch();
        if (this.pouchItem != null) {
            ItemStack[] stacks = this.pouchItem.getInventory(this.pouchStack);
            for (int i = 0; i < stacks.length && i < SLOTS; i++) {
                this.pouchInventory.setInventorySlotContents(i, stacks[i]);
            }
        }

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 13; col++) {
                this.addSlotToContainer(new Slot(this.pouchInventory, row * 13 + col,
                        12 + col * 18, 8 + row * 18) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return !stack.isEmpty() && !(stack.getItem() instanceof ItemFocusPouch);
                    }
                });
            }
        }

        if (playerInventory != null) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    addPlayerSlot(playerInventory, col + row * 9 + 9,
                            INV_X + col * 18, INV_Y + row * 18);
                }
            }
            for (int col = 0; col < 9; col++) {
                addPlayerSlot(playerInventory, col, INV_X + col * 18, INV_Y + 58);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn != null && playerIn == this.player && !playerIn.isDead
                && playerIn.world == this.worldObj
                && this.pouchItem != null && !this.pouchStack.isEmpty();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.pouchItem == null || this.pouchStack.isEmpty()) {
            return;
        }
        ItemStack[] stacks = new ItemStack[SLOTS];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = this.pouchInventory.getStackInSlot(i);
        }
        this.pouchItem.setInventory(this.pouchStack, stacks);
        if (this.player != null) {
            this.player.inventory.markDirty();
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (index == this.blockedContainerSlot) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index < SLOTS) {
                if (!this.mergeItemStack(stack, SLOTS, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() instanceof ItemFocusPouch) {
                return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(stack, 0, SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return copy;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer clicker) {
        if (slotId == this.blockedContainerSlot) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, clicker);
    }

    private void addPlayerSlot(InventoryPlayer inventory, int index, int x, int y) {
        if (index == this.pouchPlayerSlot) {
            this.addSlotToContainer(new Slot(inventory, index, x, y) {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn) {
                    return false;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }
            });
        } else {
            this.addSlotToContainer(new Slot(inventory, index, x, y));
        }
    }

    private static ItemStack findPouch(InventoryPlayer inventory, int slot) {
        if (inventory == null) {
            return ItemStack.EMPTY;
        }
        ItemStack main = inventory.player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemIchorPouch) {
            return main;
        }
        ItemStack off = inventory.player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemIchorPouch) {
            return off;
        }
        if (slot >= 0 && slot < inventory.mainInventory.size()) {
            ItemStack stack = inventory.mainInventory.get(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemIchorPouch) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static int findPouchSlot(InventoryPlayer inventory) {
        if (inventory == null) {
            return -1;
        }
        ItemStack held = inventory.player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemIchorPouch) {
            return inventory.currentItem;
        }
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack stack = inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemIchorPouch) {
                return i;
            }
        }
        return -1;
    }

    /** Player slot index to container slot index, given the grid above it. */
    private static int toContainerSlot(int playerSlot) {
        if (playerSlot >= 0 && playerSlot < 9) {
            return SLOTS + 27 + playerSlot;
        }
        if (playerSlot >= 9 && playerSlot < 36) {
            return SLOTS + (playerSlot - 9);
        }
        return -1;
    }

    /** Anything but another pouch, sixty-four to a slot — the original's rules. */
    private static final class InventoryIchorPouch extends InventoryBasic {

        InventoryIchorPouch() {
            super("container.ichor_pouch", false, SLOTS);
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            return !stack.isEmpty() && !(stack.getItem() instanceof ItemFocusPouch);
        }
    }
}
