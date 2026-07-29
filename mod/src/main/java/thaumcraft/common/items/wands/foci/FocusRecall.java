package thaumcraft.common.items.wands.foci;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.tinkerer.kami.ItemSkyPearl;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * Wand Focus: Recall — the port of Thaumic Tinkerer's {@code ItemFocusRecall}.
 *
 * <p>Held down for sixty ticks, then it looks along the hotbar for the first
 * attuned sky pearl and throws the caster at the gate it names. Only within
 * the same dimension, and the vis is charged twice: once as a dry run to check
 * the wand can afford it, and again for real once the jump has happened.</p>
 */
public class FocusRecall extends ItemFocusBasic {

    private static final AspectList COST = new AspectList()
            .add(Aspect.AIR, 4000).add(Aspect.EARTH, 4000).add(Aspect.ORDER, 4000);

    public FocusRecall() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x9CF8FF;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            return;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        if (Integer.MAX_VALUE - count <= 60) {
            return;
        }

        ItemStack pearl = ItemStack.EMPTY;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack inSlot = player.inventory.getStackInSlot(slot);
            if (!inSlot.isEmpty() && inSlot.getItem() instanceof ItemSkyPearl
                    && ItemSkyPearl.isAttuned(inSlot)) {
                pearl = inSlot;
                break;
            }
        }

        if (!pearl.isEmpty() && ItemSkyPearl.getDim(pearl) == player.dimension) {
            BlockPos dest = new BlockPos(
                    ItemSkyPearl.getX(pearl), ItemSkyPearl.getY(pearl), ItemSkyPearl.getZ(pearl));
            if (wand.consumeAllVis(wandStack, player, getVisCost(wandStack), false, false)
                    && TileWarpGate.teleportPlayer(player, dest)) {
                wand.consumeAllVis(wandStack, player, getVisCost(wandStack), true, false);
            }
        }
        player.resetActiveHand();
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "RECALL" + super.getSortingHelper(stack);
    }

    // From the original's ItemModKamiFocus — see FocusXpDrain for the note.

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public int getItemEnchantability() {
        return 5;
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.WAVE;
    }
}
