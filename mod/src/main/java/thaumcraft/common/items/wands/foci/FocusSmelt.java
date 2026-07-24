package thaumcraft.common.items.wands.foci;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus of Smelting — reimplemented from Thaumic Tinkerer (pixlepix, nekosune)
 * for the 1.12.2 port. Right-click a block to smelt it: the block is replaced
 * in place when its furnace result is itself a block (cobblestone → stone,
 * sand → glass), otherwise it is broken and drops the smelted item (ore → ingot).
 */
public class FocusSmelt extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.FIRE, 30);

    public FocusSmelt() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF6600;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "SM" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 200;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting) || mop == null
                || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return wandStack;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);
        BlockPos pos = mop.getBlockPos();

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.isAir(state, world, pos) || state.getBlockHardness(world, pos) < 0.0F) {
            return wandStack;
        }

        ItemStack asStack = block.getPickBlock(state, mop, world, pos, player);
        if (asStack == null || asStack.isEmpty()) {
            asStack = new ItemStack(block, 1, block.getMetaFromState(state));
        }
        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(asStack);
        if (result.isEmpty()) {
            return wandStack;
        }

        if (!wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }

        if (!world.isRemote) {
            IBlockState smeltedBlockState = null;
            if (result.getItem() instanceof ItemBlock) {
                Block resultBlock = ((ItemBlock) result.getItem()).getBlock();
                smeltedBlockState = resultBlock.getStateFromMeta(result.getMetadata());
            }
            if (smeltedBlockState != null) {
                world.setBlockState(pos, smeltedBlockState, 3);
            } else {
                world.setBlockToAir(pos);
                Block.spawnAsEntity(world, pos, result.copy());
            }
            SoundEvent sound = SoundEvents.BLOCK_LAVA_EXTINGUISH;
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 0.5F, 2.2F);
            Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 0xFF6600, 6);
        }
        player.swingArm(hand);
        return wandStack;
    }
}
