package thaumcraft.common.items.tinkerer.kami.tool;

/**
 * Marks an awakened ichorium tool and names its kind — ported from Thaumic
 * Tinkerer's {@code IAdvancedTool} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>The type string is what {@code ttmisc.mode.<type>.<n>} is keyed on, and
 * what the Protoclay matches against the block being struck to decide which
 * tool to swap into the hand.</p>
 */
public interface IAdvancedTool {

    /** One of {@code pick}, {@code shovel}, {@code axe}, {@code sword}. */
    String getType();
}
