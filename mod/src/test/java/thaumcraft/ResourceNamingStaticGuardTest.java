package thaumcraft;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Resource paths must be lowercase, and every model must point at a texture
 * that exists.
 *
 * <p>Both failures are silent: since 1.11 a {@code ResourceLocation} with an
 * uppercase character simply never resolves, and a model naming a missing
 * texture draws the black-and-magenta placeholder. Nothing throws, no log line
 * is obviously wrong, and the first anyone hears of it is a player saying an
 * item has no texture — which is how {@code ichorGem1.png} survived on the
 * awakened ichorcloth armour until 1.1.37.3.</p>
 */
public class ResourceNamingStaticGuardTest {

    private static final Path ASSETS = Paths.get("src/main/resources/assets/thaumcraft");

    /**
     * Carried over from Azanor's own jar, uppercase and all. The block texture
     * corpus test pins their presence, so they cannot be renamed or dropped —
     * they are simply never addressed as a ResourceLocation. Sounds are exempt
     * as a class: sounds.json addresses them by name, not by path.
     */
    private static final List<String> ORIGINAL_CORPUS_EXCEPTIONS = java.util.Arrays.asList(
            "textures/blocks/es_i_1 - Copy.png",
            "textures/models/Thaumaturge_eyes.png");

    @Test
    public void everyResourceFileIsLowercase() throws IOException {
        List<String> offenders = new ArrayList<>();
        try (Stream<Path> files = Files.walk(ASSETS)) {
            files.filter(Files::isRegularFile).forEach(path -> {
                String name = ASSETS.relativize(path).toString().replace('\\', '/');
                if (name.startsWith("sounds/") || ORIGINAL_CORPUS_EXCEPTIONS.contains(name)) {
                    return;
                }
                if (!name.equals(name.toLowerCase(java.util.Locale.ROOT))) {
                    offenders.add(name);
                }
            });
        }
        assertTrue("these resource paths have uppercase characters and will never"
                + " resolve as a ResourceLocation: " + offenders, offenders.isEmpty());
    }

    @Test
    public void everyModelTextureExists() throws IOException {
        List<String> broken = new ArrayList<>();
        for (String dir : new String[]{"models/item", "models/block"}) {
            Path folder = ASSETS.resolve(dir);
            if (!Files.isDirectory(folder)) {
                continue;
            }
            try (Stream<Path> models = Files.list(folder)) {
                models.filter(p -> p.toString().endsWith(".json")).forEach(model -> {
                    String json;
                    try {
                        json = new String(Files.readAllBytes(model), java.nio.charset.StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        broken.add(model + " (unreadable)");
                        return;
                    }
                    int t = json.indexOf("\"textures\"");
                    if (t < 0) {
                        return;
                    }
                    int end = json.indexOf('}', t);
                    String block = end < 0 ? json.substring(t) : json.substring(t, end);
                    java.util.regex.Matcher m = java.util.regex.Pattern
                            .compile("\"thaumcraft:([a-z0-9_/]+)\"").matcher(block);
                    while (m.find()) {
                        String ref = m.group(1);
                        if (ref.startsWith("block/") || ref.startsWith("item/")) {
                            continue; // a parent model, not a texture
                        }
                        if (!Files.exists(ASSETS.resolve("textures/" + ref + ".png"))) {
                            broken.add(ASSETS.relativize(model) + " -> " + ref);
                        }
                    }
                });
            }
        }
        assertTrue("these models name a texture that does not ship: " + broken, broken.isEmpty());
    }
}
