package thaumcraft.common.items.tinkerer;

import java.util.Arrays;
import java.util.List;
import thaumcraft.api.aspects.Aspect;

/**
 * The fifteen aspects a Soul Aspect can carry, numbered — ported from Thaumic
 * Tinkerer's {@code NumericAspectHelper} (pixlepix / nekosune, originally
 * Vazkii).
 *
 * <p><b>The order is item metadata.</b> Upstream builds it by constructing
 * fifteen helpers in sequence and taking the construction order as the number;
 * that sequence is the list below, copied whole.</p>
 *
 * <p>Until 1.1.38.4 this list began at {@code FIRE} and held eleven entries —
 * upstream's first four, WATER, MAN, AIR and FLIGHT, had been dropped and every
 * remaining aspect sat four numbers below its upstream value. Restoring them
 * shifts the metadata of the existing eleven back to where upstream puts it, so
 * soul aspects already sitting in a world will read as a different aspect. They
 * are KAMI-tier, so few worlds hold any; correctness against upstream was
 * chosen over that.</p>
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

    /** Construction order upstream, which is the metadata. Do not reorder. */
    private static final List<Aspect> ORDER = Arrays.asList(
            Aspect.WATER, Aspect.MAN, Aspect.AIR, Aspect.FLIGHT, Aspect.FIRE,
            Aspect.MAGIC, Aspect.UNDEAD, Aspect.FLESH, Aspect.BEAST, Aspect.POISON,
            Aspect.EARTH, Aspect.ELDRITCH, Aspect.TRAVEL, Aspect.METAL, Aspect.SLIME);

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
