package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Витрина рецепта «Вознесение» обязана показывать нагрудник, УЖЕ несущий
 * Парение.
 *
 * <p>Вознесение — апгрейд: {@code InfusionWingsRecipe.matches} требует
 * {@code getTier(central) == tier - 1}, то есть для яруса 2 в центре должен
 * лежать нагрудник яруса 1. Пока витрина показывала голый таумиевый
 * нагрудник, игрок ставил в центр именно его, алтарь молча отказывался
 * запускаться, и узнать причину из книги было нельзя — единственной
 * подсказкой был исходный код.</p>
 */
public class EndLegacyWingsDisplayStaticGuardTest {

    @Test
    public void ascensionRecipeShouldDisplayAlreadySoaringChestplate() throws IOException {
        String source = read("src/main/java/thaumcraft/common/config/ConfigEndLegacy.java");

        assertTrue("витрина Вознесения строится отдельным стеком",
                source.contains("ItemStack ascensionDisplayInput = new ItemStack(ConfigItems.itemChestThaumium);"));
        assertTrue("и помечается тегом крыльев яруса Парения",
                source.contains("ascensionDisplayInput.setTagInfo(")
                        && source.contains("SoaringHandler.TAG_WINGS")
                        && source.contains("SoaringHandler.TIER_SOARING)"));
        assertTrue("рецепт Вознесения получает именно этот стек",
                source.contains("ascensionDisplayInput,"));
    }

    @Test
    public void ascensionPageShouldSpellOutTheUpgradeRule() throws IOException {
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("русская страница объясняет, что нужен нагрудник с Парением",
                ru.contains("уже несущий Парение"));
        assertTrue("английская страница объясняет то же",
                en.contains("already carrying Soaring"));
    }

    /** Парение — первый ярус: его витрина, наоборот, должна быть без тега. */
    @Test
    public void soaringRecipeShouldStillDisplayBareChestplate() throws IOException {
        String source = read("src/main/java/thaumcraft/common/config/ConfigEndLegacy.java");
        int soaring = source.indexOf("\"SOARING\"");
        int ascension = source.indexOf("ItemStack ascensionDisplayInput");
        assertTrue("оба рецепта на месте", soaring > 0 && ascension > soaring);
        String soaringBlock = source.substring(soaring, ascension);
        assertTrue("у Парения в центре — обычный нагрудник",
                soaringBlock.contains("new ItemStack(ConfigItems.itemChestThaumium)"));
        assertTrue("и никакого тега крыльев ему не навешивается",
                !soaringBlock.contains("setTagInfo"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
