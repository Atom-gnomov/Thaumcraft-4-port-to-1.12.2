package thaumcraft.common.items.tinkerer.kami;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * KAMI resources — ported from Thaumic Tinkerer's {@code ItemKamiResource}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The eight subtypes and their order are the original's
 * {@code LibItemNames.KAMI_RESOURCE_NAMES}: ichor, ichorcloth, ichorium,
 * ichor nugget, ichor cap, ichorcloth rod, and the two dimensional shards that
 * drop from mobs in the Nether and the End.</p>
 *
 * <p>KAMI is Thaumic Tinkerer's endgame tier, so everything here is
 * {@link EnumRarity#EPIC}, as in the original.</p>
 */
public class ItemKamiResource extends Item {

    /** Subtype order is load-bearing: recipes and drops address these by meta. */
    public static final String[] NAMES = {
            "ichor", "ichorcloth", "ichorium", "ichor_nugget",
            "ichor_cap", "ichorcloth_rod", "nether_shard", "ender_shard"
    };

    public static final int ICHOR = 0;
    public static final int ICHORCLOTH = 1;
    public static final int ICHORIUM = 2;
    public static final int ICHOR_NUGGET = 3;
    public static final int ICHOR_CAP = 4;
    public static final int ICHORCLOTH_ROD = 5;
    public static final int NETHER_SHARD = 6;
    public static final int ENDER_SHARD = 7;

    public ItemKamiResource() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta < 0 || meta >= NAMES.length) {
            return super.getTranslationKey();
        }
        return super.getTranslationKey() + "." + NAMES[meta];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (int meta = 0; meta < NAMES.length; meta++) {
            items.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
