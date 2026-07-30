package thaumcraft.common.tiles.tinkerer.kami;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * The warp gate showed nothing and did nothing, and the cause was neither the
 * screen nor the tile's logic — both were transcribed faithfully. It was that
 * the tile never told the client anything.
 *
 * <p>Everything the gate presents is drawn from the <em>client's</em> copy:
 * the destination map places its markers by reading the pearls out of the tile's
 * slots, and the lock button reads {@code locked}. With no sync that copy stays
 * as constructed — ten empty slots — so the map is blank however many pearls
 * are really inside, and there is nothing to click. The port carried
 * {@code writeCustomNBT} across from upstream and never wired it to a packet.</p>
 */
public class TileWarpGateSyncStaticGuardTest {

    @Test
    public void theGateTellsTheClientWhatItHolds() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/kami/TileWarpGate.java");

        assertTrue("the chunk-load path must carry the gate's contents",
                tile.contains("public NBTTagCompound getUpdateTag()")
                        && tile.contains("handleUpdateTag"));
        assertTrue("and so must the live update packet",
                tile.contains("getUpdatePacket()") && tile.contains("onDataPacket("));
        assertTrue("both must go through writeCustomNBT/readCustomNBT rather than a"
                + " second, drifting copy of the format",
                tile.contains("writeCustomNBT(cmp)") && tile.contains("readCustomNBT("));
    }

    /**
     * Slot changes arrive through {@code markDirty}. Without a resync hung
     * there, the map would only catch up on chunk reload — pearls would go in
     * and the destination list would not change.
     */
    @Test
    public void changingTheContentsResyncs() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/kami/TileWarpGate.java");
        assertTrue("markDirty must push the new state to watching clients",
                tile.contains("public void markDirty()")
                        && tile.contains("notifyBlockUpdate"));
        assertTrue("and only from the server, or the client would fight its own copy",
                tile.contains("!this.world.isRemote"));
    }

    /** The destination map reads the client tile directly; that is why the sync matters. */
    @Test
    public void theDestinationMapReadsTheClientTile() throws IOException {
        String gui = read("src/main/java/thaumcraft/client/gui/tinkerer/GuiWarpGateDestinations.java");
        assertTrue("markers come from the tile's own slots",
                gui.contains("this.warpGate.getStackInSlot(i)"));
        assertTrue("and only attuned pearls are placed",
                gui.contains("ItemSkyPearl.isAttuned(stack)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
