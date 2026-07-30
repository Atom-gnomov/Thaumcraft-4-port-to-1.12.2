package thaumcraft.common.tiles.tinkerer;

import javax.annotation.Nullable;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarFillableVoid;

/**
 * Funnel tile — ported 1:1 from Thaumic Tinkerer's {@code TileFunnel}
 * (pixlepix / nekosune / Vazkii). It holds one filled jar and drips its
 * essentia, one point at a time, into a jar reached through the hopper beneath
 * it — the original's way of emptying jars back into a storage wall.
 */
public class TileFunnel extends TileTinkerer implements ITickable, IAspectContainer {

    /** The original's single slot, which only ever holds a filled jar. */
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof BlockJarItem;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileFunnel.this.markDirty();
        }
    };

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        ItemStack jar = inventory.getStackInSlot(0);
        if (jar.isEmpty() || !(jar.getItem() instanceof BlockJarItem)) {
            return;
        }
        BlockJarItem item = (BlockJarItem) jar.getItem();
        AspectList aspectList = item.getAspects(jar);
        if (aspectList == null || aspectList.size() != 1) {
            return;
        }
        Aspect aspect = aspectList.getAspects()[0];

        // Only a vanilla hopper is followed, exactly as the original did.
        TileEntity below = world.getTileEntity(pos.down());
        if (!(below instanceof TileEntityHopper)) {
            return;
        }
        TileEntity target = getHopperFacing(pos.down());
        if (!(target instanceof TileJarFillable)) {
            return;
        }
        TileJarFillable destination = (TileJarFillable) target;
        boolean voidJar = target instanceof TileJarFillableVoid;
        AspectList contents = destination.getAspects();

        boolean empty = contents == null || contents.size() == 0;
        boolean acceptsWhenEmpty = empty
                && (destination.aspectFilter == null || destination.aspectFilter == aspect);
        boolean acceptsWhenFilled = !empty && contents.getAspects()[0] == aspect
                && (contents.getAmount(contents.getAspects()[0]) < 64 || voidJar);

        if (acceptsWhenEmpty || acceptsWhenFilled) {
            destination.addToContainer(aspect, 1);
            item.setAspects(jar, aspectList.remove(aspect, 1));
            markDirty();
        }
    }

    /** Resolves the tile the hopper below is pointing at. */
    @Nullable
    private TileEntity getHopperFacing(net.minecraft.util.math.BlockPos hopperPos) {
        IBlockState state = world.getBlockState(hopperPos);
        if (!(state.getBlock() instanceof BlockHopper)) {
            return null;
        }
        EnumFacing facing = BlockHopper.getFacing(state.getBlock().getMetaFromState(state));
        return world.getTileEntity(hopperPos.offset(facing));
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    // ---- IAspectContainer: the jar in the slot is what the funnel "contains" ----

    @Override
    public AspectList getAspects() {
        ItemStack jar = inventory.getStackInSlot(0);
        if (jar.isEmpty() || !(jar.getItem() instanceof BlockJarItem)) {
            return new AspectList();
        }
        AspectList aspects = ((BlockJarItem) jar.getItem()).getAspects(jar);
        return aspects == null ? new AspectList() : aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        ItemStack jar = inventory.getStackInSlot(0);
        if (!jar.isEmpty() && jar.getItem() instanceof BlockJarItem) {
            ((BlockJarItem) jar.getItem()).setAspects(jar, aspects);
            markDirty();
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return getAspects().getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        AspectList mine = getAspects();
        for (Aspect aspect : ot.getAspects()) {
            if (mine.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return getAspects().getAmount(tag);
    }

    // ---- persistence + capability ----

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("Inventory", inventory.serializeNBT());
        return nbt;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }
}
