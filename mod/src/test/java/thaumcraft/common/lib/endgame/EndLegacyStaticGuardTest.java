package thaumcraft.common.lib.endgame;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * The End Legacy module's wiring — every link in the chain from enchantment to
 * screen, because the module's failure mode is the one this repo knows best:
 * the working half ported, the invoking half forgotten.
 *
 * <p>This is <b>new content with no 1.7.10 original</b> (owner's decision,
 * {@code END_LEGACY_PLAN.md}). Audits: do not "restore" it toward an original
 * that does not exist; the plan document is the spec.</p>
 */
public class EndLegacyStaticGuardTest {

    private static final String MAIN = "src/main/java/thaumcraft/";

    /** Both enchantments exist, are constructed, and reach the Forge registry. */
    @Test
    public void theEnchantmentsAreConstructedAndRegistered() throws IOException {
        String thaumcraft = read(MAIN + "common/Thaumcraft.java");
        assertTrue(thaumcraft.contains("Config.enchSoaring = new thaumcraft.common.lib.enchantment.endgame.EnchantmentSoaring();"));
        assertTrue(thaumcraft.contains("Config.enchAscension = new thaumcraft.common.lib.enchantment.endgame.EnchantmentAscension();"));
        assertTrue("registration must not be forgotten",
                thaumcraft.contains("registerAll(Config.enchSoaring, Config.enchAscension)"));
        assertTrue("and the handler that gives them behaviour must be on the bus",
                thaumcraft.contains("new thaumcraft.common.lib.endgame.SoaringHandler()"));

        String soaring = read(MAIN + "common/lib/enchantment/endgame/EnchantmentSoaring.java");
        String ascension = read(MAIN + "common/lib/enchantment/endgame/EnchantmentAscension.java");
        assertTrue("infusion-only: the table must never roll these",
                soaring.contains("return 1000;") && ascension.contains("return 1000;")
                        && soaring.contains("isAllowedOnBooks") && ascension.contains("isAllowedOnBooks"));
    }

    /** The thrust packet is appended to the registry tail, never inserted. */
    @Test
    public void theThrustPacketRidesTheRegistryTail() throws IOException {
        String handler = read(MAIN + "common/lib/network/PacketHandler.java");
        int packet = handler.indexOf("PacketSoaringThrust.class");
        int check = handler.indexOf("if (idx != REFERENCE_PACKET_COUNT)");
        assertTrue("registered at all", packet >= 0);
        assertTrue("appended directly before the count check — the tail, not the middle",
                packet < check && handler.indexOf("register(", packet + 1) > check);

        String client = read(MAIN + "client/lib/ClientTickEventsFML.java");
        assertTrue("the client reports jump edges, or thrust can never engage",
                client.contains("movementInput != null && mc.player.movementInput.jump")
                        && client.contains("new PacketSoaringThrust(held)"));
    }

    /** Research, recipes and gates: each references the other by the same key. */
    @Test
    public void researchAndRecipesShareTheirKeys() throws IOException {
        String config = read(MAIN + "common/config/ConfigEndLegacy.java");
        for (String piece : new String[]{
                "\"SOARING\"", "\"ASCENSION\"", "\"DRACONIC_SECRETS\"",
                "recipeInfusionEnchantment(\"InfEnchSoaring\")",
                "recipeInfusionEnchantment(\"InfEnchAscension\")",
                "setParentsHidden(\"DRACONIC_SECRETS\")",
                "setItemTriggers(new ItemStack(Items.ELYTRA))"}) {
            assertTrue(piece + " must be present", config.contains(piece));
        }
        assertTrue("research init is called from the research config",
                read(MAIN + "common/config/research/ConfigResearch.java")
                        .contains("ConfigEndLegacy.initResearch()"));
        assertTrue("recipe init is called from the recipe config, before research reads the handles",
                read(MAIN + "common/config/ConfigRecipes.java")
                        .contains("ConfigEndLegacy.initRecipes()"));
    }

    /** The visible half: the wing layer is attached to both skin types. */
    @Test
    public void theWingLayerIsAttached() throws IOException {
        String proxy = read(MAIN + "client/ClientProxy.java");
        assertTrue(proxy.contains("attachSoaringWingLayers()"));
        assertTrue("one layer per skin map entry",
                proxy.contains("getSkinMap().values()")
                        && proxy.contains("new thaumcraft.client.renderers.entity.LayerSoaringWings(renderPlayer)"));

        String layer = read(MAIN + "client/renderers/entity/LayerSoaringWings.java");
        assertTrue("the Stratosphere robe keeps its own wings — no doubling",
                layer.contains("instanceof ItemGemChest"));
    }

    /** Names, lore and pages exist in both languages — no raw keys in either book. */
    @Test
    public void bothLanguagesCarryEveryKey() throws IOException {
        for (String lang : new String[]{"en_us", "ru_ru"}) {
            String file = read("src/main/resources/assets/thaumcraft/lang/" + lang + ".lang");
            for (String key : new String[]{
                    "enchantment.soaring=", "enchantment.ascension=",
                    "tc.research_name.SOARING=", "tc.research_text.SOARING=",
                    "tc.research_page.SOARING.1=", "tc.research_page.SOARING.2=",
                    "tc.research_name.ASCENSION=", "tc.research_page.ASCENSION.1=",
                    "tc.research_name.DRACONIC_SECRETS=",
                    "tc.research_page.DRACONIC_SECRETS.1=",
                    "tc.research_page.DRACONIC_SECRETS.2="}) {
                assertTrue(key + " missing from " + lang, file.contains(key));
            }
        }
    }

    /** Phase 2: the wards — items, handler, recipes, and the death refusal's price. */
    @Test
    public void theWardsAreWiredEndToEnd() throws IOException {
        String items = read(MAIN + "common/config/ConfigItems.java");
        for (String field : new String[]{
                "itemVoidExtract", "itemWardDeflection", "itemWardLastBreath", "itemWardLastBreathCracked"}) {
            assertTrue(field + " must be constructed and added",
                    items.contains(field + " = (") && items.contains("allItems.add(" + field + ")"));
        }
        String thaumcraft = read(MAIN + "common/Thaumcraft.java");
        assertTrue("the ward handler must be on the bus",
                thaumcraft.contains("new thaumcraft.common.lib.endgame.WardHandler()"));

        String handler = read(MAIN + "common/lib/endgame/WardHandler.java");
        assertTrue("deflection cancels the attack and wears the charm",
                handler.contains("isProjectile()") && handler.contains("charm.damageItem(1, player)"));
        assertTrue("the last breath costs two points of temporary warp",
                handler.contains("WARP_TEMPORARY = 2")
                        && handler.contains("addWarpToPlayer(player, WARP_TEMPORARY, true)"));
        assertTrue("and shatters the ward into its cracked remnant in place",
                handler.contains("new ItemStack(ConfigItems.itemWardLastBreathCracked)"));
        assertTrue("the void still kills — even dragons cannot argue with it",
                handler.contains("canHarmInCreative()"));

        String config = read(MAIN + "common/config/ConfigEndLegacy.java");
        for (String key : new String[]{
                "\"VoidExtract\"", "\"WardDeflection\"", "\"WardLastBreath\"", "\"WardLastBreathReforge\"",
                "\"WARD_ARROWS\"", "\"WARD_LASTBREATH\""}) {
            assertTrue(key + " must be present", config.contains(key));
        }
        assertTrue("the reforge must be cheaper than the first forging",
                config.indexOf("add(Aspect.LIFE, 16)") > config.indexOf("add(Aspect.LIFE, 32)"));
    }

    /** Phase 3: the spires generate only on the outer islands, from existing blocks. */
    @Test
    public void theSpiresStayOnTheOuterIslands() throws IOException {
        String gen = read(MAIN + "common/lib/endgame/WorldGenEndSpires.java");
        assertTrue("outer-island guard", gen.contains("OUTER_ISLAND_CHUNKS = 64"));
        assertTrue("the crown seals a crab spawner",
                gen.contains("CRAB_SPAWNER = 9") && gen.contains("BlockEldritch.TYPE, CRAB_SPAWNER"));
        assertTrue("urns at the base", gen.contains("blockLootUrn"));

        String worldGen = read(MAIN + "common/lib/world/ThaumcraftWorldGenerator.java");
        assertTrue("hooked into the End branch, new chunks only",
                worldGen.contains("WorldGenEndSpires.generate(world, random, chunkX, chunkZ)"));
        String research = read(MAIN + "common/config/ConfigEndLegacy.java");
        assertTrue("the END_SPIRES entry exists", research.contains("\"END_SPIRES\""));
    }

    /** Phase 2-3 lang keys, both languages. */
    @Test
    public void phaseTwoAndThreeSpeakBothLanguages() throws IOException {
        for (String lang : new String[]{"en_us", "ru_ru"}) {
            String file = read("src/main/resources/assets/thaumcraft/lang/" + lang + ".lang");
            for (String key : new String[]{
                    "item.thaumcraft.void_extract.name=", "item.thaumcraft.ward_deflection.name=",
                    "item.thaumcraft.ward_lastbreath.name=", "item.thaumcraft.ward_lastbreath_cracked.name=",
                    "tc.research_page.WARD_ARROWS.1=", "tc.research_page.WARD_LASTBREATH.1=",
                    "tc.research_page.WARD_LASTBREATH.2=", "tc.research_page.END_SPIRES.1=",
                    "tc.research_page.END_SPIRES.2="}) {
                assertTrue(key + " missing from " + lang, file.contains(key));
            }
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
