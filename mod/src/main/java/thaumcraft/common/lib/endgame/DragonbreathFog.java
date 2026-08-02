package thaumcraft.common.lib.endgame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * The dragon's fog, cast by the Dragonbreath focus — End Legacy module (new
 * content, no 1.7.10 original; the owner's design, refined twice: it is a
 * <b>focus</b>, like Fire or Excavation, and it breathes a <b>lingering
 * cloud</b> exactly the way the Ender Dragon does).
 *
 * <p>One die, rolled once at cast start, across the six primals — the owner's
 * table verbatim:</p>
 *
 * <pre>
 * огонь    — горение      (fire:    ignite)
 * вода     — утомление    (water:   mining fatigue)
 * воздух   — замедление   (air:     slowness)
 * земля    — слабость     (earth:   weakness)
 * порядок  — слепота      (order:   blindness)
 * энтропия — урон         (entropy: harming, the dragon's own)
 * </pre>
 *
 * <p>Five of the six ride vanilla's {@link EntityAreaEffectCloud} as potion
 * effects, precisely like a lingering potion. Fire is not a potion, so
 * fire-clouds are remembered here and a world-tick handler sets whatever
 * stands in them alight — the cloud itself stays the genuine article.</p>
 */
public class DragonbreathFog {

    public static final float RADIUS = 3.0F;
    /** Ten seconds of fog. */
    public static final int CLOUD_DURATION = 200;
    public static final int EFFECT_TICKS = 200;
    public static final int FIRE_SECONDS = 5;

    /** Fire-rolled clouds, watched by the ticker; everything else is vanilla's business. */
    private static final List<EntityAreaEffectCloud> FIRE_CLOUDS = new ArrayList<>();

    /** Rolls the die and breathes the fog at {@code at}. Server-side only. */
    public static void breathe(World world, EntityPlayer caster, Vec3d at) {
        if (world.isRemote) {
            return;
        }
        int roll = world.rand.nextInt(6);

        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, at.x, at.y, at.z);
        cloud.setOwner(caster);
        cloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
        cloud.setRadius(RADIUS);
        cloud.setDuration(CLOUD_DURATION);
        cloud.setRadiusPerTick((0.5F - RADIUS) / (float) CLOUD_DURATION);

        switch (roll) {
            case 0:   // огонь — горение; not a potion, the ticker handles it
                FIRE_CLOUDS.add(cloud);
                break;
            case 1:   // вода — утомление
                cloud.addEffect(new PotionEffect(MobEffects.MINING_FATIGUE, EFFECT_TICKS, 1));
                break;
            case 2:   // воздух — замедление
                cloud.addEffect(new PotionEffect(MobEffects.SLOWNESS, EFFECT_TICKS, 1));
                break;
            case 3:   // земля — слабость
                cloud.addEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_TICKS, 1));
                break;
            case 4:   // порядок — слепота
                cloud.addEffect(new PotionEffect(MobEffects.BLINDNESS, EFFECT_TICKS, 0));
                break;
            default:  // энтропия — урон; the dragon's own harming cloud
                cloud.setPotion(net.minecraft.init.PotionTypes.EMPTY);
                cloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
                break;
        }
        world.spawnEntity(cloud);
    }

    /** Sets alight whatever stands in a fire-rolled cloud, for as long as it lasts. */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient() || FIRE_CLOUDS.isEmpty()) {
            return;
        }
        Iterator<EntityAreaEffectCloud> clouds = FIRE_CLOUDS.iterator();
        while (clouds.hasNext()) {
            EntityAreaEffectCloud cloud = clouds.next();
            if (cloud.isDead) {
                clouds.remove();
                continue;
            }
            if (cloud.world != event.world) {
                continue;
            }
            List<EntityLivingBase> victims = cloud.world.getEntitiesWithinAABB(EntityLivingBase.class,
                    cloud.getEntityBoundingBox().grow(cloud.getRadius(), 0.5D, cloud.getRadius()));
            for (EntityLivingBase victim : victims) {
                victim.setFire(FIRE_SECONDS);
            }
        }
    }
}
