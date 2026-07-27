package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Ichor axe — ported from Thaumic Tinkerer's {@code ItemIchorAxe}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out. Harvest level 4, as in the original.</p>
 */
public class ItemIchorAxe extends ItemAxe {

    public ItemIchorAxe() {
        super(KamiMaterials.ICHOR, 5.0F, -3.0F);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
        this.setHarvestLevel("axe", 4);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
