package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * A block item resolves its model through
 * {@code ModelResourceLocation(registryName, "inventory")}. If the blockstate
 * declares no {@code inventory} variant and nothing registers a model
 * explicitly, the item shows the missing-model placeholder while the placed
 * block looks perfectly normal.
 *
 * <p>{@link MultipartBlockItemModelStaticGuardTest} caught this for multipart
 * blockstates after the six imbued fires shipped broken. That guard was too
 * narrow: the trap has nothing to do with multipart. Any blockstate without an
 * inventory variant falls into it, and seventeen more blocks were sitting in it
 * the whole time — reported from the game, again, not from here.</p>
 */
public class BlockItemModelCoverageStaticGuardTest {

    private static final Path BLOCKSTATES =
            Paths.get("src/main/resources/assets/thaumcraft/blockstates");
    private static final Path ITEM_MODELS =
            Paths.get("src/main/resources/assets/thaumcraft/models/item");

    @Test
    public void everyBlockItemCanResolveAModel() throws IOException {
        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        String config = read(Paths.get("src/main/java/thaumcraft/common/config/ConfigBlocks.java"));

        List<String> unresolvable = new ArrayList<>();
        Matcher blocks = Pattern.compile("legacyPath\\(\"([A-Za-z0-9_]+)\"\\)").matcher(config);
        while (blocks.find()) {
            String name = blocks.group(1);
            String lower = name.toLowerCase(Locale.ROOT);
            // The field a block is held in need not be named after its registry
            // path — the cosmetic slabs register as blockCosmeticSlabWood but
            // live in blockSlabWood — and ClientProxy refers to the field. Look
            // back for the assignment this registration belongs to.
            String field = fieldHolding(config, blocks.start());

            Path state = BLOCKSTATES.resolve(lower + ".json");
            if (!Files.exists(state)) {
                continue;
            }
            if (read(state).contains("\"inventory\"")) {
                continue;   // the blockstate answers for the item itself
            }
            if (proxy.contains(name) || proxy.contains(lower)
                    || (field != null && proxy.contains("ConfigBlocks." + field))) {
                continue;   // ClientProxy points the item somewhere explicitly
            }
            if (!Files.exists(ITEM_MODELS.resolve(lower + ".json"))) {
                // No item json either. These are the effect blocks — gases, the
                // forcefield — which carry no creative tab and never show an icon.
                continue;
            }
            unresolvable.add(name);
        }

        assertTrue("these blocks have an item json and a blockstate with no inventory"
                + " variant, and nothing registers a model for them, so the item renders"
                + " as the missing-model placeholder while the placed block looks fine: "
                + unresolvable, unresolvable.isEmpty());
    }

    /**
     * The static field the registration at {@code offset} is being assigned to,
     * found by walking back to the nearest preceding assignment.
     */
    private static String fieldHolding(String config, int offset) {
        Matcher assignments = Pattern.compile("(?m)^\\s{8}([a-z][A-Za-z0-9_]*)\\s*=").matcher(config);
        String nearest = null;
        while (assignments.find() && assignments.start() < offset) {
            nearest = assignments.group(1);
        }
        return nearest;
    }

    /**
     * The first test's blind spot, closed after four blocks shipped checkered
     * for months: an {@code inventory} variant in the blockstate is only used
     * if something registers the item against it — the variant alone resolves
     * nothing. So, mechanically: every block that gets an ItemBlock must be
     * mentioned by ClientProxy's model registration, no exceptions, no
     * variant-based excuses. This is the check that found the mobilizer, its
     * relay, the platform and the summoning tablet in one sweep.
     */
    @Test
    public void everyItemBlockIsMentionedByTheModelRegistration() throws IOException {
        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        String config = read(Paths.get("src/main/java/thaumcraft/common/config/ConfigBlocks.java"));

        java.util.Set<String> withItems = new java.util.TreeSet<>();
        Matcher itemBlocks = Pattern.compile(
                "new (?:net\\.minecraft\\.item\\.)?ItemBlock[A-Za-z]*\\((block[A-Za-z0-9]+)").matcher(config);
        while (itemBlocks.find()) {
            withItems.add(itemBlocks.group(1));
        }
        Matcher customItems = Pattern.compile("new [A-Za-z]+Item\\((block[A-Za-z0-9]+)\\)").matcher(config);
        while (customItems.find()) {
            withItems.add(customItems.group(1));
        }

        List<String> unmentioned = new ArrayList<>();
        for (String field : withItems) {
            if (!proxy.contains("ConfigBlocks." + field)) {
                unmentioned.add(field);
            }
        }
        assertTrue("these blocks have items but no model registration ever touches them"
                        + " — they are checkered in every inventory: " + unmentioned,
                unmentioned.isEmpty());
    }

    /** The urn has three vases; registering meta 0 alone left two of them checkered. */
    @Test
    public void everyVaseOfTheUrnHasItsModel() throws IOException {
        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        assertTrue("all three urn subtypes must be registered against their type variants",
                proxy.contains("registerBlockItemModel(lootUrnItem, meta, \"type=\" + meta)"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
