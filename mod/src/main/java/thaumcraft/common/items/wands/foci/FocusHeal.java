package thaumcraft.common.items.wands.foci;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.TCSounds;

/**
 * Focus of Healing — ported 1:1 from Thaumic Tinkerer's ItemFocusHeal
 * (pixlepix / nekosune / Vazkii). Held to channel: a counter runs while the
 * caster can be healed and every 30 ticks (less with potency) one half-heart is
 * restored and the vis is actually charged.
 */
public class FocusHeal extends ItemFocusBasic {

    /** The original's visUsage: charged on each heal tick. */
    private static final AspectList COST = new AspectList().add(Aspect.EARTH, 45).add(Aspect.WATER, 45);

    /** Per-player countdown, as the original's playerHealData map. */
    private static final Map<UUID, Integer> HEAL_DATA = new HashMap<>();

    public FocusHeal() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF88AA;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "HL" + super.getSortingHelper(stack);
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        player.setActiveHand(ItemWandCasting.getHandHoldingWand(player, wandStack));
        WandManager.setCooldown(player, -1);
        return wandStack;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        World world = player.world;

        if (!wand.consumeAllVis(wandStack, player, COST, false, false) || !player.shouldHeal()) {
            return;
        }

        int potency = this.getUpgradeLevel(wand.getFocusItem(wandStack), FocusUpgradeType.potency);
        int progress = HEAL_DATA.getOrDefault(player.getUniqueID(), 0) + 1;
        HEAL_DATA.put(player.getUniqueID(), progress);

        Thaumcraft.proxy.sparkle(
                (float) player.posX + world.rand.nextFloat() - 0.5F,
                (float) player.posY + world.rand.nextFloat(),
                (float) player.posZ + world.rand.nextFloat() - 0.5F, 0);

        if (progress >= 30 - potency * 10 / 3) {
            HEAL_DATA.put(player.getUniqueID(), 0);
            wand.consumeAllVis(wandStack, player, COST, true, false);
            player.heal(1);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    TCSounds.WAND, SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wandstack, World world, EntityPlayer player, int count) {
        HEAL_DATA.remove(player.getUniqueID());
    }
}
