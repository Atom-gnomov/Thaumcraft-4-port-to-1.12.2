package thaumcraft.common.lib.tinkerer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.enchantment.tinkerer.ModEnchantmentsTinkerer;

/**
 * Vis costs for enchanting through the Osmotic Enchanter — ported from Thaumic
 * Tinkerer's {@code EnchantmentManager} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The per-enchantment tables below are the original's hand-tuned level-1
 * costs, and {@link #costFor} reproduces its exponential curve exactly:
 * {@code level1 × level × (1 + level × 0.2)} — so a level-1 enchant costs 1.2×
 * the base, level 2 costs 2.8×, level 3 4.8×, level 4 7.2× and level 5 10×.</p>
 *
 * <p>Thaumic Tinkerer's own fourteen enchantments are included as of 1.0.55,
 * at the original's base costs, alongside the vanilla and Thaumcraft ones.</p>
 */
public final class EnchantmentCosts {

    private static final Map<Enchantment, AspectList> BASE_COSTS = new HashMap<>();
    private static boolean initialised;

    private EnchantmentCosts() {
    }

    /** Built lazily: the Thaumcraft enchantments do not exist until preInit runs. */
    private static synchronized void init() {
        if (initialised) {
            return;
        }
        initialised = true;

        // --- Thaumic Tinkerer's own fourteen, at the original's base costs ---
        put(ModEnchantmentsTinkerer.ascentBoost, aspects(Aspect.ENTROPY, 8, Aspect.AIR, 10));
        put(ModEnchantmentsTinkerer.slowFall, aspects(Aspect.ORDER, 8, Aspect.AIR, 10));
        put(ModEnchantmentsTinkerer.autoSmelt, aspects(Aspect.ENTROPY, 20, Aspect.FIRE, 30));
        put(ModEnchantmentsTinkerer.desintegrate,
                new AspectList().add(Aspect.ENTROPY, 25).add(Aspect.AIR, 10).add(Aspect.EARTH, 10));
        put(ModEnchantmentsTinkerer.quickDraw,
                new AspectList().add(Aspect.ORDER, 10).add(Aspect.AIR, 10).add(Aspect.WATER, 5));
        put(ModEnchantmentsTinkerer.vampirism,
                new AspectList().add(Aspect.ENTROPY, 8).add(Aspect.FIRE, 10).add(Aspect.WATER, 10));
        put(ModEnchantmentsTinkerer.focusedStrike,
                new AspectList().add(Aspect.ORDER, 12).add(Aspect.AIR, 10).add(Aspect.WATER, 10));
        put(ModEnchantmentsTinkerer.dispersedStrikes,
                new AspectList().add(Aspect.ENTROPY, 12).add(Aspect.FIRE, 10).add(Aspect.EARTH, 10));
        put(ModEnchantmentsTinkerer.valiance,
                new AspectList().add(Aspect.ORDER, 12).add(Aspect.FIRE, 10).add(Aspect.EARTH, 10));
        put(ModEnchantmentsTinkerer.finalStrike, aspects(Aspect.ENTROPY, 16, Aspect.FIRE, 16));
        put(ModEnchantmentsTinkerer.tunnel, aspects(Aspect.EARTH, 16, Aspect.ORDER, 16));
        put(ModEnchantmentsTinkerer.shatter, aspects(Aspect.EARTH, 16, Aspect.ENTROPY, 16));
        put(ModEnchantmentsTinkerer.shockwave, aspects(Aspect.EARTH, 16, Aspect.AIR, 16));
        put(ModEnchantmentsTinkerer.pounce, aspects(Aspect.EARTH, 16, Aspect.AIR, 16));

        // --- armour ---
        put(Enchantments.PROTECTION, aspects(Aspect.EARTH, 10, Aspect.ENTROPY, 7));
        put(Enchantments.FIRE_PROTECTION, aspects(Aspect.FIRE, 10, Aspect.ENTROPY, 3, Aspect.WATER, 4));
        put(Enchantments.FEATHER_FALLING, aspects(Aspect.AIR, 16, Aspect.ORDER, 5));
        put(Enchantments.BLAST_PROTECTION, aspects(Aspect.EARTH, 5, Aspect.FIRE, 5, Aspect.ENTROPY, 8));
        put(Enchantments.PROJECTILE_PROTECTION, aspects(Aspect.AIR, 10, Aspect.ENTROPY, 7));
        put(Enchantments.RESPIRATION, aspects(Aspect.WATER, 10, Aspect.AIR, 8, Aspect.ORDER, 5));
        put(Enchantments.AQUA_AFFINITY, aspects(Aspect.WATER, 25, Aspect.ORDER, 20, Aspect.EARTH, 5));
        put(Enchantments.THORNS, aspects(Aspect.EARTH, 10, Aspect.ENTROPY, 12));

        // --- weapons ---
        put(Enchantments.SHARPNESS, aspects(Aspect.ORDER, 10));
        put(Enchantments.SMITE, aspects(Aspect.ORDER, 5, Aspect.AIR, 5));
        put(Enchantments.BANE_OF_ARTHROPODS, aspects(Aspect.ORDER, 5, Aspect.FIRE, 5));
        put(Enchantments.KNOCKBACK, aspects(Aspect.ENTROPY, 5, Aspect.AIR, 10));
        put(Enchantments.FIRE_ASPECT, aspects(Aspect.FIRE, 15, Aspect.EARTH, 4));
        put(Enchantments.LOOTING, allSix());

        // --- tools ---
        put(Enchantments.EFFICIENCY, aspects(Aspect.ENTROPY, 12, Aspect.EARTH, 4));
        put(Enchantments.SILK_TOUCH, aspects(Aspect.ORDER, 50, Aspect.EARTH, 10, Aspect.ENTROPY, 10));
        put(Enchantments.UNBREAKING, aspects(Aspect.ORDER, 15, Aspect.WATER, 8, Aspect.EARTH, 8));
        put(Enchantments.FORTUNE, allSix());

        // --- bow ---
        put(Enchantments.POWER, aspects(Aspect.EARTH, 5, Aspect.ORDER, 10));
        put(Enchantments.PUNCH, aspects(Aspect.AIR, 4, Aspect.EARTH, 10, Aspect.ENTROPY, 5));
        put(Enchantments.FLAME, aspects(Aspect.ENTROPY, 5, Aspect.FIRE, 20, Aspect.EARTH, 5));
        put(Enchantments.INFINITY, aspects(Aspect.ENTROPY, 40, Aspect.ORDER, 40, Aspect.EARTH, 10));

        // --- fishing ---
        put(Enchantments.LURE, aspects(Aspect.WATER, 20, Aspect.BEAST, 20));
        put(Enchantments.LUCK_OF_THE_SEA, aspects(Aspect.ENTROPY, 20, Aspect.WATER, 20));

        // --- Thaumcraft's own ---
        put(Config.enchPotency, aspects(Aspect.ORDER, 15));
        put(Config.enchFrugal, aspects(Aspect.WATER, 10, Aspect.EARTH, 10, Aspect.ENTROPY, 10));
        put(Config.enchWandFortune, allSix());
        put(Config.enchHaste, aspects(Aspect.AIR, 10, Aspect.ENTROPY, 5, Aspect.EARTH, 5));
        put(Config.enchRepair, aspects(Aspect.WATER, 20, Aspect.FIRE, 20, Aspect.EARTH, 20,
                Aspect.AIR, 20, Aspect.ORDER, 20, Aspect.ENTROPY, 5));
    }

    /** Looting/Fortune/Treasure all shared this "one of everything" cost. */
    private static AspectList allSix() {
        return aspects(Aspect.AIR, 10, Aspect.FIRE, 10, Aspect.WATER, 10,
                Aspect.EARTH, 10, Aspect.ORDER, 15, Aspect.ENTROPY, 15);
    }

    private static void put(Enchantment enchantment, AspectList cost) {
        if (enchantment != null) {
            BASE_COSTS.put(enchantment, cost);
        }
    }

    private static AspectList aspects(Object... pairs) {
        AspectList list = new AspectList();
        for (int i = 0; i < pairs.length; i += 2) {
            list.add((Aspect) pairs[i], (Integer) pairs[i + 1]);
        }
        return list;
    }

    /** Whether the enchanter knows how to price this enchantment at all. */
    public static boolean isSupported(Enchantment enchantment) {
        init();
        return BASE_COSTS.containsKey(enchantment);
    }

    /**
     * Vis cost of applying {@code enchantment} at {@code level}, or {@code null}
     * when the enchantment is not priced.
     */
    public static AspectList costFor(Enchantment enchantment, int level) {
        init();
        AspectList base = BASE_COSTS.get(enchantment);
        if (base == null || level <= 0) {
            return null;
        }
        double factor = level * (1.0D + level * 0.2D);
        AspectList out = new AspectList();
        for (Aspect aspect : base.getAspectsSorted()) {
            out.add(aspect, Math.max(1, (int) Math.round(base.getAmount(aspect) * factor)));
        }
        return out;
    }
}
