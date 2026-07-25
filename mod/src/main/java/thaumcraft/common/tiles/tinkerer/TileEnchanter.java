package thaumcraft.common.tiles.tinkerer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.tinkerer.EnchantmentCosts;

/**
 * Osmotic Enchanter — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2.
 *
 * <p>Applies enchantments to the tool in its first slot, paid for in vis drawn
 * from a wand in its second slot, one point per tick. It only runs while
 * standing in the original's multiblock: <strong>six</strong> pillars within
 * four blocks, each two to twelve Obsidian Totems tall and capped with
 * Nitor.</p>
 *
 * <p>Where the original opened a GUI listing every enchantment, this version
 * takes an enchanted book: right-click the enchanter with one and its
 * enchantments are queued and applied at full strength, ignoring the anvil's
 * compatibility rules and level caps — which is the point of the device. Costs
 * come from {@link EnchantmentCosts}, ported from the original's table.</p>
 */
public class TileEnchanter extends TileThaumcraft implements ITickable {

    public static final int SLOT_TOOL = 0;
    public static final int SLOT_WAND = 1;

    private static final int PILLARS_REQUIRED = 6;
    private static final int SEARCH_RADIUS = 4;
    private static final int MIN_PILLAR = 2;
    private static final int MAX_PILLAR = 12;

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            if (slot == SLOT_TOOL) {
                cancel();
            }
        }
    };

    /** Queued enchantment → level. */
    private final Map<Enchantment, Integer> queued = new LinkedHashMap<>();
    private AspectList totalCost = new AspectList();
    private AspectList paid = new AspectList();
    private boolean working;

    @Override
    public void update() {
        if (world == null || world.isRemote || !working) {
            return;
        }
        if (inventory.getStackInSlot(SLOT_TOOL).isEmpty() || queued.isEmpty()) {
            cancel();
            return;
        }
        if (countPillars() < PILLARS_REQUIRED) {
            // Structure broken mid-run: stop but keep the paid vis, as the
            // original did when its pillar check failed.
            working = false;
            markDirty();
            return;
        }
        if (isPaid()) {
            applyEnchantments();
            return;
        }
        drainOnePoint();
    }

    private void drainOnePoint() {
        ItemStack wand = inventory.getStackInSlot(SLOT_WAND);
        if (wand.isEmpty() || !(wand.getItem() instanceof ItemWandCasting)) {
            return;
        }
        ItemWandCasting wandItem = (ItemWandCasting) wand.getItem();
        if (wandItem.isStaff(wand)) {
            return;
        }
        AspectList onWand = wandItem.getAllVis(wand);

        List<Aspect> candidates = new ArrayList<>();
        for (Aspect aspect : totalCost.getAspectsSorted()) {
            int missing = totalCost.getAmount(aspect) - paid.getAmount(aspect);
            // TC4 wands store vis at 100 per point.
            if (missing > 0 && onWand.getAmount(aspect) >= 100) {
                candidates.add(aspect);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        Aspect aspect = candidates.get(world.rand.nextInt(candidates.size()));
        if (wandItem.consumeAllVisCrafting(wand, null, new AspectList().add(aspect, 1), true)) {
            paid.add(aspect, 1);
            markDirty();
        }
    }

    private boolean isPaid() {
        for (Aspect aspect : totalCost.getAspectsSorted()) {
            if (paid.getAmount(aspect) < totalCost.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    private void applyEnchantments() {
        ItemStack tool = inventory.getStackInSlot(SLOT_TOOL);
        for (Map.Entry<Enchantment, Integer> entry : queued.entrySet()) {
            tool.addEnchantment(entry.getKey(), entry.getValue());
        }
        world.playSound(null, pos, TCSounds.WAND, SoundCategory.BLOCKS, 1.0F, 1.0F);
        cancel();
    }

    /**
     * Queues everything on an enchanted book. Returns false when nothing on it
     * can be priced, so the caller can keep the book.
     */
    public boolean beginFromBook(ItemStack book) {
        Map<Enchantment, Integer> found = EnchantmentHelper.getEnchantments(book);
        if (found.isEmpty() || inventory.getStackInSlot(SLOT_TOOL).isEmpty()) {
            return false;
        }
        AspectList cost = new AspectList();
        Map<Enchantment, Integer> accepted = new LinkedHashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : found.entrySet()) {
            AspectList part = EnchantmentCosts.costFor(entry.getKey(), entry.getValue());
            if (part == null) {
                continue;
            }
            accepted.put(entry.getKey(), entry.getValue());
            for (Aspect aspect : part.getAspectsSorted()) {
                // Wands only ever hold primals, so only primal cost is payable —
                // the same aspects the original actually drained and checked.
                if (aspect.isPrimal()) {
                    cost.add(aspect, part.getAmount(aspect));
                }
            }
        }
        if (accepted.isEmpty() || cost.size() == 0) {
            return false;
        }
        queued.clear();
        queued.putAll(accepted);
        totalCost = cost;
        paid = new AspectList();
        working = true;
        markDirty();
        return true;
    }

    public void cancel() {
        queued.clear();
        totalCost = new AspectList();
        paid = new AspectList();
        working = false;
        markDirty();
    }

    /** Number of valid Obsidian-Totem-and-Nitor pillars around the enchanter. */
    public int countPillars() {
        int found = 0;
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                if (isPillar(pos.add(x, 0, z))) {
                    found++;
                    if (found >= PILLARS_REQUIRED) {
                        return found;
                    }
                }
            }
        }
        return found;
    }

    /** A stack of 2..12 Obsidian Totems capped with Nitor, as in the original. */
    private boolean isPillar(BlockPos base) {
        int totems = 0;
        for (int y = 0; base.getY() + y < world.getHeight(); y++) {
            BlockPos at = base.up(y);
            IBlockState state = world.getBlockState(at);
            if (state.getBlock() == ConfigBlocks.blockCosmeticSolid
                    && state.getBlock().getMetaFromState(state) == 0) {
                totems++;
                continue;
            }
            if (state.getBlock() == ConfigBlocks.blockAiry
                    && state.getBlock().getMetaFromState(state) == 1) {
                return totems >= MIN_PILLAR && totems <= MAX_PILLAR;
            }
            return false;
        }
        return false;
    }

    public boolean isWorking() {
        return working;
    }

    public AspectList getTotalCost() {
        return totalCost;
    }

    public AspectList getPaid() {
        return paid;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    // ---- NBT / capabilities ----

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
        working = nbt.getBoolean("working");
        totalCost = new AspectList();
        totalCost.readFromNBT(nbt.getCompoundTag("totalCost"));
        paid = new AspectList();
        paid.readFromNBT(nbt.getCompoundTag("paid"));
        queued.clear();
        NBTTagCompound q = nbt.getCompoundTag("queued");
        for (String key : q.getKeySet()) {
            Enchantment enchantment = Enchantment.getEnchantmentByLocation(key);
            if (enchantment != null) {
                queued.put(enchantment, q.getInteger(key));
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setTag("Inventory", inventory.serializeNBT());
        nbt.setBoolean("working", working);
        NBTTagCompound total = new NBTTagCompound();
        totalCost.writeToNBT(total);
        nbt.setTag("totalCost", total);
        NBTTagCompound paidTag = new NBTTagCompound();
        paid.writeToNBT(paidTag);
        nbt.setTag("paid", paidTag);
        NBTTagCompound q = new NBTTagCompound();
        for (Map.Entry<Enchantment, Integer> entry : queued.entrySet()) {
            if (entry.getKey().getRegistryName() != null) {
                q.setInteger(entry.getKey().getRegistryName().toString(), entry.getValue());
            }
        }
        nbt.setTag("queued", q);
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
