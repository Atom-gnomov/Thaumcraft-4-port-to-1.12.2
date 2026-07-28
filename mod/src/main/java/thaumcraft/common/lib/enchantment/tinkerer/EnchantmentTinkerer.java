package thaumcraft.common.lib.enchantment.tinkerer;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;

/**
 * Thaumic Tinkerer's own enchantments (pixlepix / nekosune, originally Vazkii),
 * ported to 1.12.2 from {@code ModEnchantments} and its fourteen
 * {@code Enchantment*} classes.
 *
 * <p>The original's {@code EnchantmentMod} base is reproduced here: none of
 * these appear at an enchanting table — they exist only through the Osmotic
 * Enchanter — and each carries its own maximum level. Every compatibility rule
 * below is the original's, rule for rule; the per-enchantment differences live
 * in {@link Kind} rather than in fourteen near-identical files.</p>
 */
public class EnchantmentTinkerer extends Enchantment {

    private static final EntityEquipmentSlot[] HAND = {
            EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND
    };
    private static final EntityEquipmentSlot[] FEET = { EntityEquipmentSlot.FEET };
    private static final EntityEquipmentSlot[] LEGS = { EntityEquipmentSlot.LEGS };

    /** One entry per enchantment the original registered, with its own rules. */
    public enum Kind {
        ASCENT_BOOST("ascentBoost", 4, EnumEnchantmentType.ARMOR_LEGS, LEGS),
        SLOW_FALL("slowFall", 3, EnumEnchantmentType.ARMOR_FEET, FEET),
        AUTO_SMELT("autoSmelt", 1, EnumEnchantmentType.DIGGER, HAND),
        DESINTEGRATE("desintegrate", 1, EnumEnchantmentType.DIGGER, HAND),
        QUICK_DRAW("quickDraw", 2, EnumEnchantmentType.BOW, HAND),
        VAMPIRISM("vampirism", 2, EnumEnchantmentType.WEAPON, HAND),
        DISPERSED_STRIKES("dispersedStrike", 5, EnumEnchantmentType.WEAPON, HAND),
        FINAL_STRIKE("finalStrike", 5, EnumEnchantmentType.WEAPON, HAND),
        FOCUSED_STRIKE("focusedStrike", 5, EnumEnchantmentType.WEAPON, HAND),
        POUNCE("pounce", 5, EnumEnchantmentType.ARMOR_LEGS, LEGS),
        SHATTER("shatter", 5, EnumEnchantmentType.DIGGER, HAND),
        SHOCKWAVE("shockwave", 5, EnumEnchantmentType.ARMOR_FEET, FEET),
        TUNNEL("tunnel", 5, EnumEnchantmentType.DIGGER, HAND),
        VALIANCE("valiance", 5, EnumEnchantmentType.WEAPON, HAND);

        final String name;
        final int maxLevel;
        final EnumEnchantmentType type;
        final EntityEquipmentSlot[] slots;

        Kind(String name, int maxLevel, EnumEnchantmentType type, EntityEquipmentSlot[] slots) {
            this.name = name;
            this.maxLevel = maxLevel;
            this.type = type;
            this.slots = slots;
        }
    }

    private final Kind kind;

    public EnchantmentTinkerer(Kind kind) {
        // Upstream passes weight 0 — never rolled randomly. The lowest
        // weight this version can express is VERY_RARE (1); the table is
        // closed off anyway by canApplyAtEnchantingTable below.
        super(Rarity.VERY_RARE, kind.type, kind.slots);
        this.kind = kind;
        this.setName(kind.name);
        this.setRegistryName("thaumcraft", kind.name.toLowerCase(java.util.Locale.ROOT));
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public int getMaxLevel() {
        return this.kind.maxLevel;
    }

    /** The original's EnchantmentMod: never offered at an enchanting table. */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    protected boolean canApplyTogether(Enchantment other) {
        switch (this.kind) {
            case SLOW_FALL:
                return other != Enchantments.FEATHER_FALLING;
            case AUTO_SMELT:
            case DESINTEGRATE:
                return other == Enchantments.UNBREAKING || other == Config.enchRepair;
            case QUICK_DRAW:
                return other != Enchantments.PUNCH;
            case VAMPIRISM:
                return other != Enchantments.FIRE_ASPECT && other != Enchantments.KNOCKBACK
                        || other == Enchantments.UNBREAKING || other == Config.enchRepair;
            case DISPERSED_STRIKES:
            case FOCUSED_STRIKE:
                return other == Enchantments.UNBREAKING || other == Config.enchRepair
                        || other == Enchantments.KNOCKBACK || other == Enchantments.LOOTING;
            case FINAL_STRIKE:
            case VALIANCE:
                return other == Enchantments.UNBREAKING || other == Config.enchRepair
                        || other == Enchantments.SHARPNESS || other == Enchantments.SMITE;
            case SHATTER:
                return other != Enchantments.EFFICIENCY
                        && other != ModEnchantmentsTinkerer.desintegrate;
            case TUNNEL:
                return other != ModEnchantmentsTinkerer.shatter
                        && other != Enchantments.EFFICIENCY;
            case SHOCKWAVE:
                return other != Enchantments.FEATHER_FALLING
                        && other != ModEnchantmentsTinkerer.slowFall
                        && super.canApplyTogether(other);
            case POUNCE:
            case ASCENT_BOOST:
            default:
                return super.canApplyTogether(other);
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        // The original refused Auto Smelt on the Elemental Axe, which fells
        // whole trees and would trivially combine with it.
        if (this.kind == Kind.AUTO_SMELT) {
            return super.canApply(stack) && stack.getItem() != ConfigItems.itemAxeElemental;
        }
        // Slow Fall was refused on TT's Gem Boots and Vampirism on its Blood
        // Sword; neither item exists here, so nothing further to exclude.
        return super.canApply(stack);
    }
}
