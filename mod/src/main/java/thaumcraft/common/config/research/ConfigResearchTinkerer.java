package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

/**
 * Thaumic Tinkerer's own Thaumonomicon tab and its entries, transcribed from
 * each object's {@code getResearchItem()} in the original
 * (pixlepix / nekosune / Vazkii).
 *
 * <p>Without these the module's content exists but cannot be reached: both
 * {@code ShapedArcaneRecipe.matches} and {@code InfusionRecipe.matches} begin
 * with a research check, and an unknown key never completes. Keys, aspects,
 * map coordinates, complexity, parents and page counts are the original's.</p>
 */
public final class ConfigResearchTinkerer {

    /** The original's LibResearch.CATEGORY_THAUMICTINKERER. */
    public static final String CATEGORY = "TT_CATEGORY";

    private ConfigResearchTinkerer() {
    }

    public static void initCategory() {
        ResearchCategories.registerCategory(
                CATEGORY,
                new ResourceLocation("thaumcraft", "textures/misc/r_enchanting.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
    }

    public static void initResearch() {
        registerFoci();
        registerDarkQuartzBranch();
    }

    /**
     * The dark quartz trunk and everything hanging off it. Dark quartz itself
     * is a stub: auto-unlocked, round, no aspects and no complexity — the way
     * into the tab rather than something to research.
     */
    private static void registerDarkQuartzBranch() {
        new TinkererResearchItem("DARK_QUARTZ",
                new AspectList(),
                -2, 2, 0, new ItemStack(ConfigItems.itemDarkQuartz))
                .setStub().setAutoUnlock().setRound()
                .setPages(new ResearchPage("0"),
                        benchRecipePage("DARK_QUARTZ0"),
                        benchRecipePage("DARK_QUARTZ1"),
                        benchRecipePage("DARK_QUARTZ2"),
                        benchRecipePage("DARK_QUARTZ3"),
                        benchRecipePage("DARK_QUARTZ4"),
                        benchRecipePage("DARK_QUARTZ5"))
                .registerResearchItem();

        new TinkererResearchItem("INTERFACE",
                new AspectList().add(Aspect.ENTROPY, 4).add(Aspect.ORDER, 4),
                -4, 2, 1, new ItemStack(ConfigBlocks.blockTransvectorInterface))
                .setParents("DARK_QUARTZ")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("TransvectorInterface"),
                        new ResearchPage("1"),
                        arcaneRecipePage("TransvectorConnector"),
                        new ResearchPage("2"))
                .registerResearchItem();

        new TinkererResearchItem("MAGNETS",
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1),
                -6, 3, 3, new ItemStack(ConfigBlocks.blockMagnet))
                .setParents("INTERFACE").setConcealed()
                .setPages(new ResearchPage("0"), new ResearchPage("1"),
                        arcaneRecipePage("Magnet"),
                        arcaneRecipePage("MobMagnet"),
                        crucibleRecipePage("SoulMould"))
                .registerResearchItem();

        // The hidden MIRROR parent is Thaumcraft's own — the dislocator is a
        // mirror trick, and upstream keeps it behind that research too.
        new TinkererResearchItem("DISLOCATOR",
                new AspectList().add(Aspect.TRAVEL, 2).add(Aspect.MECHANISM, 1).add(Aspect.ELDRITCH, 1),
                -6, 1, 3, new ItemStack(ConfigBlocks.blockTransvectorDislocator))
                .setConcealed().setParents("INTERFACE").setParentsHidden("MIRROR")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("TransvectorDislocator"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("ANIMATION_TABLET",
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.METAL, 1)
                        .add(Aspect.MOTION, 1).add(Aspect.ENERGY, 1),
                -8, 2, 4, new ItemStack(ConfigBlocks.blockAnimationTablet))
                .setParents("MAGNETS")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("AnimationTablet"))
                .registerResearchItem();

        // Upstream's KEY_MOBILIZER is the string "LEVITATOR", and its strings
        // and recipe gate follow the key, not the class name.
        new TinkererResearchItem("LEVITATOR",
                new AspectList().add(Aspect.MOTION, 2).add(Aspect.ORDER, 2),
                -7, 5, 3, new ItemStack(ConfigBlocks.blockMobilizer))
                .setParents("MAGNETS")
                .setPages(new ResearchPage("0"),
                        infusionPage("Mobilizer"),
                        arcaneRecipePage("MobilizerRelay"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("CLEANSING_TALISMAN",
                new AspectList().add(Aspect.HEAL, 2).add(Aspect.ORDER, 1).add(Aspect.POISON, 1),
                -3, 4, 3, new ItemStack(ConfigItems.itemCleansingTalisman))
                .setSecondary().setParents("DARK_QUARTZ")
                .setPages(new ResearchPage("0"),
                        infusionPage("CleansingTalisman"))
                .registerResearchItem();

        new TinkererResearchItem("BLOOD_SWORD",
                new AspectList().add(Aspect.HUNGER, 2).add(Aspect.WEAPON, 1)
                        .add(Aspect.FLESH, 1).add(Aspect.SOUL, 1),
                -4, 6, 3, new ItemStack(ConfigItems.itemBloodSword))
                .setParents("CLEANSING_TALISMAN")
                .setPages(new ResearchPage("0"),
                        infusionPage("BloodSword"),
                        new ResearchPage("1"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("SUMMON",
                new AspectList().add(Aspect.WEAPON, 1).add(Aspect.BEAST, 3).add(Aspect.MAGIC, 3),
                -5, 8, 3, new ItemStack(ConfigBlocks.blockSummon))
                .setParents("BLOOD_SWORD")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("SUMMON0"),
                        benchRecipePage("SUMMON1"),
                        infusionPage("SUMMON"),
                        new ResearchPage("1"))
                .registerResearchItem();

        new TinkererResearchItem("PLATFORM",
                new AspectList().add(Aspect.SENSES, 2).add(Aspect.TREE, 1).add(Aspect.MOTION, 1),
                -2, 6, 3, new ItemStack(ConfigBlocks.blockPlatform))
                .setConcealed().setParents("CLEANSING_TALISMAN")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("Platform"))
                .setSecondary()
                .registerResearchItem();
    }

    /**
     * The seven wand foci. The branch is rooted in Thaumcraft's own
     * FOCUSEXCAVATION, which is where the original hung it too, so the tab has
     * a way in; everything else descends from the smelting focus.
     */
    private static void registerFoci() {
        new TinkererResearchItem("FOCUS_SMELT",
                new AspectList().add(Aspect.FIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.MAGIC, 1),
                -2, -2, 2, new ItemStack(ConfigItems.focusSmelt))
                .setParents("FOCUSEXCAVATION").setConcealed()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("FocusSmelt"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_FLIGHT",
                new AspectList().add(Aspect.MOTION, 1).add(Aspect.MAGIC, 1).add(Aspect.AIR, 2),
                -3, -4, 2, new ItemStack(ConfigItems.focusFlight))
                .setParents("FOCUS_SMELT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusFlight"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_DEFLECT",
                new AspectList().add(Aspect.MOTION, 2).add(Aspect.AIR, 1)
                        .add(Aspect.ORDER, 1).add(Aspect.DEATH, 1),
                -4, -3, 3, new ItemStack(ConfigItems.focusDeflect))
                .setConcealed().setParents("FOCUS_SMELT")
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusDeflect"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_TELEKINESIS",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.MOTION, 1),
                -4, -6, 2, new ItemStack(ConfigItems.focusTelekinesis))
                .setParents("FOCUS_FLIGHT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusTelekinesis"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_DISLOCATION",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1),
                -5, -5, 2, new ItemStack(ConfigItems.focusDislocation))
                .setSecondary().setParents("FOCUS_FLIGHT").setConcealed()
                .setPages(new ResearchPage("0"), new ResearchPage("1"),
                        infusionPage("FocusDislocation"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_HEAL",
                new AspectList().add(Aspect.HEAL, 2).add(Aspect.SOUL, 1).add(Aspect.MAGIC, 1),
                -6, -4, 2, new ItemStack(ConfigItems.focusHeal))
                .setParents("FOCUS_DEFLECT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusHeal"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_ENDER_CHEST",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.VOID, 1).add(Aspect.MAGIC, 1),
                -6, -2, 2, new ItemStack(ConfigItems.focusEnderChest))
                .setParents("FOCUS_DEFLECT").setConcealed()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("FocusEnderChest"))
                .registerResearchItem();
    }

    /** The original's ResearchHelper.arcaneRecipePage, over our recipe map. */
    private static ResearchPage arcaneRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeArcane(recipeKey));
    }

    /** The original's ResearchHelper.infusionPage. */
    private static ResearchPage infusionPage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeInfusion(recipeKey));
    }

    /** The original's ResearchHelper.recipePage — a plain bench recipe. */
    private static ResearchPage benchRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeI(recipeKey));
    }

    /** The original's ResearchHelper.crucibleRecipePage. */
    private static ResearchPage crucibleRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeCrucible(recipeKey));
    }
}
