package thaumcraft.common.tiles.tinkerer.kami;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import thaumcraft.common.items.tinkerer.kami.ItemSkyPearl;

/**
 * The warp gate — the port of Thaumic Tinkerer's {@code TileWarpGate}.
 *
 * <p>Ten slots holding sky pearls; each attuned pearl is one destination in
 * the gate's list. Teleporting checks the gate at the far end: a locked gate
 * refuses arrivals, and a slot whose pearl points at nothing says so.</p>
 */
public class TileWarpGate extends TileEntity implements IInventory, ITickable {

    private static final String TAG_LOCKED = "locked";

    public boolean locked = false;

    /** One teleport per tick, so a crowded gate cannot fling a player twice. */
    private boolean teleportedThisTick = false;

    private ItemStack[] inventorySlots = new ItemStack[10];

    public TileWarpGate() {
        clearInventory();
    }

    private void clearInventory() {
        for (int i = 0; i < this.inventorySlots.length; i++) {
            this.inventorySlots[i] = ItemStack.EMPTY;
        }
    }

    /**
     * Standing on the gate and sneaking opens the destination map. The check
     * against the client player is what keeps this from firing server-side —
     * upstream relies on the same comparison.
     */
    @Override
    public void update() {
        java.util.List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class,
                new net.minecraft.util.math.AxisAlignedBB(
                        this.pos.getX(), this.pos.getY() + 1, this.pos.getZ(),
                        this.pos.getX() + 1, this.pos.getY() + 1.5D, this.pos.getZ() + 1));
        EntityPlayer clientPlayer = thaumcraft.common.Thaumcraft.proxy.getClientPlayer();
        for (EntityPlayer player : players) {
            if (player != null && player == clientPlayer && player.isSneaking()) {
                player.openGui(thaumcraft.common.Thaumcraft.instance,
                        thaumcraft.common.CommonProxy.GUI_WARP_GATE_DESTINATIONS,
                        this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
                break;
            }
        }
        this.teleportedThisTick = false;
    }

    public void teleportPlayer(EntityPlayer player, int index) {
        if (this.teleportedThisTick) {
            return;
        }
        ItemStack stack = index < getSizeInventory() ? getStackInSlot(index) : ItemStack.EMPTY;
        if (!stack.isEmpty() && ItemSkyPearl.isAttuned(stack)) {
            BlockPos dest = new BlockPos(
                    ItemSkyPearl.getX(stack), ItemSkyPearl.getY(stack), ItemSkyPearl.getZ(stack));
            if (teleportPlayer(player, dest)) {
                this.teleportedThisTick = true;
            }
        }
    }

    public static boolean teleportPlayer(EntityPlayer player, BlockPos dest) {
        TileEntity tile = player.world.getTileEntity(dest);
        if (tile instanceof TileWarpGate) {
            TileWarpGate destGate = (TileWarpGate) tile;
            if (!destGate.locked) {
                playArrival(player, 1.0F);
                sparkleAround(player);
                player.dismountRidingEntity();
                if (player instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) player).connection.setPlayerLocation(
                            dest.getX() + 0.5D, dest.getY() + 1.6D, dest.getZ() + 0.5D,
                            player.rotationYaw, player.rotationPitch);
                }
                sparkleAround(player);
                playArrival(player, 0.1F);
                return true;
            } else if (!player.world.isRemote) {
                player.sendMessage(new TextComponentTranslation("ttmisc.noTeleport"));
            }
        } else if (!player.world.isRemote) {
            player.sendMessage(new TextComponentTranslation("ttmisc.noDest"));
        }
        return false;
    }

    private static void playArrival(EntityPlayer player, float pitch) {
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(
                        new net.minecraft.util.ResourceLocation("thaumcraft", "wand")),
                SoundCategory.PLAYERS, 1.0F, pitch);
    }

    /** Twenty sparks either side of the jump, as upstream throws them. */
    private static void sparkleAround(EntityPlayer player) {
        for (int i = 0; i < 20; i++) {
            thaumcraft.common.Thaumcraft.proxy.sparkle(
                    (float) player.posX + player.world.rand.nextFloat() - 0.5F,
                    (float) player.posY + player.world.rand.nextFloat(),
                    (float) player.posZ + player.world.rand.nextFloat() - 0.5F,
                    6.0F, 0, 0.0F);
        }
    }

    // ---- NBT ----

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);
        readCustomNBT(cmp);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);
        writeCustomNBT(cmp);
        return cmp;
    }

    public void readCustomNBT(NBTTagCompound cmp) {
        this.locked = cmp.getBoolean(TAG_LOCKED);
        NBTTagList list = cmp.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        this.inventorySlots = new ItemStack[getSizeInventory()];
        clearInventory();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound slotCmp = list.getCompoundTagAt(i);
            int slot = slotCmp.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.inventorySlots.length) {
                this.inventorySlots[slot] = new ItemStack(slotCmp);
            }
        }
    }

    public void writeCustomNBT(NBTTagCompound cmp) {
        cmp.setBoolean(TAG_LOCKED, this.locked);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.inventorySlots.length; ++i) {
            if (!this.inventorySlots[i].isEmpty()) {
                NBTTagCompound slotCmp = new NBTTagCompound();
                slotCmp.setByte("Slot", (byte) i);
                this.inventorySlots[i].writeToNBT(slotCmp);
                list.appendTag(slotCmp);
            }
        }
        cmp.setTag("Items", list);
    }

    // ---- Client sync ----

    /**
     * Sends the gate's pearls and lock state to the client.
     *
     * <p>Everything the gate shows is drawn from the <em>client's</em> copy of
     * this tile: the destination map reads the pearls out of
     * {@link #getStackInSlot} to place its markers, and the lock button reads
     * {@link #locked}. Without this the client's copy stays as it was
     * constructed — ten empty slots — so the map is blank however many pearls
     * are really inside, and there is nothing to click. Upstream syncs it with
     * {@code getDescriptionPacket}/{@code onDataPacket}; the port carried
     * {@link #writeCustomNBT} across and never wired it to anything.</p>
     */
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound cmp = super.getUpdateTag();
        writeCustomNBT(cmp);
        return cmp;
    }

    @Override
    public net.minecraft.network.play.server.SPacketUpdateTileEntity getUpdatePacket() {
        return new net.minecraft.network.play.server.SPacketUpdateTileEntity(
                this.pos, -999, getUpdateTag());
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager manager,
                             net.minecraft.network.play.server.SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound cmp) {
        readCustomNBT(cmp);
    }

    /**
     * Pushes the gate's state to every client watching it. Slot changes go
     * through {@code markDirty}, so this is where the resync has to hang, or
     * the map would only refresh on chunk reload.
     */
    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isRemote) {
            net.minecraft.block.state.IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    // ---- IInventory ----

    @Override
    public int getSizeInventory() {
        return this.inventorySlots.length;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventorySlots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.inventorySlots.length
                ? this.inventorySlots[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getStackInSlot(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack taken;
        if (stack.getCount() <= count) {
            taken = stack;
            this.inventorySlots[index] = ItemStack.EMPTY;
        } else {
            taken = stack.splitStack(count);
            if (stack.getCount() == 0) {
                this.inventorySlots[index] = ItemStack.EMPTY;
            }
        }
        markDirty();
        return taken;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getStackInSlot(index);
        this.inventorySlots[index] = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= 0 && index < this.inventorySlots.length) {
            this.inventorySlots[index] = stack == null ? ItemStack.EMPTY : stack;
            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(this.pos.getX() + 0.5D,
                        this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    /** Only attuned pearls belong here — an empty one is not a destination. */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemSkyPearl;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        clearInventory();
    }

    @Override
    public String getName() {
        return "container.warpGate";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
