package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * A block whose blockstate is {@code multipart} has no {@code inventory}
 * variant, so its item cannot resolve through the blockstate. Unless the item
 * model is registered explicitly in {@code ClientProxy}, the item renders as
 * the black-and-magenta placeholder while the placed block looks perfectly
 * normal — which is exactly how the six imbued fires shipped up to 1.1.37.4.
 *
 * <p>The three asset guards written before this one all looked at files and
 * found nothing wrong, because nothing was: the model and its texture were
 * both present. What was missing lived in the Java, not in the resources.</p>
 */
public class MultipartBlockItemModelStaticGuardTest {

    private static final Path BLOCKSTATES =
            Paths.get("src/main/resources/assets/thaumcraft/blockstates");
    private static final Path ITEM_MODELS =
            Paths.get("src/main/resources/assets/thaumcraft/models/item");

    @Test
    public void everyMultipartBlockItemHasItsModelRegistered() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String blocks = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");

        List<String> unregistered = new ArrayList<>();
        try (Stream<Path> files = Files.list(BLOCKSTATES)) {
            for (Path state : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".json"))::iterator) {
                String name = state.getFileName().toString().replace(".json", "");
                String json = new String(Files.readAllBytes(state), StandardCharsets.UTF_8);

                boolean multipart = json.contains("\"multipart\"");
                boolean hasInventoryVariant = json.contains("\"inventory\"");
                if (!multipart || hasInventoryVariant) {
                    continue;
                }
                // Only blocks that actually have an item can show a broken icon.
                if (!blocks.contains("legacyPath(\"" + name + "\")")
                        && !blocks.contains("legacyPath(\"" + name.toUpperCase(java.util.Locale.ROOT) + "\")")) {
                    continue;
                }
                if (!Files.exists(ITEM_MODELS.resolve(name + ".json"))) {
                    continue;
                }
                if (!proxy.contains(name)) {
                    unregistered.add(name);
                }
            }
        }

        assertTrue("these blocks have a multipart blockstate with no inventory variant, so"
                + " their items cannot resolve a model, and nothing in ClientProxy registers"
                + " one for them — they will render as the missing-model placeholder: "
                + unregistered, unregistered.isEmpty());
    }

    /** The fires are the case this guard was written for; keep them wired. */
    @Test
    public void imbuedFiresPointAtTheirItemJson() throws IOException {
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        assertTrue("the item-json registration helper must exist",
                proxy.contains("private static void registerBlockItemModelFromItemJson(Item item)"));
        for (String fire : new String[]{"blockFireAir", "blockFireChaos", "blockFireEarth",
                "blockFireIgnis", "blockFireOrder", "blockFireWater"}) {
            assertTrue(fire + " must be registered", proxy.contains("ConfigBlocks." + fire));
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
