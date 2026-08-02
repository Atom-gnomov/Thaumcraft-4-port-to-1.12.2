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

    /**
     * The boost is the firework rocket's formula verbatim, so it must do what
     * the rocket does: converge on 1.5 blocks/tick along the look vector.
     */
    @Test
    public void theBoostConvergesOnTheFireworkTarget() {
        double[] motion = {0.0D, 0.0D, 0.0D};
        double[] up = {0.0D, 1.0D, 0.0D};
        for (int tick = 0; tick < 40; tick++) {
            motion = SoaringPhysics.boost(motion, up);
        }
        assertEquals("sustained ascent settles at the rocket's own ceiling",
                SoaringPhysics.BOOST_TARGET + SoaringPhysics.BOOST_ADD * 2.0D,
                motion[1], 0.01D);
        assertEquals(0.0D, motion[0], 1.0E-9D);
    }

    /** One tick from rest matches vanilla's first firework tick exactly. */
    @Test
    public void theFirstBoostTickIsVanillas() {
        double[] motion = SoaringPhysics.boost(
                new double[]{0.0D, 0.0D, 0.0D}, new double[]{1.0D, 0.0D, 0.0D});
        // 0 + 1*0.1 + (1*1.5 - 0)*0.5 = 0.85 — the rocket's opening kick.
        assertEquals(0.85D, motion[0], 1.0E-9D);
    }

    /** The launch clears real ground obstacles; the fuel maths stays whole. */
    @Test
    public void theDesignConstantsHoldTheirShape() {
        assertTrue("the launch must out-jump a vanilla jump (0.42)",
                SoaringPhysics.LAUNCH_IMPULSE > 0.42D);
        assertTrue("a point of vis buys a usable stretch of ascent",
                SoaringPhysics.THRUST_TICKS_PER_VIS_POINT >= 20);
        assertTrue("the glide is gentler than an elytra's steepest dive",
                SoaringPhysics.GLIDE_FALL_CAP > -0.5D);
    }
}
