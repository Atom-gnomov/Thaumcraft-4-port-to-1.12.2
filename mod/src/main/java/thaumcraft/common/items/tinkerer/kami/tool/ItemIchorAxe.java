package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Ichor axe — ported from Thaumic Tinkerer's {@code ItemIchorAxe}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out. Harvest level 4, as in the original.</p>
 */
public class ItemIchorAxe extends ItemAxe {

    /**
     * The damage argument of {@code ItemAxe(material, damage, speed)} is the
     * <em>total</em>, not an increment on the material — unlike every other
     * tool class, whose {@code ItemTool} constructor adds the material's own
     * attack damage. Upstream's axe came out at {@code 3.0F + 5.0F = 8.0F}, so
     * that is the figure passed here; a vanilla diamond axe is 8.0F too. This
     * read 5.0F until 1.1.15.0, which made the KAMI axe weaker than diamond.
     */
    public ItemIchorAxe() {
        super(KamiMaterials.ICHOR, 8.0F, -3.0F);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.setHarvestLevel("axe", 4);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
