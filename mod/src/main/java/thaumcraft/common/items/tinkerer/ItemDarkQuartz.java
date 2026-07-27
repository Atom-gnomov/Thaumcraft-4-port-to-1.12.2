package thaumcraft.common.items.tinkerer;

import net.minecraft.item.Item;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Smokey Quartz — ported from Thaumic Tinkerer's {@code ItemDarkQuartz}
 * (pixlepix / nekosune / Vazkii). The gem the smokey quartz blocks and both
 * talismans are made from; upstream it is a plain {@code ItemBase} with no
 * behaviour of its own, crafted eight at a time from quartz around a coal.
 */
public class ItemDarkQuartz extends Item {

    public ItemDarkQuartz() {
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }
}
