package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Ichor sword — ported from Thaumic Tinkerer's {@code ItemIchorSword}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out.</p>
 */
public class ItemIchorSword extends ItemSword {

    public ItemIchorSword() {
        super(KamiMaterials.ICHOR);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
