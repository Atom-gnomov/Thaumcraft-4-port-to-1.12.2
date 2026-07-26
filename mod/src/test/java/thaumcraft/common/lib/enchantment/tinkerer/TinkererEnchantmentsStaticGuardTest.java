package thaumcraft.common.lib.enchantment.tinkerer;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Pins Thaumic Tinkerer's own fourteen enchantments: that all of them exist and
 * are registered, that their levels and slots are the original's, that the
 * effect handler keeps the original's formulas, and that they are priced in the
 * Osmotic Enchanter and named in both languages.
 */
public class TinkererEnchantmentsStaticGuardTest {

    private static final String[] NAMES = {
            "ascentBoost", "slowFall", "autoSmelt", "desintegrate", "quickDraw", "vampirism",
            "dispersedStrike", "finalStrike", "focusedStrike", "pounce", "shatter", "shockwave",
            "tunnel", "valiance"
    };

    private static String field(String name) {
        return name.equals("dispersedStrike") ? "dispersedStrikes" : name;
    }

    @Test
    public void allFourteenExistAndAreRegistered() throws IOException {
        String mod = read("src/main/java/thaumcraft/common/lib/enchantment/tinkerer/ModEnchantmentsTinkerer.java");
        String main = read("src/main/java/thaumcraft/common/Thaumcraft.java");
        for (String name : NAMES) {
            assertTrue(field(name) + " must be declared",
                    mod.contains("public static Enchantment " + field(name) + ";"));
            assertTrue(field(name) + " must be built",
                    mod.contains(field(name) + " = new EnchantmentTinkerer("));
        }
        assertTrue("the fourteen must reach the registry",
                main.contains("ModEnchantmentsTinkerer.create()"));
        assertTrue("the effect handler must be on the event bus",
                main.contains("new thaumcraft.common.lib.enchantment.tinkerer.TinkererEnchantmentHandler()"));
    }

    /** The original's EnchantmentMod: table-invisible, own max levels, own slots. */
    @Test
    public void levelsAndTableRulesMatchTheOriginal() throws IOException {
        String src = read("src/main/java/thaumcraft/common/lib/enchantment/tinkerer/EnchantmentTinkerer.java");
        assertTrue("never offered at an enchanting table",
                src.contains("public boolean canApplyAtEnchantingTable(ItemStack stack)")
                        && src.contains("return false;"));
        assertTrue("ascent boost is level 4 on leggings",
                src.contains("ASCENT_BOOST(\"ascentBoost\", 4, EnumEnchantmentType.ARMOR_LEGS, LEGS)"));
        assertTrue("slow fall is level 3 on boots",
                src.contains("SLOW_FALL(\"slowFall\", 3, EnumEnchantmentType.ARMOR_FEET, FEET)"));
        assertTrue("auto smelt and desintegrate are single-level diggers",
                src.contains("AUTO_SMELT(\"autoSmelt\", 1, EnumEnchantmentType.DIGGER, HAND)")
                        && src.contains("DESINTEGRATE(\"desintegrate\", 1, EnumEnchantmentType.DIGGER, HAND)"));
        assertTrue("quick draw is level 2 on bows",
                src.contains("QUICK_DRAW(\"quickDraw\", 2, EnumEnchantmentType.BOW, HAND)"));
        assertTrue("vampirism is level 2 on weapons",
                src.contains("VAMPIRISM(\"vampirism\", 2, EnumEnchantmentType.WEAPON, HAND)"));
        assertTrue("auto smelt is still refused on the elemental axe",
                src.contains("stack.getItem() != ConfigItems.itemAxeElemental"));
        assertTrue("shatter and tunnel exclude each other and efficiency",
                src.contains("other != ModEnchantmentsTinkerer.desintegrate")
                        && src.contains("other != ModEnchantmentsTinkerer.shatter"));
    }

    /** Formula for formula, as in ModEnchantmentHandler. */
    @Test
    public void handlerKeepsTheOriginalFormulas() throws IOException {
        String src = read("src/main/java/thaumcraft/common/lib/enchantment/tinkerer/TinkererEnchantmentHandler.java");
        assertTrue("pounce adds a quarter per level while airborne",
                src.contains("1 + 0.25 * pounce"));
        assertTrue("final strike triples on a 1-in-(20-level) roll",
                src.contains("rand.nextInt(20 - finalStrike) == 0") && src.contains("event.getAmount() * 3"));
        assertTrue("valiance adds a tenth per level below half health",
                src.contains("attacker.getHealth() / attacker.getMaxHealth() < 0.5F")
                        && src.contains("1 + 0.1 * valiance"));
        assertTrue("focused and dispersed strikes track successive hits",
                src.contains("NBT_LAST_TARGET") && src.contains("NBT_SUCCESSIVE_STRIKE")
                        && src.contains("0.5 * successiveStrikes * amount * focusedStrikes"));
        assertTrue("vampirism heals its level and plays the zap",
                src.contains("attacker.heal(vampirism)") && src.contains("TCSounds.ZAP"));
        assertTrue("slow fall divides the descent and trims the fall distance",
                src.contains("1 + slowfall * 0.33F")
                        && src.contains("Math.max(2.9F, player.fallDistance - slowfall / 3.0F)"));
        assertTrue("ascent boost scales the jump", src.contains("(boost + 2) / 2.0D"));
        assertTrue("shockwave hits everything within ten blocks on a fall over three",
                src.contains("0.1F * shockwave * event.getDistance()"));
        assertTrue("shatter favours very hard blocks",
                src.contains("> 20.0F") && src.contains("3 * shatter"));
        assertTrue("tunnel rewards holding a heading",
                src.contains("NBT_TUNNEL_DIRECTION") && src.contains("dif < 50.0F")
                        && src.contains("1 + 0.2 * tunnel"));
        assertTrue("desintegrate and auto smelt break their own kind instantly and refuse the rest",
                src.contains("hardness <= 1.5F") && src.contains("Material.WOOD")
                        && src.contains("Float.MAX_VALUE") && src.contains("event.setCanceled(true)"));
    }

    @Test
    public void pricedAndNamedInBothLanguages() throws IOException {
        String costs = read("src/main/java/thaumcraft/common/lib/tinkerer/EnchantmentCosts.java");
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        assertTrue("ascent boost priced at the original's base",
                costs.contains("put(ModEnchantmentsTinkerer.ascentBoost, aspects(Aspect.ENTROPY, 8, Aspect.AIR, 10))"));
        assertTrue("auto smelt priced at the original's base",
                costs.contains("put(ModEnchantmentsTinkerer.autoSmelt, aspects(Aspect.ENTROPY, 20, Aspect.FIRE, 30))"));
        for (String name : NAMES) {
            assertTrue("cost for " + name, costs.contains("ModEnchantmentsTinkerer." + field(name)));
            assertTrue("en name for " + name, en.contains("enchantment." + name + "="));
            assertTrue("ru name for " + name, ru.contains("enchantment." + name + "="));
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
