package thaumcraft.common.tiles.tinkerer;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.blocks.tinkerer.BlockAnimationTablet;

/**
 * Animation Tablet ("Tool Dynamism Tablet") — reimplemented from Thaumic
 * Tinkerer (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Holds a single tool and works it against whatever sits in front of the
 * face it points at. In <em>strike</em> mode it attacks creatures standing
 * there, or chews through the block; in <em>use</em> mode it right-clicks the
 * block or the creature with the held item. The swing cycle length matches the
 * original ({@value #MAX_DEGREE} degrees at {@value #SWING_SPEED} per tick,
 * up and back).</p>
 *
 * <p>Where the original opened a GUI to set the tool and the two toggles, this
 * version uses direct interaction — right-click to insert or take the tool,
 * sneak-click to switch mode, sneak-click holding redstone to switch between
 * running freely and waiting for a redstone pulse. The swing is state kept for
 * a renderer; there is no TESR yet, so the tablet does not visibly swing.</p>
 */
public class TileAnimationTablet extends TileThaumcraft implements ITickable {

    private static final int SWING_SPEED = 3;
    private static final int MAX_DEGREE = 45;
    /** Vanilla-equivalent work needed to break a block of hardness 1. */
    private static final float BREAK_WORK_PER_HARDNESS = 30.0F;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
            breakProgress = 0.0F;
        }
    };

    /** true = strike (attack/break), false = use (right-click). */
    private boolean strikeMode = true;
    /** true = only act on a redstone pulse. */
    private boolean redstoneMode;
    private boolean pulseQueued;

    private int swingProgress;
    private int swingMod;
    private float breakProgress;
    private BlockPos breakingAt;

    private FakePlayer fakePlayer;

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        ItemStack tool = inventory.getStackInSlot(0);
        if (tool.isEmpty()) {
            resetSwing();
            return;
        }

        if (swingProgress >= MAX_DEGREE) {
            swingHit();
        }
        swingMod = swingProgress <= 0 ? 0 : swingProgress >= MAX_DEGREE ? -SWING_SPEED : swingMod;
        swingProgress = Math.max(0, swingProgress + swingMod);

        if (swingProgress == 0 && shouldStart()) {
            swingMod = SWING_SPEED;
            swingProgress = 1;
        }
    }

    private boolean shouldStart() {
        if (!redstoneMode) {
            return true;
        }
        if (pulseQueued) {
            pulseQueued = false;
            return true;
        }
        return false;
    }

    /** Called by the block on a rising redstone edge. */
    public void onRedstonePulse() {
        if (redstoneMode) {
            pulseQueued = true;
        }
    }

    private void resetSwing() {
        swingMod = 0;
        swingProgress = 0;
        breakProgress = 0.0F;
        breakingAt = null;
    }

    private void swingHit() {
        BlockPos target = pos.offset(getFacing());
        ItemStack tool = inventory.getStackInSlot(0);
        FakePlayer player = getFakePlayer();
        if (player == null || tool.isEmpty()) {
            return;
        }
        player.setHeldItem(EnumHand.MAIN_HAND, tool);
        player.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        if (strikeMode) {
            strike(player, target);
        } else {
            use(player, target);
        }

        // The fake player may have consumed, damaged or emptied the stack.
        inventory.setStackInSlot(0, player.getHeldItem(EnumHand.MAIN_HAND));
    }

    private void strike(FakePlayer player, BlockPos target) {
        EntityLivingBase victim = pickVictim(target);
        if (victim != null) {
            player.attackTargetEntityWithCurrentItem(victim);
            breakProgress = 0.0F;
            return;
        }
        IBlockState state = world.getBlockState(target);
        if (state.getBlock() == Blocks.AIR || state.getBlockHardness(world, target) < 0.0F) {
            breakProgress = 0.0F;
            breakingAt = null;
            return;
        }
        if (!target.equals(breakingAt)) {
            breakingAt = target;
            breakProgress = 0.0F;
        }
        float hardness = Math.max(0.05F, state.getBlockHardness(world, target));
        breakProgress += player.getDigSpeed(state, target);
        if (breakProgress >= hardness * BREAK_WORK_PER_HARDNESS) {
            harvest(player, target, state);
            breakProgress = 0.0F;
            breakingAt = null;
        } else {
            // Show the crack overlay while the tablet works, as the original did.
            world.sendBlockBreakProgress(player.getEntityId(), target,
                    (int) (breakProgress / (hardness * BREAK_WORK_PER_HARDNESS) * 10.0F));
        }
    }

    private void harvest(FakePlayer player, BlockPos target, IBlockState state) {
        world.sendBlockBreakProgress(player.getEntityId(), target, -1);
        state.getBlock().harvestBlock(world, player, target, state, world.getTileEntity(target),
                player.getHeldItem(EnumHand.MAIN_HAND));
        world.destroyBlock(target, false);
        pullDrops(target);
    }

    /** Sweeps drops back onto the tablet so they do not scatter. */
    private void pullDrops(BlockPos target) {
        List<EntityItem> drops = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(target).grow(1.0D));
        for (EntityItem drop : drops) {
            drop.setPosition(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            drop.motionX = drop.motionY = drop.motionZ = 0.0D;
        }
    }

    private void use(FakePlayer player, BlockPos target) {
        EntityLivingBase victim = pickVictim(target);
        ItemStack tool = player.getHeldItem(EnumHand.MAIN_HAND);
        if (victim != null && tool.getItem().itemInteractionForEntity(tool, player, victim, EnumHand.MAIN_HAND)) {
            return;
        }
        IBlockState state = world.getBlockState(target);
        EnumFacing side = getFacing().getOpposite();
        if (state.getBlock() != Blocks.AIR
                && state.getBlock().onBlockActivated(world, target, state, player, EnumHand.MAIN_HAND, side,
                        0.5F, 0.5F, 0.5F)) {
            return;
        }
        if (tool.getItem().onItemUse(player, world, target, EnumHand.MAIN_HAND, side, 0.5F, 0.5F, 0.5F)
                == EnumActionResult.SUCCESS) {
            return;
        }
        tool.getItem().onItemRightClick(world, player, EnumHand.MAIN_HAND);
    }

    @Nullable
    private EntityLivingBase pickVictim(BlockPos target) {
        List<EntityLivingBase> found = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(target));
        found.removeIf(e -> e instanceof FakePlayer);
        return found.isEmpty() ? null : found.get(world.rand.nextInt(found.size()));
    }

    @Nullable
    private FakePlayer getFakePlayer() {
        if (!(world instanceof WorldServer)) {
            return null;
        }
        if (fakePlayer == null) {
            fakePlayer = FakePlayerFactory.get((WorldServer) world, BlockAnimationTablet.TABLET_PROFILE);
        }
        return fakePlayer;
    }

    private EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockAnimationTablet
                ? state.getValue(BlockAnimationTablet.FACING)
                : EnumFacing.NORTH;
    }

    // ---- interaction helpers used by the block ----

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean isStrikeMode() {
        return strikeMode;
    }

    public boolean isRedstoneMode() {
        return redstoneMode;
    }

    public void toggleMode() {
        strikeMode = !strikeMode;
        resetSwing();
        markDirty();
    }

    public void toggleRedstoneMode() {
        redstoneMode = !redstoneMode;
        pulseQueued = false;
        markDirty();
    }

    // ---- NBT / capabilities ----

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        inventory.deserializeNBT(nbt.getCompoundTag("Inventory"));
        strikeMode = nbt.getBoolean("strikeMode");
        redstoneMode = nbt.getBoolean("redstoneMode");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setTag("Inventory", inventory.serializeNBT());
        nbt.setBoolean("strikeMode", strikeMode);
        nbt.setBoolean("redstoneMode", redstoneMode);
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
