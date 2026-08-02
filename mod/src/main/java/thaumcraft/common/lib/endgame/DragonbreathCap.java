package thaumcraft.common.lib.endgame;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * The Dragonbreath Cap's exhale — End Legacy module (new content, no 1.7.10
 * original; owner's design, 2026-08-02).
 *
 * <p>When a cast <em>begins</em> with a dragonbreath-capped wand, the cap
 * rolls one die across the six primals and breathes that aspect out around
 * the caster. The mapping is the owner's, verbatim:</p>
 *
 * <pre>
 * огонь    — горение      (fire:    ignite)
 * вода     — утомление    (water:   mining fatigue)
 * воздух   — замедление   (air:     slowness)
 * земля    — слабость     (earth:   weakness)
 * порядок  — слепота      (order:   blindness)
 * энтропия — урон         (entropy: harm)
 * </pre>
 *
 * <p>The breath touches every living thing within {@link #RADIUS} of the
 * caster except the caster — a dragon does not scorch its own throat.</p>
 */
public final class DragonbreathCap {

    public static final String TAG = "dragonbreath";
    public static final double RADIUS = 3.0D;
    /** Ten seconds, in ticks — the length of every non-instant exhale. */
    public static final int EFFECT_TICKS = 200;
    public static final int FIRE_SECONDS = 5;
    public static final float HARM_DAMAGE = 4.0F;

    private DragonbreathCap() {
    }

    /** Rolls the die and breathes. Server-side; the roll happens once per cast start. */
    public static void exhale(World world, EntityPlayer caster) {
        if (world.isRemote) {
            return;
        }
        int roll = world.rand.nextInt(6);

        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.DRAGON_BREATH,
                    caster.posX, caster.posY + caster.getEyeHeight() * 0.6D, caster.posZ,
                    24, RADIUS * 0.5D, 0.4D, RADIUS * 0.5D, 0.02D);
        }

        List<EntityLivingBase> victims = world.getEntitiesWithinAABB(EntityLivingBase.class,
                caster.getEntityBoundingBox().grow(RADIUS));
        for (EntityLivingBase victim : victims) {
            if (victim == caster) {
                continue;
            }
            breatheOn(caster, victim, roll);
        }
    }

    /** One victim, one aspect — the owner's table, in primal order. */
    static void breatheOn(EntityPlayer caster, EntityLivingBase victim, int roll) {
        switch (roll) {
            case 0:   // огонь — горение
                victim.setFire(FIRE_SECONDS);
                break;
            case 1:   // вода — утомление
                victim.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, EFFECT_TICKS, 1));
                break;
            case 2:   // воздух — замедление
                victim.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, EFFECT_TICKS, 1));
                break;
            case 3:   // земля — слабость
                victim.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_TICKS, 1));
                break;
            case 4:   // порядок — слепота
                victim.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, EFFECT_TICKS, 0));
                break;
            default:  // энтропия — урон
                victim.attackEntityFrom(DamageSource.causePlayerDamage(caster).setMagicDamage(),
                        HARM_DAMAGE);
                break;
        }
    }
}
