package thaumcraft.common.lib.enchantment.endgame;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * Ascension — take off from the ground and fly under thrust, no fireworks and
 * no cliff required; the owner's core request for the End Legacy module (new
 * content, no 1.7.10 original — see {@code END_LEGACY_PLAN.md}).
 *
 * <p>A superset of {@link EnchantmentSoaring}: gliding is included, and on top
 * of it a jump held on the ground launches, and a jump held in the air burns
 * Aer vis from any wand in the inventory for thrust. Numbers live in
 * {@code SoaringPhysics}; the research gate ({@code ASCENSION}) hangs off the
 * dragon — scanning what the Ender Dragon leaves behind is what unlocks it,
 * which is the tie-in the owner asked for.</p>
 */
public class EnchantmentAscension extends Enchantment {

    public EnchantmentAscension() {
        super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST,
                new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
        this.setName("ascension");
        this.setRegistryName("thaumcraft", "ascension");
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
