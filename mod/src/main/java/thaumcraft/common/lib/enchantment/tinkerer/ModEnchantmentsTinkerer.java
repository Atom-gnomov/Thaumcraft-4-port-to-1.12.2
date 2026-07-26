package thaumcraft.common.lib.enchantment.tinkerer;

import net.minecraft.enchantment.Enchantment;

/**
 * The fourteen enchantments Thaumic Tinkerer registered in
 * {@code ModEnchantments}, held here so the rest of the port (compatibility
 * rules, the Osmotic Enchanter's cost table, the effect handler) can refer to
 * them by name.
 */
public final class ModEnchantmentsTinkerer {

    public static Enchantment ascentBoost;
    public static Enchantment slowFall;
    public static Enchantment autoSmelt;
    public static Enchantment desintegrate;
    public static Enchantment quickDraw;
    public static Enchantment vampirism;
    public static Enchantment dispersedStrikes;
    public static Enchantment finalStrike;
    public static Enchantment focusedStrike;
    public static Enchantment pounce;
    public static Enchantment shatter;
    public static Enchantment shockwave;
    public static Enchantment tunnel;
    public static Enchantment valiance;

    private ModEnchantmentsTinkerer() {
    }

    /** Builds every instance; called before the registry event fires. */
    public static Enchantment[] create() {
        ascentBoost = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.ASCENT_BOOST);
        slowFall = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.SLOW_FALL);
        autoSmelt = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.AUTO_SMELT);
        desintegrate = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.DESINTEGRATE);
        quickDraw = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.QUICK_DRAW);
        vampirism = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.VAMPIRISM);
        dispersedStrikes = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.DISPERSED_STRIKES);
        finalStrike = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.FINAL_STRIKE);
        focusedStrike = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.FOCUSED_STRIKE);
        pounce = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.POUNCE);
        shatter = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.SHATTER);
        shockwave = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.SHOCKWAVE);
        tunnel = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.TUNNEL);
        valiance = new EnchantmentTinkerer(EnchantmentTinkerer.Kind.VALIANCE);

        return new Enchantment[]{
                ascentBoost, slowFall, autoSmelt, desintegrate, quickDraw, vampirism,
                dispersedStrikes, finalStrike, focusedStrike, pounce, shatter, shockwave,
                tunnel, valiance
        };
    }
}
