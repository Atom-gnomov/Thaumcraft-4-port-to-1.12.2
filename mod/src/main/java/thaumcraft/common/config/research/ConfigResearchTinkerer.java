package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;
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
}
