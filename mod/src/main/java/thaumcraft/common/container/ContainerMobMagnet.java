package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import thaumcraft.common.items.tinkerer.ItemSoulMould;
import thaumcraft.common.tiles.tinkerer.TileMobMagnet;

/**
 * Container for the Mob Magnet — mirrors Thaumic Tinkerer's
 * {@code ContainerMobMagnet} (pixlepix/nekosune, originally Vazkii): the Soul
 * Mould filter slot plus the player inventory, with the adult/baby switch
 * arriving as a container button.
 */
public class ContainerMobMagnet extends Container {

    public static final int BUTTON_ADULT = 0;
    public static final int BUTTON_BABY = 1;

    private final TileMobMagnet magnet;

    public ContainerMobMagnet(InventoryPlayer playerInv, TileMobMagnet magnet) {
        this.magnet = magnet;
        this.addSlotToContainer(new SlotItemHandler(magnet.getInventory(), 0, 53, 37) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof ItemSoulMould;
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

    public TileMobMagnet getMagnet() {
        return magnet;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == BUTTON_ADULT) {
            magnet.setAdult(true);
            return true;
        }
        if (id == BUTTON_BABY) {
            magnet.setAdult(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return magnet.getWorld().getTileEntity(magnet.getPos()) == magnet
                && player.getDistanceSq(magnet.getPos()) <= 64.0D;
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
