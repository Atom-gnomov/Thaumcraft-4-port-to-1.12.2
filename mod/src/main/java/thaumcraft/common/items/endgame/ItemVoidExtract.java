package thaumcraft.common.items.endgame;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.Thaumcraft;

/**
 * Void Extract — chorus fruit, boiled down in the crucible until only the
 * refusal to stay in one place remains. Crafting component for the wards.
 *
 * <p>End Legacy module, phase 2 (new content, no 1.7.10 original — owner's
 * decision, {@code END_LEGACY_PLAN.md}).</p>
 */
public class ItemVoidExtract extends Item {

    public ItemVoidExtract() {
        this.setMaxStackSize(16);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
