package thaumcraft.common.config.research;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;

public class ConfigResearch {
    private static boolean strictRecipeLookups = false;
    public static final Map<String, Object> recipes = new ResearchRecipeHandleMap();

    static IRecipe recipeI(String key) {
        return requireRecipeHandle(key, IRecipe.class);
    }

    public static IArcaneRecipe recipeArcane(String key) {
        return requireRecipeHandle(key, IArcaneRecipe.class);
    }

    public static CrucibleRecipe recipeCrucible(String key) {
        return requireRecipeHandle(key, CrucibleRecipe.class);
    }

    public static InfusionRecipe recipeInfusion(String key) {
        return requireRecipeHandle(key, InfusionRecipe.class);
    }

    public static InfusionEnchantmentRecipe recipeInfusionEnchantment(String key) {
        return requireRecipeHandle(key, InfusionEnchantmentRecipe.class);
    }

    static List<?> recipeList(String key) {
        return requireRecipeHandle(key, List.class);
    }

    private static <T> T requireRecipeHandle(String key, Class<T> expectedType) {
        Object value = recipes.get(key);
        if (!expectedType.isInstance(value)) {
            String actual = value == null ? "null" : value.getClass().getName();
            throw new IllegalStateException("Invalid ConfigResearch recipe handle type for key " + key
                    + ": expected " + expectedType.getName() + ", got " + actual);
        }
        return expectedType.cast(value);
    }

    public static void init() {
        initCategories();
        strictRecipeLookups = true;
        try {
            ConfigResearchBasics.initBasicResearchBaseline();
            ConfigResearchBasics.initBasicResearchProgressionBaseline();
            ConfigResearchAlchemy.initAlchemyResearchBaseline();
            ConfigResearchAlchemy.initAlchemyResearchTextOnlyBaseline();
            ConfigResearchArtifice.initArtificeResearchBaseline();
            ConfigResearchArtifice.initArtificeResearchTextOnlyBaseline();
            ConfigResearchGolemancy.initGolemancyResearchBaseline();
            ConfigResearchGolemancy.initGolemancyResearchTextOnlyBaseline();
            ConfigResearchThaumaturgy.initThaumaturgyResearchBaseline();
            ConfigResearchBasics.initBasicResearchTextOnlyExtended();
            // Thaumic Tinkerer's entries, so its recipes have a research to sit behind.
            ConfigResearchTinkerer.initResearch();
            ConfigResearchThaumaturgy.initThaumaturgyResearchTextOnlyBaseline();
            ConfigResearchEldritch.initEldritchResearchTextOnlyBaseline();
            ConfigResearchEldritch.initEldritchResearchBaseline();
            // End Legacy — new content in TC4's own tabs (owner's decision,
            // END_LEGACY_PLAN.md). After the tabs it lives in.
            thaumcraft.common.config.ConfigEndLegacy.initResearch();
        } finally {
            strictRecipeLookups = false;
        }
    }

    private static final class ResearchRecipeHandleMap extends HashMap<String, Object> {
        @Override
        public Object get(Object key) {
            if (strictRecipeLookups && key instanceof String) {
                if (!containsKey(key)) {
                    throw new IllegalStateException("Missing ConfigResearch recipe handle: " + key);
                }
                Object value = super.get(key);
                if (value == null) {
                    throw new IllegalStateException("Null ConfigResearch recipe handle: " + key);
                }
                return value;
            }
            return super.get(key);
        }
    }

    private static void initCategories() {
        ResearchCategories.registerCategory(
                "BASICS",
                new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "THAUMATURGY",
                new ResourceLocation("thaumcraft", "textures/misc/r_thaumaturgy.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "ALCHEMY",
                new ResourceLocation("thaumcraft", "textures/misc/r_crucible.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "ARTIFICE",
                new ResourceLocation("thaumcraft", "textures/misc/r_artifice.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
                "GOLEMANCY",
                new ResourceLocation("thaumcraft", "textures/misc/r_golemancy.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        // Thaumic Tinkerer's own tab, at the original's icon and background.
        ConfigResearchTinkerer.initCategory();
        ResearchCategories.registerCategory(
                "ELDRITCH",
                new ResourceLocation("thaumcraft", "textures/misc/r_eldritch.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png"));
    }

}
