package thaumcraft.common.tiles.tinkerer;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.tinkerer.BlockRepairer;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;

/**
 * Repairer tile — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2.
 *
 * <p>Holds a single damaged item and mends it by drawing essentia from a tube
 * attached to its facing side. Faithful to the original: one point of essentia
 * is pulled per attempt, every ten ticks, and how much durability that restores
 * depends on which aspect arrives — {@code TOOL} 8, {@code CRAFT} 5,
 * {@code ORDER} 3. It advertises {@code TOOL} suction at strength 128 but
 * accepts any of the three, so a jar of either will keep it running.</p>
 *
 * <p>The original's Tinkers' Construct branch is deliberately not carried
 * over.</p>
 */
public class TileRepairer extends TileTinkerer implements ITickable, IEssentiaTransport, IAspectContainer {

    /** Durability restored per point of essentia, by aspect. Ordered best-first. */
    private static final Map<Aspect, Integer> REPAIR_VALUES = new LinkedHashMap<>();

    static {
        REPAIR_VALUES.put(Aspect.TOOL, 8);
        REPAIR_VALUES.put(Aspect.CRAFT, 5);
        REPAIR_VALUES.put(Aspect.ORDER, 3);
    }

    private static final int INTERVAL = 10;
    private static final int SUCTION = 128;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return !stack.isEmpty() && stack.isItemStackDamageable();
        }
    };

    private int ticks;

    /**
     * Upstream's {@code ticksExisted}, kept because the renderer spins the item
     * off it. The work below runs server-side only, so this has to be counted
     * before that early return or the client would never advance it and the
     * item would hang motionless.
     */
    public int renderTicks;

    /**
     * Whether essentia was actually drawn on the last attempt. Upstream's
     * {@code tookLastTick}: the renderer spins one of two markers under the
     * block from it, so it has to reach the client, which is why it rides in
     * the custom NBT rather than staying a bare server-side field.
     */
    public boolean tookLastTick;

    @Override
    public void update() {
        this.renderTicks++;
        if (world == null || world.isRemote || ++ticks % INTERVAL != 0) {
            return;
        }
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || stack.getItemDamage() <= 0) {
            return;
        }
        int restored = drawEssentia();
        this.tookLastTick = restored > 0;
        if (restored <= 0) {
            markDirty();
            return;
        }
        stack.setItemDamage(Math.max(0, stack.getItemDamage() - restored));
        markDirty();
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockSparkle(pos.getX(), pos.getY(), pos.getZ(), 0x9080FF),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(),
                        pos.getX(), pos.getY(), pos.getZ(), 32.0D));
    }

    /**
     * Pulls a single point of essentia from the attached tube and reports how
     * much durability it is worth, or 0 when nothing could be drawn.
     */
    private int drawEssentia() {
        EnumFacing facing = getFacing();
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(
                world, pos.getX(), pos.getY(), pos.getZ(), facing);
        if (!(te instanceof IEssentiaTransport)) {
            return 0;
        }
        IEssentiaTransport transport = (IEssentiaTransport) te;
        EnumFacing opposite = facing.getOpposite();
        if (!transport.canOutputTo(opposite)) {
            return 0;
        }
        if (transport.getSuctionAmount(opposite) >= getSuctionAmount(facing)) {
            return 0;
        }
        for (Map.Entry<Aspect, Integer> entry : REPAIR_VALUES.entrySet()) {
            if (transport.getSuctionType(opposite) == entry.getKey()
                    && transport.takeEssentia(entry.getKey(), 1, opposite) == 1) {
                return entry.getValue();
            }
        }
        return 0;
    }

    private EnumFacing getFacing() {
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockRepairer
                ? state.getValue(BlockRepairer.FACING)
                : EnumFacing.DOWN;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    // ---- NBT ----

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
        this.tookLastTick = nbt.getBoolean("tookLastTick");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setTag("Inventory", inventory.serializeNBT());
        nbt.setBoolean("tookLastTick", this.tookLastTick);
    }

    // ---- Capabilities: the slot is open to hoppers and pipes ----

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

    // ---- IEssentiaTransport: a pure consumer on its facing side ----

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == getFacing();
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return false;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return isConnectable(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return face == getFacing() ? Aspect.TOOL : null;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return face == getFacing() ? SUCTION : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }

    // ---- IAspectContainer: goggles show the outstanding damage as Entropy ----

    @Override
    public AspectList getAspects() {
        ItemStack stack = inventory.getStackInSlot(0);
        return stack.isEmpty() || stack.getItemDamage() <= 0
                ? null
                : new AspectList().add(Aspect.ENTROPY, stack.getItemDamage());
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return 0;
    }
}
