package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSpade;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Ichor shovel — ported from Thaumic Tinkerer's {@code ItemIchorShovel}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out. Harvest level 4, as in the original.</p>
 */
public class ItemIchorShovel extends ItemSpade {

    public ItemIchorShovel() {
        super(KamiMaterials.ICHOR);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.setHarvestLevel("shovel", 4);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
