package thaumcraft.common.lib.world.dim.bedrock;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Weighted ore table for the Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code EnumOreFrequency} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Entries are ore-dictionary names, so whatever other mods register under
 * them is picked up automatically — the original worked the same way, which is
 * why the list mentions ores from mods that need not be installed. Weights are
 * the original's, unchanged.</p>
 */
public enum OreFrequency {

    AMBER("oreAmber", 161),
    APATITE("oreApatite", 269),
    BLUE_TOPAZ("oreBlueTopaz", 238),
    CERTUS_QUARTZ("oreCertusQuartz", 234),
    CHIMERITE("oreChimerite", 270),
    CINNABAR("oreCinnabar", 172),
    COAL("oreCoal", 2648),
    COPPER("oreCopper", 603),
    DIAMOND("oreDiamond", 67),
    EMERALD("oreEmerald", 48),
    DARK_IRON("oreFzDarkIron", 61),
    GOLD("oreGold", 164),
    AIR("oreInfusedAir", 94),
    EARTH("oreInfusedEarth", 35),
    ENTROPY("oreInfusedEntropy", 53),
    FIRE("oreInfusedFire", 42),
    ORDER("oreInfusedOrder", 31),
    WATER("oreInfusedWater", 27),
    IRON("oreIron", 1503),
    LAPIS("oreLapis", 57),
    LEAD("oreLead", 335),
    NICKEL("oreNickel", 72),
    PERIDOT("orePeridot", 79),
    REDSTONE("oreRedstone", 364),
    RUBY("oreRuby", 57),
    SALT("oreSaltpeter", 768),
    SAPPHIRE("oreSapphire", 70),
    SILVER("oreSilver", 416),
    SULFUR("oreSulfur", 105),
    TIN("oreTin", 507),
    URANIUM("oreUranium", 112),
    VINETUM("oreVinteum", 392);

    /** The original refused to place this one. */
    private static final String[] BLACKLIST = {"oreFirestone"};

    public final String oreName;
    public final int frequency;

    OreFrequency(String oreName, int frequency) {
        this.oreName = oreName;
        this.frequency = frequency;
    }

    /**
     * Picks an ore by weight among those actually registered in this instance,
     * or {@code null} when none of them are.
     */
    @Nullable
    public static ItemStack getRandomOre(Random random) {
        int total = 0;
        for (OreFrequency entry : values()) {
            if (entry.isAvailable()) {
                total += entry.frequency;
            }
        }
        if (total <= 0) {
            return null;
        }
        int roll = random.nextInt(total);
        for (OreFrequency entry : values()) {
            if (!entry.isAvailable()) {
                continue;
            }
            roll -= entry.frequency;
            if (roll < 0) {
                List<ItemStack> ores = OreDictionary.getOres(entry.oreName, false);
                return ores.isEmpty() ? null : ores.get(random.nextInt(ores.size())).copy();
            }
        }
        return null;
    }

    private boolean isAvailable() {
        for (String banned : BLACKLIST) {
            if (banned.equals(this.oreName)) {
                return false;
            }
        }
        return !OreDictionary.getOres(this.oreName, false).isEmpty();
    }
}
