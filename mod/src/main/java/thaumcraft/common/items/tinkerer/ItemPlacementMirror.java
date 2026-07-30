package thaumcraft.common.items.tinkerer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabTinkerer;
import thaumcraft.common.lib.TCSounds;

/**
 * Placement Mirror — ported 1:1 from Thaumic Tinkerer's
 * {@code ItemPlacementMirror} (pixlepix / nekosune / Vazkii). Sneak-click a
 * full cube to bind it, then click to lay a whole square of that block against
 * the face you are looking at, paying for every one out of your inventory.
 * Sneak-right-click in the air cycles the square from 3x3 up to 11x11.
 */
public class ItemPlacementMirror extends Item {

    private static final String TAG_BLOCK_NAME = "bName";
    private static final String TAG_BLOCK_META = "bMeta";
    private static final String TAG_SIZE = "size";

    public ItemPlacementMirror() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    /** Sneak in the air steps the square 3 → 5 → 7 → 9 → 11 → 3. */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            int size = getSize(stack);
            int newSize = size == 11 ? 3 : size + 2;
            setSize(stack, newSize);
            if (!world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation(
                        "tc.placementmirror.size", newSize + " x " + newSize), true);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if (player.isSneaking()) {
            // Only ordinary full cubes can be bound, as upstream checked render type 0.
            if (state.getRenderType() == EnumBlockRenderType.MODEL) {
                setBlock(stack, state.getBlock(), state.getBlock().getMetaFromState(state));
                if (!world.isRemote) {
                    player.sendStatusMessage(new TextComponentTranslation("tc.placementmirror.bound",
                            new ItemStack(state.getBlock(), 1,
                                    state.getBlock().getMetaFromState(state)).getDisplayName()), true);
                }
            }
        } else {
            placeAllBlocks(stack, player);
        }
        return EnumActionResult.SUCCESS;
    }

    private void placeAllBlocks(ItemStack stack, EntityPlayer player) {
        Block bound = getBlock(stack);
        if (bound == null) {
            return;
        }
        List<BlockPos> blocksToPlace = getBlocksToPlace(stack, player);
        ItemStack stackToPlace = new ItemStack(bound, 1, getBlockMeta(stack));
        if (stackToPlace.isEmpty() || !hasBlocks(player, stackToPlace, blocksToPlace.size())) {
            return;
        }
        for (BlockPos coords : blocksToPlace) {
            placeBlockAndConsume(player, stackToPlace, coords);
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    /**
     * The square the mirror will fill: laid against the face being looked at,
     * squared off by the size, and turned with the player when placed on a
     * floor or ceiling. Only air and replaceable blocks are included.
     */
    public static List<BlockPos> getBlocksToPlace(ItemStack stack, EntityPlayer player) {
        List<BlockPos> coords = new ArrayList<>();
        RayTraceResult pos = rayTrace(player, 5.0D);
        if (pos == null || pos.typeOfHit != RayTraceResult.Type.BLOCK) {
            return coords;
        }
        World world = player.world;
        BlockPos hit = pos.getBlockPos();
        if (world.getBlockState(hit).getBlock().isReplaceable(world, hit)) {
            hit = hit.down();
        }
        EnumFacing dir = pos.sideHit;
        int rotation = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int range = (getSize(stack) ^ 1) / 2;
        boolean topOrBottom = dir == EnumFacing.UP || dir == EnumFacing.DOWN;

        int xOff = !(dir == EnumFacing.WEST || dir == EnumFacing.EAST)
                ? (topOrBottom ? (player.rotationPitch > 75 || (rotation & 1) == 0 ? range : 0) : range) : 0;
        int yOff = topOrBottom ? (player.rotationPitch > 75 ? 0 : range) : range;
        int zOff = !(dir == EnumFacing.SOUTH || dir == EnumFacing.NORTH)
                ? (topOrBottom ? (player.rotationPitch > 75 || (rotation & 1) == 1 ? range : 0) : range) : 0;

        for (int x = -xOff; x < xOff + 1; x++) {
            for (int y = 0; y < yOff * 2 + 1; y++) {
                for (int z = -zOff; z < zOff + 1; z++) {
                    BlockPos target = new BlockPos(
                            hit.getX() + x + dir.getXOffset(),
                            hit.getY() + y + dir.getYOffset(),
                            hit.getZ() + z + dir.getZOffset());
                    IBlockState there = world.getBlockState(target);
                    if (there.getBlock().isAir(there, world, target)
                            || there.getBlock().isReplaceable(world, target)) {
                        coords.add(target);
                    }
                }
            }
        }
        return coords;
    }

    private static RayTraceResult rayTrace(EntityPlayer player, double range) {
        return player.world.rayTraceBlocks(
                player.getPositionEyes(1.0F),
                player.getPositionEyes(1.0F).add(player.getLookVec().scale(range)),
                true, false, false);
    }

    /** Creative pays nothing; otherwise every block in the square must be carried. */
    private static boolean hasBlocks(EntityPlayer player, ItemStack blockToPlace, int needed) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        int found = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (!slot.isEmpty() && slot.getItem() == blockToPlace.getItem()
                    && slot.getMetadata() == blockToPlace.getMetadata()) {
                found += slot.getCount();
                if (found >= needed) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeBlockAndConsume(EntityPlayer player, ItemStack blockToPlace, BlockPos coords) {
        if (!(blockToPlace.getItem() instanceof ItemBlock)) {
            return;
        }
        Block block = ((ItemBlock) blockToPlace.getItem()).getBlock();
        if (!player.world.isRemote) {
            player.world.setBlockState(coords, block.getStateFromMeta(blockToPlace.getMetadata()), 3);
        }
        if (player.capabilities.isCreativeMode) {
            return;
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (!slot.isEmpty() && slot.getItem() == blockToPlace.getItem()
                    && slot.getMetadata() == blockToPlace.getMetadata()) {
                player.inventory.decrStackSize(i, 1);
                return;
            }
        }
    }

    // ---- NBT ----

    public static Block getBlock(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG_BLOCK_NAME)) {
            return null;
        }
        Block block = Block.getBlockFromName(stack.getTagCompound().getString(TAG_BLOCK_NAME));
        return block == net.minecraft.init.Blocks.AIR ? null : block;
    }

    public static int getBlockMeta(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_BLOCK_META) : 0;
    }

    private static void setBlock(ItemStack stack, Block block, int meta) {
        ResourceLocation name = Block.REGISTRY.getNameForObject(block);
        if (name == null) {
            return;
        }
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setString(TAG_BLOCK_NAME, name.toString());
        tag.setInteger(TAG_BLOCK_META, meta);
        stack.setTagCompound(tag);
    }

    /** The original defaulted to a 3x3. */
    public static int getSize(ItemStack stack) {
        int size = stack.hasTagCompound() ? stack.getTagCompound().getInteger(TAG_SIZE) : 0;
        return size < 3 ? 3 : size;
    }

    private static void setSize(ItemStack stack, int size) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(TAG_SIZE, size);
        stack.setTagCompound(tag);
    }
}
