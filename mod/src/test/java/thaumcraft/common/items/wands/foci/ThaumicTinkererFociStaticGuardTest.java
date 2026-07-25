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

    @Test
    public void magnetBlockAndTileAreRegistered() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockMagnet registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockMagnet;") && cfg.contains("blockMagnet,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockMagnet)"));
        assertTrue("TileMagnet registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileMagnet.class, \"TileMagnet\")"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockMagnet.java");
        assertTrue("magnet must expose the attract/repel state and toggle on right-click",
                block.contains("PropertyBool.create(\"pulling\")")
                        && block.contains("public boolean onBlockActivated(")
                        && block.contains("cycleProperty(PULLING)"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileMagnet.java");
        assertTrue("TileMagnet must tick, need redstone, and move dropped items",
                tile.contains("implements ITickable")
                        && tile.contains("public void update()")
                        && tile.contains("EntityItem")
                        && tile.contains("getRedstonePower"));
        assertTrue("reach must stay the original signal/2", tile.contains("redstone / 2.0D"));

        assertTrue("magnet blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockmagnet.json")));
        for (String m : new String[]{"blockmagnet_pull", "blockmagnet_push"}) {
            assertTrue("model " + m,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/" + m + ".json")));
        }
        for (String t : new String[]{"magnet", "magnet_top_pull", "magnet_top_push"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void repairerConsumesEssentiaToMendTools() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockRepairer registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockRepairer;") && cfg.contains("blockRepairer,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockRepairer)"));
        assertTrue("TileRepairer registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileRepairer.class, \"TileRepairer\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileRepairer.java");
        assertTrue("repairer must be an essentia consumer that ticks",
                tile.contains("implements ITickable") && tile.contains("IEssentiaTransport")
                        && tile.contains("public void update()"));
        assertTrue("original repair values must be preserved: TOOL 8, CRAFT 5, ORDER 3",
                tile.contains("REPAIR_VALUES.put(Aspect.TOOL, 8)")
                        && tile.contains("REPAIR_VALUES.put(Aspect.CRAFT, 5)")
                        && tile.contains("REPAIR_VALUES.put(Aspect.ORDER, 3)"));
        assertTrue("must draw exactly one essentia per attempt through the facing side",
                tile.contains("takeEssentia(entry.getKey(), 1, opposite)"));
        assertTrue("must expose its slot to pipes/hoppers",
                tile.contains("CapabilityItemHandler.ITEM_HANDLER_CAPABILITY"));
        assertTrue("the held tool must survive breaking the block",
                read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockRepairer.java")
                        .contains("InventoryHelper.spawnItemStack"));

        assertTrue("repairer blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockrepairer.json")));
        for (String t : new String[]{"repairer_side", "repairer_top"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void transvectorInterfaceProxiesALinkedBlock() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockTransvectorInterface registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockTransvectorInterface;") && cfg.contains("blockTransvectorInterface,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockTransvectorInterface)"));
        assertTrue("TileTransvectorInterface registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileTransvectorInterface.class, \"TileTransvectorInterface\")"));

        String base = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvector.java");
        assertTrue("links must be range-checked per axis and dropped when out of range",
                base.contains("public boolean withinRange(") && base.contains("unlink();"));
        assertTrue("must not force-load the target chunk",
                base.contains("world.isBlockLoaded(target)"));
        assertTrue("must never resolve to itself", base.contains("tile == this ? null : tile"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvectorInterface.java");
        assertTrue("interface reach must stay the original 4", tile.contains("MAX_DISTANCE = 4"));
        assertTrue("must forward capabilities to the linked tile",
                tile.contains("public <T> T getCapability(") && tile.contains("linked.getCapability(capability, facing)"));
        assertTrue("must delegate Thaumcraft essentia interfaces",
                tile.contains("IAspectContainer") && tile.contains("IEssentiaTransport"));

        String connector = read("src/main/java/thaumcraft/common/items/tinkerer/ItemTransvectorConnector.java");
        assertTrue("connector must link in two clicks and refuse interface-to-interface chains",
                connector.contains("device.link(pos)") && connector.contains("tc.transvector.nochain"));

        assertTrue("interface blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blocktransvectorinterface.json")));
        assertTrue("interface texture ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/transvector_interface.png")));
        assertTrue("connector texture ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/transvector_connector.png")));
    }

    @Test
    public void dislocatorSwapsBlocksOnARedstonePulse() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockTransvectorDislocator registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockTransvectorDislocator;") && cfg.contains("blockTransvectorDislocator,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockTransvectorDislocator)"));
        assertTrue("TileTransvectorDislocator registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileTransvectorDislocator.class, \"TileTransvectorDislocator\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvectorDislocator.java");
        assertTrue("dislocator reach must stay the original 16", tile.contains("MAX_DISTANCE = 16"));
        assertTrue("cooldown must stay the original 10 ticks", tile.contains("COOLDOWN = 10"));
        assertTrue("a pulse during cooldown must be remembered, not dropped",
                tile.contains("pulseStored = true") && tile.contains("pulseStored = false"));
        assertTrue("tiles must travel with their blocks",
                tile.contains("detachTile(") && tile.contains("attachTile("));
        assertTrue("entities standing on either side must be swapped too",
                tile.contains("swapEntities("));
        assertTrue("must refuse nodes, blacklisted blocks and unbreakable blocks",
                tile.contains("ConfigBlocks.blockAiry")
                        && tile.contains("portableHoleBlackList")
                        && tile.contains("getBlockHardness(world, at) >= 0.0F"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockTransvectorDislocator.java");
        assertTrue("must fire on the rising redstone edge only",
                block.contains("public void neighborChanged(") && block.contains("powered == wasPowered"));

        assertTrue("dislocator blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blocktransvectordislocator.json")));
        for (String t : new String[]{"transvector_dislocator", "transvector_dislocator_on"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void animationTabletWorksAToolAgainstTheBlockItFaces() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockAnimationTablet registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockAnimationTablet;") && cfg.contains("blockAnimationTablet,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockAnimationTablet)"));
        assertTrue("TileAnimationTablet registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileAnimationTablet.class, \"TileAnimationTablet\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileAnimationTablet.java");
        assertTrue("swing cycle must keep the original speed and arc",
                tile.contains("SWING_SPEED = 3") && tile.contains("MAX_DEGREE = 45"));
        assertTrue("must act through a fake player",
                tile.contains("FakePlayerFactory.get") && tile.contains("attackTargetEntityWithCurrentItem"));
        assertTrue("strike mode must break blocks progressively, not instantly",
                tile.contains("breakProgress") && tile.contains("getDigSpeed")
                        && tile.contains("sendBlockBreakProgress"));
        assertTrue("use mode must try block activation and item use",
                tile.contains("onBlockActivated(") && tile.contains("onItemUse("));
        assertTrue("must never target its own fake player",
                tile.contains("e instanceof FakePlayer"));
        assertTrue("both toggles must persist", tile.contains("strikeMode") && tile.contains("redstoneMode"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockAnimationTablet.java");
        assertTrue("sneak-click toggles mode, redstone in hand toggles the trigger",
                block.contains("toggleMode()") && block.contains("toggleRedstoneMode()"));
        assertTrue("the held tool must survive breaking the block",
                block.contains("InventoryHelper.spawnItemStack"));

        assertTrue("tablet blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockanimationtablet.json")));
        for (String t : new String[]{"animation_tablet_top", "animation_tablet_side"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    /** Every TT block must be obtainable in survival, not creative-only. */
    @Test
    public void tinkererBlocksAreCraftable() throws IOException {
        String recipes = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        for (String key : new String[]{"DarkQuartz", "Funnel", "Magnet", "Repairer",
                "TransvectorInterface", "TransvectorConnector", "TransvectorDislocator",
                "AnimationTablet"}) {
            assertTrue(key + " must have an arcane recipe", recipes.contains("\"" + key + "\""));
        }
        assertTrue("block recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerBlockRecipes()"));
    }

    /** The module ships in both languages — the rest of the mod is fully translated. */
    @Test
    public void tinkererContentIsLocalisedInBothLanguages() throws IOException {
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        String[] keys = {
                "item.thaumcraft.focus_smelt.name", "item.thaumcraft.focus_telekinesis.name",
                "item.thaumcraft.focus_flight.name", "item.thaumcraft.focus_heal.name",
                "item.thaumcraft.focus_deflect.name", "item.thaumcraft.focus_dislocation.name",
                "item.thaumcraft.focus_enderchest.name", "item.thaumcraft.placement_mirror.name",
                "item.thaumcraft.cleansing_talisman.name", "item.thaumcraft.xp_talisman.name",
                "item.thaumcraft.cat_amulet.name", "tile.thaumcraft.dark_quartz.0.name",
                "tile.thaumcraft.funnel.name", "tile.thaumcraft.magnet.name",
                "tile.thaumcraft.repairer.name",
                "tile.thaumcraft.transvector_interface.name",
                "item.thaumcraft.transvector_connector.name",
                "tile.thaumcraft.transvector_dislocator.name",
                "tile.thaumcraft.animation_tablet.name",
        };
        for (String key : keys) {
            assertTrue(key + " missing from en_us.lang", en.contains(key + "="));
            assertTrue(key + " missing from ru_ru.lang", ru.contains(key + "="));
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
