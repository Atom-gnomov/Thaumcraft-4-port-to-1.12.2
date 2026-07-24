package thaumcraft.common.config;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.research.ConfigResearch;

/**
 * Registration for the Thaumic Tinkerer content module (reimplemented for
 * 1.12.2 from Thaumic Tinkerer by pixlepix / nekosune).
 *
 * Deliberately kept OUT of {@code thaumcraft.common.config.recipes} so the
 * TC4-parity recipe-corpus audits stay pinned to the original set; this is new,
 * non-parity content. Registered at mod init from {@link ConfigRecipes}.
 */
public class ConfigTinkerer {

    /** Wand foci from Thaumic Tinkerer, gated behind existing thematically-related focus research. */
    public static void registerFociRecipes() {
        arcane("FocusSmelt", "FOCUSFIRE", ConfigItems.focusSmelt,
                new AspectList().add(Aspect.FIRE, 20).add(Aspect.ORDER, 10),
                Items.BLAZE_POWDER, 1);
        arcane("FocusTelekinesis", "FOCUSEXCAVATION", ConfigItems.focusTelekinesis,
                new AspectList().add(Aspect.AIR, 15).add(Aspect.ORDER, 10),
                Items.ENDER_PEARL, 0);
        arcane("FocusFlight", "FOCUSSHOCK", ConfigItems.focusFlight,
                new AspectList().add(Aspect.AIR, 20).add(Aspect.ENERGY, 10),
                Items.FEATHER, 0);
        arcane("FocusHeal", "FOCUSFROST", ConfigItems.focusHeal,
                new AspectList().add(Aspect.WATER, 15).add(Aspect.ORDER, 15),
                Items.GHAST_TEAR, 2);
        arcane("FocusDeflect", "FOCUSWARDING", ConfigItems.focusDeflect,
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 20),
                Items.IRON_INGOT, 4);
        arcane("FocusDislocation", "FOCUSPORTABLEHOLE", ConfigItems.focusDislocation,
                new AspectList().add(Aspect.ENTROPY, 15).add(Aspect.ORDER, 15),
                Items.ENDER_PEARL, 5);
        arcane("FocusEnderChest", "FOCUSPORTABLEHOLE", ConfigItems.focusEnderChest,
                new AspectList().add(Aspect.ENTROPY, 10).add(Aspect.ORDER, 20),
                Items.ENDER_EYE, 5);
    }

    /** Standard TC4 focus recipe frame: shard corners, quartz edges, theme item centre. */
    private static void arcane(String key, String research, net.minecraft.item.Item focus,
                               AspectList aspects, net.minecraft.item.Item centre, int shardMeta) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addArcaneCraftingRecipe(
                research, new ItemStack(focus), aspects,
                "CQC", "Q#Q", "CQC",
                '#', new ItemStack(centre),
                'Q', new ItemStack(Items.QUARTZ),
                'C', new ItemStack(ConfigItems.itemShard, 1, shardMeta)));
    }
}
