package thaumcraft.common.config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.research.ConfigResearch;

/**
 * The End Legacy module — <b>new content with no 1.7.10 original</b>, by the
 * owner's decision of 2026-08-02 ({@code END_LEGACY_PLAN.md}). The 1:1 rule
 * does not apply here; the calibration rule does: every cost below is scaled
 * against a neighbouring TC4 recipe, and the research sits in TC4's own tabs.
 *
 * <p>Phase 1: flight. {@code SOARING} (glide on any chestplate, unlocked by
 * scanning an elytra), {@code DRACONIC_SECRETS} (hidden, unlocked by scanning
 * what the Ender Dragon leaves behind — the dragon tie-in the owner asked
 * for), and {@code ASCENSION} (ground takeoff and Aer-fuelled thrust), which
 * needs both.</p>
 */
public final class ConfigEndLegacy {

    private ConfigEndLegacy() {
    }

    public static void initResearch() {
        // ARTIFICE, below INFUSIONENCHANTMENT (-6, 11): the two flight enchants.
        new ResearchItem(
                "SOARING",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 6)
                        .add(Aspect.AIR, 4)
                        .add(Aspect.MOTION, 4)
                        .add(Aspect.MAGIC, 3),
                -4, 12, 2,
                new ItemStack(Items.ELYTRA))
                .setPages(
                        new ResearchPage("tc.research_page.SOARING.1"),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnchSoaring")),
                        new ResearchPage("tc.research_page.SOARING.2"))
                .setParents("INFUSIONENCHANTMENT")
                .setHidden()
                .setItemTriggers(new ItemStack(Items.ELYTRA))
                .registerResearchItem();

        new ResearchItem(
                "ASCENSION",
                "ARTIFICE",
                new AspectList()
                        .add(Aspect.FLIGHT, 8)
                        .add(Aspect.ELDRITCH, 6)
                        .add(Aspect.ENERGY, 4)
                        .add(Aspect.AIR, 4),
                -2, 13, 3,
                new ItemStack(Items.DRAGON_BREATH))
                .setPages(
                        new ResearchPage("tc.research_page.ASCENSION.1"),
                        new ResearchPage(ConfigResearch.recipeInfusionEnchantment("InfEnchAscension")))
                .setParents("SOARING")
                .setParentsHidden("DRACONIC_SECRETS")
                .setConcealed()
                .setSpecial()
                .registerResearchItem();

        // ELDRITCH (4, 3): the dragon. Scanning its breath or its egg is what
        // reveals the entry — meaning the player has stood over a dead dragon.
        new ResearchItem(
                "DRACONIC_SECRETS",
                "ELDRITCH",
                new AspectList()
                        .add(Aspect.ELDRITCH, 8)
                        .add(Aspect.LIFE, 6)
                        .add(Aspect.MAGIC, 4)
                        .add(Aspect.FLIGHT, 4),
                4, 3, 2,
                new ItemStack(Items.DRAGON_BREATH))
                .setPages(
                        new ResearchPage("tc.research_page.DRACONIC_SECRETS.1"),
                        new ResearchPage("tc.research_page.DRACONIC_SECRETS.2"))
                .setParentsHidden("ELDRITCHMINOR")
                .setHidden()
                .setItemTriggers(
                        new ItemStack(Items.DRAGON_BREATH),
                        new ItemStack(net.minecraft.init.Blocks.DRAGON_EGG))
                .registerResearchItem();
    }

    /**
     * Costs calibrated against the infusion-enchantment baseline: Repair is
     * instability 4 at 28 essentia with two components; Soaring sits at the
     * same instability with rarer parts, Ascension above it — End loot all the
     * way down, no KAMI dependency.
     */
    public static void initRecipes() {
        ConfigResearch.recipes.put("InfEnchSoaring", ThaumcraftApi.addInfusionEnchantmentRecipe(
                "SOARING", Config.enchSoaring, 4,
                new AspectList().add(Aspect.FLIGHT, 24).add(Aspect.AIR, 16).add(Aspect.MAGIC, 8),
                new ItemStack[]{
                        new ItemStack(Items.ELYTRA),
                        new ItemStack(Items.FEATHER),
                        new ItemStack(Items.FEATHER),
                        new ItemStack(ConfigItems.itemResource, 1, 14)}));

        ConfigResearch.recipes.put("InfEnchAscension", ThaumcraftApi.addInfusionEnchantmentRecipe(
                "ASCENSION", Config.enchAscension, 6,
                new AspectList().add(Aspect.ELDRITCH, 24).add(Aspect.FLIGHT, 24)
                        .add(Aspect.ENERGY, 16).add(Aspect.MAGIC, 8),
                new ItemStack[]{
                        new ItemStack(Items.DRAGON_BREATH),
                        new ItemStack(Items.SHULKER_SHELL),
                        new ItemStack(net.minecraft.init.Blocks.END_ROD),
                        new ItemStack(ConfigItems.itemResource, 1, 14)}));
    }
}
