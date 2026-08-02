package thaumcraft.common.lib.enchantment.endgame;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Soaring — glide on any chestplate. Part of the End Legacy module: this is
 * <b>new content with no 1.7.10 original</b>, added by the owner's decision of
 * 2026-08-02 (see {@code END_LEGACY_PLAN.md}; the 1:1 rule does not apply
 * here, so audits must not "fix" it against an original that does not exist).
 *
 * <p>An infusion enchantment in the mould of {@code enchRepair}: one
 * enchantment that lands on <em>any</em> chest armour, which is exactly why
 * the owner chose enchantment-into-chestplate over a wearable item — no
 * per-chestplate variants, and the player keeps their armour.</p>
 *
 * <p>The physics lives in {@code SoaringHandler}; this class only declares the
 * enchantment. Obtainable through the infusion altar alone — not the
 * enchanting table, not loot, not books.</p>
 */
public class EnchantmentSoaring extends Enchantment {

    public EnchantmentSoaring() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST,
                new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
        this.setName("soaring");
        this.setRegistryName("thaumcraft", "soaring");
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    /** Unreachable numbers: the enchanting table must never roll this. */
    @Override
    public int getMinEnchantability(int level) {
        return 1000;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 2000;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }
}
