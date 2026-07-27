package thaumcraft.common.tiles.tinkerer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.items.tinkerer.ItemSoulMould;

/**
 * Mob Magnet tile — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2.
 *
 * <p>Shares everything with the item magnet — redstone gating, a reach of
 * {@code signal / 2}, the 0.25 pull speed and the attract/repel toggle — but
 * moves living creatures instead of dropped items. Players are never affected.
 * An {@code adult} switch picks whether ageable mobs are taken as adults or as
 * babies, and a Soul Mould in its slot narrows it to a single kind of
 * creature.</p>
 */
public class TileMobMagnet extends TileMagnet {

    private static final String TAG_ADULT = "adultCheck";

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof ItemSoulMould;
        }
    };

    /** true = pull grown mobs, false = pull babies. Matches the original's flag. */
    private boolean adult = true;

    @Override
    protected Class<? extends Entity> getTargetClass() {
        return EntityLivingBase.class;
    }

    @Override
    protected boolean isTarget(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || entity instanceof EntityPlayer) {
            return false;
        }
        if (entity instanceof EntityAgeable && adult == ((EntityAgeable) entity).isChild()) {
            return false;
        }
        ItemStack mould = inventory.getStackInSlot(0);
        return mould.isEmpty() || ItemSoulMould.matches(mould, (EntityLivingBase) entity);
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean value) {
        this.adult = value;
        markDirty();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        adult = nbt.getBoolean(TAG_ADULT);
        inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setBoolean(TAG_ADULT, adult);
        nbt.setTag("Inventory", inventory.serializeNBT());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
    }
}
