package thaumcraft.common.items.wands.foci;

import java.awt.Color;
import java.util.List;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.tinkerer.ExperienceHelper;

/**
 * Wand Focus: Experience Drain — the port of Thaumic Tinkerer's
 * {@code ItemFocusXPDrain}.
 *
 * <p>Costs no vis at all; it is paid for in experience. Each tick it walks the
 * six primals in turn, finds the first that is not already full, spends the
 * player's experience and puts five vis of that aspect into the wand.</p>
 */
public class FocusXpDrain extends ItemFocusBasic {

    /** Empty on purpose: the price is experience, not vis. */
    private static final AspectList COST = new AspectList();

    /** Where the round-robin over the primals left off, as upstream's field. */
    private int lastGiven = 0;

    public FocusXpDrain() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (player.world.isRemote) {
            return;
        }
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            return;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        AspectList aspects = wand.getAllVis(wandStack);

        Aspect aspectToAdd = null;
        int takes = 0;
        while (aspectToAdd == null && takes < 7) {
            this.lastGiven = this.lastGiven == 5 ? 0 : this.lastGiven + 1;
            Aspect aspect = Aspect.getPrimalAspects().get(this.lastGiven);
            if (aspects.getAmount(aspect) < ItemWandCasting.getMaxVis(wandStack)) {
                aspectToAdd = aspect;
            }
            ++takes;
        }

        if (aspectToAdd != null) {
            int xpUse = getXpUse(wandStack);
            if (player.experienceTotal >= xpUse) {
                ExperienceHelper.drainPlayerXP(player, xpUse);
                wand.storeVis(wandStack, aspectToAdd,
                        Math.min(ItemWandCasting.getMaxVis(wandStack),
                                ItemWandCasting.getVis(wandStack, aspectToAdd) + 500));
            }
        }
    }

    /** Cycles through the rainbow rather than sitting on one colour. */
    @Override
    public int getFocusColor(ItemStack stack) {
        EntityPlayer player = thaumcraft.common.Thaumcraft.proxy.getClientPlayer();
        return player == null
                ? 0xFFFFFF
                : Color.HSBtoRGB(player.ticksExisted * 2 % 360 / 360.0F, 1.0F, 1.0F);
    }

    /** Thirty experience a tick, three less for every level of Frugal. */
    int getXpUse(ItemStack stack) {
        if (Config.enchFrugal == null) {
            return 30;
        }
        return 30 - EnchantmentHelper.getEnchantmentLevel(Config.enchFrugal, stack) * 3;
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return Config.enchFrugal != null
                && id == net.minecraft.enchantment.Enchantment.getEnchantmentID(Config.enchFrugal);
    }

    /** The cost line the base class would print is empty, so state the real one. */
    @Override
    public void addFocusInformation(ItemStack focusstack, EntityPlayer player,
                                    List<String> list, boolean advanced) {
        list.add(" §a" + I18n.translateToLocal("ttmisc.experience")
                + "§f x " + getXpUse(focusstack));
        super.addFocusInformation(focusstack, player, list, advanced);
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "XPDRAIN" + super.getSortingHelper(stack);
    }

    // The three below came from the original's ItemModKamiFocus, which this
    // port has no counterpart for — ItemFocusBasic already does everything
    // else that base class did, so its values live on the foci themselves.

    @Override
    public net.minecraft.item.EnumRarity getRarity(ItemStack stack) {
        return net.minecraft.item.EnumRarity.EPIC;
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
