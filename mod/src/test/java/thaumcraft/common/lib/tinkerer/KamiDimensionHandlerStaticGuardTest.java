package thaumcraft.common.lib.tinkerer;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Two features that were ported as far as the code that does the work and no
 * further, so neither could ever run. Both were reported from the game.
 */
public class KamiDimensionHandlerStaticGuardTest {

    /**
     * The Awakened Ichor Pickaxe turns bedrock into the Bedrock dimension's
     * portal inside {@code onBlockStartBreak}. Nothing calls it there: bedrock's
     * hardness is {@code -1}, so {@code ForgeHooks.blockStrength} returns zero,
     * the break never completes, and {@code tryHarvestBlock} — the only caller
     * of {@code onBlockStartBreak} — is never reached.
     *
     * <p>Upstream's answer is a left-click handler that calls the hook by hand.
     * The port carried the hook and left the handler behind, so striking bedrock
     * did nothing whatsoever.</p>
     */
    @Test
    public void strikingBedrockReachesThePicksHook() throws IOException {
        Path handler = Paths.get(
                "src/main/java/thaumcraft/common/lib/tinkerer/KamiDimensionHandler.java");
        assertTrue("the handler is the only thing that can invoke the pick's hook on an"
                + " unbreakable block", Files.exists(handler));

        String source = read(handler);
        assertTrue("it watches the left click", source.contains("PlayerInteractEvent.LeftClickBlock"));
        assertTrue("only for bedrock", source.contains("Blocks.BEDROCK"));
        assertTrue("only for the awakened pick", source.contains("instanceof ItemIchorPickAdv"));
        assertTrue("and it calls the hook by hand", source.contains("onBlockStartBreak("));

        assertTrue("and the handler has to be on the event bus, or it is just a file",
                read(Paths.get("src/main/java/thaumcraft/common/Thaumcraft.java"))
                        .contains("new thaumcraft.common.lib.tinkerer.KamiDimensionHandler()"));

        assertTrue("the hook it reaches still has to open the portal",
                read(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/kami/tool/"
                        + "KamiToolHandler.java")).contains("blockBedrockPortal"));
    }

    /**
     * The Soul Mould records a creature by writing NBT. It has to write through
     * the player, because {@code EntityPlayer.interactOn} swaps in a copy of the
     * held stack in creative mode — write to the argument and the pattern is
     * discarded the moment the method returns. Upstream writes to
     * {@code getCurrentEquippedItem()} for exactly this reason.
     */
    @Test
    public void theSoulMouldWritesThroughThePlayerNotTheArgument() throws IOException {
        String mould = read(Paths.get(
                "src/main/java/thaumcraft/common/items/tinkerer/ItemSoulMould.java"));
        assertTrue("the pattern must be written to the held stack",
                mould.contains("ItemStack held = player.getHeldItem(hand);")
                        && mould.contains("setPattern(held.isEmpty() ? stack : held,"));
        assertTrue("recording must not be skipped on the client, or creative mode never records",
                !mould.contains("if (player.world.isRemote || target instanceof EntityPlayer)"));
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
