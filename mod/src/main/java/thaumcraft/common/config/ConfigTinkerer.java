package thaumcraft.common.config;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.research.ConfigResearch;
import thaumcraft.common.items.tinkerer.kami.ItemKamiResource;

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

    /** Thaumic Tinkerer utility items, gated behind existing thematically-related research. */
    public static void registerUtilityItemRecipes() {
        ConfigResearch.recipes.put("PlacementMirror", ThaumcraftApi.addArcaneCraftingRecipe(
                "MIRROR", new ItemStack(ConfigItems.itemPlacementMirror),
                new AspectList().add(Aspect.AIR, 15).add(Aspect.ORDER, 15).add(Aspect.EXCHANGE, 5),
                "GQG", "QMQ", "GQG",
                'M', new ItemStack(Items.QUARTZ),
                'Q', new ItemStack(Blocks.GLASS_PANE),
                'G', new ItemStack(Items.GOLD_NUGGET)));

        ConfigResearch.recipes.put("CleansingTalisman", ThaumcraftApi.addArcaneCraftingRecipe(
                "BATHSALTS", new ItemStack(ConfigItems.itemCleansingTalisman),
                new AspectList().add(Aspect.WATER, 15).add(Aspect.ORDER, 15),
                " G ", "GSG", " G ",
                'S', new ItemStack(ConfigItems.itemShard, 1, 2),
                'G', new ItemStack(Items.GOLD_INGOT)));

        ConfigResearch.recipes.put("XpTalisman", ThaumcraftApi.addArcaneCraftingRecipe(
                "ENCHANT", new ItemStack(ConfigItems.itemXpTalisman),
                new AspectList().add(Aspect.ORDER, 20).add(Aspect.MAGIC, 10),
                " G ", "GXG", " G ",
                'X', new ItemStack(Items.EXPERIENCE_BOTTLE),
                'G', new ItemStack(Items.GOLD_INGOT)));

        ConfigResearch.recipes.put("CatAmulet", ThaumcraftApi.addArcaneCraftingRecipe(
                "BOOTSTRAVELLER", new ItemStack(ConfigItems.itemCatAmulet),
                new AspectList().add(Aspect.AIR, 20).add(Aspect.MOTION, 10),
                " G ", "GFG", " G ",
                'F', new ItemStack(Items.FEATHER),
                'G', new ItemStack(Items.GOLD_INGOT)));
    }

    /**
     * Thaumic Tinkerer blocks. Until 1.0.46 the dark quartz and the funnel were
     * creative-only (no recipe at all); they are craftable from here on, together
     * with the magnet.
     */
    public static void registerBlockRecipes() {
        // Dark quartz: plain -> chiseled -> pillar, mirroring vanilla quartz shapes.
        ConfigResearch.recipes.put("DarkQuartz", ThaumcraftApi.addArcaneCraftingRecipe(
                "ARCANESTONE", new ItemStack(ConfigBlocks.blockDarkQuartz, 4, 0),
                new AspectList().add(Aspect.ENTROPY, 5).add(Aspect.EARTH, 5),
                "QQ", "QQ",
                'Q', new ItemStack(Blocks.QUARTZ_BLOCK, 1, 0)));

        ConfigResearch.recipes.put("DarkQuartzChiseled", ThaumcraftApi.addArcaneCraftingRecipe(
                "ARCANESTONE", new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 1),
                new AspectList().add(Aspect.ENTROPY, 3).add(Aspect.CRAFT, 3),
                "D", "D",
                'D', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0)));

        ConfigResearch.recipes.put("DarkQuartzPillar", ThaumcraftApi.addArcaneCraftingRecipe(
                "ARCANESTONE", new ItemStack(ConfigBlocks.blockDarkQuartz, 2, 2),
                new AspectList().add(Aspect.ENTROPY, 3).add(Aspect.CRAFT, 3),
                "D", "D",
                'D', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 1)));

        // Funnel: hopper-like collector, gated behind the grate (item-flow research).
        ConfigResearch.recipes.put("Funnel", ThaumcraftApi.addArcaneCraftingRecipe(
                "GRATE", new ItemStack(ConfigBlocks.blockFunnel),
                new AspectList().add(Aspect.AIR, 10).add(Aspect.ORDER, 10).add(Aspect.VOID, 5),
                "I I", "IHI", " I ",
                'I', new ItemStack(Items.IRON_INGOT),
                'H', new ItemStack(Blocks.HOPPER)));

        // Magnet: redstone-driven item attractor.
        ConfigResearch.recipes.put("Magnet", ThaumcraftApi.addArcaneCraftingRecipe(
                "GRATE", new ItemStack(ConfigBlocks.blockMagnet),
                new AspectList().add(Aspect.AIR, 15).add(Aspect.MOTION, 15).add(Aspect.MAGIC, 5),
                "III", "IRI", "III",
                'I', new ItemStack(Items.IRON_INGOT),
                'R', new ItemStack(Blocks.REDSTONE_BLOCK)));

        // Mob magnet: the item magnet re-tuned onto living things.
        ConfigResearch.recipes.put("MobMagnet", ThaumcraftApi.addArcaneCraftingRecipe(
                "GRATE", new ItemStack(ConfigBlocks.blockMagnet, 1, 2),
                new AspectList().add(Aspect.AIR, 20).add(Aspect.MOTION, 20).add(Aspect.BEAST, 10),
                " F ", "FMF", " F ",
                'M', new ItemStack(ConfigBlocks.blockMagnet, 1, 0),
                'F', new ItemStack(Items.ROTTEN_FLESH)));

        // Soul mould: keys machines to one kind of creature.
        ConfigResearch.recipes.put("SoulMould", ThaumcraftApi.addArcaneCraftingRecipe(
                "GRATE", new ItemStack(ConfigItems.itemSoulMould),
                new AspectList().add(Aspect.SOUL, 15).add(Aspect.BEAST, 10).add(Aspect.CRAFT, 5),
                "CTC", "T T", "CTC",
                'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                'C', new ItemStack(Items.CLAY_BALL)));

        // Transvector interface + its connector: remote block proxying.
        ConfigResearch.recipes.put("TransvectorInterface", ThaumcraftApi.addArcaneCraftingRecipe(
                "MIRROR", new ItemStack(ConfigBlocks.blockTransvectorInterface),
                new AspectList().add(Aspect.EXCHANGE, 20).add(Aspect.MAGIC, 15).add(Aspect.VOID, 10),
                "SQS", "QEQ", "SQS",
                'S', new ItemStack(ConfigItems.itemShard, 1, 5),
                'Q', new ItemStack(Items.QUARTZ),
                'E', new ItemStack(Items.ENDER_EYE)));

        ConfigResearch.recipes.put("TransvectorDislocator", ThaumcraftApi.addArcaneCraftingRecipe(
                "MIRROR", new ItemStack(ConfigBlocks.blockTransvectorDislocator),
                new AspectList().add(Aspect.EXCHANGE, 25).add(Aspect.MOTION, 15).add(Aspect.VOID, 10),
                "SPS", "PEP", "SPS",
                'S', new ItemStack(ConfigItems.itemShard, 1, 5),
                'P', new ItemStack(Blocks.PISTON),
                'E', new ItemStack(Items.ENDER_EYE)));

        ConfigResearch.recipes.put("TransvectorConnector", ThaumcraftApi.addArcaneCraftingRecipe(
                "MIRROR", new ItemStack(ConfigItems.itemTransvectorConnector),
                new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.MAGIC, 10),
                "  Q", " R ", "R  ",
                'Q', new ItemStack(Items.QUARTZ),
                'R', new ItemStack(Items.BLAZE_ROD)));

        // Osmotic enchanter: vis-powered enchanting, needs the totem/nitor pillars.
        ConfigResearch.recipes.put("Enchanter", ThaumcraftApi.addArcaneCraftingRecipe(
                "INFUSIONENCHANTMENT", new ItemStack(ConfigBlocks.blockEnchanter),
                new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 20).add(Aspect.EXCHANGE, 15),
                "SBS", "TQT", "OOO",
                'S', new ItemStack(ConfigItems.itemShard, 1, 5),
                'B', new ItemStack(Items.BOOK),
                'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                'Q', new ItemStack(Items.QUARTZ),
                'O', new ItemStack(Blocks.OBSIDIAN)));

        // Animation tablet: works a tool against the block it faces.
        ConfigResearch.recipes.put("AnimationTablet", ThaumcraftApi.addArcaneCraftingRecipe(
                "GOLEMANCY", new ItemStack(ConfigBlocks.blockAnimationTablet),
                new AspectList().add(Aspect.MECHANISM, 20).add(Aspect.MOTION, 15).add(Aspect.TOOL, 10),
                "TQT", "QMQ", "SSS",
                'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                'Q', new ItemStack(Items.QUARTZ),
                'M', new ItemStack(Blocks.PISTON),
                'S', new ItemStack(Blocks.STONE_SLAB)));

        // Repairer: essentia-fed tool mender, gated behind essentia distillation.
        ConfigResearch.recipes.put("Repairer", ThaumcraftApi.addArcaneCraftingRecipe(
                "DISTILESSENTIA", new ItemStack(ConfigBlocks.blockRepairer),
                new AspectList().add(Aspect.TOOL, 20).add(Aspect.CRAFT, 15).add(Aspect.ORDER, 10),
                "ATA", "TQT", "ATA",
                'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                'Q', new ItemStack(Items.QUARTZ),
                'A', new ItemStack(Items.IRON_INGOT)));
    }

    /**
     * KAMI resources — the endgame tier. Recipes are the original's, pattern for
     * pattern and aspect for aspect ({@code ItemKamiResource.getRecipeItem}).
     * The two shards are not craftable: they drop from mobs (see
     * {@code DimensionalShardDropHandler}).
     */
    public static void registerKamiRecipes() {
        ItemStack ichor = new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR);
        ItemStack cloth = new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH);
        ItemStack ichorium = new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORIUM);

        // Ichor: infusion on a nether star, instability 7.
        ConfigResearch.recipes.put("Ichor", ThaumcraftApi.addInfusionCraftingRecipe(
                "INFUSION",
                new ItemStack(ConfigItems.itemKamiResource, 8, ItemKamiResource.ICHOR), 7,
                new AspectList().add(Aspect.MAN, 32).add(Aspect.LIGHT, 32).add(Aspect.SOUL, 64),
                new ItemStack(Items.NETHER_STAR),
                new ItemStack[]{
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 8, ItemKamiResource.ENDER_SHARD),
                        new ItemStack(Items.ENDER_EYE),
                        new ItemStack(ConfigItems.itemKamiResource, 8, ItemKamiResource.NETHER_SHARD)}));

        // Ichorcloth: 3 per craft, 125 of every primal.
        ConfigResearch.recipes.put("IchorCloth", ThaumcraftApi.addArcaneCraftingRecipe(
                "INFUSION", new ItemStack(ConfigItems.itemKamiResource, 3, ItemKamiResource.ICHORCLOTH),
                allPrimals(125),
                "CCC", "III", "DDD",
                'C', new ItemStack(ConfigItems.itemResource, 1, 7),
                'I', ichor,
                'D', new ItemStack(Items.DIAMOND)));

        // Ichorium ingot: 100 of every primal.
        ConfigResearch.recipes.put("Ichorium", ThaumcraftApi.addArcaneCraftingRecipe(
                "INFUSION", new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORIUM),
                allPrimals(100),
                " T ", "IDI", " I ",
                'T', new ItemStack(ConfigItems.itemResource, 1, 2),
                'I', ichor,
                'D', new ItemStack(Items.DIAMOND)));

        // Ichor cap: 2 per craft, on charged thaumium caps.
        ConfigResearch.recipes.put("IchorCap", ThaumcraftApi.addArcaneCraftingRecipe(
                "INFUSION", new ItemStack(ConfigItems.itemKamiResource, 2, ItemKamiResource.ICHOR_CAP),
                allPrimals(100),
                "ICI", " M ", "ICI",
                'M', ichorium,
                'I', ichor,
                'C', new ItemStack(ConfigItems.itemWandCap, 1, 2)));

        // Ichorcloth rod: infusion on a greatwood rod, instability 9.
        ConfigResearch.recipes.put("IchorclothRod", ThaumcraftApi.addInfusionCraftingRecipe(
                "INFUSION",
                new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH_ROD), 9,
                new AspectList().add(Aspect.MAGIC, 100).add(Aspect.LIGHT, 32).add(Aspect.TOOL, 32),
                new ItemStack(ConfigItems.itemWandRod, 1, 2),
                new ItemStack[]{
                        ichor.copy(), cloth.copy(),
                        new ItemStack(ConfigItems.itemResource, 1, 14),
                        new ItemStack(Items.GHAST_TEAR),
                        new ItemStack(ConfigItems.itemResource, 1, 14),
                        cloth.copy()}));
    }

    /** The original priced KAMI crafts at the same amount of every primal. */
    private static AspectList allPrimals(int amount) {
        return new AspectList()
                .add(Aspect.FIRE, amount).add(Aspect.EARTH, amount).add(Aspect.WATER, amount)
                .add(Aspect.AIR, amount).add(Aspect.ORDER, amount).add(Aspect.ENTROPY, amount);
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
