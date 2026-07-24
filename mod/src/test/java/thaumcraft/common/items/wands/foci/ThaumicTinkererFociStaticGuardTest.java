package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Guards the Thaumic Tinkerer foci module (reimplemented from TT by
 * pixlepix/nekosune): each focus must exist, extend ItemFocusBasic with a
 * right-click behaviour, be registered + recipe-gated, and ship its texture,
 * model, and lang entry.
 */
public class ThaumicTinkererFociStaticGuardTest {

    private static final String[] FOCI = {
            "Smelt", "Telekinesis", "Flight", "Heal", "Deflect", "Dislocation", "EnderChest"
    };

    @Test
    public void eachFocusClassIsAWandFocus() throws IOException {
        for (String f : FOCI) {
            String src = read("src/main/java/thaumcraft/common/items/wands/foci/Focus" + f + ".java");
            assertTrue(f + " must extend ItemFocusBasic", src.contains("extends ItemFocusBasic"));
            assertTrue(f + " must implement the right-click cast",
                    src.contains("public ItemStack onFocusRightClick("));
            assertTrue(f + " must declare a vis cost",
                    src.contains("public AspectList getVisCost("));
        }
    }

    @Test
    public void fociAreRegisteredAndCraftable() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String[] fields = {"focusSmelt", "focusTelekinesis", "focusFlight", "focusHeal",
                "focusDeflect", "focusDislocation", "focusEnderChest"};
        String[] langKeys = {"focus_smelt", "focus_telekinesis", "focus_flight", "focus_heal",
                "focus_deflect", "focus_dislocation", "focus_enderchest"};
        for (String field : fields) {
            assertTrue(field + " must be a ConfigItems field", cfg.contains("public static ")
                    && cfg.contains(field + ";"));
            assertTrue(field + " must be added to allItems", cfg.contains("allItems.add(" + field + ")"));
        }
        for (String f : FOCI) {
            assertTrue("Focus" + f + " must have an arcane recipe in ConfigTinkerer",
                    rec.contains("\"Focus" + f + "\"") && rec.contains("ConfigItems.focus" + f));
        }
        assertTrue("Tinkerer foci recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerFociRecipes()"));
    }

    @Test
    public void utilityItemsAreRegisteredAndCraftable() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String[] fields = {"itemPlacementMirror", "itemCleansingTalisman", "itemXpTalisman", "itemCatAmulet"};
        String[] classes = {"ItemPlacementMirror", "ItemCleansingTalisman", "ItemXpTalisman", "ItemCatAmulet"};
        String[] langKeys = {"placement_mirror", "cleansing_talisman", "xp_talisman", "cat_amulet"};
        String[] recipeKeys = {"PlacementMirror", "CleansingTalisman", "XpTalisman", "CatAmulet"};
        for (String field : fields) {
            assertTrue(field + " registered", cfg.contains(field + ";") && cfg.contains("allItems.add(" + field + ")"));
        }
        for (String c : classes) {
            assertTrue(c + " source must exist as a tinkerer item",
                    Files.exists(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/" + c + ".java")));
        }
        for (String key : recipeKeys) {
            assertTrue("recipe " + key, rec.contains("\"" + key + "\"") && rec.contains("ConfigItems.item" + key));
        }
        for (String key : langKeys) {
            assertTrue("lang " + key, lang.contains("item.thaumcraft." + key + ".name="));
        }
        assertTrue("utility recipes registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerUtilityItemRecipes()"));
        for (String key : langKeys) {
            assertTrue("lang key for " + key, lang.contains("item.thaumcraft." + key + ".name="));
        }
    }

    @Test
    public void fociShipTexturesAndModels() {
        String[] tex = {"focus_smelt", "focus_telekinesis", "focus_flight", "focus_heal",
                "focus_deflect", "focus_dislocation", "focus_enderchest"};
        String[] model = {"focussmelt", "focustelekinesis", "focusflight", "focusheal",
                "focusdeflect", "focusdislocation", "focusenderchest"};
        for (String t : tex) {
            assertTrue("texture " + t + ".png must ship",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/" + t + ".png")));
        }
        for (String m : model) {
            assertTrue("model " + m + ".json must ship",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/item/" + m + ".json")));
        }
    }

    @Test
    public void darkQuartzBlockIsRegisteredWithVariants() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockDarkQuartz registered + item + in getAllBlocks",
                cfg.contains("blockDarkQuartz;") && cfg.contains("blockDarkQuartzItem;")
                        && cfg.contains("blockDarkQuartz,") && cfg.contains("blockDarkQuartzItem,"));
        assertTrue("BlockDarkQuartz source is a tinkerer block",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockDarkQuartz.java")));
        assertTrue("dark quartz blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockdarkquartz.json")));
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        for (int m = 0; m <= 2; m++) {
            assertTrue("dark quartz block model " + m,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/blockdarkquartz_" + m + ".json")));
            assertTrue("lang dark_quartz." + m, lang.contains("tile.thaumcraft.dark_quartz." + m + ".name="));
        }
        for (String t : new String[]{"dark_quartz", "dark_quartz_chiseled", "dark_quartz_pillar"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void funnelBlockAndTileAreRegistered() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockFunnel registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockFunnel;") && cfg.contains("blockFunnel,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockFunnel)"));
        assertTrue("TileFunnel registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileFunnel.class, \"TileFunnel\")"));
        assertTrue("BlockFunnel source",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockFunnel.java")));
        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileFunnel.java");
        assertTrue("TileFunnel must tick and vacuum into the inventory below",
                tile.contains("implements ITickable")
                        && tile.contains("public void update()")
                        && tile.contains("EntityItem")
                        && tile.contains("ItemHandlerHelper.insertItem"));
        assertTrue("funnel blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockfunnel.json")));
        assertTrue("funnel block model ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/blockfunnel.json")));
        for (String t : new String[]{"funnel_top", "funnel_side"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
        assertTrue("funnel lang",
                read("src/main/resources/assets/thaumcraft/lang/en_us.lang").contains("tile.thaumcraft.funnel.name="));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
