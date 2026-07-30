package thaumcraft.common.tiles.tinkerer;

import net.minecraft.block.state.IBlockState;
import thaumcraft.api.TileThaumcraft;

/**
 * Common base for the Thaumic Tinkerer tiles — the port of upstream's
 * {@code TileMod} (pixlepix / nekosune, the pattern originally Vazkii's).
 *
 * <p>Upstream's version is four lines long and easy to overlook, which is
 * precisely why it kept getting lost in the port:</p>
 *
 * <pre>
 * public void markDirty() {
 *     super.markDirty();
 *     worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
 * }
 * </pre>
 *
 * <p>Every Thaumic Tinkerer tile inherits it, and a great deal of the module
 * quietly depends on it. Vanilla {@code markDirty} only flags the chunk for
 * saving — it sends nothing. The screens in this module read their state
 * straight off the client's copy of the tile (the queue in the enchanter, the
 * mode in the magnets, the tool in the tablet), so without this override the
 * client's copy is whatever arrived when the chunk loaded and never moves
 * again. The symptom is not a glitch but a dead device: the server does the
 * work and the player sees none of it.</p>
 *
 * <p>Thaumcraft's own {@link TileThaumcraft} deliberately does <em>not</em> do
 * this, and must not be made to — its tiles are jars, tubes and crucibles that
 * tick constantly, and upstream Thaumcraft syncs them by hand where it wants
 * to. The split lives here instead, exactly where upstream drew it.</p>
 */
public class TileTinkerer extends TileThaumcraft {

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isRemote) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }
}
