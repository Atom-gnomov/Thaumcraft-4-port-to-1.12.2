package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import thaumcraft.common.tiles.tinkerer.TileAnimationTablet;

/**
 * Container for the Tool Dynamism Tablet — mirrors Thaumic Tinkerer's
 * {@code ContainerAnimationTablet} (pixlepix/nekosune, originally Vazkii):
 * a single tool slot at the original's coordinates plus the player inventory.
 *
 * <p>The two toggles arrive as button presses through
 * {@link #enchantItem(EntityPlayer, int)} — the same channel the rest of this
 * port uses for GUI buttons.</p>
 */
public class ContainerAnimationTablet extends Container {

    public static final int BUTTON_REDSTONE = 0;
    public static final int BUTTON_STRIKE = 1;
    public static final int BUTTON_USE = 2;

    private final TileAnimationTablet tablet;

    public ContainerAnimationTablet(InventoryPlayer playerInv, TileAnimationTablet tablet) {
        this.tablet = tablet;
        this.addSlotToContainer(new SlotItemHandler(tablet.getInventory(), 0, 80, 15));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public TileAnimationTablet getTablet() {
        return tablet;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        switch (id) {
            case BUTTON_REDSTONE:
                tablet.toggleRedstoneMode();
                return true;
            case BUTTON_STRIKE:
                tablet.setStrikeMode(true);
                return true;
            case BUTTON_USE:
                tablet.setStrikeMode(false);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tablet.getWorld().getTileEntity(tablet.getPos()) == tablet
                && player.getDistanceSq(tablet.getPos()) <= 64.0D;
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

        if (index == 0) {
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.mergeItemStack(stack, 0, 1, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return result;
    }
}
