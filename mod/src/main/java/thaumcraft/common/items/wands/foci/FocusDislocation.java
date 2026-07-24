package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
 * Focus of Dislocation — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. Right-click a block to blink the caster onto the targeted face
 * (short-range teleport).
 */
public class FocusDislocation extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.TRAVEL, 25).add(Aspect.ENTROPY, 5);

    public FocusDislocation() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x9933CC;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "DL" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 60;
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

        EnumFacing side = mop.sideHit;
        BlockPos dest = mop.getBlockPos().offset(side);
        // require headroom so we don't suffocate
        if (world.getBlockState(dest).getMaterial().blocksMovement()
                || world.getBlockState(dest.up()).getMaterial().blocksMovement()) {
            return wandStack;
        }
        if (!wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }
        if (!world.isRemote) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.7F, 1.0F);
            player.setPositionAndUpdate(dest.getX() + 0.5D, dest.getY(), dest.getZ() + 0.5D);
            player.fallDistance = 0.0F;
            world.playSound(null, dest.getX() + 0.5D, dest.getY(), dest.getZ() + 0.5D,
                    SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.7F, 1.0F);
        }
        Thaumcraft.proxy.blockSparkle(world, dest.getX(), dest.getY(), dest.getZ(), 0x9933CC, 10);
        player.swingArm(hand);
        return wandStack;
    }
}
