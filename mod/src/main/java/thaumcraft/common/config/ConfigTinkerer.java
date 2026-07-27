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

    /**
     * The seven wand foci, transcribed from the original's own
     * {@code getRecipeItem()} - shape, aspects, instability and components
     * unchanged. Five are infusions upstream and two are arcane crafts.
     *
     * <p>The research keys are the original's (FOCUS_SMELT and friends). The
     * Thaumic Tinkerer research branch is not ported yet, so these recipes are
     * registered but stay locked until it is; that is the original's gate, not
     * a substitute for it.</p>
     */
    public static void registerFociRecipes() {
        // FOCUS_SMELT: arcane, "FNE" - fire focus, nitor, excavation focus.
        ConfigResearch.recipes.put("FocusSmelt", ThaumcraftApi.addArcaneCraftingRecipe(
                "FOCUS_SMELT", new ItemStack(ConfigItems.focusSmelt),
                new AspectList().add(Aspect.FIRE, 10).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 6),
                "FNE",
                'F', new ItemStack(ConfigItems.focusFire),
                'N', new ItemStack(ConfigItems.itemResource, 1, 1),
                'E', new ItemStack(ConfigItems.focusExcavation)));

        ConfigResearch.recipes.put("FocusTelekinesis", ThaumcraftApi.addInfusionCraftingRecipe(
                "FOCUS_TELEKINESIS", new ItemStack(ConfigItems.focusTelekinesis), 5,
                new AspectList().add(Aspect.MOTION, 10).add(Aspect.AIR, 20)
                        .add(Aspect.ENTROPY, 20).add(Aspect.MIND, 10),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack[]{
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT),
                        new ItemStack(ConfigItems.itemShard, 1, 0)}));

        ConfigResearch.recipes.put("FocusFlight", ThaumcraftApi.addInfusionCraftingRecipe(
                "FOCUS_FLIGHT", new ItemStack(ConfigItems.focusFlight), 3,
                new AspectList().add(Aspect.AIR, 15).add(Aspect.MOTION, 20).add(Aspect.TRAVEL, 10),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack[]{
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(Items.FEATHER), new ItemStack(Items.FEATHER),
                        new ItemStack(ConfigItems.itemShard, 1, 0)}));

        ConfigResearch.recipes.put("FocusHeal", ThaumcraftApi.addInfusionCraftingRecipe(
                "FOCUS_HEAL", new ItemStack(ConfigItems.focusHeal), 4,
                new AspectList().add(Aspect.HEAL, 10).add(Aspect.SOUL, 10).add(Aspect.LIFE, 15),
                new ItemStack(ConfigItems.focusPech),
                new ItemStack[]{
                        new ItemStack(Items.GOLDEN_CARROT),
                        new ItemStack(Items.GOLD_NUGGET), new ItemStack(Items.GOLD_NUGGET),
                        new ItemStack(Items.GOLD_NUGGET)}));

        // Centre is the flight focus itself; mirror glass and a warding stone around it.
        ConfigResearch.recipes.put("FocusDeflect", ThaumcraftApi.addInfusionCraftingRecipe(
                "FOCUS_DEFLECT", new ItemStack(ConfigItems.focusDeflect), 5,
                new AspectList().add(Aspect.AIR, 15).add(Aspect.ARMOR, 5).add(Aspect.ORDER, 20),
                new ItemStack(ConfigItems.focusFlight),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemResource, 1, 10),
                        new ItemStack(ConfigItems.itemResource, 1, 10),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 3),
                        new ItemStack(ConfigItems.itemShard, 1, 4)}));

        ConfigResearch.recipes.put("FocusDislocation", ThaumcraftApi.addInfusionCraftingRecipe(
                "FOCUS_DISLOCATION", new ItemStack(ConfigItems.focusDislocation), 8,
                new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.DARKNESS, 10)
                        .add(Aspect.VOID, 25).add(Aspect.MAGIC, 20).add(Aspect.TAINT, 5),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack[]{
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(Items.QUARTZ), new ItemStack(Items.QUARTZ),
                        new ItemStack(ConfigItems.itemResource, 1, 6),
                        new ItemStack(ConfigItems.itemResource, 1, 6),
                        new ItemStack(ConfigItems.itemResource, 1, 6),
                        new ItemStack(Items.DIAMOND)}));

        // FOCUS_ENDER_CHEST: arcane, a vertical column of mirror, eye and hole focus.
        ConfigResearch.recipes.put("FocusEnderChest", ThaumcraftApi.addArcaneCraftingRecipe(
                "FOCUS_ENDER_CHEST", new ItemStack(ConfigItems.focusEnderChest),
                new AspectList().add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10),
                "M", "E", "P",
                'M', new ItemStack(ConfigBlocks.blockMirror),
                'E', new ItemStack(Items.ENDER_EYE),
                'P', new ItemStack(ConfigItems.focusPortableHole)));
    }

    /**
     * Thaumic Tinkerer's utility items.
     *
     * <p>The Worldshaper's Looking Glass still registers nothing: upstream it
     * infuses on a Block Talisman, which this port does not have yet. Its full
     * recipe is PLACEMENT_MIRROR, instability 12, CRAFT 65 + CRYSTAL 32 +
     * MAGIC 50 + MIND 32, on the talisman with ichor, a dropper, a diamond,
     * glass, blaze powder and a second ichor.</p>
     */
    public static void registerUtilityItemRecipes() {
        // CAT_AMULET: every component exists here, so it ports whole.
        ConfigResearch.recipes.put("CatAmulet", ThaumcraftApi.addInfusionCraftingRecipe(
                "CAT_AMULET", new ItemStack(ConfigItems.itemCatAmulet), 8,
                new AspectList().add(Aspect.DARKNESS, 16).add(Aspect.ORDER, 32).add(Aspect.MIND, 16),
                new ItemStack(Blocks.QUARTZ_BLOCK),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT),
                        new ItemStack(Items.DYE, 1, 3),
                        new ItemStack(Blocks.LEAVES, 1, 3),
                        new ItemStack(Items.FISH)}));

        // CLEANSING_TALISMAN: unblocked by the smokey quartz gem.
        ConfigResearch.recipes.put("CleansingTalisman", ThaumcraftApi.addInfusionCraftingRecipe(
                "CLEANSING_TALISMAN", new ItemStack(ConfigItems.itemCleansingTalisman), 5,
                new AspectList().add(Aspect.HEAL, 10).add(Aspect.TOOL, 10)
                        .add(Aspect.MAN, 20).add(Aspect.LIFE, 10),
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemDarkQuartz),
                        new ItemStack(ConfigItems.itemDarkQuartz),
                        new ItemStack(ConfigItems.itemDarkQuartz),
                        new ItemStack(ConfigItems.itemDarkQuartz),
                        new ItemStack(Items.GHAST_TEAR),
                        new ItemStack(ConfigItems.itemResource, 1, 1)}));

        // XP_TALISMAN: likewise. Meta 5 of itemResource is the zombie brain.
        ConfigResearch.recipes.put("XpTalisman", ThaumcraftApi.addInfusionCraftingRecipe(
                "XP_TALISMAN", new ItemStack(ConfigItems.itemXpTalisman), 6,
                new AspectList().add(Aspect.GREED, 20).add(Aspect.EXCHANGE, 10)
                        .add(Aspect.BEAST, 10).add(Aspect.MECHANISM, 5),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack[]{
                        new ItemStack(Items.QUARTZ),
                        new ItemStack(ConfigItems.itemDarkQuartz),
                        new ItemStack(ConfigItems.itemResource, 1, 5),
                        new ItemStack(Items.DIAMOND)}));
    }

    /**
     * Thaumic Tinkerer's plain workbench recipes. The original registered the
     * smokey quartz gem twice, once with coal and once with charcoal, both
     * giving eight from a ring of nether quartz.
     */
    public static void registerBenchRecipes(net.minecraftforge.registries.IForgeRegistry<
            net.minecraft.item.crafting.IRecipe> registry) {
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemDarkQuartz, 8),
                "QQQ", "QCQ", "QQQ",
                'Q', new ItemStack(Items.QUARTZ),
                'C', new ItemStack(Items.COAL, 1, 0))
                .setRegistryName("thaumcraft", "darkquartz_coal"));
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigItems.itemDarkQuartz, 8),
                "QQQ", "QCQ", "QQQ",
                'Q', new ItemStack(Items.QUARTZ),
                'C', new ItemStack(Items.COAL, 1, 1))
                .setRegistryName("thaumcraft", "darkquartz_charcoal"));
    }

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

    /**
     * KAMI tools. Each is a plain arcane craft on a greatwood rod, priced at 75
     * of a single primal — the original's {@code getRecipeItem} on each tool.
     */
    public static void registerKamiToolRecipes() {
        kamiTool("IchorPick", ConfigItems.itemIchorPick, Aspect.FIRE, "III", " R ", " R ");
        kamiTool("IchorAxe", ConfigItems.itemIchorAxe, Aspect.WATER, "II ", "IR ", " R ");
        kamiTool("IchorShovel", ConfigItems.itemIchorShovel, Aspect.EARTH, " I ", " R ", " R ");
        kamiTool("IchorSword", ConfigItems.itemIchorSword, Aspect.AIR, " I ", " I ", " R ");
    }

    /**
     * Advanced KAMI tools — infusions on the plain tool at instability 15.
     * Aspect lists and the twelve-component ring are the original's, taken from
     * each tool's {@code getRecipeItem}.
     */
    public static void registerKamiAdvancedToolRecipes() {
        advTool("IchorPickAdv", ConfigItems.itemIchorPickAdv, ConfigItems.itemIchorPick,
                new AspectList().add(Aspect.FIRE, 50).add(Aspect.MINE, 64).add(Aspect.METAL, 32)
                        .add(Aspect.EARTH, 32).add(Aspect.HARVEST, 32).add(Aspect.GREED, 16)
                        .add(Aspect.SENSES, 16),
                ConfigItems.itemPickElemental, ConfigItems.focusExcavation, new ItemStack(Blocks.TNT));

        advTool("IchorAxeAdv", ConfigItems.itemIchorAxeAdv, ConfigItems.itemIchorAxe,
                new AspectList().add(Aspect.WATER, 50).add(Aspect.MINE, 64).add(Aspect.TOOL, 32)
                        .add(Aspect.TREE, 32).add(Aspect.HARVEST, 32).add(Aspect.CROP, 16)
                        .add(Aspect.SENSES, 16),
                ConfigItems.itemAxeElemental, ConfigItems.focusExcavation, new ItemStack(Blocks.TNT));

        advTool("IchorShovelAdv", ConfigItems.itemIchorShovelAdv, ConfigItems.itemIchorShovel,
                new AspectList().add(Aspect.EARTH, 50).add(Aspect.MINE, 64).add(Aspect.TOOL, 32)
                        .add(Aspect.EARTH, 32).add(Aspect.HARVEST, 32).add(Aspect.TRAP, 16)
                        .add(Aspect.SENSES, 16),
                ConfigItems.itemShovelElemental, ConfigItems.focusExcavation, new ItemStack(Blocks.TNT));
    }

    /**
     * The shared shape of an advanced-tool infusion: ichorium and ichor, the
     * matching elemental tool and focus either side of a themed reagent, three
     * nuggets, a diamond, and ichorcloth to close the ring.
     */
    private static void advTool(String key, net.minecraft.item.Item result, net.minecraft.item.Item base,
                                AspectList aspects, net.minecraft.item.Item elemental,
                                net.minecraft.item.Item focus, ItemStack reagent) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addInfusionCraftingRecipe(
                "INFUSION", new ItemStack(result), 15, aspects, new ItemStack(base),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORIUM),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(elemental),
                        new ItemStack(focus),
                        reagent,
                        new ItemStack(ConfigItems.itemNugget, 1, 21),
                        new ItemStack(ConfigItems.itemNugget, 1, 16),
                        new ItemStack(ConfigItems.itemNugget, 1, 31),
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(focus),
                        new ItemStack(elemental),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH)}));
    }

    private static void kamiTool(String key, net.minecraft.item.Item tool, Aspect aspect, String... pattern) {
        Object[] recipe = new Object[pattern.length + 4];
        System.arraycopy(pattern, 0, recipe, 0, pattern.length);
        recipe[pattern.length] = 'R';
        recipe[pattern.length + 1] = new ItemStack(ConfigItems.itemWandRod, 1, 2);
        recipe[pattern.length + 2] = 'I';
        recipe[pattern.length + 3] = new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORIUM);
        ConfigResearch.recipes.put(key, ThaumcraftApi.addArcaneCraftingRecipe(
                "INFUSION", new ItemStack(tool), new AspectList().add(aspect, 75), recipe));
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
