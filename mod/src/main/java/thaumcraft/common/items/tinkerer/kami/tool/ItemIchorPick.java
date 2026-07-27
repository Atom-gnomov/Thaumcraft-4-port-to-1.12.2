package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPickaxe;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Ichor pickaxe — ported from Thaumic Tinkerer's {@code ItemIchorPick}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out. Harvest level 4, as in the original.</p>
 */
public class ItemIchorPick extends ItemPickaxe {

    public ItemIchorPick() {
        super(KamiMaterials.ICHOR);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
        this.setHarvestLevel("pickaxe", 4);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
