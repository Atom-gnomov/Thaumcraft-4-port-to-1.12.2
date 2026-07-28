package thaumcraft.common.items.tinkerer;

import java.util.Arrays;
import java.util.List;
import thaumcraft.api.aspects.Aspect;

/**
 * The eleven aspects a Soul Aspect can carry, numbered — ported from Thaumic
 * Tinkerer's {@code NumericAspectHelper} (pixlepix / nekosune, originally
 * Vazkii).
 *
 * <p><b>The order is item metadata and must not change.</b> Upstream built it
 * by constructing eleven helpers in sequence and taking the construction order
 * as the number; that sequence is the list below.</p>
 *
 * <p>The stride between tiers is {@value #TIER_STRIDE} rather than eleven, and
 * upstream says why in a comment: the real count is smaller, and the gap is
 * padding left so that adding an aspect later would not shift every existing
 * item's metadata. Kept for the same reason, and because saved worlds depend
 * on it.</p>
 */
public final class SoulAspects {

    /** Upstream's {@code aspectCount}: the spacing between tiers, not the count. */
    public static final int TIER_STRIDE = 20;

    /** Construction order upstream, which is the metadata. */
    private static final List<Aspect> ORDER = Arrays.asList(
            Aspect.FIRE, Aspect.MAGIC, Aspect.UNDEAD, Aspect.FLESH, Aspect.BEAST,
            Aspect.POISON, Aspect.EARTH, Aspect.ELDRITCH, Aspect.TRAVEL, Aspect.METAL,
            Aspect.SLIME);

    private SoulAspects() {
    }

    public static int count() {
        return ORDER.size();
    }

    public static List<Aspect> all() {
        return ORDER;
    }

    /** The number upstream assigned, or -1 for an aspect it never numbered. */
    public static int numberOf(Aspect aspect) {
        return ORDER.indexOf(aspect);
    }

    public static Aspect byNumber(int number) {
        return number >= 0 && number < ORDER.size() ? ORDER.get(number) : null;
    }
}
