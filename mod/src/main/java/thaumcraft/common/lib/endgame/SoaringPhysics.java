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
    /** Thrust acceleration along the look vector, per tick. */
    public static final double THRUST_ACCEL = 0.06D;
    /** Speed cap under thrust, blocks per tick. */
    public static final double THRUST_SPEED_CAP = 1.6D;
    /** One point of Aer buys this many ticks of thrust. */
    public static final int THRUST_TICKS_PER_VIS_POINT = 25;

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
     * One axis of thrust: current motion plus acceleration along the look
     * vector, clamped so the total never passes the cap. The clamp scales the
     * <em>new</em> vector rather than refusing the tick — thrust at the cap
     * still lets the player turn.
     */
    public static double[] thrust(double[] motion, double[] look) {
        double mx = motion[0] + look[0] * THRUST_ACCEL;
        double my = motion[1] + look[1] * THRUST_ACCEL;
        double mz = motion[2] + look[2] * THRUST_ACCEL;
        double speed = Math.sqrt(mx * mx + my * my + mz * mz);
        if (speed > THRUST_SPEED_CAP) {
            double scale = THRUST_SPEED_CAP / speed;
            mx *= scale;
            my *= scale;
            mz *= scale;
        }
        return new double[]{mx, my, mz};
    }
}
