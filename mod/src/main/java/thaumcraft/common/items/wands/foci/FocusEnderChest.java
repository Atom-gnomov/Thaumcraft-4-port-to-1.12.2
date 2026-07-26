package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Focus of the Ender Chest — ported from Thaumic Tinkerer's
 * ItemFocusEnderChest (pixlepix / nekosune / Vazkii): opens the caster's ender
 * chest from anywhere, at the original's cost.
 */
public class FocusEnderChest extends ItemFocusBasic {

    /** The original's visUsage. */
    public static final AspectList COST = new AspectList().add(Aspect.ENTROPY, 100).add(Aspect.ORDER, 100);

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
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();

        if (wand.consumeAllVis(wandStack, player, COST, true, false)) {
            if (!world.isRemote) {
                InventoryEnderChest ender = player.getInventoryEnderChest();
                if (ender != null) {
                    player.displayGUIChest(ender);
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            SoundEvents.BLOCK_ENDERCHEST_OPEN, SoundCategory.PLAYERS, 0.5F, 1.0F);
                }
            }
            player.swingArm(ItemWandCasting.getHandHoldingWand(player, wandStack));
        }
        return wandStack;
    }
}
