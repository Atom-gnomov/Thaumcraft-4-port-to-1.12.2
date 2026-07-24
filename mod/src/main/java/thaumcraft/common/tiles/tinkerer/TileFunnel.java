package thaumcraft.common.tiles.tinkerer;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.TileThaumcraft;

/**
 * Funnel tile — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. Vacuums up dropped items in a small region above it and inserts them
 * into the inventory directly below.
 */
public class TileFunnel extends TileThaumcraft implements ITickable {

    private static final int INTERVAL = 8;

    @Override
    public void update() {
        if (world == null || world.isRemote || world.getTotalWorldTime() % INTERVAL != 0) {
            return;
        }
        IItemHandler below = getInventoryBelow();
        if (below == null) {
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB(pos).grow(1.0D, 0.0D, 1.0D).expand(0.0D, 4.0D, 0.0D);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem entity : items) {
            if (entity == null || entity.isDead || entity.cannotPickup()) continue;
            ItemStack stack = entity.getItem();
            if (stack.isEmpty()) continue;
            ItemStack remainder = insert(below, stack.copy());
            if (remainder.isEmpty()) {
                entity.setDead();
            } else if (remainder.getCount() != stack.getCount()) {
                entity.setItem(remainder);
            }
        }
    }

    private IItemHandler getInventoryBelow() {
        TileEntity te = world.getTileEntity(pos.down());
        if (te == null) return null;
        if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        }
        return null;
    }

    private static ItemStack insert(IItemHandler handler, ItemStack stack) {
        return ItemHandlerHelper.insertItem(handler, stack, false);
    }
}
