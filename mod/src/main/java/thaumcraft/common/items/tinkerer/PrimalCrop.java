package thaumcraft.common.items.tinkerer;

import thaumcraft.api.aspects.Aspect;

/**
 * The four primals the infused crops come in, in the order Thaumic Tinkerer
 * lists them — ported from the {@code PRIMAL_ASPECT_ENUM} it repeats in
 * {@code ItemInfusedSeeds}, {@code ItemInfusedGrain} and
 * {@code ItemInfusedPotion} (pixlepix / nekosune, originally Vazkii).
 *
 * <p><b>The order is load-bearing.</b> It is the item metadata: seeds, grain
 * and potion all address their variant by this ordinal, and so do their
 * recipes. Note that it is <em>not</em> the order of the elemental shards —
 * the seed recipes use shard metas 0, 1, 3, 2 against ordinals 0..3 — so this
 * cannot be replaced with any other primal ordering.</p>
 */
public enum PrimalCrop {

    AIR(Aspect.AIR, "aer"),
    FIRE(Aspect.FIRE, "ignis"),
    EARTH(Aspect.EARTH, "terra"),
    WATER(Aspect.WATER, "aqua");

    private final Aspect aspect;
    private final String tag;

    PrimalCrop(Aspect aspect, String tag) {
        this.aspect = aspect;
        this.tag = tag;
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    /** The lowercase name the original built its texture paths from. */
    public String getTag() {
        return this.tag;
    }

    public static PrimalCrop byMeta(int meta) {
        PrimalCrop[] all = values();
        return all[Math.max(0, Math.min(all.length - 1, meta))];
    }

    public static int metaFor(Aspect aspect) {
        for (PrimalCrop crop : values()) {
            if (crop.aspect == aspect) {
                return crop.ordinal();
            }
        }
        return 0;
    }
}
