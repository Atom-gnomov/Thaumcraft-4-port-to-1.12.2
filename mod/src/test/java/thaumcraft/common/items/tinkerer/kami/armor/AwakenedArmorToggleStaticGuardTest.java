package thaumcraft.common.items.tinkerer.kami.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * All four awakened ichorcloth entries end with the same sentence — "pressing U
 * will toggle this armor's effects" — and the port had no such key at all. The
 * armour has two switches upstream, and only the per-piece one had been carried
 * across.
 *
 * <p>This guard pins the whole chain, because any one link missing leaves a key
 * that presses and does nothing: the binding, the packet that carries it to the
 * side that applies the effects, the stored flag, and — the part that was
 * actually absent — {@code isActive} consulting it.</p>
 */
public class AwakenedArmorToggleStaticGuardTest {

    @Test
    public void isActiveConsultsBothSwitches() throws IOException {
        String base = read("src/main/java/thaumcraft/common/items/tinkerer/kami/armor/"
                + "ItemIchorclothArmorAdv.java");
        assertTrue("the per-piece switch is the item damage, flipped by sneak-right-click",
                base.contains("worn.getItemDamage() == 0"));
        assertTrue("and the player-wide switch is the one behind the U key —"
                + " without this the key does nothing at all",
                base.contains("KamiArmorHandler.getArmorStatus(player)"));
    }

    @Test
    public void theKeyIsBoundAndReachesTheServer() throws IOException {
        String keys = read("src/main/java/thaumcraft/client/lib/KeyHandler.java");
        assertTrue("U must be bound", keys.contains("Keyboard.KEY_U"));
        assertTrue("under upstream's own label", keys.contains("\"ttmisc.toggleArmor\""));
        assertTrue("it only fires while awakened armour is worn",
                keys.contains("wearsAwakenedIchorcloth(player)"));
        assertTrue("the flag has to cross to the server, which is where effects apply",
                keys.contains("PacketToggleArmor(enabled)"));

        assertTrue("and the packet must be registered",
                read("src/main/java/thaumcraft/common/lib/network/PacketHandler.java")
                        .contains("register(PacketToggleArmor.class"));
    }

    /** Absent means on, or armour would be dead for anyone who never pressed the key. */
    @Test
    public void theStoredFlagDefaultsToOn() throws IOException {
        String handler = read("src/main/java/thaumcraft/common/lib/tinkerer/KamiArmorHandler.java");
        assertTrue("a player who never pressed U must have working armour",
                handler.contains("!cmp.hasKey(TAG_STATUS) || cmp.getBoolean(TAG_STATUS)"));
        assertTrue("the client keeps its own copy, since the tick code runs on both sides",
                handler.contains("getClientStatus()"));
    }

    /** Both strings upstream shows on the toggle, in both languages. */
    @Test
    public void theToggleIsLocalised() throws IOException {
        for (String lang : new String[]{"en_us", "ru_ru"}) {
            String text = read("src/main/resources/assets/thaumcraft/lang/" + lang + ".lang");
            for (String key : new String[]{"ttmisc.toggleArmor=", "ttmisc.enableAllArmor=",
                    "ttmisc.disableAllArmor="}) {
                assertTrue(lang + " is missing " + key, text.contains(key));
            }
        }
    }

    /**
     * 1.7.10 had no attack-speed stat and no swing cooldown, so the ichor sword
     * hit as fast as you could click. Carrying it to 1.12 unchanged silently
     * applied vanilla's sword penalty — a restriction the original never had.
     * Owner's decision of 2026-07-30 to drop it.
     */
    @Test
    public void theIchorSwordCarriesNoAttackSpeedPenalty() throws IOException {
        String sword = read("src/main/java/thaumcraft/common/items/tinkerer/kami/tool/"
                + "ItemIchorSword.java");
        assertTrue("the sword must strip the attack speed modifier",
                sword.contains("SharedMonsterAttributes.ATTACK_SPEED.getName()"));
        assertTrue("and say why, since it is a deliberate deviation",
                sword.contains("Owner's decision"));
    }

    private static String read(String path) throws IOException {
        Path file = Paths.get(path);
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }
}
