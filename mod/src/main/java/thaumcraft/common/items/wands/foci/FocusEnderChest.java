package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus of the Ender Chest — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune) for 1.12.2. Right-click to open your ender chest inventory
 * from anywhere.
 */
public class FocusEnderChest extends ItemFocusBasic {

    private static final AspectList COST = new AspectList().add(Aspect.VOID, 10).add(Aspect.TRAVEL, 5);

    public FocusEnderChest() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x116655;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "EC" + super.getSortingHelper(stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 40;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        EnumHand hand = ItemWandCasting.getHandHoldingWand(player, wandStack);

        if (!wand.consumeAllVis(wandStack, player, getVisCost(focusStack), true, false)) {
            return wandStack;
        }
        if (!world.isRemote) {
            InventoryEnderChest ender = player.getInventoryEnderChest();
            if (ender != null) {
                player.displayGUIChest(ender);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }
        player.swingArm(hand);
        return wandStack;
    }
}
