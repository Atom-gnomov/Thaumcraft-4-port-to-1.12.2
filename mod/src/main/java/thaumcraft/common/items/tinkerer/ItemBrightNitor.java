package thaumcraft.common.items.tinkerer;

import net.minecraft.item.Item;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Hyperenergetic Nitor — ported from Thaumic Tinkerer's {@code ItemBrightNitor}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Nitor pushed past its limit in a crucible. It does nothing on its own; it
 * is what the six imbued fires are made from.</p>
 */
public class ItemBrightNitor extends Item {

    public ItemBrightNitor() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }
}
