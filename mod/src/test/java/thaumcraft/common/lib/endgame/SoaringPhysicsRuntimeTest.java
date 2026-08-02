package thaumcraft.common.lib.endgame;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The End Legacy flight numbers, exercised as maths — no world, no player.
 * The design being pinned is {@code END_LEGACY_PLAN.md} §2 (owner-approved,
 * no 1.7.10 original to defer to).
 */
public class SoaringPhysicsRuntimeTest {

    /** A glide never falls faster than the cap, and never slows an ascent. */
    @Test
    public void theGlideCapsTheFallAndLeavesRisingAlone() {
        assertEquals(SoaringPhysics.GLIDE_FALL_CAP, SoaringPhysics.glideMotionY(-1.5D), 1.0E-9D);
        assertEquals(SoaringPhysics.GLIDE_FALL_CAP, SoaringPhysics.glideMotionY(-0.12000001D), 1.0E-9D);
        assertEquals(-0.05D, SoaringPhysics.glideMotionY(-0.05D), 1.0E-9D);
        assertEquals(0.4D, SoaringPhysics.glideMotionY(0.4D), 1.0E-9D);
    }

    /** Gliding needs to actually be a fall: airborne, descending, hands-off. */
    @Test
    public void glidingRequiresAnAirborneDescent() {
        assertTrue(SoaringPhysics.isGlidingFall(-0.3D, false, false, false));
        assertFalse("on the ground is not a glide",
                SoaringPhysics.isGlidingFall(-0.3D, true, false, false));
        assertFalse("sneaking opts out — that is the dive",
                SoaringPhysics.isGlidingFall(-0.3D, false, true, false));
        assertFalse("water is swimming, not flying",
                SoaringPhysics.isGlidingFall(-0.3D, false, false, true));
        assertFalse("rising is the thrust's business",
                SoaringPhysics.isGlidingFall(0.1D, false, false, false));
    }

    /** Thrust accelerates along the look vector and hits a hard speed cap. */
    @Test
    public void thrustAcceleratesToTheCapAndNotPastIt() {
        double[] motion = {0.0D, 0.0D, 0.0D};
        double[] look = {1.0D, 0.0D, 0.0D};

        motion = SoaringPhysics.thrust(motion, look);
        assertEquals(SoaringPhysics.THRUST_ACCEL, motion[0], 1.0E-9D);

        for (int tick = 0; tick < 200; tick++) {
            motion = SoaringPhysics.thrust(motion, look);
            double speed = Math.sqrt(motion[0] * motion[0] + motion[1] * motion[1] + motion[2] * motion[2]);
            assertTrue("speed " + speed + " exceeded the cap at tick " + tick,
                    speed <= SoaringPhysics.THRUST_SPEED_CAP + 1.0E-6D);
        }
        assertEquals("sustained thrust settles at the cap",
                SoaringPhysics.THRUST_SPEED_CAP, Math.abs(motion[0]), 1.0E-6D);
    }

    /** At the cap the player can still steer: thrust turns the vector, not refuses it. */
    @Test
    public void thrustAtTheCapStillTurns() {
        double[] motion = {SoaringPhysics.THRUST_SPEED_CAP, 0.0D, 0.0D};
        double[] up = {0.0D, 1.0D, 0.0D};

        double[] turned = SoaringPhysics.thrust(motion, up);
        assertTrue("the vertical component must grow when thrusting upward",
                turned[1] > 0.0D);
        double speed = Math.sqrt(turned[0] * turned[0] + turned[1] * turned[1] + turned[2] * turned[2]);
        assertTrue(speed <= SoaringPhysics.THRUST_SPEED_CAP + 1.0E-6D);
    }

    /** The launch clears real ground obstacles; the fuel maths stays whole. */
    @Test
    public void theDesignConstantsHoldTheirShape() {
        assertTrue("the launch must out-jump a vanilla jump (0.42)",
                SoaringPhysics.LAUNCH_IMPULSE > 0.42D);
        assertTrue("a point of vis buys a usable stretch of thrust",
                SoaringPhysics.THRUST_TICKS_PER_VIS_POINT >= 20);
        assertTrue("the glide is gentler than an elytra's steepest dive",
                SoaringPhysics.GLIDE_FALL_CAP > -0.5D);
    }
}
