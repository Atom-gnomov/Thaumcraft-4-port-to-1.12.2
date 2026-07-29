package thaumcraft.common.entities.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The shadow beam thrown by the shadowbeam focus — the port of the {@code Beam}
 * class nested inside the original's {@code ItemFocusShadowbeam}.
 *
 * <p>It never joins the world. The focus builds one and calls
 * {@link #updateUntilDead()}, which runs its whole three-hundred-step flight
 * inside a single tick, bouncing off whatever it strikes and hurting whatever
 * it passes through. That is why there is no entity registration for it here:
 * nothing ever needs to track or sync it.</p>
 */
public class EntityShadowbeam extends EntityThrowable {

    private static final int MAX_TICKS = 300;

    private int potency;
    private Vec3d movementVector = Vec3d.ZERO;

    public EntityShadowbeam(World world) {
        super(world);
    }

    public EntityShadowbeam(World world, EntityLivingBase thrower, int potency) {
        super(world, thrower);
        this.potency = potency;
        setProjectileVelocity(this.motionX / 10, this.motionY / 10, this.motionZ / 10);
        this.movementVector = new Vec3d(this.motionX, this.motionY, this.motionZ);
    }

    /** A copy of setVelocity, which is client-only for some reason. */
    public void setProjectileVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(y, f) * 180.0D / Math.PI);
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        float f2 = MathHelper.sqrt(x * x + y * y + z * z);
        x /= f2;
        y /= f2;
        z /= f2;
        x += 0.007499999832361937D * inaccuracy;
        y += 0.007499999832361937D * inaccuracy;
        z += 0.007499999832361937D * inaccuracy;
        x *= velocity;
        y *= velocity;
        z *= velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    /** An entity is hurt and the beam stops; a surface reflects it and it goes on. */
    @Override
    protected void onImpact(RayTraceResult result) {
        if (result == null) {
            return;
        }
        if (result.entityHit != null) {
            boolean pvp = this.world.getMinecraftServer() != null
                    && this.world.getMinecraftServer().isPVPEnabled();
            if ((pvp || !(result.entityHit instanceof EntityPlayer))
                    && result.entityHit != getThrower()
                    && getThrower() instanceof EntityPlayer
                    && !result.entityHit.world.isRemote) {
                result.entityHit.attackEntityFrom(
                        DamageSource.causePlayerDamage((EntityPlayer) getThrower()),
                        8 + this.potency);
            }
            return;
        }

        Vec3d movement = new Vec3d(this.motionX, this.motionY, this.motionZ);
        EnumFacing dir = result.sideHit;
        Vec3d normal = new Vec3d(dir.getXOffset(), dir.getYOffset(), dir.getZOffset()).normalize();
        double scale = -2.0D * movement.dotProduct(normal);
        this.movementVector = new Vec3d(
                normal.x * scale + movement.x,
                normal.y * scale + movement.y,
                normal.z * scale + movement.z);
        this.motionX = this.movementVector.x;
        this.motionY = this.movementVector.y;
        this.motionZ = this.movementVector.z;
    }

    @Override
    public void onUpdate() {
        this.motionX = this.movementVector.x;
        this.motionY = this.movementVector.y;
        this.motionZ = this.movementVector.z;
        super.onUpdate();
        if (this.ticksExisted > 2) {
            thaumcraft.common.Thaumcraft.proxy.sparkle(
                    (float) this.posX, (float) this.posY, (float) this.posZ, 1.5F, 0, 0.0F);
        }
        ++this.ticksExisted;
        if (this.ticksExisted >= MAX_TICKS) {
            setDead();
        }
    }

    /** The whole flight, run inside the tick that cast it. */
    public void updateUntilDead() {
        while (!this.isDead) {
            onUpdate();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
