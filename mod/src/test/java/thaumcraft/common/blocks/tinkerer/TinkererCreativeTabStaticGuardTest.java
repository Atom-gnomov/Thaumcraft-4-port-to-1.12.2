package thaumcraft.common.blocks.tinkerer;

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
 * Every Thaumic Tinkerer block belongs in the Thaumic Tinkerer creative tab.
 *
 * <p>The owner's decision in 1.1.34.0 was that all of the module's content is
 * visible in creative under its own tab, with progression held by research
 * alone. The items were moved then; the <em>blocks</em> were not, and sixteen
 * of them sat at the bottom of the Thaumcraft tab until a player noticed.</p>
 */
public class TinkererCreativeTabStaticGuardTest {

    private static final Path BLOCKS = Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer");

    @Test
    public void noTinkererBlockSitsInTheThaumcraftTab() throws IOException {
        List<String> strays = new ArrayList<>();
        try (Stream<Path> tree = Files.walk(BLOCKS)) {
            for (Path file : (Iterable<Path>) tree.filter(p -> p.toString().endsWith(".java"))::iterator) {
                String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                if (source.contains("setCreativeTab(Thaumcraft.tabTC)")) {
                    strays.add(file.getFileName().toString());
                }
            }
        }
        assertTrue("Thaumic Tinkerer blocks belong in the Tinkerer tab, not Thaumcraft's: "
                + strays, strays.isEmpty());
    }

    /** The category the branch lives under needs a name in both languages. */
    @Test
    public void theTinkererResearchCategoryIsLocalised() throws IOException {
        for (String lang : new String[]{"en_us", "ru_ru"}) {
            String text = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/assets/thaumcraft/lang/" + lang + ".lang")),
                    StandardCharsets.UTF_8);
            assertTrue(lang + " must name the Tinkerer research category, or the"
                    + " Thaumonomicon tab shows the raw key",
                    text.contains("tc.research_category.TT_CATEGORY="));
        }
    }
}
