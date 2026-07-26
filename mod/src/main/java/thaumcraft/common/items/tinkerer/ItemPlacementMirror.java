package thaumcraft.common.items.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Placement Mirror — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. Sneak-right-click a block to bind its type; then right-click a
 * block face at range to place the bound block there, consuming one matching
 * item from your inventory.
 */
public class ItemPlacementMirror extends Item {

    private static final String KEY_BLOCK = "BoundBlock";
    private static final String KEY_META = "BoundMeta";

    public ItemPlacementMirror() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState clicked = world.getBlockState(pos);

        if (player.isSneaking()) {
            ResourceLocation name = Block.REGISTRY.getNameForObject(clicked.getBlock());
            if (name != null) {
                NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
                tag.setString(KEY_BLOCK, name.toString());
                tag.setInteger(KEY_META, clicked.getBlock().getMetaFromState(clicked));
                stack.setTagCompound(tag);
                if (!world.isRemote) {
                    player.sendStatusMessage(new TextComponentTranslation("tc.placementmirror.bound",
                            new ItemStack(clicked.getBlock(), 1, clicked.getBlock().getMetaFromState(clicked))
                                    .getDisplayName()), true);
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }

        Block bound = getBoundBlock(stack);
        if (bound == null) {
            return EnumActionResult.PASS;
        }
        int meta = stack.getTagCompound().getInteger(KEY_META);
        BlockPos target = pos.offset(facing);
        if (!world.getBlockState(target).getBlock().isReplaceable(world, target)) {
            return EnumActionResult.PASS;
        }
        int slot = findMatchingSlot(player, bound, meta);
        if (slot < 0) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            world.setBlockState(target, bound.getStateFromMeta(meta), 3);
            if (!player.capabilities.isCreativeMode) {
                player.inventory.decrStackSize(slot, 1);
            }
            world.playSound(null, target, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        player.swingArm(hand);
        return EnumActionResult.SUCCESS;
    }

    private static Block getBoundBlock(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(KEY_BLOCK)) return null;
        Block block = Block.REGISTRY.getObject(new ResourceLocation(stack.getTagCompound().getString(KEY_BLOCK)));
        return block == net.minecraft.init.Blocks.AIR ? null : block;
    }

    private static int findMatchingSlot(EntityPlayer player, Block bound, int meta) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack s = player.inventory.getStackInSlot(i);
            if (s.isEmpty() || !(s.getItem() instanceof ItemBlock)) continue;
            if (((ItemBlock) s.getItem()).getBlock() == bound && s.getMetadata() == meta) {
                return i;
            }
        }
        return -1;
    }
}
