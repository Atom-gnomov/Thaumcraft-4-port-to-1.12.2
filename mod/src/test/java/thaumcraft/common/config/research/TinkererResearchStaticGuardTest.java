package thaumcraft.common.config.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    /** The original's TTResearchItem kept the module's strings under its own keys. */
    @Test
    public void entriesReadTheModulesOwnStrings() throws IOException {
        String src = read("src/main/java/thaumcraft/common/config/research/TinkererResearchItem.java");
        assertTrue("names come from ttresearch.name",
                src.contains("return \"ttresearch.name.\" + this.key;"));
        assertTrue("hover lore comes from ttresearch.lore",
                src.contains("return \"ttresearch.lore.\" + this.key;"));
        assertTrue("text pages are prefixed, recipe pages are left alone",
                normalize(src).contains(
                        "if (page != null && page.text != null && page.recipe == null) "
                                + "{ page.text = \"ttresearch.page.\" + this.key + \".\" + page.text; }"));
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
