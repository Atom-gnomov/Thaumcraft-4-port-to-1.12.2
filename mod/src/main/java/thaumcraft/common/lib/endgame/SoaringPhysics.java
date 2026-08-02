package thaumcraft.common.lib.endgame;

/**
 * The numbers behind Soaring and Ascension, kept as pure functions so the
 * runtime test can fly without a player or a world.
 *
 * <p>End Legacy module — new content, no 1.7.10 original; the numbers are the
 * owner-approved design in {@code END_LEGACY_PLAN.md} §2. The intended feel:
 * gliding is a paraglider (gentler than an elytra, but it works off a jump and
 * over any armour), thrust is honest flight paid in Aer.</p>
 */
public final class SoaringPhysics {

    /** A glide never falls faster than this. */
    public static final double GLIDE_FALL_CAP = -0.12D;
    /** Horizontal pull toward where the player looks, per tick of gliding. */
    public static final double GLIDE_DRIFT = 0.035D;
    /** Ground launch: the vertical impulse Ascension gives a held jump. */
    public static final double LAUNCH_IMPULSE = 0.9D;
    /** One point of Aer buys this many ticks of powered ascent. */
    public static final int THRUST_TICKS_PER_VIS_POINT = 25;
    /** The firework rocket's own boost numbers — vanilla's, verbatim. */
    public static final double BOOST_ADD = 0.1D;
    public static final double BOOST_TARGET = 1.5D;
    public static final double BOOST_PULL = 0.5D;

    private SoaringPhysics() {
    }

    /** The glide's vertical component: the fall, softened to the cap. */
    public static double glideMotionY(double motionY) {
        return Math.max(motionY, GLIDE_FALL_CAP);
    }

    /** Whether this tick counts as gliding at all: airborne and going down. */
    public static boolean isGlidingFall(double motionY, boolean onGround, boolean sneaking, boolean inLiquid) {
        return !onGround && !sneaking && !inLiquid && motionY < 0.0D;
    }

    /**
     * One axis of the forward drift: the look direction's horizontal component,
     * normalised outside, scaled to the drift rate.
     */
    public static double glideDrift(double lookAxisNormalised) {
        return lookAxisNormalised * GLIDE_DRIFT;
    }

    /**
     * One tick of powered ascent while elytra-flying: the vanilla firework
     * rocket's formula, verbatim — accelerate along the look vector and pull
     * hard toward 1.5 blocks/tick in that direction. Point the nose up and the
     * armour climbs; that is the whole "fly upward for vis" mechanic, using
     * the aerodynamics the player already knows.
     */
    public static double[] boost(double[] motion, double[] look) {
        return new double[]{
                motion[0] + look[0] * BOOST_ADD + (look[0] * BOOST_TARGET - motion[0]) * BOOST_PULL,
                motion[1] + look[1] * BOOST_ADD + (look[1] * BOOST_TARGET - motion[1]) * BOOST_PULL,
                motion[2] + look[2] * BOOST_ADD + (look[2] * BOOST_TARGET - motion[2]) * BOOST_PULL,
        };
    }
}
