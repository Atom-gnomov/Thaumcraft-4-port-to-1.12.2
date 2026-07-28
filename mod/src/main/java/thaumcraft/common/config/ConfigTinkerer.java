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
     * Thaumic Tinkerer's utility items. Every one of them registers its real
     * recipe as of 1.1.8.0, when the Black Hole Ring closed the last gap.
     */
    public static void registerUtilityItemRecipes() {
        // BLOCK_TALISMAN: the KAMI ring, infused on the portable hole focus.
        ConfigResearch.recipes.put("BlockTalisman", ThaumcraftApi.addInfusionCraftingRecipe(
                "BLOCK_TALISMAN", new ItemStack(ConfigItems.itemBlockTalisman), 9,
                new AspectList().add(Aspect.VOID, 65).add(Aspect.DARKNESS, 32)
                        .add(Aspect.MAGIC, 50).add(Aspect.ELDRITCH, 32),
                new ItemStack(ConfigItems.focusPortableHole),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(Blocks.ENDER_CHEST),
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemResource, 1, 11),
                        new ItemStack(ConfigBlocks.blockJar, 1, 3)}));

        // ICHOR_POUCH: the focus pouch grown to thirteen by nine.
        ConfigResearch.recipes.put("IchorPouch", ThaumcraftApi.addInfusionCraftingRecipe(
                "ICHOR_POUCH", new ItemStack(ConfigItems.itemIchorPouch), 9,
                new AspectList().add(Aspect.VOID, 64).add(Aspect.MAN, 32).add(Aspect.CLOTH, 32)
                        .add(Aspect.ELDRITCH, 32).add(Aspect.AIR, 64),
                new ItemStack(ConfigItems.itemFocusPouch),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH),
                        new ItemStack(ConfigItems.focusPortableHole),
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH),
                        new ItemStack(ConfigBlocks.blockChestHungry),
                        new ItemStack(ConfigBlocks.blockJar, 1, 3)}));

        // PROTOCLAY: swaps the awakened tools for you, so it follows them.
        ConfigResearch.recipes.put("Protoclay", ThaumcraftApi.addInfusionCraftingRecipe(
                "PROTOCLAY", new ItemStack(ConfigItems.itemProtoclay), 4,
                new AspectList().add(Aspect.MINE, 16).add(Aspect.TOOL, 16),
                new ItemStack(Items.CLAY_BALL),
                new ItemStack[]{
                        new ItemStack(Blocks.DIRT),
                        new ItemStack(Blocks.STONE),
                        new ItemStack(Blocks.LOG),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ENDER_SHARD)}));

        // PLACEMENT_MIRROR: unblocked by the ring above.
        ConfigResearch.recipes.put("PlacementMirror", ThaumcraftApi.addInfusionCraftingRecipe(
                "PLACEMENT_MIRROR", new ItemStack(ConfigItems.itemPlacementMirror), 12,
                new AspectList().add(Aspect.CRAFT, 65).add(Aspect.CRYSTAL, 32)
                        .add(Aspect.MAGIC, 50).add(Aspect.MIND, 32),
                new ItemStack(ConfigItems.itemBlockTalisman),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(Blocks.DROPPER),
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(Blocks.GLASS),
                        new ItemStack(Items.BLAZE_POWDER),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR)}));

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
     * The infused crops: four seeds infused from wheat seeds and a matching set
     * of elemental shards, and the four potions brewed from what they yield.
     *
     * <p>The shard metas are upstream's and are deliberately not the primal
     * order — seeds 0..3 take shards 0, 1, 3, 2. See {@code PrimalCrop}.</p>
     */
    public static void registerCropRecipes() {
        ConfigResearch.recipes.put("InfusedSeeds0", ThaumcraftApi.addInfusionCraftingRecipe(
                "POTIONS0", new ItemStack(ConfigItems.itemInfusedSeeds, 1, 0), 5,
                new AspectList().add(Aspect.CROP, 32).add(Aspect.HARVEST, 32),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemShard, 1, 0),
                        new ItemStack(ConfigItems.itemShard, 1, 0),
                        new ItemStack(ConfigItems.itemShard, 1, 0),
                        new ItemStack(ConfigItems.itemShard, 1, 0)}));

        ConfigResearch.recipes.put("InfusedSeeds1", ThaumcraftApi.addInfusionCraftingRecipe(
                "POTIONS1", new ItemStack(ConfigItems.itemInfusedSeeds, 1, 1), 5,
                new AspectList().add(Aspect.CROP, 32).add(Aspect.HARVEST, 32),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemShard, 1, 1),
                        new ItemStack(ConfigItems.itemShard, 1, 1),
                        new ItemStack(ConfigItems.itemShard, 1, 1),
                        new ItemStack(ConfigItems.itemShard, 1, 1)}));

        ConfigResearch.recipes.put("InfusedSeeds2", ThaumcraftApi.addInfusionCraftingRecipe(
                "POTIONS2", new ItemStack(ConfigItems.itemInfusedSeeds, 1, 2), 5,
                new AspectList().add(Aspect.CROP, 32).add(Aspect.HARVEST, 32),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemShard, 1, 3),
                        new ItemStack(ConfigItems.itemShard, 1, 3),
                        new ItemStack(ConfigItems.itemShard, 1, 3),
                        new ItemStack(ConfigItems.itemShard, 1, 3)}));

        ConfigResearch.recipes.put("InfusedSeeds3", ThaumcraftApi.addInfusionCraftingRecipe(
                "POTIONS3", new ItemStack(ConfigItems.itemInfusedSeeds, 1, 3), 5,
                new AspectList().add(Aspect.CROP, 32).add(Aspect.HARVEST, 32),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack[]{
                        new ItemStack(ConfigItems.itemShard, 1, 2),
                        new ItemStack(ConfigItems.itemShard, 1, 2),
                        new ItemStack(ConfigItems.itemShard, 1, 2),
                        new ItemStack(ConfigItems.itemShard, 1, 2)}));

        ConfigResearch.recipes.put("InfusedPotion0", ThaumcraftApi.addCrucibleRecipe(
                "POTIONSPOT0", new ItemStack(ConfigItems.itemInfusedPotion, 1, 0),
                new ItemStack(ConfigItems.itemInfusedGrain, 1, 0),
                new AspectList().add(Aspect.AURA, 5).add(Aspect.AIR, 5)));

        ConfigResearch.recipes.put("InfusedPotion1", ThaumcraftApi.addCrucibleRecipe(
                "POTIONSPOT1", new ItemStack(ConfigItems.itemInfusedPotion, 1, 1),
                new ItemStack(ConfigItems.itemInfusedGrain, 1, 1),
                new AspectList().add(Aspect.AURA, 5).add(Aspect.FIRE, 5)));

        ConfigResearch.recipes.put("InfusedPotion2", ThaumcraftApi.addCrucibleRecipe(
                "POTIONSPOT2", new ItemStack(ConfigItems.itemInfusedPotion, 1, 2),
                new ItemStack(ConfigItems.itemInfusedGrain, 1, 2),
                new AspectList().add(Aspect.AURA, 5).add(Aspect.EARTH, 5)));

        ConfigResearch.recipes.put("InfusedPotion3", ThaumcraftApi.addCrucibleRecipe(
                "POTIONSPOT3", new ItemStack(ConfigItems.itemInfusedPotion, 1, 3),
                new ItemStack(ConfigItems.itemInfusedGrain, 1, 3),
                new AspectList().add(Aspect.AURA, 5).add(Aspect.WATER, 5)));
    }

    /**
     * The gases in a bottle and the thing that clears them away. Both bottles
     * boil out of an empty phial; the dissipator is an arcane craft that needs
     * one of each gas in it.
     */
    public static void registerGasRecipes() {
        ConfigResearch.recipes.put("GaseousLight", ThaumcraftApi.addCrucibleRecipe(
                "GASEOUS_LIGHT", new ItemStack(ConfigItems.itemGaseousLight),
                new ItemStack(ConfigItems.itemEssence, 1, 0),
                new AspectList().add(Aspect.LIGHT, 16).add(Aspect.AIR, 10).add(Aspect.MOTION, 8)));

        ConfigResearch.recipes.put("GaseousShadow", ThaumcraftApi.addCrucibleRecipe(
                "GASEOUS_SHADOW", new ItemStack(ConfigItems.itemGaseousShadow),
                new ItemStack(ConfigItems.itemEssence, 1, 0),
                new AspectList().add(Aspect.DARKNESS, 16).add(Aspect.AIR, 10).add(Aspect.MOTION, 8)));

        ConfigResearch.recipes.put("GasRemover", ThaumcraftApi.addArcaneCraftingRecipe(
                "GAS_REMOVER", new ItemStack(ConfigItems.itemGasRemover),
                new AspectList().add(Aspect.AIR, 2).add(Aspect.ORDER, 2),
                "DDD", "T G", "QQQ",
                'D', new ItemStack(ConfigItems.itemDarkQuartz),
                'T', new ItemStack(ConfigItems.itemGaseousLight),
                'G', new ItemStack(ConfigItems.itemGaseousShadow),
                'Q', new ItemStack(Items.QUARTZ)));
    }

    /**
     * Hyperenergetic Nitor and the six imbued fires — all crucible recipes
     * upstream, every one of them boiled out of a single nitor.
     */
    public static void registerFireRecipes() {
        ConfigResearch.recipes.put("BrightNitor", ThaumcraftApi.addCrucibleRecipe(
                "BRIGHT_NITOR", new ItemStack(ConfigItems.itemBrightNitor),
                new ItemStack(ConfigItems.itemResource, 1, 1),
                new AspectList().add(Aspect.ENERGY, 25).add(Aspect.LIGHT, 25)
                        .add(Aspect.AIR, 10).add(Aspect.FIRE, 10)));

        fire("FIRE_AER", ConfigBlocks.blockFireAir,
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.AIR, 5));
        fire("FIRE_AQUA", ConfigBlocks.blockFireWater,
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.WATER, 5));
        fire("FIRE_TERRA", ConfigBlocks.blockFireEarth,
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.EARTH, 5));
        // Ignis is the odd one: no MAGIC, and twice the FIRE.
        fire("FIRE_IGNIS", ConfigBlocks.blockFireIgnis,
                new AspectList().add(Aspect.FIRE, 10).add(Aspect.AIR, 5));
        fire("FIRE_ORDO", ConfigBlocks.blockFireOrder,
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.ORDER, 5));
        fire("FIRE_PERDITIO", ConfigBlocks.blockFireChaos,
                new AspectList().add(Aspect.FIRE, 5).add(Aspect.MAGIC, 5).add(Aspect.ENTROPY, 5));
    }

    /** Every imbued fire is one Hyperenergetic Nitor in the crucible. */
    private static void fire(String key, net.minecraft.block.Block block, AspectList aspects) {
        ConfigResearch.recipes.put(key, ThaumcraftApi.addCrucibleRecipe(
                key, new ItemStack(block), new ItemStack(ConfigItems.itemBrightNitor), aspects));
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

        // The blocks: four gems make one, two blocks make two pillars, and two
        // slabs make one chiseled block. All bench recipes upstream.
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0),
                "QQ", "QQ",
                'Q', new ItemStack(ConfigItems.itemDarkQuartz))
                .setRegistryName("thaumcraft", "darkquartz_block"));
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockDarkQuartz, 2, 2),
                "Q", "Q",
                'Q', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0))
                .setRegistryName("thaumcraft", "darkquartz_pillar"));
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 1),
                "Q", "Q",
                'Q', new ItemStack(ConfigBlocks.blockSlabDarkQuartz))
                .setRegistryName("thaumcraft", "darkquartz_chiseled"));
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockSlabDarkQuartz, 6),
                "QQQ",
                'Q', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0))
                .setRegistryName("thaumcraft", "darkquartz_slab"));
        // Stairs are registered both ways round, as upstream does.
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsDarkQuartz, 4),
                "  Q", " QQ", "QQQ",
                'Q', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0))
                .setRegistryName("thaumcraft", "darkquartz_stairs"));
        registry.register(new net.minecraftforge.oredict.ShapedOreRecipe(null,
                new ItemStack(ConfigBlocks.blockStairsDarkQuartz, 4),
                "Q  ", "QQ ", "QQQ",
                'Q', new ItemStack(ConfigBlocks.blockDarkQuartz, 1, 0))
                .setRegistryName("thaumcraft", "darkquartz_stairs_mirrored"));

        // The cloth's own rule: one cloth plus one enchanted item, nothing else.
        registry.register(new thaumcraft.common.items.tinkerer.SpellClothRecipe(
                ConfigItems.itemSpellCloth)
                .setRegistryName("thaumcraft", "spellcloth_disenchant"));
    }

    /**
     * Thaumic Tinkerer's blocks, transcribed from the original's own
     * {@code getRecipeItem()}. Only five of them are arcane crafts: the smokey
     * quartz family is plain bench work (see {@link #registerBenchRecipes}),
     * the Soul Mould is a crucible recipe, and the Osmotic Enchanter and the
     * Thaumic Restorer are infusions.
     */
    public static void registerBlockRecipes() {
        // FUNNEL: a single row — stone, thaumium, stone.
        ConfigResearch.recipes.put("Funnel", ThaumcraftApi.addArcaneCraftingRecipe(
                "FUNNEL", new ItemStack(ConfigBlocks.blockFunnel),
                new AspectList().add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
                "STS",
                'S', new ItemStack(Blocks.STONE),
                'T', new ItemStack(ConfigItems.itemResource, 1, 2)));

        // MAGNET and MOB_MAGNET share a shape and a cost; the middle column is
        // iron for one and gold for the other. Both hang off MAGNETS research.
        ConfigResearch.recipes.put("Magnet", ThaumcraftApi.addArcaneCraftingRecipe(
                "MAGNETS", new ItemStack(ConfigBlocks.blockMagnet),
                new AspectList().add(Aspect.AIR, 20).add(Aspect.ORDER, 5)
                        .add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 5),
                " I ", "SIs", "WFW",
                'I', new ItemStack(Items.IRON_INGOT),
                's', new ItemStack(ConfigItems.itemShard, 1, 3),
                'S', new ItemStack(ConfigItems.itemShard, 1, 0),
                'W', new ItemStack(ConfigBlocks.blockMagicalLog),
                'F', new ItemStack(ConfigItems.focusTelekinesis)));

        // Item damage 1 is the mob magnet — see BlockMagnetItem.
        ConfigResearch.recipes.put("MobMagnet", ThaumcraftApi.addArcaneCraftingRecipe(
                "MAGNETS", new ItemStack(ConfigBlocks.blockMagnet, 1, 1),
                new AspectList().add(Aspect.AIR, 20).add(Aspect.ORDER, 5)
                        .add(Aspect.EARTH, 15).add(Aspect.ENTROPY, 5),
                " G ", "SGs", "WFW",
                'G', oreDictOrStack(new ItemStack(Items.GOLD_INGOT), "ingotCopper"),
                's', new ItemStack(ConfigItems.itemShard, 1, 3),
                'S', new ItemStack(ConfigItems.itemShard, 1, 0),
                'W', new ItemStack(ConfigBlocks.blockMagicalLog),
                'F', new ItemStack(ConfigItems.focusTelekinesis)));

        // SOUL_MOULD: a crucible recipe on an ender pearl, not a craft.
        ConfigResearch.recipes.put("SoulMould", ThaumcraftApi.addCrucibleRecipe(
                "MAGNETS", new ItemStack(ConfigItems.itemSoulMould),
                new ItemStack(Items.ENDER_PEARL),
                new AspectList().add(Aspect.BEAST, 4).add(Aspect.MIND, 8).add(Aspect.SENSES, 8)));

        // SPELL_CLOTH: a crucible recipe on enchanted fabric. It sits here
        // because the enchanter below is infused on one.
        ConfigResearch.recipes.put("SpellCloth", ThaumcraftApi.addCrucibleRecipe(
                "SPELL_CLOTH", new ItemStack(ConfigItems.itemSpellCloth),
                new ItemStack(ConfigItems.itemResource, 1, 7),
                new AspectList().add(Aspect.MAGIC, 10).add(Aspect.ENTROPY, 6)
                        .add(Aspect.EXCHANGE, 4)));

        // INTERFACE: pedestal-top corners, redstone, lapis and an ender pearl.
        ConfigResearch.recipes.put("TransvectorInterface", ThaumcraftApi.addArcaneCraftingRecipe(
                "INTERFACE", new ItemStack(ConfigBlocks.blockTransvectorInterface),
                new AspectList().add(Aspect.ORDER, 12).add(Aspect.ENTROPY, 16),
                "BRB", "LEL", "BRB",
                'B', new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6),
                'E', new ItemStack(Items.ENDER_PEARL),
                'L', new ItemStack(Items.DYE, 1, 4),
                'R', new ItemStack(Items.REDSTONE)));

        // The binder is the interface research's second recipe page upstream.
        ConfigResearch.recipes.put("TransvectorConnector", ThaumcraftApi.addArcaneCraftingRecipe(
                "INTERFACE", new ItemStack(ConfigItems.itemTransvectorConnector),
                new AspectList().add(Aspect.ORDER, 2),
                " I ", " WI", "S  ",
                'I', new ItemStack(Items.IRON_INGOT),
                'W', new ItemStack(Items.STICK),
                'S', new ItemStack(ConfigItems.itemShard, 1, 4)));

        // DISLOCATOR: a column of mirror glass, an interface and a comparator.
        ConfigResearch.recipes.put("TransvectorDislocator", ThaumcraftApi.addArcaneCraftingRecipe(
                "DISLOCATOR", new ItemStack(ConfigBlocks.blockTransvectorDislocator),
                new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5),
                " M ", " I ", " C ",
                'M', new ItemStack(ConfigItems.itemResource, 1, 10),
                'I', new ItemStack(ConfigBlocks.blockTransvectorInterface),
                'C', new ItemStack(Items.COMPARATOR)));

        // ENCHANTER: infusion on an enchanting table, five obsidian totems round it.
        ConfigResearch.recipes.put("Enchanter", ThaumcraftApi.addInfusionCraftingRecipe(
                "ENCHANTER", new ItemStack(ConfigBlocks.blockEnchanter), 15,
                new AspectList().add(Aspect.MAGIC, 50).add(Aspect.ENERGY, 20)
                        .add(Aspect.ELDRITCH, 20).add(Aspect.VOID, 20).add(Aspect.MIND, 10),
                new ItemStack(Blocks.ENCHANTING_TABLE),
                new ItemStack[]{
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1),
                        new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 1),
                        new ItemStack(ConfigItems.itemResource, 1, 2),
                        new ItemStack(ConfigItems.itemResource, 1, 2),
                        new ItemStack(ConfigItems.itemSpellCloth)}));

        // ANIMATION_TABLET: gold, iron and a blank golem core.
        ConfigResearch.recipes.put("AnimationTablet", ThaumcraftApi.addArcaneCraftingRecipe(
                "ANIMATION_TABLET", new ItemStack(ConfigBlocks.blockAnimationTablet),
                new AspectList().add(Aspect.AIR, 25).add(Aspect.ORDER, 15).add(Aspect.FIRE, 10),
                "GIG", "ICI",
                'G', new ItemStack(Items.GOLD_INGOT),
                'I', new ItemStack(Items.IRON_INGOT),
                'C', new ItemStack(ConfigItems.itemGolemCore, 1, 100)));

        // PLATFORM: two at a time, a silverwood plank over two greatwood ones.
        ConfigResearch.recipes.put("Platform", ThaumcraftApi.addArcaneCraftingRecipe(
                "PLATFORM", new ItemStack(ConfigBlocks.blockPlatform, 2),
                new AspectList().add(Aspect.AIR, 2).add(Aspect.ENTROPY, 4),
                " S ", "G G",
                'G', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6),
                'S', new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7)));

        // REVEALING_HELM: goggles onto a thaumium helm, five of every primal.
        ConfigResearch.recipes.put("RevealingHelm", ThaumcraftApi.addArcaneCraftingRecipe(
                "REVEALING_HELM", new ItemStack(ConfigItems.itemRevealingHelm),
                new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 5).add(Aspect.WATER, 5)
                        .add(Aspect.AIR, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5),
                "GH",
                'G', new ItemStack(ConfigItems.itemGoggles),
                'H', new ItemStack(ConfigItems.itemHelmThaumium)));

        // REPAIRER: infusion on a block of thaumium, one of everything around it.
        ConfigResearch.recipes.put("Repairer", ThaumcraftApi.addInfusionCraftingRecipe(
                "REPAIRER", new ItemStack(ConfigBlocks.blockRepairer), 8,
                new AspectList().add(Aspect.TOOL, 15).add(Aspect.CRAFT, 20)
                        .add(Aspect.ORDER, 10).add(Aspect.MAGIC, 15),
                new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4),
                new ItemStack[]{
                        new ItemStack(Items.IRON_INGOT),
                        new ItemStack(Items.GOLD_INGOT),
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(Blocks.COBBLESTONE),
                        new ItemStack(Blocks.PLANKS),
                        new ItemStack(Items.LEATHER),
                        new ItemStack(ConfigItems.itemResource, 1, 7),
                        new ItemStack(ConfigItems.itemResource, 1, 2)}));
    }

    /**
     * The original's {@code ThaumicTinkererRecipe.oreDictOrStack}: prefer the
     * ore-dictionary name when another mod provides it, else the plain stack.
     */
    private static Object oreDictOrStack(ItemStack stack, String oreDict) {
        return net.minecraftforge.oredict.OreDictionary.getOres(oreDict).isEmpty() ? stack : oreDict;
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
     * Ichorcloth armour. Four arcane crafts from ichorcloth alone, each priced
     * at 75 of a single primal — a different one per piece, which is the
     * original's own pattern.
     */
    public static void registerKamiArmorRecipes() {
        ConfigResearch.recipes.put("IchorclothHelm", ThaumcraftApi.addArcaneCraftingRecipe(
                "ICHORCLOTH_ARMOR", new ItemStack(ConfigItems.itemIchorclothHelm),
                new AspectList().add(Aspect.WATER, 75),
                "CCC", "C C",
                'C', new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH)));

        ConfigResearch.recipes.put("IchorclothChest", ThaumcraftApi.addArcaneCraftingRecipe(
                "ICHORCLOTH_ARMOR", new ItemStack(ConfigItems.itemIchorclothChest),
                new AspectList().add(Aspect.AIR, 75),
                "C C", "CCC", "CCC",
                'C', new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH)));

        ConfigResearch.recipes.put("IchorclothLegs", ThaumcraftApi.addArcaneCraftingRecipe(
                "ICHORCLOTH_ARMOR", new ItemStack(ConfigItems.itemIchorclothLegs),
                new AspectList().add(Aspect.FIRE, 75),
                "CCC", "C C", "C C",
                'C', new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH)));

        ConfigResearch.recipes.put("IchorclothBoots", ThaumcraftApi.addArcaneCraftingRecipe(
                "ICHORCLOTH_ARMOR", new ItemStack(ConfigItems.itemIchorclothBoots),
                new AspectList().add(Aspect.EARTH, 75),
                "C C", "C C",
                'C', new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH)));
    }

    /**
     * The awakened armour: each piece is an infusion at instability 13 on its
     * plain counterpart. Three of the four are here — see the note at the end
     * for the leggings.
     */
    public static void registerKamiAwakenedArmorRecipes() {
        // ICHORCLOTH_HELM_GEM: the cowl, on the plain cowl.
        ConfigResearch.recipes.put("IchorclothHelmGem", ThaumcraftApi.addInfusionCraftingRecipe(
                "ICHORCLOTH_HELM_GEM", new ItemStack(ConfigItems.itemIchorclothHelmGem), 13,
                new AspectList().add(Aspect.WATER, 50).add(Aspect.ARMOR, 32)
                        .add(Aspect.HUNGER, 32).add(Aspect.AURA, 32)
                        .add(Aspect.LIGHT, 64).add(Aspect.FLESH, 16).add(Aspect.MIND, 16),
                new ItemStack(ConfigItems.itemIchorclothHelm),
                new ItemStack[]{
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemThaumonomicon),
                        new ItemStack(ConfigItems.focusPrimal),
                        new ItemStack(Items.GOLDEN_HELMET),
                        new ItemStack(ConfigItems.itemGoggles),
                        new ItemStack(ConfigItems.itemCleansingTalisman),
                        new ItemStack(Items.FISH),
                        new ItemStack(Items.CAKE),
                        new ItemStack(Items.ENDER_EYE)}));

        // ICHORCLOTH_CHEST_GEM: the robes, on the plain robe.
        ConfigResearch.recipes.put("IchorclothChestGem", ThaumcraftApi.addInfusionCraftingRecipe(
                "ICHORCLOTH_CHEST_GEM", new ItemStack(ConfigItems.itemIchorclothChestGem), 13,
                new AspectList().add(Aspect.AIR, 50).add(Aspect.ARMOR, 32)
                        .add(Aspect.FLIGHT, 32).add(Aspect.ORDER, 32)
                        .add(Aspect.LIGHT, 64).add(Aspect.ELDRITCH, 16).add(Aspect.SENSES, 16),
                new ItemStack(ConfigItems.itemIchorclothChest),
                new ItemStack[]{
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.focusPrimal),
                        new ItemStack(ConfigItems.itemThaumonomicon),
                        new ItemStack(Items.GOLDEN_CHESTPLATE),
                        new ItemStack(ConfigItems.focusFlight),
                        new ItemStack(ConfigItems.itemHoverHarness),
                        new ItemStack(ConfigItems.focusDeflect),
                        new ItemStack(Items.FEATHER),
                        new ItemStack(Items.FIREWORKS),
                        new ItemStack(Items.ARROW)}));

        // ICHORCLOTH_BOOTS_GEM: the boots, on the plain boots.
        ConfigResearch.recipes.put("IchorclothBootsGem", ThaumcraftApi.addInfusionCraftingRecipe(
                "ICHORCLOTH_BOOTS_GEM", new ItemStack(ConfigItems.itemIchorclothBootsGem), 13,
                new AspectList().add(Aspect.EARTH, 50).add(Aspect.ARMOR, 32)
                        .add(Aspect.MINE, 32).add(Aspect.MOTION, 32)
                        .add(Aspect.LIGHT, 64).add(Aspect.PLANT, 16).add(Aspect.TRAVEL, 16),
                new ItemStack(ConfigItems.itemIchorclothBoots),
                new ItemStack[]{
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemThaumonomicon),
                        new ItemStack(ConfigItems.focusPrimal),
                        new ItemStack(Items.GOLDEN_BOOTS),
                        new ItemStack(Blocks.GRASS),
                        new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 5),
                        new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8),
                        new ItemStack(Items.WHEAT_SEEDS),
                        new ItemStack(Blocks.WOOL),
                        new ItemStack(Items.LEAD)}));

        // ICHORCLOTH_LEGS_GEM: unblocked in 1.1.17.0 by the gases.
        ConfigResearch.recipes.put("IchorclothLegsGem", ThaumcraftApi.addInfusionCraftingRecipe(
                "ICHORCLOTH_LEGS_GEM", new ItemStack(ConfigItems.itemIchorclothLegsGem), 13,
                new AspectList().add(Aspect.FIRE, 50).add(Aspect.ARMOR, 32)
                        .add(Aspect.HEAL, 32).add(Aspect.ENERGY, 32)
                        .add(Aspect.LIGHT, 64).add(Aspect.GREED, 16).add(Aspect.ELDRITCH, 16),
                new ItemStack(ConfigItems.itemIchorclothLegs),
                new ItemStack[]{
                        new ItemStack(Items.DIAMOND),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR),
                        new ItemStack(ConfigItems.focusPrimal),
                        new ItemStack(ConfigItems.itemThaumonomicon),
                        new ItemStack(Items.GOLDEN_CHESTPLATE),
                        fireResistancePotion(),
                        new ItemStack(ConfigItems.focusSmelt),
                        new ItemStack(ConfigItems.itemBrightNitor),
                        new ItemStack(Items.LAVA_BUCKET),
                        new ItemStack(Items.FIRE_CHARGE),
                        new ItemStack(Items.BLAZE_ROD)}));
    }

    /**
     * A potion of fire resistance, which upstream wrote as damage 8195 on the
     * old flat potion item. Potions carry their type in NBT in this version.
     */
    private static ItemStack fireResistancePotion() {
        return net.minecraft.potion.PotionUtils.addPotionToItemStack(
                new ItemStack(Items.POTIONITEM), net.minecraft.init.PotionTypes.FIRE_RESISTANCE);
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
