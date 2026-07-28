package thaumcraft.common.lib.tinkerer;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Thaumic Tinkerer's four primal potions — ported from its {@code ModPotions}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Each is drunk as a brewed grain and lasts three minutes. None of them does
 * anything on its own; the work is in {@link TinkererPotionHandler}, which
 * watches for the drinker to hit something.</p>
 *
 * <p>Upstream had to reflect its way into vanilla's fixed potion array to make
 * room for these. This version has a registry, so they are simply registered —
 * the ids it hard-coded (86, 87, 89, 90) were an artefact of that array and
 * mean nothing here.</p>
 */
public final class ModPotionsTinkerer {

    /** The original's duration on every one of the four: 3600 ticks. */
    public static final int DURATION = 3600;

    public static Potion potionAir;
    public static Potion potionFire;
    public static Potion potionEarth;
    public static Potion potionWater;

    private ModPotionsTinkerer() {
    }

    public static void register(IForgeRegistry<Potion> registry) {
        potionAir = make("air", 0x8FD0FF);
        potionFire = make("fire", 0xFF8A3C);
        potionEarth = make("earth", 0x76B856);
        potionWater = make("water", 0x3C7CFF);
        registry.registerAll(potionAir, potionFire, potionEarth, potionWater);
    }

    private static Potion make(String name, int colour) {
        Potion potion = new PrimalPotion(colour);
        potion.setRegistryName(new ResourceLocation("thaumcraft", "tinkerer_" + name));
        potion.setPotionName("potion.thaumcraft.tinkerer_" + name);
        return potion;
    }

    /** No per-tick effect of its own: everything happens on hit. */
    private static final class PrimalPotion extends Potion {

        private PrimalPotion(int colour) {
            super(false, colour);
        }

        @Override
        public boolean isReady(int duration, int amplifier) {
            return false;
        }
    }

    /** Registration hook, called from the mod's registry events. */
    public static void onRegister(net.minecraftforge.event.RegistryEvent.Register<Potion> event) {
        register(event.getRegistry());
    }

    /** Convenience for code that only has ForgeRegistries to hand. */
    public static void registerAll() {
        register(ForgeRegistries.POTIONS);
    }
}
