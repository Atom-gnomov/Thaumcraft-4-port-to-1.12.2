package thaumcraft.common.items.endgame;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.Thaumcraft;

/**
 * What is left of a Ward of the Last Breath after it has argued with death
 * and won. Not wearable — but the soul inside is already broken in, so
 * re-infusing it into a whole ward is cheaper than forging the first one.
 *
 * <p>End Legacy module, phase 2 ({@code END_LEGACY_PLAN.md}).</p>
 */
public class ItemWardLastBreathCracked extends Item {

    public ItemWardLastBreathCracked() {
        this.setMaxStackSize(1);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}
