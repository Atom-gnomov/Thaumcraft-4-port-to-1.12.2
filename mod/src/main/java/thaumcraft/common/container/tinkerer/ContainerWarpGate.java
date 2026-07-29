package thaumcraft.common.container.tinkerer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * The warp gate's inventory — the port of Thaumic Tinkerer's
 * {@code ContainerWarpGate}: ten pearl slots in two rows of five, at the
 * original's coordinates, over the usual player inventory.
 */
public class ContainerWarpGate extends Container {

    private static final int GATE_SLOTS = 10;

    private final TileWarpGate gate;

    public ContainerWarpGate(TileWarpGate gate, InventoryPlayer playerInv) {
        this.gate = gate;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 5; x++) {
                addSlotToContainer(new SlotSkyPearl(gate, y * 5 + x, 30 + x * 25, 27 + y * 25));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInv, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.gate.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < GATE_SLOTS) {
                if (!mergeItemStack(stack, GATE_SLOTS, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (result.getItem() == ConfigItems.itemSkyPearl
                    && !mergeItemStack(stack, 0, GATE_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }

    /** Only sky pearls belong in the gate. */
    private static class SlotSkyPearl extends Slot {
        SlotSkyPearl(TileWarpGate gate, int index, int x, int y) {
            super(gate, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() == ConfigItems.itemSkyPearl;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
