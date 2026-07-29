package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

/**
 * Thaumic Tinkerer's own Thaumonomicon tab and its entries, transcribed from
 * each object's {@code getResearchItem()} in the original
 * (pixlepix / nekosune / Vazkii).
 *
 * <p>Without these the module's content exists but cannot be reached: both
 * {@code ShapedArcaneRecipe.matches} and {@code InfusionRecipe.matches} begin
 * with a research check, and an unknown key never completes. Keys, aspects,
 * map coordinates, complexity, parents and page counts are the original's.</p>
 */
public final class ConfigResearchTinkerer {

    /** The original's LibResearch.CATEGORY_THAUMICTINKERER. */
    public static final String CATEGORY = "TT_CATEGORY";

    private ConfigResearchTinkerer() {
    }

    public static void initCategory() {
        ResearchCategories.registerCategory(
                CATEGORY,
                new ResourceLocation("thaumcraft", "textures/misc/r_enchanting.png"),
                new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
    }

    public static void initResearch() {
        registerFoci();
        registerDarkQuartzBranch();
        registerGaseousLightBranch();
        registerSpellClothBranch();
        registerKamiBranch();
    }

    /**
     * The KAMI tier. Every entry here is a {@link TinkererKamiResearchItem},
     * which is what gates the tier: concealed, and hidden behind every other
     * research in the book.
     *
     * <p>The warp gate, the sky pearl and the three KAMI foci are not here —
     * their objects are not ported, and an entry whose recipe page cannot
     * resolve would throw at load. They go in with their objects.</p>
     */
    private static void registerKamiBranch() {
        new TinkererKamiResearchItem("DIMENSION_SHARDS",
                new AspectList(),
                7, 8, 0, new ItemStack(ConfigItems.itemKamiResource, 1, 7))
                .setStub().setAutoUnlock().setRound()
                .setPages(new ResearchPage("0"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR",
                new AspectList().add(Aspect.MAN, 1).add(Aspect.LIGHT, 2)
                        .add(Aspect.SOUL, 1).add(Aspect.TAINT, 1),
                9, 8, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 0))
                .setPages(new ResearchPage("0"), infusionPage("Ichor"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR_CLOTH",
                new AspectList().add(Aspect.CLOTH, 2).add(Aspect.LIGHT, 1)
                        .add(Aspect.CRAFT, 1).add(Aspect.SENSES, 1),
                11, 7, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 1))
                .setConcealed().setParents("ICHOR")
                .setPages(new ResearchPage("0"), arcaneRecipePage("IchorCloth"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORIUM",
                new AspectList().add(Aspect.METAL, 2).add(Aspect.LIGHT, 1)
                        .add(Aspect.CRAFT, 1).add(Aspect.TOOL, 1),
                11, 9, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 2))
                .setConcealed().setParents("ICHOR").setParentsHidden("ICHOR_CLOTH")
                .setPages(new ResearchPage("0"), arcaneRecipePage("Ichorium"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR_CAP",
                new AspectList().add(Aspect.TOOL, 2).add(Aspect.METAL, 1)
                        .add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1),
                11, 11, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 4))
                .setConcealed().setParents("ICHORIUM")
                .setPages(new ResearchPage("0"), arcaneRecipePage("IchorCap"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORCLOTH_ROD",
                new AspectList().add(Aspect.TOOL, 2).add(Aspect.CLOTH, 1)
                        .add(Aspect.LIGHT, 1).add(Aspect.MAGIC, 1),
                14, 2, 5, new ItemStack(ConfigItems.itemKamiResource, 1, 5))
                .setConcealed().setParents("ICHOR_CLOTH").setParentsHidden("ICHOR_CAP")
                .setPages(new ResearchPage("0"), infusionPage("IchorclothRod"))
                .registerResearchItem();

        // The helmet owns this entry upstream — the other three pieces return
        // null — and all four crafts sit on its pages.
        new TinkererKamiResearchItem("ICHORCLOTH_ARMOR",
                new AspectList().add(Aspect.ARMOR, 2).add(Aspect.CLOTH, 1)
                        .add(Aspect.LIGHT, 1).add(Aspect.CRAFT, 1),
                17, 5, 5, new ItemStack(ConfigItems.itemIchorclothHelm))
                .setConcealed().setParents("ICHOR_CLOTH")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("IchorclothHelm"),
                        arcaneRecipePage("IchorclothChest"),
                        arcaneRecipePage("IchorclothLegs"),
                        arcaneRecipePage("IchorclothBoots"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORCLOTH_HELM_GEM",
                new AspectList().add(Aspect.WATER, 2).add(Aspect.HEAL, 1)
                        .add(Aspect.HUNGER, 1).add(Aspect.AURA, 1),
                18, 3, 5, new ItemStack(ConfigItems.itemIchorclothHelmGem))
                .setParents("ICHORCLOTH_ARMOR")
                .setPages(new ResearchPage("0"), infusionPage("IchorclothHelmGem"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORCLOTH_CHEST_GEM",
                new AspectList().add(Aspect.AIR, 2).add(Aspect.MOTION, 1)
                        .add(Aspect.FLIGHT, 1).add(Aspect.ELDRITCH, 1),
                17, 7, 5, new ItemStack(ConfigItems.itemIchorclothChestGem))
                .setParents("ICHORCLOTH_ARMOR")
                .setPages(new ResearchPage("0"), infusionPage("IchorclothChestGem"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORCLOTH_LEGS_GEM",
                new AspectList().add(Aspect.FIRE, 2).add(Aspect.HEAL, 1)
                        .add(Aspect.GREED, 1).add(Aspect.ENERGY, 1),
                17, 9, 5, new ItemStack(ConfigItems.itemIchorclothLegsGem))
                .setParents("ICHORCLOTH_ARMOR")
                .setPages(new ResearchPage("0"), infusionPage("IchorclothLegsGem"),
                        new ResearchPage("1"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHORCLOTH_BOOTS_GEM",
                new AspectList().add(Aspect.EARTH, 2).add(Aspect.TRAVEL, 1)
                        .add(Aspect.MINE, 1).add(Aspect.PLANT, 1),
                15, 10, 5, new ItemStack(ConfigItems.itemIchorclothBootsGem))
                .setParents("ICHORCLOTH_ARMOR")
                .setPages(new ResearchPage("0"), infusionPage("IchorclothBootsGem"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR_TOOLS",
                new AspectList().add(Aspect.TOOL, 2).add(Aspect.WEAPON, 1)
                        .add(Aspect.METAL, 1).add(Aspect.CRAFT, 1),
                13, 12, 5, new ItemStack(ConfigItems.itemIchorPick))
                .setConcealed().setParents("ICHORIUM").setParentsHidden("ICHORCLOTH_ROD")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("IchorPick"),
                        arcaneRecipePage("IchorShovel"),
                        arcaneRecipePage("IchorAxe"),
                        arcaneRecipePage("IchorSword"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR_PICK_GEM",
                new AspectList().add(Aspect.FIRE, 2).add(Aspect.TOOL, 1)
                        .add(Aspect.MINE, 1).add(Aspect.EARTH, 1),
                13, 15, 5, new ItemStack(ConfigItems.itemIchorPickAdv))
                .setParents("ICHOR_TOOLS")
                .setPages(new ResearchPage("0"), infusionPage("IchorPickAdv"),
                        new ResearchPage("1"))
                .registerResearchItem();

        // Upstream lists EARTH twice here; an AspectList adds, so the tag is
        // EARTH 3. Reproduced rather than tidied.
        new TinkererKamiResearchItem("ICHOR_SHOVEL_GEM",
                new AspectList().add(Aspect.EARTH, 2).add(Aspect.TOOL, 1)
                        .add(Aspect.MINE, 1).add(Aspect.EARTH, 1),
                15, 15, 5, new ItemStack(ConfigItems.itemIchorShovelAdv))
                .setParents("ICHOR_TOOLS")
                .setPages(new ResearchPage("0"), infusionPage("IchorShovelAdv"))
                .registerResearchItem();

        new TinkererKamiResearchItem("ICHOR_AXE_GEM",
                new AspectList().add(Aspect.WATER, 2).add(Aspect.TOOL, 1)
                        .add(Aspect.TREE, 1).add(Aspect.CROP, 1),
                16, 14, 5, new ItemStack(ConfigItems.itemIchorAxeAdv))
                .setParents("ICHOR_TOOLS")
                .setPages(new ResearchPage("0"), infusionPage("IchorAxeAdv"))
                .registerResearchItem();

        // ICHOR_SWORD_GEM waits on its object: the awakened ichorium sword is
        // not ported, so there is no recipe for its page to show.

        new TinkererKamiResearchItem("ICHOR_POUCH",
                new AspectList().add(Aspect.VOID, 2).add(Aspect.CLOTH, 1)
                        .add(Aspect.ELDRITCH, 1).add(Aspect.MAN, 1),
                13, 6, 5, new ItemStack(ConfigItems.itemIchorPouch))
                .setParents("ICHOR_CLOTH")
                .setPages(new ResearchPage("0"), infusionPage("IchorPouch"))
                .registerResearchItem();

        new TinkererKamiResearchItem("CAT_AMULET",
                new AspectList().add(Aspect.MIND, 2).add(Aspect.ORDER, 1)
                        .add(Aspect.DARKNESS, 1).add(Aspect.DEATH, 1),
                13, 10, 5, new ItemStack(ConfigItems.itemCatAmulet))
                .setParents("ICHORIUM")
                .setPages(new ResearchPage("0"), infusionPage("CatAmulet"))
                .registerResearchItem();

        new TinkererKamiResearchItem("BLOCK_TALISMAN",
                new AspectList().add(Aspect.VOID, 2).add(Aspect.DARKNESS, 1)
                        .add(Aspect.ELDRITCH, 1).add(Aspect.MAGIC, 1),
                14, 17, 5, new ItemStack(ConfigItems.itemBlockTalisman))
                .setParents("ICHOR_PICK_GEM", "ICHOR_SHOVEL_GEM")
                .setPages(new ResearchPage("0"), infusionPage("BlockTalisman"))
                .registerResearchItem();

        new TinkererKamiResearchItem("PLACEMENT_MIRROR",
                new AspectList().add(Aspect.CRAFT, 2).add(Aspect.CRYSTAL, 1)
                        .add(Aspect.ELDRITCH, 1).add(Aspect.MIND, 1),
                17, 16, 5, new ItemStack(ConfigItems.itemPlacementMirror))
                .setParents("BLOCK_TALISMAN")
                .setPages(new ResearchPage("0"), infusionPage("PlacementMirror"))
                .registerResearchItem();

        new TinkererKamiResearchItem("PROTOCLAY",
                new AspectList().add(Aspect.TOOL, 2).add(Aspect.MINE, 1)
                        .add(Aspect.MAN, 1).add(Aspect.MECHANISM, 1),
                12, 17, 5, new ItemStack(ConfigItems.itemProtoclay))
                .setParents("ICHOR_PICK_GEM").setParentsHidden("ICHOR_SHOVEL_GEM")
                .setPages(new ResearchPage("0"), infusionPage("Protoclay"))
                .registerResearchItem();
    }

    /**
     * The spell cloth and what it leads to: the osmotic enchanter, and the
     * experience talisman off to the side. Rooted in Thaumcraft's enchanted
     * fabric rather than in anything of Tinkerer's own.
     */
    private static void registerSpellClothBranch() {
        new TinkererResearchItem("SPELL_CLOTH",
                new AspectList().add(Aspect.MAGIC, 2).add(Aspect.CLOTH, 1),
                3, 2, 2, new ItemStack(ConfigItems.itemSpellCloth))
                .setParentsHidden("ENCHFABRIC")
                .setPages(new ResearchPage("0"),
                        crucibleRecipePage("SpellCloth"))
                .registerResearchItem();

        new TinkererResearchItem("ENCHANTER",
                new AspectList().add(Aspect.MAGIC, 2).add(Aspect.AURA, 1)
                        .add(Aspect.ELDRITCH, 1).add(Aspect.DARKNESS, 1).add(Aspect.MIND, 1),
                5, 4, 5, new ItemStack(ConfigBlocks.blockEnchanter))
                .setParents("SPELL_CLOTH")
                .setPages(new ResearchPage("0"), new ResearchPage("1"), new ResearchPage("2"),
                        infusionPage("Enchanter"))
                .registerResearchItem();

        new TinkererResearchItem("XP_TALISMAN",
                new AspectList().add(Aspect.GREED, 1).add(Aspect.MAGIC, 1).add(Aspect.MAN, 1),
                4, -1, 2, new ItemStack(ConfigItems.itemXpTalisman, 1, 1))
                .setParents("JARBRAIN", "SPELL_CLOTH").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("XpTalisman"))
                .setSecondary()
                .registerResearchItem();

        // Two that hang off Thaumcraft's own tree rather than Tinkerer's.
        new TinkererResearchItem("GAS_REMOVER",
                new AspectList().add(Aspect.DARKNESS, 2).add(Aspect.LIGHT, 2),
                -2, -7, 0, new ItemStack(ConfigItems.itemGasRemover))
                .setRound()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("GasRemover"))
                .setParents("GASEOUS_SHADOW")
                .registerResearchItem();

        new TinkererResearchItem("REVEALING_HELM",
                new AspectList().add(Aspect.AURA, 2).add(Aspect.ARMOR, 1),
                0, 0, 1, new ItemStack(ConfigItems.itemRevealingHelm))
                .setParents("GOGGLES").setParentsHidden("THAUMIUM")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("RevealingHelm"))
                .registerResearchItem();
    }

    /**
     * The gases, the hyperenergetic nitor and everything lit from it: the six
     * imbued fires and the infused crops beyond them, plus the funnel and the
     * restorer. Rooted in Thaumcraft's own NITOR.
     */
    private static void registerGaseousLightBranch() {
        new TinkererResearchItem("GASEOUS_LIGHT",
                new AspectList().add(Aspect.LIGHT, 2).add(Aspect.AIR, 1),
                0, -3, 1, new ItemStack(ConfigItems.itemGaseousLight))
                .setParents("NITOR")
                .setPages(new ResearchPage("0"),
                        crucibleRecipePage("GaseousLight"))
                .registerResearchItem();

        new TinkererResearchItem("GASEOUS_SHADOW",
                new AspectList().add(Aspect.DARKNESS, 2).add(Aspect.AIR, 1).add(Aspect.MOTION, 4),
                -1, -5, 2, new ItemStack(ConfigItems.itemGaseousShadow))
                .setSecondary().setParents("GASEOUS_LIGHT")
                .setPages(new ResearchPage("0"),
                        crucibleRecipePage("GaseousShadow"))
                .registerResearchItem();

        new TinkererResearchItem("BRIGHT_NITOR",
                new AspectList().add(Aspect.LIGHT, 2).add(Aspect.FIRE, 1)
                        .add(Aspect.ENERGY, 1).add(Aspect.AIR, 1),
                1, -5, 2, new ItemStack(ConfigItems.itemBrightNitor))
                .setParents("GASEOUS_LIGHT").setConcealed()
                .setPages(new ResearchPage("0"),
                        crucibleRecipePage("BrightNitor"))
                .setSecondary()
                .registerResearchItem();

        // The six imbued fires. Each is one hyperenergetic nitor in a crucible,
        // and each sits at its own spot on the map. Chaos is the odd one: no
        // second primal in its tags and complexity 3 where the rest are 2.
        imbuedFire("FIRE_AER", new AspectList().add(Aspect.FIRE, 5).add(Aspect.AIR, 5),
                3, -7, 2, ConfigBlocks.blockFireAir);
        imbuedFire("FIRE_PERDITIO", new AspectList().add(Aspect.FIRE, 5).add(Aspect.ENTROPY, 5),
                2, -8, 3, ConfigBlocks.blockFireChaos);
        imbuedFire("FIRE_TERRA", new AspectList().add(Aspect.FIRE, 5).add(Aspect.EARTH, 5),
                4, -6, 2, ConfigBlocks.blockFireEarth);
        imbuedFire("FIRE_IGNIS", new AspectList().add(Aspect.FIRE, 10),
                4, -4, 2, ConfigBlocks.blockFireIgnis);
        imbuedFire("FIRE_ORDO", new AspectList().add(Aspect.FIRE, 5).add(Aspect.ORDER, 5),
                3, -3, 2, ConfigBlocks.blockFireOrder);
        imbuedFire("FIRE_AQUA", new AspectList().add(Aspect.FIRE, 5).add(Aspect.WATER, 5),
                2, -2, 2, ConfigBlocks.blockFireWater);

        // Upstream's key for this is INFUSED_POTIONS, and it opens only once
        // all six fires are known.
        new TinkererResearchItem("INFUSED_POTIONS",
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ENTROPY, 5),
                7, -5, 2, new ItemStack(ConfigItems.itemInfusedPotion))
                .setParents("FIRE_PERDITIO", "FIRE_ORDO", "FIRE_IGNIS",
                        "FIRE_TERRA", "FIRE_AER", "FIRE_AQUA")
                .setParentsHidden("INFUSION").setConcealed()
                .setPages(new ResearchPage("0"), new ResearchPage("1"),
                        infusionPages("INFUSED_POTIONS", 4),
                        crucibleRecipePage("INFUSED_POTIONSPOT0"),
                        crucibleRecipePage("INFUSED_POTIONSPOT1"),
                        crucibleRecipePage("INFUSED_POTIONSPOT2"),
                        crucibleRecipePage("INFUSED_POTIONSPOT3"))
                .registerResearchItem();

        new TinkererResearchItem("FUNNEL",
                new AspectList().add(Aspect.TOOL, 1).add(Aspect.TRAVEL, 2),
                0, -7, 1, new ItemStack(ConfigBlocks.blockFunnel))
                .setParentsHidden("DISTILESSENTIA").setParents("BRIGHT_NITOR").setConcealed()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("Funnel"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("REPAIRER",
                new AspectList().add(Aspect.TOOL, 2).add(Aspect.CRAFT, 1)
                        .add(Aspect.ORDER, 1).add(Aspect.MAGIC, 1),
                -1, -9, 3, new ItemStack(ConfigBlocks.blockRepairer))
                .setConcealed().setParents("FUNNEL").setParentsHidden("THAUMIUM", "ENCHFABRIC")
                .setPages(new ResearchPage("0"),
                        infusionPage("Repairer"))
                .registerResearchItem();
    }

    /** Every imbued fire is built the same way; only its tags and spot differ. */
    private static void imbuedFire(String key, AspectList tags, int col, int row,
                                   int complexity, net.minecraft.block.Block block) {
        new TinkererResearchItem(key, tags, col, row, complexity, new ItemStack(block))
                .setParents("BRIGHT_NITOR").setConcealed()
                .setPages(new ResearchPage("0"), crucibleRecipePage(key))
                .setSecondary()
                .registerResearchItem();
    }

    /**
     * The dark quartz trunk and everything hanging off it. Dark quartz itself
     * is a stub: auto-unlocked, round, no aspects and no complexity — the way
     * into the tab rather than something to research.
     */
    private static void registerDarkQuartzBranch() {
        new TinkererResearchItem("DARK_QUARTZ",
                new AspectList(),
                -2, 2, 0, new ItemStack(ConfigItems.itemDarkQuartz))
                .setStub().setAutoUnlock().setRound()
                .setPages(new ResearchPage("0"),
                        benchRecipePage("DARK_QUARTZ0"),
                        benchRecipePage("DARK_QUARTZ1"),
                        benchRecipePage("DARK_QUARTZ2"),
                        benchRecipePage("DARK_QUARTZ3"),
                        benchRecipePage("DARK_QUARTZ4"),
                        benchRecipePage("DARK_QUARTZ5"))
                .registerResearchItem();

        new TinkererResearchItem("INTERFACE",
                new AspectList().add(Aspect.ENTROPY, 4).add(Aspect.ORDER, 4),
                -4, 2, 1, new ItemStack(ConfigBlocks.blockTransvectorInterface))
                .setParents("DARK_QUARTZ")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("TransvectorInterface"),
                        new ResearchPage("1"),
                        arcaneRecipePage("TransvectorConnector"),
                        new ResearchPage("2"))
                .registerResearchItem();

        new TinkererResearchItem("MAGNETS",
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1),
                -6, 3, 3, new ItemStack(ConfigBlocks.blockMagnet))
                .setParents("INTERFACE").setConcealed()
                .setPages(new ResearchPage("0"), new ResearchPage("1"),
                        arcaneRecipePage("Magnet"),
                        arcaneRecipePage("MobMagnet"),
                        crucibleRecipePage("SoulMould"))
                .registerResearchItem();

        // The hidden MIRROR parent is Thaumcraft's own — the dislocator is a
        // mirror trick, and upstream keeps it behind that research too.
        new TinkererResearchItem("DISLOCATOR",
                new AspectList().add(Aspect.TRAVEL, 2).add(Aspect.MECHANISM, 1).add(Aspect.ELDRITCH, 1),
                -6, 1, 3, new ItemStack(ConfigBlocks.blockTransvectorDislocator))
                .setConcealed().setParents("INTERFACE").setParentsHidden("MIRROR")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("TransvectorDislocator"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("ANIMATION_TABLET",
                new AspectList().add(Aspect.MECHANISM, 2).add(Aspect.METAL, 1)
                        .add(Aspect.MOTION, 1).add(Aspect.ENERGY, 1),
                -8, 2, 4, new ItemStack(ConfigBlocks.blockAnimationTablet))
                .setParents("MAGNETS")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("AnimationTablet"))
                .registerResearchItem();

        // Upstream's KEY_MOBILIZER is the string "LEVITATOR", and its strings
        // and recipe gate follow the key, not the class name.
        new TinkererResearchItem("LEVITATOR",
                new AspectList().add(Aspect.MOTION, 2).add(Aspect.ORDER, 2),
                -7, 5, 3, new ItemStack(ConfigBlocks.blockMobilizer))
                .setParents("MAGNETS")
                .setPages(new ResearchPage("0"),
                        infusionPage("Mobilizer"),
                        arcaneRecipePage("MobilizerRelay"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("CLEANSING_TALISMAN",
                new AspectList().add(Aspect.HEAL, 2).add(Aspect.ORDER, 1).add(Aspect.POISON, 1),
                -3, 4, 3, new ItemStack(ConfigItems.itemCleansingTalisman))
                .setSecondary().setParents("DARK_QUARTZ")
                .setPages(new ResearchPage("0"),
                        infusionPage("CleansingTalisman"))
                .registerResearchItem();

        new TinkererResearchItem("BLOOD_SWORD",
                new AspectList().add(Aspect.HUNGER, 2).add(Aspect.WEAPON, 1)
                        .add(Aspect.FLESH, 1).add(Aspect.SOUL, 1),
                -4, 6, 3, new ItemStack(ConfigItems.itemBloodSword))
                .setParents("CLEANSING_TALISMAN")
                .setPages(new ResearchPage("0"),
                        infusionPage("BloodSword"),
                        new ResearchPage("1"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("SUMMON",
                new AspectList().add(Aspect.WEAPON, 1).add(Aspect.BEAST, 3).add(Aspect.MAGIC, 3),
                -5, 8, 3, new ItemStack(ConfigBlocks.blockSummon))
                .setParents("BLOOD_SWORD")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("SUMMON0"),
                        benchRecipePage("SUMMON1"),
                        infusionPage("SUMMON"),
                        new ResearchPage("1"))
                .registerResearchItem();

        new TinkererResearchItem("PLATFORM",
                new AspectList().add(Aspect.SENSES, 2).add(Aspect.TREE, 1).add(Aspect.MOTION, 1),
                -2, 6, 3, new ItemStack(ConfigBlocks.blockPlatform))
                .setConcealed().setParents("CLEANSING_TALISMAN")
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("Platform"))
                .setSecondary()
                .registerResearchItem();
    }

    /**
     * The seven wand foci. The branch is rooted in Thaumcraft's own
     * FOCUSEXCAVATION, which is where the original hung it too, so the tab has
     * a way in; everything else descends from the smelting focus.
     */
    private static void registerFoci() {
        new TinkererResearchItem("FOCUS_SMELT",
                new AspectList().add(Aspect.FIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.MAGIC, 1),
                -2, -2, 2, new ItemStack(ConfigItems.focusSmelt))
                .setParents("FOCUSEXCAVATION").setConcealed()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("FocusSmelt"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_FLIGHT",
                new AspectList().add(Aspect.MOTION, 1).add(Aspect.MAGIC, 1).add(Aspect.AIR, 2),
                -3, -4, 2, new ItemStack(ConfigItems.focusFlight))
                .setParents("FOCUS_SMELT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusFlight"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_DEFLECT",
                new AspectList().add(Aspect.MOTION, 2).add(Aspect.AIR, 1)
                        .add(Aspect.ORDER, 1).add(Aspect.DEATH, 1),
                -4, -3, 3, new ItemStack(ConfigItems.focusDeflect))
                .setConcealed().setParents("FOCUS_SMELT")
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusDeflect"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_TELEKINESIS",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.MOTION, 1),
                -4, -6, 2, new ItemStack(ConfigItems.focusTelekinesis))
                .setParents("FOCUS_FLIGHT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusTelekinesis"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_DISLOCATION",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.EXCHANGE, 1),
                -5, -5, 2, new ItemStack(ConfigItems.focusDislocation))
                .setSecondary().setParents("FOCUS_FLIGHT").setConcealed()
                .setPages(new ResearchPage("0"), new ResearchPage("1"),
                        infusionPage("FocusDislocation"))
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_HEAL",
                new AspectList().add(Aspect.HEAL, 2).add(Aspect.SOUL, 1).add(Aspect.MAGIC, 1),
                -6, -4, 2, new ItemStack(ConfigItems.focusHeal))
                .setParents("FOCUS_DEFLECT").setConcealed()
                .setPages(new ResearchPage("0"),
                        infusionPage("FocusHeal"))
                .setSecondary()
                .registerResearchItem();

        new TinkererResearchItem("FOCUS_ENDER_CHEST",
                new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.VOID, 1).add(Aspect.MAGIC, 1),
                -6, -2, 2, new ItemStack(ConfigItems.focusEnderChest))
                .setParents("FOCUS_DEFLECT").setConcealed()
                .setPages(new ResearchPage("0"),
                        arcaneRecipePage("FocusEnderChest"))
                .registerResearchItem();
    }

    /** The original's ResearchHelper.arcaneRecipePage, over our recipe map. */
    private static ResearchPage arcaneRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeArcane(recipeKey));
    }

    /** The original's ResearchHelper.infusionPage. */
    private static ResearchPage infusionPage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeInfusion(recipeKey));
    }

    /**
     * The original's two-argument ResearchHelper.infusionPage: one page showing
     * {@code count} recipes filed under {@code key0}, {@code key1}, and so on.
     */
    private static ResearchPage infusionPages(String recipeKeyPrefix, int count) {
        thaumcraft.api.crafting.InfusionRecipe[] found =
                new thaumcraft.api.crafting.InfusionRecipe[count];
        for (int i = 0; i < count; i++) {
            found[i] = ConfigResearch.recipeInfusion(recipeKeyPrefix + i);
        }
        return new ResearchPage(found);
    }

    /** The original's ResearchHelper.recipePage — a plain bench recipe. */
    private static ResearchPage benchRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeI(recipeKey));
    }

    /** The original's ResearchHelper.crucibleRecipePage. */
    private static ResearchPage crucibleRecipePage(String recipeKey) {
        return new ResearchPage(ConfigResearch.recipeCrucible(recipeKey));
    }
}
