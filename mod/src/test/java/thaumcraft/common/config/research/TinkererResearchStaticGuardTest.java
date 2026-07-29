package thaumcraft.common.config.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Pins Thaumic Tinkerer's own Thaumonomicon tab and the seven foci entries that
 * hang off it, transcribed from each focus item's {@code getResearchItem()} in
 * the original.
 *
 * <p>The branch is what makes the module playable: an arcane or infusion recipe
 * checks its research key before it will match, so an entry that does not exist
 * is a recipe that never completes. The last test here is therefore the load
 * bearing one — it pins that every gate key the recipes name is a key this file
 * actually registers.</p>
 */
public class TinkererResearchStaticGuardTest {

    private static final String[] KEYS = {
            "FOCUS_SMELT", "FOCUS_FLIGHT", "FOCUS_DEFLECT", "FOCUS_TELEKINESIS",
            "FOCUS_DISLOCATION", "FOCUS_HEAL", "FOCUS_ENDER_CHEST"
    };

    /** The original's LibResearch.CATEGORY_THAUMICTINKERER, icon and background. */
    @Test
    public void ownTabIsRegisteredUnderTheOriginalsKey() throws IOException {
        String src = source();
        String main = read("src/main/java/thaumcraft/common/config/research/ConfigResearch.java");
        assertTrue("the tab keeps the original's category key",
                src.contains("public static final String CATEGORY = \"TT_CATEGORY\";"));
        assertTrue("the tab keeps the original's icon and background",
                src.contains("new ResourceLocation(\"thaumcraft\", \"textures/misc/r_enchanting.png\"),")
                        && src.contains("new ResourceLocation(\"thaumcraft\", \"textures/gui/gui_researchback.png\")"));
        assertTrue("the tab must be registered with the other categories",
                main.contains("ConfigResearchTinkerer.initCategory();"));
        assertTrue("the entries must be registered with the other research",
                main.contains("ConfigResearchTinkerer.initResearch();"));
        assertTrue("the tab icon must ship",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/misc/r_enchanting.png")));
    }

    /**
     * The original's TTResearchItem kept the module's strings under its own
     * keys — and <em>translated</em> them. Returning the raw key here puts
     * "ttresearch.name.FOCUS_SMELT" on the page instead of its name.
     */
    @Test
    public void entriesReadAndTranslateTheModulesOwnStrings() throws IOException {
        String src = read("src/main/java/thaumcraft/common/config/research/TinkererResearchItem.java");
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        assertTrue("names come from ttresearch.name, translated",
                src.contains("return I18n.translateToLocal(\"ttresearch.name.\" + this.key);"));
        assertTrue("hover lore is the [TT] prefix plus ttresearch.lore, translated",
                src.contains("return I18n.translateToLocal(getPrefix())"
                        + " + I18n.translateToLocal(\"ttresearch.lore.\" + this.key);"));
        assertTrue("the prefix key is the original's", src.contains("return \"ttresearch.prefix\";"));
        assertTrue("and it is defined in both languages, with its trailing space",
                en.contains("ttresearch.prefix=[TT] \n") && ru.contains("ttresearch.prefix=[TT] \n"));
        assertTrue("only text pages get the key prefix",
                normalize(src).contains("if (page.type == PageType.TEXT)"
                        + " { page.text = \"ttresearch.page.\" + this.key + \".\" + page.text; }"));
    }

    /**
     * The half of {@code setPages} that is easiest to drop: an entry showing an
     * infusion recipe gains INFUSION as a hidden parent. Without it the
     * module's infused items offer themselves before the player has an altar.
     */
    @Test
    public void infusionPagesAddTheHiddenInfusionParent() throws IOException {
        String src = normalize(read("src/main/java/thaumcraft/common/config/research/TinkererResearchItem.java"));
        assertTrue("the check is on the page type",
                src.contains("if (checkInfusion() && page.type == PageType.INFUSION_CRAFTING) {"));
        assertTrue("first hidden parent when there were none",
                src.contains("this.parentsHidden = new String[]{\"INFUSION\"};"));
        assertTrue("otherwise INFUSION goes in front of the existing ones",
                src.contains("newParents[0] = \"INFUSION\";")
                        && src.contains("newParents[i + 1] = this.parentsHidden[i];"));
        assertTrue("subclasses can opt out, as the original's KAMI entries do",
                src.contains("boolean checkInfusion() { return true; }"));
    }

    /**
     * Aspects, map position, complexity, parents and pages, one entry at a time,
     * exactly as the original built them.
     */
    @Test
    public void sevenFociMatchTheOriginal() throws IOException {
        String src = normalize(source());
        assertTrue("smelting focus hangs off Thaumcraft's own excavation focus", src.contains(
                "new TinkererResearchItem(\"FOCUS_SMELT\", new AspectList()"
                        + ".add(Aspect.FIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.MAGIC, 1),"
                        + " -2, -2, 2, new ItemStack(ConfigItems.focusSmelt))"
                        + ".setParents(\"FOCUSEXCAVATION\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"FocusSmelt\"))"));
        assertTrue("uprising", src.contains(
                "new TinkererResearchItem(\"FOCUS_FLIGHT\", new AspectList()"
                        + ".add(Aspect.MOTION, 1).add(Aspect.MAGIC, 1).add(Aspect.AIR, 2),"
                        + " -3, -4, 2, new ItemStack(ConfigItems.focusFlight))"
                        + ".setParents(\"FOCUS_SMELT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"FocusFlight\"))"));
        assertTrue("distortion", src.contains(
                "new TinkererResearchItem(\"FOCUS_DEFLECT\", new AspectList()"
                        + ".add(Aspect.MOTION, 2).add(Aspect.AIR, 1).add(Aspect.ORDER, 1).add(Aspect.DEATH, 1),"
                        + " -4, -3, 3, new ItemStack(ConfigItems.focusDeflect))"
                        + ".setConcealed().setParents(\"FOCUS_SMELT\")"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"FocusDeflect\")).setSecondary()"));
        assertTrue("telekinesis", src.contains(
                "new TinkererResearchItem(\"FOCUS_TELEKINESIS\", new AspectList()"
                        + ".add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.MOTION, 1),"
                        + " -4, -6, 2, new ItemStack(ConfigItems.focusTelekinesis))"
                        + ".setParents(\"FOCUS_FLIGHT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"FocusTelekinesis\")).setSecondary()"));
        assertTrue("dislocation keeps its second text page", src.contains(
                "new TinkererResearchItem(\"FOCUS_DISLOCATION\", new AspectList()"
                        + ".add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1),"
                        + " -5, -5, 2, new ItemStack(ConfigItems.focusDislocation))"
                        + ".setSecondary().setParents(\"FOCUS_FLIGHT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), new ResearchPage(\"1\"),"
                        + " infusionPage(\"FocusDislocation\"))"));
        assertTrue("mending", src.contains(
                "new TinkererResearchItem(\"FOCUS_HEAL\", new AspectList()"
                        + ".add(Aspect.HEAL, 2).add(Aspect.SOUL, 1).add(Aspect.MAGIC, 1),"
                        + " -6, -4, 2, new ItemStack(ConfigItems.focusHeal))"
                        + ".setParents(\"FOCUS_DEFLECT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"FocusHeal\")).setSecondary()"));
        assertTrue("ender rift", src.contains(
                "new TinkererResearchItem(\"FOCUS_ENDER_CHEST\", new AspectList()"
                        + ".add(Aspect.ELDRITCH, 2).add(Aspect.VOID, 1).add(Aspect.MAGIC, 1),"
                        + " -6, -2, 2, new ItemStack(ConfigItems.focusEnderChest))"
                        + ".setParents(\"FOCUS_DEFLECT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"FocusEnderChest\"))"));
    }

    /**
     * The dark quartz trunk and its branch, from each object's
     * {@code getResearchItem()}. Dark quartz itself is the way into the tab:
     * a stub, auto-unlocked and round, with no aspects and no complexity.
     */
    @Test
    public void darkQuartzBranchMatchesTheOriginal() throws IOException {
        String src = normalize(source());
        assertTrue("dark quartz is the auto-unlocked stub with six bench pages", src.contains(
                "new TinkererResearchItem(\"DARK_QUARTZ\", new AspectList(),"
                        + " -2, 2, 0, new ItemStack(ConfigItems.itemDarkQuartz))"
                        + ".setStub().setAutoUnlock().setRound()"
                        + ".setPages(new ResearchPage(\"0\"), benchRecipePage(\"DARK_QUARTZ0\"),"
                        + " benchRecipePage(\"DARK_QUARTZ1\"), benchRecipePage(\"DARK_QUARTZ2\"),"
                        + " benchRecipePage(\"DARK_QUARTZ3\"), benchRecipePage(\"DARK_QUARTZ4\"),"
                        + " benchRecipePage(\"DARK_QUARTZ5\"))"));
        assertTrue("transvector interface, two recipes across three text pages", src.contains(
                "new TinkererResearchItem(\"INTERFACE\", new AspectList()"
                        + ".add(Aspect.ENTROPY, 4).add(Aspect.ORDER, 4),"
                        + " -4, 2, 1, new ItemStack(ConfigBlocks.blockTransvectorInterface))"
                        + ".setParents(\"DARK_QUARTZ\")"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"TransvectorInterface\"),"
                        + " new ResearchPage(\"1\"), arcaneRecipePage(\"TransvectorConnector\"),"
                        + " new ResearchPage(\"2\"))"));
        assertTrue("magnets carry the soul mould's crucible page", src.contains(
                "new TinkererResearchItem(\"MAGNETS\", new AspectList()"
                        + ".add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1),"
                        + " -6, 3, 3, new ItemStack(ConfigBlocks.blockMagnet))"
                        + ".setParents(\"INTERFACE\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), new ResearchPage(\"1\"),"
                        + " arcaneRecipePage(\"Magnet\"), arcaneRecipePage(\"MobMagnet\"),"
                        + " crucibleRecipePage(\"SoulMould\"))"));
        assertTrue("the dislocator keeps Thaumcraft's MIRROR as a hidden parent", src.contains(
                "new TinkererResearchItem(\"DISLOCATOR\", new AspectList()"
                        + ".add(Aspect.TRAVEL, 2).add(Aspect.MECHANISM, 1).add(Aspect.ELDRITCH, 1),"
                        + " -6, 1, 3, new ItemStack(ConfigBlocks.blockTransvectorDislocator))"
                        + ".setConcealed().setParents(\"INTERFACE\").setParentsHidden(\"MIRROR\")"
                        + ".setPages(new ResearchPage(\"0\"),"
                        + " arcaneRecipePage(\"TransvectorDislocator\")).setSecondary()"));
        assertTrue("animation tablet", src.contains(
                "new TinkererResearchItem(\"ANIMATION_TABLET\", new AspectList()"
                        + ".add(Aspect.MECHANISM, 2).add(Aspect.METAL, 1)"
                        + ".add(Aspect.MOTION, 1).add(Aspect.ENERGY, 1),"
                        + " -8, 2, 4, new ItemStack(ConfigBlocks.blockAnimationTablet))"
                        + ".setParents(\"MAGNETS\")"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"AnimationTablet\"))"));
        assertTrue("the levitator's key is LEVITATOR, not MOBILIZER", src.contains(
                "new TinkererResearchItem(\"LEVITATOR\", new AspectList()"
                        + ".add(Aspect.MOTION, 2).add(Aspect.ORDER, 2),"
                        + " -7, 5, 3, new ItemStack(ConfigBlocks.blockMobilizer))"
                        + ".setParents(\"MAGNETS\")"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"Mobilizer\"),"
                        + " arcaneRecipePage(\"MobilizerRelay\")).setSecondary()"));
        assertTrue("cleansing talisman", src.contains(
                "new TinkererResearchItem(\"CLEANSING_TALISMAN\", new AspectList()"
                        + ".add(Aspect.HEAL, 2).add(Aspect.ORDER, 1).add(Aspect.POISON, 1),"
                        + " -3, 4, 3, new ItemStack(ConfigItems.itemCleansingTalisman))"
                        + ".setSecondary().setParents(\"DARK_QUARTZ\")"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"CleansingTalisman\"))"));
        assertTrue("blood sword, with its second text page after the recipe", src.contains(
                "new TinkererResearchItem(\"BLOOD_SWORD\", new AspectList()"
                        + ".add(Aspect.HUNGER, 2).add(Aspect.WEAPON, 1)"
                        + ".add(Aspect.FLESH, 1).add(Aspect.SOUL, 1),"
                        + " -4, 6, 3, new ItemStack(ConfigItems.itemBloodSword))"
                        + ".setParents(\"CLEANSING_TALISMAN\")"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"BloodSword\"),"
                        + " new ResearchPage(\"1\")).setSecondary()"));
        assertTrue("summoning, arcane then bench then infusion", src.contains(
                "new TinkererResearchItem(\"SUMMON\", new AspectList()"
                        + ".add(Aspect.WEAPON, 1).add(Aspect.BEAST, 3).add(Aspect.MAGIC, 3),"
                        + " -5, 8, 3, new ItemStack(ConfigBlocks.blockSummon))"
                        + ".setParents(\"BLOOD_SWORD\")"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"SUMMON0\"),"
                        + " benchRecipePage(\"SUMMON1\"), infusionPage(\"SUMMON\"),"
                        + " new ResearchPage(\"1\"))"));
        assertTrue("aether platform", src.contains(
                "new TinkererResearchItem(\"PLATFORM\", new AspectList()"
                        + ".add(Aspect.SENSES, 2).add(Aspect.TREE, 1).add(Aspect.MOTION, 1),"
                        + " -2, 6, 3, new ItemStack(ConfigBlocks.blockPlatform))"
                        + ".setConcealed().setParents(\"CLEANSING_TALISMAN\")"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"Platform\")).setSecondary()"));
    }

    /** The gases, the hyperenergetic nitor, the six fires and what follows them. */
    @Test
    public void gaseousLightBranchMatchesTheOriginal() throws IOException {
        String src = normalize(source());
        assertTrue("gaseous light hangs off Thaumcraft's own nitor", src.contains(
                "new TinkererResearchItem(\"GASEOUS_LIGHT\", new AspectList()"
                        + ".add(Aspect.LIGHT, 2).add(Aspect.AIR, 1),"
                        + " 0, -3, 1, new ItemStack(ConfigItems.itemGaseousLight))"
                        + ".setParents(\"NITOR\")"
                        + ".setPages(new ResearchPage(\"0\"), crucibleRecipePage(\"GaseousLight\"))"));
        assertTrue("gaseous shadow", src.contains(
                "new TinkererResearchItem(\"GASEOUS_SHADOW\", new AspectList()"
                        + ".add(Aspect.DARKNESS, 2).add(Aspect.AIR, 1).add(Aspect.MOTION, 4),"
                        + " -1, -5, 2, new ItemStack(ConfigItems.itemGaseousShadow))"
                        + ".setSecondary().setParents(\"GASEOUS_LIGHT\")"
                        + ".setPages(new ResearchPage(\"0\"), crucibleRecipePage(\"GaseousShadow\"))"));
        assertTrue("hyperenergetic nitor", src.contains(
                "new TinkererResearchItem(\"BRIGHT_NITOR\", new AspectList()"
                        + ".add(Aspect.LIGHT, 2).add(Aspect.FIRE, 1)"
                        + ".add(Aspect.ENERGY, 1).add(Aspect.AIR, 1),"
                        + " 1, -5, 2, new ItemStack(ConfigItems.itemBrightNitor))"
                        + ".setParents(\"GASEOUS_LIGHT\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), crucibleRecipePage(\"BrightNitor\")).setSecondary()"));
        assertTrue("every fire hangs off the nitor, concealed and secondary", src.contains(
                "new TinkererResearchItem(key, tags, col, row, complexity, new ItemStack(block))"
                        + ".setParents(\"BRIGHT_NITOR\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), crucibleRecipePage(key)).setSecondary()"));
        // The six spots and tags, one line each — chaos alone is complexity 3.
        assertTrue("air fire", src.contains("imbuedFire(\"FIRE_AER\", new AspectList()"
                + ".add(Aspect.FIRE, 5).add(Aspect.AIR, 5), 3, -7, 2, ConfigBlocks.blockFireAir)"));
        assertTrue("chaos fire is the deeper one", src.contains(
                "imbuedFire(\"FIRE_PERDITIO\", new AspectList()"
                        + ".add(Aspect.FIRE, 5).add(Aspect.ENTROPY, 5), 2, -8, 3, ConfigBlocks.blockFireChaos)"));
        assertTrue("earth fire", src.contains("imbuedFire(\"FIRE_TERRA\", new AspectList()"
                + ".add(Aspect.FIRE, 5).add(Aspect.EARTH, 5), 4, -6, 2, ConfigBlocks.blockFireEarth)"));
        assertTrue("ignis carries no second primal", src.contains(
                "imbuedFire(\"FIRE_IGNIS\", new AspectList()"
                        + ".add(Aspect.FIRE, 10), 4, -4, 2, ConfigBlocks.blockFireIgnis)"));
        assertTrue("order fire", src.contains("imbuedFire(\"FIRE_ORDO\", new AspectList()"
                + ".add(Aspect.FIRE, 5).add(Aspect.ORDER, 5), 3, -3, 2, ConfigBlocks.blockFireOrder)"));
        assertTrue("water fire", src.contains("imbuedFire(\"FIRE_AQUA\", new AspectList()"
                + ".add(Aspect.FIRE, 5).add(Aspect.WATER, 5), 2, -2, 2, ConfigBlocks.blockFireWater)"));
        assertTrue("infused potions open only behind all six fires", src.contains(
                "new TinkererResearchItem(\"INFUSED_POTIONS\", new AspectList()"
                        + ".add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5),"
                        + " 7, -5, 2, new ItemStack(ConfigItems.itemInfusedPotion))"
                        + ".setParents(\"FIRE_PERDITIO\", \"FIRE_ORDO\", \"FIRE_IGNIS\","
                        + " \"FIRE_TERRA\", \"FIRE_AER\", \"FIRE_AQUA\")"
                        + ".setParentsHidden(\"INFUSION\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), new ResearchPage(\"1\"),"
                        + " infusionPages(\"INFUSED_POTIONS\", 4),"));
        assertTrue("the funnel keeps its hidden distillation parent", src.contains(
                "new TinkererResearchItem(\"FUNNEL\", new AspectList()"
                        + ".add(Aspect.TOOL, 1).add(Aspect.TRAVEL, 2),"
                        + " 0, -7, 1, new ItemStack(ConfigBlocks.blockFunnel))"
                        + ".setParentsHidden(\"DISTILESSENTIA\").setParents(\"BRIGHT_NITOR\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"Funnel\")).setSecondary()"));
        assertTrue("the restorer keeps both of its hidden parents", src.contains(
                "new TinkererResearchItem(\"REPAIRER\", new AspectList()"
                        + ".add(Aspect.TOOL, 2).add(Aspect.CRAFT, 1)"
                        + ".add(Aspect.ORDER, 1).add(Aspect.MAGIC, 1),"
                        + " -1, -9, 3, new ItemStack(ConfigBlocks.blockRepairer))"
                        + ".setConcealed().setParents(\"FUNNEL\").setParentsHidden(\"THAUMIUM\", \"ENCHFABRIC\")"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"Repairer\"))"));
    }

    /** The spell cloth, the enchanter, and the two that hang off Thaumcraft. */
    @Test
    public void spellClothBranchMatchesTheOriginal() throws IOException {
        String src = normalize(source());
        assertTrue("the cloth is a root, hidden behind enchanted fabric", src.contains(
                "new TinkererResearchItem(\"SPELL_CLOTH\", new AspectList()"
                        + ".add(Aspect.MAGIC, 2).add(Aspect.CLOTH, 1),"
                        + " 3, 2, 2, new ItemStack(ConfigItems.itemSpellCloth))"
                        + ".setParentsHidden(\"ENCHFABRIC\")"
                        + ".setPages(new ResearchPage(\"0\"), crucibleRecipePage(\"SpellCloth\"))"));
        assertTrue("the enchanter carries three text pages before its recipe", src.contains(
                "new TinkererResearchItem(\"ENCHANTER\", new AspectList()"
                        + ".add(Aspect.MAGIC, 2).add(Aspect.AURA, 1)"
                        + ".add(Aspect.ELDRITCH, 1).add(Aspect.DARKNESS, 1).add(Aspect.MIND, 1),"
                        + " 5, 4, 5, new ItemStack(ConfigBlocks.blockEnchanter))"
                        + ".setParents(\"SPELL_CLOTH\")"
                        + ".setPages(new ResearchPage(\"0\"), new ResearchPage(\"1\"),"
                        + " new ResearchPage(\"2\"), infusionPage(\"Enchanter\"))"));
        assertTrue("the xp talisman's icon is meta 1, and it needs a brain in a jar", src.contains(
                "new TinkererResearchItem(\"XP_TALISMAN\", new AspectList()"
                        + ".add(Aspect.GREED, 1).add(Aspect.MAGIC, 1).add(Aspect.MAN, 1),"
                        + " 4, -1, 2, new ItemStack(ConfigItems.itemXpTalisman, 1, 1))"
                        + ".setParents(\"JARBRAIN\", \"SPELL_CLOTH\").setConcealed()"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"XpTalisman\")).setSecondary()"));
        assertTrue("the gas remover is round and free of complexity", src.contains(
                "new TinkererResearchItem(\"GAS_REMOVER\", new AspectList()"
                        + ".add(Aspect.DARKNESS, 2).add(Aspect.LIGHT, 2),"
                        + " -2, -7, 0, new ItemStack(ConfigItems.itemGasRemover)).setRound()"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"GasRemover\"))"
                        + ".setParents(\"GASEOUS_SHADOW\")"));
        assertTrue("the revealing helm sits on Thaumcraft's goggles", src.contains(
                "new TinkererResearchItem(\"REVEALING_HELM\", new AspectList()"
                        + ".add(Aspect.AURA, 2).add(Aspect.ARMOR, 1),"
                        + " 0, 0, 1, new ItemStack(ConfigItems.itemRevealingHelm))"
                        + ".setParents(\"GOGGLES\").setParentsHidden(\"THAUMIUM\")"
                        + ".setPages(new ResearchPage(\"0\"), arcaneRecipePage(\"RevealingHelm\"))"));
    }

    /**
     * The fourteen enchantment entries. Their keys are TTENCH_*, and three of
     * them are shortened — TTENCH_FOCUSED, TTENCH_DISPERSED, TTENCH_FINAL —
     * where the constant names say STRIKE. Guessing from the constant name
     * gives an entry whose strings never resolve.
     */
    @Test
    public void enchantmentEntriesUseTheOriginalsKeys() throws IOException {
        String src = normalize(source());
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        assertTrue("all fourteen are built the same way", src.contains(
                "new TinkererResearchItem(key, tags, col, row, 2,"
                        + " new ResourceLocation(\"thaumcraft\", \"textures/enchants/\" + texture + \".png\"))"
                        + ".setParents(parents).setPages(new ResearchPage(\"0\")).setSecondary()"));
        assertTrue("the strike trio keep their shortened keys",
                src.contains("enchantEntry(\"TTENCH_FOCUSED\",")
                        && src.contains("enchantEntry(\"TTENCH_DISPERSED\",")
                        && src.contains("enchantEntry(\"TTENCH_FINAL\","));
        assertTrue("the final strike needs both of its parents", src.contains(
                "0, 8, \"final_strike\", \"TTENCH_FOCUSED\", \"TTENCH_DISPERSED\")"));
        assertTrue("six hang straight off the enchanter", src.contains(
                "6, 2, \"ascent_boost\", \"ENCHANTER\")")
                && src.contains("3, 5, \"vampirism\", \"ENCHANTER\")"));
        String[][] entries = {
                {"TTENCH_ASCENT_BOOST", "ascent_boost"}, {"TTENCH_SLOW_FALL", "slow_fall"},
                {"TTENCH_AUTO_SMELT", "auto_smelt"}, {"TTENCH_DESINTEGRATE", "desintegrate"},
                {"TTENCH_QUICK_DRAW", "quick_draw"}, {"TTENCH_VAMPIRISM", "vampirism"},
                {"TTENCH_FOCUSED", "focused_strikes"}, {"TTENCH_DISPERSED", "dispersed_strikes"},
                {"TTENCH_FINAL", "final_strike"}, {"TTENCH_POUNCE", "pounce"},
                {"TTENCH_SHATTER", "shatter"}, {"TTENCH_SHOCKWAVE", "shockwave"},
                {"TTENCH_TUNNEL", "tunnel"}, {"TTENCH_VALIANCE", "valiance"},
        };
        for (String[] entry : entries) {
            assertTrue("entry for " + entry[0], src.contains("enchantEntry(\"" + entry[0] + "\","));
            assertTrue("icon ships for " + entry[0], Files.exists(
                    Paths.get("src/main/resources/assets/thaumcraft/textures/enchants/"
                            + entry[1] + ".png")));
            assertTrue("en strings for " + entry[0],
                    en.contains("ttresearch.name." + entry[0] + "=")
                            && en.contains("ttresearch.page." + entry[0] + ".0="));
            assertTrue("ru strings for " + entry[0],
                    ru.contains("ttresearch.name." + entry[0] + "="));
        }
    }

    /**
     * The KAMI class <em>is</em> the tier's gate: concealed on construction,
     * and on setPages it sweeps every other research in the book into its own
     * hidden parents. Lose that sweep and ichorium tools become available to a
     * fresh world.
     */
    @Test
    public void kamiEntriesAreGatedBehindTheWholeBook() throws IOException {
        String src = normalize(read(
                "src/main/java/thaumcraft/common/config/research/TinkererKamiResearchItem.java"));
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        assertTrue("every KAMI entry is concealed from birth",
                src.contains("super(key, tags, col, row, complexity, icon); setConcealed(); }"));
        assertTrue("it sweeps every category and every research in it",
                src.contains("for (String categoryStr : ResearchCategories.researchCategories.keySet())")
                        && src.contains("for (String tag : category.research.keySet())"));
        assertTrue("the original's exclusions, all four of them",
                src.contains("research.isLost()")
                        && src.contains("(research.parentsHidden == null && research.parents == null)")
                        && src.contains("research.isVirtual()")
                        && src.contains("research instanceof TinkererKamiResearchItem"));
        assertTrue("the blacklist is seeded with MINILITH and matched by prefix",
                src.contains("BLACKLIST.add(\"MINILITH\");")
                        && src.contains("if (tag.startsWith(black))"));
        assertTrue("anything ending in KAMI is skipped too",
                src.contains("if (tag.endsWith(\"KAMI\"))"));
        assertTrue("an auto-unlocked stub is exempt, as upstream has it",
                src.contains("if (!isAutoUnlock()) {"));
        assertTrue("the swept list replaces the hidden parents",
                src.contains("this.parentsHidden = requirements.toArray(new String[requirements.size()]);"));
        assertTrue("KAMI lore carries its own prefix",
                src.contains("return \"ttresearch.prefix.kami\";")
                        && en.contains("ttresearch.prefix.kami=[TTKami] \n"));
    }

    /** A sample of the KAMI tier, and the trunk everything there hangs from. */
    @Test
    public void kamiBranchMatchesTheOriginal() throws IOException {
        String src = normalize(source());
        assertTrue("dimension shards are the auto-unlocked stub of the tier", src.contains(
                "new TinkererKamiResearchItem(\"DIMENSION_SHARDS\", new AspectList(),"
                        + " 7, 8, 0, new ItemStack(ConfigItems.itemKamiResource, 1, 7))"
                        + ".setStub().setAutoUnlock().setRound()"
                        + ".setPages(new ResearchPage(\"0\"))"));
        assertTrue("ichor is the root and takes no parent", src.contains(
                "new TinkererKamiResearchItem(\"ICHOR\", new AspectList()"
                        + ".add(Aspect.MAN, 1).add(Aspect.LIGHT, 2)"
                        + ".add(Aspect.SOUL, 1).add(Aspect.TAINT, 1),"
                        + " 9, 8, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 0))"
                        + ".setPages(new ResearchPage(\"0\"), infusionPage(\"Ichor\"))"));
        assertTrue("the armour entry is the helm's, and shows all four crafts", src.contains(
                "new TinkererKamiResearchItem(\"ICHORCLOTH_ARMOR\", new AspectList()"
                        + ".add(Aspect.ARMOR, 2).add(Aspect.CLOTH, 1)"
                        + ".add(Aspect.LIGHT, 1).add(Aspect.CRAFT, 1),"
                        + " 17, 5, 5, new ItemStack(ConfigItems.itemIchorclothHelm))"
                        + ".setConcealed().setParents(\"ICHOR_CLOTH\")"
                        + ".setPages(new ResearchPage(\"0\"),"
                        + " arcaneRecipePage(\"IchorclothHelm\"),"
                        + " arcaneRecipePage(\"IchorclothChest\"),"
                        + " arcaneRecipePage(\"IchorclothLegs\"),"
                        + " arcaneRecipePage(\"IchorclothBoots\"))"));
        assertTrue("the tools entry shows all four plain tools", src.contains(
                "new TinkererKamiResearchItem(\"ICHOR_TOOLS\", new AspectList()"
                        + ".add(Aspect.TOOL, 2).add(Aspect.WEAPON, 1)"
                        + ".add(Aspect.METAL, 1).add(Aspect.CRAFT, 1),"
                        + " 13, 12, 5, new ItemStack(ConfigItems.itemIchorPick))"
                        + ".setConcealed().setParents(\"ICHORIUM\").setParentsHidden(\"ICHORCLOTH_ROD\")"));
        assertTrue("the shovel keeps upstream's doubled EARTH tag", src.contains(
                "new TinkererKamiResearchItem(\"ICHOR_SHOVEL_GEM\", new AspectList()"
                        + ".add(Aspect.EARTH, 2).add(Aspect.TOOL, 1)"
                        + ".add(Aspect.MINE, 1).add(Aspect.EARTH, 1),"));
        assertTrue("the block talisman needs both gem tools", src.contains(
                "new TinkererKamiResearchItem(\"BLOCK_TALISMAN\", new AspectList()"
                        + ".add(Aspect.VOID, 2).add(Aspect.DARKNESS, 1)"
                        + ".add(Aspect.ELDRITCH, 1).add(Aspect.MAGIC, 1),"
                        + " 14, 17, 5, new ItemStack(ConfigItems.itemBlockTalisman))"
                        + ".setParents(\"ICHOR_PICK_GEM\", \"ICHOR_SHOVEL_GEM\")"));
    }

    /**
     * The KAMI resources and tools used to be gated on Thaumcraft's INFUSION,
     * which was an invented gate. They belong on their own keys.
     */
    @Test
    public void kamiRecipesAreGatedOnKamiKeys() throws IOException {
        String recipes = normalize(read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java"));
        assertTrue("ichor itself", recipes.contains(
                "ConfigResearch.recipes.put(\"Ichor\","
                        + " ThaumcraftApi.addInfusionCraftingRecipe( \"ICHOR\","));
        assertTrue("ichorcloth", recipes.contains(
                "ConfigResearch.recipes.put(\"IchorCloth\","
                        + " ThaumcraftApi.addArcaneCraftingRecipe( \"ICHOR_CLOTH\","));
        assertTrue("ichorium", recipes.contains(
                "ConfigResearch.recipes.put(\"Ichorium\","
                        + " ThaumcraftApi.addArcaneCraftingRecipe( \"ICHORIUM\","));
        assertTrue("the cap", recipes.contains(
                "ConfigResearch.recipes.put(\"IchorCap\","
                        + " ThaumcraftApi.addArcaneCraftingRecipe( \"ICHOR_CAP\","));
        assertTrue("the rod", recipes.contains(
                "ConfigResearch.recipes.put(\"IchorclothRod\","
                        + " ThaumcraftApi.addInfusionCraftingRecipe( \"ICHORCLOTH_ROD\","));
        assertTrue("and all four plain tools, through their helper", recipes.contains(
                "ConfigResearch.recipes.put(key, ThaumcraftApi.addArcaneCraftingRecipe("
                        + " \"ICHOR_TOOLS\", new ItemStack(tool),"));
    }

    /**
     * Every parent an entry names must exist, hidden ones included. A hidden
     * parent that nothing declares locks the entry shut just as firmly as a
     * missing gate locks a recipe.
     */
    @Test
    public void everyParentExists() throws IOException {
        String research = source();
        StringBuilder allResearch = new StringBuilder(research);
        for (java.io.File f : new java.io.File("src/main/java/thaumcraft/common/config/research")
                .listFiles()) {
            if (f.getName().endsWith(".java")) {
                allResearch.append(read(f.getPath().replace('\\', '/')));
            }
        }
        String pool = allResearch.toString();
        Matcher parents = Pattern.compile("\\.setParents(?:Hidden)?\\(([^)]*)\\)").matcher(research);
        int checked = 0;
        while (parents.find()) {
            Matcher keys = Pattern.compile("\"([A-Z0-9_]+)\"").matcher(parents.group(1));
            while (keys.find()) {
                String key = keys.group(1);
                boolean declared = pool.contains("new TinkererResearchItem(\"" + key + "\"")
                        || pool.contains("\"" + key + "\",\n")
                        || pool.contains("new ResearchItem(\n                \"" + key + "\",")
                        || pool.contains("imbuedFire(\"" + key + "\"");
                assertTrue("nothing declares the research " + key, declared);
                checked++;
            }
        }
        assertTrue("the entries must actually name parents", checked >= 25);
    }

    /**
     * Upstream keys the crop and potion recipes after their map slot and forgets
     * to name the gate, so all eight sit behind keys no entry declares and are
     * uncraftable in the original. Here they hang off the entry that shows them.
     */
    @Test
    public void infusedCropsAreGatedOnTheEntryThatShowsThem() throws IOException {
        String recipes = normalize(read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java"));
        for (int i = 0; i < 4; i++) {
            assertTrue("seed " + i + " is filed under its own slot but gated on the entry",
                    recipes.contains("ConfigResearch.recipes.put(\"INFUSED_POTIONS" + i + "\","
                            + " ThaumcraftApi.addInfusionCraftingRecipe( \"INFUSED_POTIONS\","));
            assertTrue("potion " + i + " likewise",
                    recipes.contains("ConfigResearch.recipes.put(\"INFUSED_POTIONSPOT" + i + "\","
                            + " ThaumcraftApi.addCrucibleRecipe( \"INFUSED_POTIONS\","));
        }
    }

    /**
     * Upstream's recipe wrappers take (mapKey, researchGate) and the two differ
     * for a handful of objects. Getting them the wrong way round gates a recipe
     * behind a key no entry declares, which is exactly how the summoning block
     * became uncraftable.
     */
    @Test
    public void recipesGatedOnTheKeyTheOriginalGatesThemOn() throws IOException {
        String recipes = normalize(read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java"));
        assertTrue("the summoning block is gated on SUMMON and stored under SUMMON0",
                recipes.contains("ConfigResearch.recipes.put(\"SUMMON0\","
                        + " ThaumcraftApi.addArcaneCraftingRecipe( \"SUMMON\","
                        + " new ItemStack(ConfigBlocks.blockSummon),"));
        assertTrue("both magnets are gated on MAGNETS, not on their own names",
                recipes.contains("\"MAGNETS\", new ItemStack(ConfigBlocks.blockMagnet),")
                        && recipes.contains("\"MAGNETS\", new ItemStack(ConfigBlocks.blockMagnet, 1, 1),"));
        assertTrue("the relay is gated on the levitator's key",
                recipes.contains("\"LEVITATOR\", new ItemStack(ConfigBlocks.blockMobilizerRelay),"));
        assertTrue("both transvector recipes are gated on INTERFACE",
                recipes.contains("\"INTERFACE\", new ItemStack(ConfigBlocks.blockTransvectorInterface),")
                        && recipes.contains("\"INTERFACE\", new ItemStack(ConfigItems.itemTransvectorConnector),"));
    }

    /**
     * Every key a page asks for must be a key some recipe was filed under.
     * The lookups are strict, so a typo here is a crash at world load rather
     * than a wrong page — this test turns that into a red build instead.
     */
    @Test
    public void everyRecipePageKeyIsRegistered() throws IOException {
        String research = source();
        String recipes = normalize(read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java"));
        Matcher pages = Pattern.compile(
                "(?:arcaneRecipePage|infusionPage|benchRecipePage|crucibleRecipePage)\\(\"([^\"]+)\"\\)")
                .matcher(research);
        int checked = 0;
        while (pages.find()) {
            String key = pages.group(1);
            // Some recipes are filed by a helper that takes the key as its
            // first argument — kamiTool("IchorPick", ...) and friends.
            boolean filed = recipes.contains("ConfigResearch.recipes.put(\"" + key + "\",")
                    || recipes.matches(".*bench\\([^)]*, \"" + Pattern.quote(key) + "\",.*")
                    || recipes.matches(".*\\b(?:kamiTool|advTool|fire|arcane)\\(\""
                            + Pattern.quote(key) + "\",.*");
            assertTrue("no recipe is filed under " + key + ", so its page would throw", filed);
            checked++;
        }
        assertTrue("the entries must actually reference recipes", checked >= 20);
    }

    /** A missing recipe handle must throw rather than quietly become a text page. */
    @Test
    public void recipePagesResolveStrictly() throws IOException {
        String src = normalize(source());
        assertTrue("arcane pages go through the strict lookup",
                src.contains("return new ResearchPage(ConfigResearch.recipeArcane(recipeKey));"));
        assertTrue("infusion pages go through the strict lookup",
                src.contains("return new ResearchPage(ConfigResearch.recipeInfusion(recipeKey));"));
    }

    @Test
    public void namedAndWrittenInBothLanguages() throws IOException {
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        for (String key : KEYS) {
            assertTrue("en name for " + key, en.contains("ttresearch.name." + key + "="));
            assertTrue("ru name for " + key, ru.contains("ttresearch.name." + key + "="));
            assertTrue("en lore for " + key, en.contains("ttresearch.lore." + key + "="));
            assertTrue("ru lore for " + key, ru.contains("ttresearch.lore." + key + "="));
            assertTrue("en page for " + key, en.contains("ttresearch.page." + key + ".0="));
            assertTrue("ru page for " + key, ru.contains("ttresearch.page." + key + ".0="));
        }
        assertTrue("dislocation's second page in en", en.contains("ttresearch.page.FOCUS_DISLOCATION.1="));
        assertTrue("dislocation's second page in ru", ru.contains("ttresearch.page.FOCUS_DISLOCATION.1="));
    }

    /**
     * The point of the whole branch: every research key the foci recipes are
     * gated behind is a key this file registers. Break the pairing and the
     * module goes back to being uncraftable in survival.
     */
    @Test
    public void everyFocusRecipeGateHasAnEntry() throws IOException {
        String research = source();
        String recipes = normalize(read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java"));
        String[][] gates = {
                {"FOCUS_SMELT", "focusSmelt"},
                {"FOCUS_FLIGHT", "focusFlight"},
                {"FOCUS_DEFLECT", "focusDeflect"},
                {"FOCUS_TELEKINESIS", "focusTelekinesis"},
                {"FOCUS_DISLOCATION", "focusDislocation"},
                {"FOCUS_HEAL", "focusHeal"},
                {"FOCUS_ENDER_CHEST", "focusEnderChest"},
        };
        for (String[] gate : gates) {
            assertTrue(gate[1] + " must be gated behind " + gate[0],
                    recipes.contains("\"" + gate[0] + "\", new ItemStack(ConfigItems." + gate[1] + ")"));
            assertTrue(gate[0] + " must be a registered entry",
                    research.contains("new TinkererResearchItem(\"" + gate[0] + "\","));
        }
    }

    private static String source() throws IOException {
        return read("src/main/java/thaumcraft/common/config/research/ConfigResearchTinkerer.java");
    }

    /** Collapses wrapping so the pins read like the original's one-line chains. */
    private static String normalize(String src) {
        return src.replaceAll("\\s+", " ").replace(" .", ".");
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
