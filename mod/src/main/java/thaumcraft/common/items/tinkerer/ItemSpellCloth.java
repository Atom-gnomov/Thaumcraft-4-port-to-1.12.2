package thaumcraft.common.items.tinkerer;

import java.awt.Color;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Spellbinding Cloth — ported from Thaumic Tinkerer's {@code ItemSpellCloth}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Put it in a crafting grid beside an enchanted item and the item comes back
 * stripped of its enchantments — see {@link SpellClothRecipe}. The cloth stays
 * in the grid and wears by one each time, for thirty-five uses.</p>
 */
public class ItemSpellCloth extends Item {

    /** The original's {@code LibFeatures.SPELL_CLOTH_USES}. */
    public static final int USES = 35;

    public ItemSpellCloth() {
        this.setMaxDamage(USES);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    /** Wears by one and stays put — the original returns the same stack. */
    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack worn = stack.copy();
        worn.setItemDamage(worn.getItemDamage() + 1);
        return worn;
    }

    // Upstream also overrode doesContainerItemLeaveCraftingGrid() to keep the
    // cloth in the grid. That hook is gone in 1.12.2 — a recipe decides for
    // itself now, and SpellClothRecipe.getRemainingItems does exactly that.

    /**
     * Fades from saturated to white as it wears: the original's
     * {@code Color.HSBtoRGB(0.75F, remaining / max * 0.5F, 1F)}.
     */
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        float remaining = stack.getMaxDamage() - stack.getItemDamage();
        return Color.HSBtoRGB(0.75F, remaining / stack.getMaxDamage() * 0.5F, 1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
