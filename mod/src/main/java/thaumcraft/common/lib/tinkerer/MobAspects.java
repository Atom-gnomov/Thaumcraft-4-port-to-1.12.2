package thaumcraft.common.lib.tinkerer;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.items.tinkerer.SoulAspects;

/**
 * What each creature is made of — ported from Thaumic Tinkerer's
 * {@code EnumMobAspect} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Three aspects per creature. The Cursed Spirit's Blade turns a kill into
 * exactly these, and the Tablet of Necromancy turns exactly these back into a
 * creature.</p>
 *
 * <p><b>Not every entry is reachable, and that is upstream's state, not an
 * omission here.</b> Only eleven aspects have an item form (see
 * {@link SoulAspects}); entries calling for WATER, MAN, AIR, FLIGHT or CROP
 * name a soul that cannot be held, so those creatures can be dismantled but not
 * rebuilt. The table is transcribed whole so the blade still yields the right
 * aspects, and so the gap stays visible instead of being quietly closed.</p>
 */
public final class MobAspects {

    /** One creature and the three aspects it is made of. */
    public static final class Entry {

        private final Class<? extends Entity> entity;
        private final ResourceLocation id;
        private final Aspect[] aspects;

        Entry(Class<? extends Entity> entity, String id, Aspect... aspects) {
            this.entity = entity;
            this.id = new ResourceLocation(id);
            this.aspects = aspects;
        }

        /** Thaumcraft's own creatures, whose ids follow this mod's convention. */
        Entry(Class<? extends Entity> entity, String token, boolean ours, Aspect... aspects) {
            this.entity = entity;
            this.id = new ResourceLocation(Thaumcraft.MODID, ConfigBlocks.legacyPath(token));
            this.aspects = aspects;
        }

        public Class<? extends Entity> getEntity() {
            return this.entity;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public Aspect[] getAspects() {
            return this.aspects;
        }

        /** Whether all three of its aspects can actually be held as items. */
        public boolean isSummonable() {
            for (Aspect aspect : this.aspects) {
                if (SoulAspects.numberOf(aspect) < 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final List<Entry> ENTRIES = Arrays.asList(
            new Entry(EntitySnowman.class, "minecraft:snowman", Aspect.WATER, Aspect.WATER, Aspect.MAN),
            new Entry(EntityBat.class, "minecraft:bat", Aspect.AIR, Aspect.AIR, Aspect.FLIGHT),
            new Entry(EntityBlaze.class, "minecraft:blaze", Aspect.FIRE, Aspect.FIRE, Aspect.FIRE),
            new Entry(EntityBrainyZombie.class, "BrainyZombie", true,
                    Aspect.MAGIC, Aspect.UNDEAD, Aspect.FLESH),
            new Entry(EntityFireBat.class, "Firebat", true,
                    Aspect.FLIGHT, Aspect.FIRE, Aspect.MAGIC),
            new Entry(EntityWisp.class, "Wisp", true,
                    Aspect.AIR, Aspect.MAGIC, Aspect.MAGIC),
            new Entry(EntityCaveSpider.class, "minecraft:cave_spider", Aspect.BEAST, Aspect.POISON, Aspect.POISON),
            new Entry(EntityChicken.class, "minecraft:chicken", Aspect.CROP, Aspect.FLIGHT, Aspect.BEAST),
            new Entry(EntityCow.class, "minecraft:cow", Aspect.BEAST, Aspect.EARTH, Aspect.BEAST),
            new Entry(EntityCreeper.class, "minecraft:creeper", Aspect.MAGIC, Aspect.BEAST, Aspect.ELDRITCH),
            new Entry(EntityEnderman.class, "minecraft:enderman", Aspect.ELDRITCH, Aspect.ELDRITCH, Aspect.MAN),
            new Entry(EntityGhast.class, "minecraft:ghast", Aspect.FIRE, Aspect.FLIGHT, Aspect.FLIGHT),
            new Entry(EntityHorse.class, "minecraft:horse", Aspect.BEAST, Aspect.BEAST, Aspect.TRAVEL),
            new Entry(EntityIronGolem.class, "minecraft:villager_golem", Aspect.METAL, Aspect.METAL, Aspect.MAN),
            new Entry(EntityMagmaCube.class, "minecraft:magma_cube", Aspect.FIRE, Aspect.SLIME, Aspect.SLIME),
            new Entry(EntityMooshroom.class, "minecraft:mooshroom", Aspect.BEAST, Aspect.EARTH, Aspect.CROP),
            new Entry(EntityOcelot.class, "minecraft:ocelot", Aspect.BEAST, Aspect.EARTH, Aspect.ELDRITCH),
            new Entry(EntityPig.class, "minecraft:pig", Aspect.BEAST, Aspect.EARTH, Aspect.TRAVEL),
            new Entry(EntityPigZombie.class, "minecraft:zombie_pigman", Aspect.UNDEAD, Aspect.FLESH, Aspect.FIRE),
            new Entry(EntitySheep.class, "minecraft:sheep", Aspect.EARTH, Aspect.EARTH, Aspect.BEAST),
            new Entry(EntitySilverfish.class, "minecraft:silverfish", Aspect.METAL, Aspect.METAL, Aspect.EARTH),
            new Entry(EntitySkeleton.class, "minecraft:skeleton", Aspect.UNDEAD, Aspect.MAN, Aspect.UNDEAD),
            new Entry(EntitySlime.class, "minecraft:slime", Aspect.SLIME, Aspect.SLIME, Aspect.BEAST),
            new Entry(EntitySpider.class, "minecraft:spider", Aspect.BEAST, Aspect.UNDEAD, Aspect.UNDEAD),
            new Entry(EntitySquid.class, "minecraft:squid", Aspect.WATER, Aspect.WATER, Aspect.WATER),
            new Entry(EntityVillager.class, "minecraft:villager", Aspect.MAN, Aspect.MAN, Aspect.MAN),
            new Entry(EntityWitch.class, "minecraft:witch", Aspect.MAGIC, Aspect.UNDEAD, Aspect.ELDRITCH),
            new Entry(EntityWolf.class, "minecraft:wolf", Aspect.BEAST, Aspect.BEAST, Aspect.BEAST),
            new Entry(EntityZombie.class, "minecraft:zombie", Aspect.FLESH, Aspect.FLESH, Aspect.UNDEAD));

    private MobAspects() {
    }

    public static List<Entry> all() {
        return ENTRIES;
    }

    @Nullable
    public static Aspect[] aspectsFor(Class<?> entity) {
        for (Entry entry : ENTRIES) {
            if (entry.entity.equals(entity)) {
                return entry.aspects;
            }
        }
        return null;
    }

    /** The creature made of exactly these three aspects, in any order. */
    @Nullable
    public static Entry match(List<Aspect> aspects) {
        for (Entry entry : ENTRIES) {
            List<Aspect> wanted = Arrays.asList(entry.aspects);
            if (wanted.containsAll(aspects) && aspects.containsAll(wanted)) {
                return entry;
            }
        }
        return null;
    }
}
