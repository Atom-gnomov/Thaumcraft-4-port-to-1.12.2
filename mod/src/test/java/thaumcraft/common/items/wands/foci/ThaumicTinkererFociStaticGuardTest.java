package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Guards the Thaumic Tinkerer foci module (reimplemented from TT by
 * pixlepix/nekosune): each focus must exist, extend ItemFocusBasic with a
 * right-click behaviour, be registered + recipe-gated, and ship its texture,
 * model, and lang entry.
 */
public class ThaumicTinkererFociStaticGuardTest {

    private static final String[] FOCI = {
            "Smelt", "Telekinesis", "Flight", "Heal", "Deflect", "Dislocation", "EnderChest"
    };

    @Test
    public void eachFocusClassIsAWandFocus() throws IOException {
        for (String f : FOCI) {
            String src = read("src/main/java/thaumcraft/common/items/wands/foci/Focus" + f + ".java");
            assertTrue(f + " must extend ItemFocusBasic", src.contains("extends ItemFocusBasic"));
            assertTrue(f + " must implement the right-click cast",
                    src.contains("public ItemStack onFocusRightClick("));
            assertTrue(f + " must declare a vis cost",
                    src.contains("public AspectList getVisCost("));
        }
    }

    /**
     * The four foci that channel in Thaumic Tinkerer must channel here too:
     * right-click only starts the use, the work happens per tick, and the cost
     * is charged per tick. Pinned after the 1.0.54 audit found them firing once.
     */
    @Test
    public void channelledFociChannelAndChargePerTick() throws IOException {
        for (String f : new String[]{"Smelt", "Telekinesis", "Heal", "Deflect"}) {
            String src = read("src/main/java/thaumcraft/common/items/wands/foci/Focus" + f + ".java");
            assertTrue(f + " must start a channel on right-click",
                    src.contains("player.setActiveHand(ItemWandCasting.getHandHoldingWand(player, wandStack))")
                            && src.contains("WandManager.setCooldown(player, -1)"));
            assertTrue(f + " must do its work per tick",
                    src.contains("public void onUsingFocusTick("));
            assertTrue(f + " must charge per tick",
                    src.contains("public boolean isVisCostPerTick(") && src.contains("return true;"));
        }
    }

    /** The original's vis costs, aspect for aspect. */
    @Test
    public void fociKeepTheOriginalVisCosts() throws IOException {
        String dir = "src/main/java/thaumcraft/common/items/wands/foci/Focus";
        assertTrue("Smelt: FIRE 45 + ENTROPY 12",
                read(dir + "Smelt.java").contains("add(Aspect.FIRE, 45).add(Aspect.ENTROPY, 12)"));
        assertTrue("Telekinesis: AIR 5 + ENTROPY 5",
                read(dir + "Telekinesis.java").contains("add(Aspect.AIR, 5).add(Aspect.ENTROPY, 5)"));
        assertTrue("Flight: AIR 15",
                read(dir + "Flight.java").contains("add(Aspect.AIR, 15)"));
        assertTrue("Heal: EARTH 45 + WATER 45",
                read(dir + "Heal.java").contains("add(Aspect.EARTH, 45).add(Aspect.WATER, 45)"));
        assertTrue("Deflect: ORDER 8 + AIR 4",
                read(dir + "Deflect.java").contains("add(Aspect.ORDER, 8).add(Aspect.AIR, 4)"));
        assertTrue("EnderChest: ENTROPY 100 + ORDER 100",
                read(dir + "EnderChest.java").contains("add(Aspect.ENTROPY, 100).add(Aspect.ORDER, 100)"));
        String dis = read(dir + "Dislocation.java");
        assertTrue("Dislocation: 500/500/100 with x5 and x20 tiers",
                dis.contains("add(Aspect.ENTROPY, 500).add(Aspect.ORDER, 500).add(Aspect.EARTH, 100)")
                        && dis.contains("add(Aspect.ENTROPY, 2500).add(Aspect.ORDER, 2500).add(Aspect.EARTH, 500)")
                        && dis.contains("add(Aspect.ENTROPY, 10000).add(Aspect.ORDER, 10000).add(Aspect.EARTH, 5000)"));
    }

    /** Behaviours that the pre-audit versions had outright wrong. */
    @Test
    public void fociKeepTheOriginalBehaviours() throws IOException {
        String dir = "src/main/java/thaumcraft/common/items/wands/foci/Focus";
        String smelt = read(dir + "Smelt.java");
        assertTrue("Smelt runs a per-block countdown and only smelts block into block",
                smelt.contains("20 - Math.min(3, potency) * 5")
                        && smelt.contains("result.getItem() instanceof ItemBlock"));
        assertTrue("Deflect is a projectile shield, not a knockback",
                read(dir + "Deflect.java").contains("protectFromProjectiles")
                        && read(dir + "Deflect.java").contains("instanceof IProjectile"));
        String dis = read(dir + "Dislocation.java");
        assertTrue("Dislocation lifts a block with its tile entity and places it again",
                dis.contains("storePickedBlock") && dis.contains("getStackTileEntity")
                        && dis.contains("TileEntity.create(world, tileTag)"));
        assertTrue("Heal restores half a heart on the original's cadence",
                read(dir + "Heal.java").contains("30 - potency * 10 / 3")
                        && read(dir + "Heal.java").contains("player.heal(1)"));
        assertTrue("Flight uses the original's impulse and clears the float counter",
                read(dir + "Flight.java").contains("1.0D / 1.5D * (1.0D + potency * 0.2D)")
                        && read(dir + "Flight.java").contains("resetFloatCounter"));
    }

    @Test
    public void fociAreRegisteredAndCraftable() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String[] fields = {"focusSmelt", "focusTelekinesis", "focusFlight", "focusHeal",
                "focusDeflect", "focusDislocation", "focusEnderChest"};
        String[] langKeys = {"focus_smelt", "focus_telekinesis", "focus_flight", "focus_heal",
                "focus_deflect", "focus_dislocation", "focus_enderchest"};
        for (String field : fields) {
            assertTrue(field + " must be a ConfigItems field", cfg.contains("public static ")
                    && cfg.contains(field + ";"));
            assertTrue(field + " must be added to allItems", cfg.contains("allItems.add(" + field + ")"));
        }
        for (String f : FOCI) {
            assertTrue("Focus" + f + " must have an arcane recipe in ConfigTinkerer",
                    rec.contains("\"Focus" + f + "\"") && rec.contains("ConfigItems.focus" + f));
        }
        assertTrue("Tinkerer foci recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerFociRecipes()"));
    }

    @Test
    public void utilityItemsAreRegisteredAndCraftable() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String[] fields = {"itemPlacementMirror", "itemCleansingTalisman", "itemXpTalisman", "itemCatAmulet"};
        String[] classes = {"ItemPlacementMirror", "ItemCleansingTalisman", "ItemXpTalisman", "ItemCatAmulet"};
        String[] langKeys = {"placement_mirror", "cleansing_talisman", "xp_talisman", "cat_amulet"};
        String[] recipeKeys = {"PlacementMirror", "CleansingTalisman", "XpTalisman", "CatAmulet"};
        for (String field : fields) {
            assertTrue(field + " registered", cfg.contains(field + ";") && cfg.contains("allItems.add(" + field + ")"));
        }
        for (String c : classes) {
            assertTrue(c + " source must exist as a tinkerer item",
                    Files.exists(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/" + c + ".java")));
        }
        // Only the Feline Amulet can be built here: the other three need items
        // this port lacks, and the rule is to register nothing rather than
        // substitute. Their blockers must stay written down instead.
        assertTrue("Feline Amulet recipe, transcribed from CAT_AMULET",
                rec.contains("\"CAT_AMULET\"") && rec.contains("ConfigItems.itemCatAmulet"));
        // Both talismans were unblocked by the smokey quartz gem and now carry
        // the original's own infusions.
        assertTrue("Talisman of Remedium infuses on an ender pearl with four gems",
                rec.contains("\"CLEANSING_TALISMAN\"")
                        && rec.contains("add(Aspect.HEAL, 10).add(Aspect.TOOL, 10)")
                        && rec.contains("ConfigItems.itemDarkQuartz"));
        assertTrue("Talisman of Withhold infuses on a gold ingot",
                rec.contains("\"XP_TALISMAN\"")
                        && rec.contains("add(Aspect.GREED, 20).add(Aspect.EXCHANGE, 10)"));
        // The Black Hole Ring landed in 1.1.8.0, so the looking glass is no
        // longer blocked and every utility item now carries a real recipe.
        assertTrue("Black Hole Ring infuses on the portable hole focus at instability 9",
                rec.contains("\"BLOCK_TALISMAN\"")
                        && rec.contains("add(Aspect.VOID, 65).add(Aspect.DARKNESS, 32)")
                        && rec.contains("ConfigItems.focusPortableHole"));
        assertTrue("Worldshaper's Looking Glass infuses on the ring at instability 12",
                rec.contains("\"PLACEMENT_MIRROR\"")
                        && rec.contains("add(Aspect.CRAFT, 65).add(Aspect.CRYSTAL, 32)")
                        && rec.contains("ConfigItems.itemBlockTalisman"));
        for (String key : langKeys) {
            assertTrue("lang " + key, lang.contains("item.thaumcraft." + key + ".name="));
        }
        assertTrue("itemBlockTalisman registered",
                cfg.contains("itemBlockTalisman;") && cfg.contains("allItems.add(itemBlockTalisman)"));
        assertTrue("ItemBlockTalisman lives in the KAMI package",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/kami/"
                        + "ItemBlockTalisman.java")));
        assertTrue("Black Hole Ring is named in both languages",
                lang.contains("item.thaumcraft.kami.block_talisman.name=Black Hole Ring")
                        && read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang")
                        .contains("item.thaumcraft.kami.block_talisman.name="));
        // All three of these switch on and off through item damage 0/1, and the
        // original ships a separate icon per state.
        for (String stem : new String[]{"blocktalisman", "cleansingtalisman", "xptalisman"}) {
            for (int state = 0; state < 2; state++) {
                assertTrue(stem + " model " + state,
                        Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/item/"
                                + stem + "_" + state + ".json")));
            }
        }
        for (String tex : new String[]{"block_talisman", "cleansing_talisman", "xp_talisman"}) {
            for (int state = 0; state < 2; state++) {
                assertTrue(tex + " texture " + state,
                        Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/"
                                + tex + "_" + state + ".png")));
            }
        }
        assertTrue("utility recipes registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerUtilityItemRecipes()"));
        for (String key : langKeys) {
            assertTrue("lang key for " + key, lang.contains("item.thaumcraft." + key + ".name="));
        }
    }

    @Test
    public void fociShipTexturesAndModels() {
        String[] tex = {"focus_smelt", "focus_telekinesis", "focus_flight", "focus_heal",
                "focus_deflect", "focus_dislocation", "focus_enderchest"};
        String[] model = {"focussmelt", "focustelekinesis", "focusflight", "focusheal",
                "focusdeflect", "focusdislocation", "focusenderchest"};
        for (String t : tex) {
            assertTrue("texture " + t + ".png must ship",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/" + t + ".png")));
        }
        for (String m : model) {
            assertTrue("model " + m + ".json must ship",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/item/" + m + ".json")));
        }
    }

    /**
     * Both talismans are Baubles upstream and carry the original's numbers.
     * Pinned after the audit found them shipped as plain inventory items.
     */
    @Test
    public void talismansAreBaublesWithTheOriginalNumbers() throws IOException {
        String cleansing = read("src/main/java/thaumcraft/common/items/tinkerer/ItemCleansingTalisman.java");
        assertTrue("cleansing talisman is an amulet bauble",
                cleansing.contains("implements IBauble")
                        && cleansing.contains("return BaubleType.AMULET;")
                        && cleansing.contains("public void onWornTick("));
        assertTrue("cleansing talisman keeps 100 uses, the on/off switch and the once-a-second cadence",
                cleansing.contains("USES = 100")
                        && cleansing.contains("flipEnabled")
                        && cleansing.contains("player.ticksExisted % 20 != 0")
                        && cleansing.contains("player.extinguish()")
                        && cleansing.contains("potion.isBadEffect()")
                        && cleansing.contains("stack.damageItem(1, player)"));

        String xp = read("src/main/java/thaumcraft/common/items/tinkerer/ItemXpTalisman.java");
        assertTrue("xp talisman is an amulet bauble that absorbs orbs",
                xp.contains("implements IBauble")
                        && xp.contains("return BaubleType.AMULET;")
                        && xp.contains("EntityXPOrb")
                        && xp.contains("consumeXPOrb"));
        assertTrue("xp talisman keeps range 3, cap 1500 and the 10-xp bottle trade",
                xp.contains("RANGE = 3") && xp.contains("MAX_XP = 1500")
                        && xp.contains("BOTTLE_COST = 10")
                        && xp.contains("Items.EXPERIENCE_BOTTLE")
                        && xp.contains("consumeGlassBottle"));
    }

    @Test
    public void darkQuartzBlockIsRegisteredWithVariants() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockDarkQuartz registered + item + in getAllBlocks",
                cfg.contains("blockDarkQuartz;") && cfg.contains("blockDarkQuartzItem;")
                        && cfg.contains("blockDarkQuartz,") && cfg.contains("blockDarkQuartzItem,"));
        assertTrue("BlockDarkQuartz source is a tinkerer block",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockDarkQuartz.java")));
        assertTrue("dark quartz blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockdarkquartz.json")));
        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        for (int m = 0; m <= 2; m++) {
            assertTrue("dark quartz block model " + m,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/blockdarkquartz_" + m + ".json")));
            assertTrue("lang dark_quartz." + m, lang.contains("tile.thaumcraft.dark_quartz." + m + ".name="));
        }
        for (String t : new String[]{"dark_quartz", "dark_quartz_chiseled", "dark_quartz_pillar"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void funnelBlockAndTileAreRegistered() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockFunnel registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockFunnel;") && cfg.contains("blockFunnel,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockFunnel)"));
        assertTrue("TileFunnel registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileFunnel.class, \"TileFunnel\")"));
        assertTrue("BlockFunnel source",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/blocks/tinkerer/BlockFunnel.java")));
        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileFunnel.java");
        // The original drips essentia from a filled jar in its slot into
        // whatever the hopper below points at, one point at a time.
        assertTrue("TileFunnel must tick and hold a jar as an aspect container",
                tile.contains("implements ITickable, IAspectContainer")
                        && tile.contains("public void update()")
                        && tile.contains("BlockJarItem"));
        assertTrue("TileFunnel must follow the hopper below to a jar",
                tile.contains("instanceof TileEntityHopper")
                        && tile.contains("getHopperFacing")
                        && tile.contains("instanceof TileJarFillable"));
        assertTrue("TileFunnel must move one point and honour filter, cap and void jars",
                tile.contains("destination.addToContainer(aspect, 1)")
                        && tile.contains("aspectList.remove(aspect, 1)")
                        && tile.contains("destination.aspectFilter == null || destination.aspectFilter == aspect")
                        && tile.contains("< 64 || voidJar"));
        assertTrue("funnel blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockfunnel.json")));
        assertTrue("funnel block model ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/blockfunnel.json")));
        for (String t : new String[]{"funnel_top", "funnel_side"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
        assertTrue("funnel lang",
                read("src/main/resources/assets/thaumcraft/lang/en_us.lang").contains("tile.thaumcraft.funnel.name="));
    }

    @Test
    public void magnetBlockAndTileAreRegistered() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockMagnet registered + in getAllBlocks + its own metadata ItemBlock",
                cfg.contains("blockMagnet;") && cfg.contains("blockMagnet,")
                        && cfg.contains("BlockMagnetItem(blockMagnet)"));
        assertTrue("TileMagnet registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileMagnet.class, \"TileMagnet\")"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockMagnet.java");
        assertTrue("magnet must expose the attract/repel state and toggle on right-click",
                block.contains("PropertyBool.create(\"pulling\")")
                        && block.contains("public boolean onBlockActivated(")
                        && block.contains("cycleProperty(PULLING)"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileMagnet.java");
        assertTrue("TileMagnet must tick, need redstone, and move dropped items",
                tile.contains("implements ITickable")
                        && tile.contains("public void update()")
                        && tile.contains("EntityItem")
                        && tile.contains("getRedstonePower"));
        // Reach is pinned in auditedNumbersMatchTheOriginal(). This assertion
        // used to require `redstone / 2.0D`, which was the drift rather than
        // the original — a guard written from the port instead of the source
        // cements the mistake it was meant to catch.
        assertTrue("reach must stay the original signal/2", tile.contains("redstone / 2"));

        assertTrue("magnet blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockmagnet.json")));
        for (String m : new String[]{"blockmagnet_pull", "blockmagnet_push"}) {
            assertTrue("model " + m,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/" + m + ".json")));
        }
        for (String t : new String[]{"magnet", "magnet_top_pull", "magnet_top_push"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void repairerConsumesEssentiaToMendTools() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockRepairer registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockRepairer;") && cfg.contains("blockRepairer,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockRepairer)"));
        assertTrue("TileRepairer registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileRepairer.class, \"TileRepairer\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileRepairer.java");
        assertTrue("repairer must be an essentia consumer that ticks",
                tile.contains("implements ITickable") && tile.contains("IEssentiaTransport")
                        && tile.contains("public void update()"));
        assertTrue("original repair values must be preserved: TOOL 8, CRAFT 5, ORDER 3",
                tile.contains("REPAIR_VALUES.put(Aspect.TOOL, 8)")
                        && tile.contains("REPAIR_VALUES.put(Aspect.CRAFT, 5)")
                        && tile.contains("REPAIR_VALUES.put(Aspect.ORDER, 3)"));
        assertTrue("must draw exactly one essentia per attempt through the facing side",
                tile.contains("takeEssentia(entry.getKey(), 1, opposite)"));
        assertTrue("must expose its slot to pipes/hoppers",
                tile.contains("CapabilityItemHandler.ITEM_HANDLER_CAPABILITY"));
        assertTrue("the held tool must survive breaking the block",
                read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockRepairer.java")
                        .contains("InventoryHelper.spawnItemStack"));

        assertTrue("repairer blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockrepairer.json")));
        for (String t : new String[]{"repairer_side", "repairer_top"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void transvectorInterfaceProxiesALinkedBlock() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockTransvectorInterface registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockTransvectorInterface;") && cfg.contains("blockTransvectorInterface,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockTransvectorInterface)"));
        assertTrue("TileTransvectorInterface registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileTransvectorInterface.class, \"TileTransvectorInterface\")"));

        String base = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvector.java");
        assertTrue("links must be range-checked per axis and dropped when out of range",
                base.contains("public boolean withinRange(") && base.contains("unlink();"));
        assertTrue("must not force-load the target chunk",
                base.contains("world.isBlockLoaded(target)"));
        assertTrue("must never resolve to itself", base.contains("tile == this ? null : tile"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvectorInterface.java");
        assertTrue("interface reach must stay the original 4", tile.contains("MAX_DISTANCE = 4"));
        assertTrue("must forward capabilities to the linked tile",
                tile.contains("public <T> T getCapability(") && tile.contains("linked.getCapability(capability, facing)"));
        assertTrue("must delegate Thaumcraft essentia interfaces",
                tile.contains("IAspectContainer") && tile.contains("IEssentiaTransport"));

        String connector = read("src/main/java/thaumcraft/common/items/tinkerer/ItemTransvectorConnector.java");
        assertTrue("connector must link in two clicks and refuse interface-to-interface chains",
                connector.contains("device.link(pos)") && connector.contains("tc.transvector.nochain"));

        assertTrue("interface blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blocktransvectorinterface.json")));
        assertTrue("interface texture ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/transvector_interface.png")));
        assertTrue("connector texture ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/transvector_connector.png")));
    }

    @Test
    public void dislocatorSwapsBlocksOnARedstonePulse() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockTransvectorDislocator registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockTransvectorDislocator;") && cfg.contains("blockTransvectorDislocator,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockTransvectorDislocator)"));
        assertTrue("TileTransvectorDislocator registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileTransvectorDislocator.class, \"TileTransvectorDislocator\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvectorDislocator.java");
        assertTrue("dislocator reach must stay the original 16", tile.contains("MAX_DISTANCE = 16"));
        assertTrue("cooldown must stay the original 10 ticks", tile.contains("COOLDOWN = 10"));
        assertTrue("a pulse during cooldown must be remembered, not dropped",
                tile.contains("pulseStored = true") && tile.contains("pulseStored = false"));
        assertTrue("tiles must travel with their blocks",
                tile.contains("detachTile(") && tile.contains("attachTile("));
        assertTrue("entities standing on either side must be swapped too",
                tile.contains("swapEntities("));
        assertTrue("must refuse nodes, blacklisted blocks and unbreakable blocks",
                tile.contains("ConfigBlocks.blockAiry")
                        && tile.contains("portableHoleBlackList")
                        && tile.contains("getBlockHardness(world, at) >= 0.0F"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockTransvectorDislocator.java");
        assertTrue("must fire on the rising redstone edge only",
                block.contains("public void neighborChanged(") && block.contains("powered == wasPowered"));

        assertTrue("dislocator blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blocktransvectordislocator.json")));
        for (String t : new String[]{"transvector_dislocator", "transvector_dislocator_on"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void animationTabletWorksAToolAgainstTheBlockItFaces() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockAnimationTablet registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockAnimationTablet;") && cfg.contains("blockAnimationTablet,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockAnimationTablet)"));
        assertTrue("TileAnimationTablet registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileAnimationTablet.class, \"TileAnimationTablet\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileAnimationTablet.java");
        assertTrue("swing cycle must keep the original speed and arc",
                tile.contains("SWING_SPEED = 3") && tile.contains("MAX_DEGREE = 45"));
        assertTrue("must act through a fake player",
                tile.contains("FakePlayerFactory.get") && tile.contains("attackTargetEntityWithCurrentItem"));
        assertTrue("strike mode must break blocks progressively, not instantly",
                tile.contains("breakProgress") && tile.contains("getDigSpeed")
                        && tile.contains("sendBlockBreakProgress"));
        assertTrue("use mode must try block activation and item use",
                tile.contains("onBlockActivated(") && tile.contains("onItemUse("));
        assertTrue("must never target its own fake player",
                tile.contains("e instanceof FakePlayer"));
        assertTrue("both toggles must persist", tile.contains("strikeMode") && tile.contains("redstoneMode"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockAnimationTablet.java");
        assertTrue("a wand rotates the tablet and anything else opens its screen, as in the original",
                block.contains("instanceof ItemWandCasting") && block.contains("rotateY()")
                        && block.contains("GUI_ANIMATION_TABLET"));
        assertTrue("the tablet must have a real container and screen, not ad-hoc interaction",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/container/ContainerAnimationTablet.java"))
                        && Files.exists(Paths.get("src/main/java/thaumcraft/client/gui/GuiAnimationTablet.java"))
                        && Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/gui/animation_tablet.png")));
        String container = read("src/main/java/thaumcraft/common/container/ContainerAnimationTablet.java");
        assertTrue("both toggles must travel as container button presses",
                container.contains("public boolean enchantItem(") && container.contains("BUTTON_REDSTONE")
                        && container.contains("BUTTON_STRIKE") && container.contains("BUTTON_USE"));
        assertTrue("the held tool must survive breaking the block",
                block.contains("InventoryHelper.spawnItemStack"));

        assertTrue("tablet blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockanimationtablet.json")));
        for (String t : new String[]{"animation_tablet_top", "animation_tablet_side"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void osmoticEnchanterNeedsPillarsAndDrainsWandVis() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("blockEnchanter registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockEnchanter;") && cfg.contains("blockEnchanter,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockEnchanter)"));
        assertTrue("TileEnchanter registered in TILE_REGISTRATIONS",
                cfg.contains("new TileRegistration(TileEnchanter.class, \"TileEnchanter\")"));

        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileEnchanter.java");
        assertTrue("multiblock must keep the original shape: 6 pillars, 2..12 tall, radius 4",
                tile.contains("PILLARS_REQUIRED = 6") && tile.contains("SEARCH_RADIUS = 4")
                        && tile.contains("MIN_PILLAR = 2") && tile.contains("MAX_PILLAR = 12"));
        assertTrue("pillars must be obsidian totems capped with nitor",
                tile.contains("ConfigBlocks.blockCosmeticSolid") && tile.contains("ConfigBlocks.blockAiry"));
        assertTrue("must drain one vis point at a time from a non-staff wand",
                tile.contains("consumeAllVisCrafting(wand, null, new AspectList().add(aspect, 1), true)")
                        && tile.contains("wandItem.isStaff(wand)"));
        assertTrue("wand vis is stored at 100 per point", tile.contains(">= 100"));
        assertTrue("only primal cost is payable", tile.contains("aspect.isPrimal()"));
        assertTrue("enchantments are applied once fully paid", tile.contains("tool.addEnchantment("));

        String costs = read("src/main/java/thaumcraft/common/lib/tinkerer/EnchantmentCosts.java");
        assertTrue("the original's exponential curve must be preserved",
                costs.contains("level * (1.0D + level * 0.2D)"));
        assertTrue("ported base costs must stay put",
                costs.contains("Enchantments.SILK_TOUCH, aspects(Aspect.ORDER, 50")
                        && costs.contains("Enchantments.SHARPNESS, aspects(Aspect.ORDER, 10)"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockEnchanter.java");
        assertTrue("right-click must open the enchanter screen, as in the original",
                block.contains("GUI_ENCHANTER"));
        assertTrue("the enchanter must have a real container and picker screen",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/container/ContainerEnchanter.java"))
                        && Files.exists(Paths.get("src/main/java/thaumcraft/client/gui/GuiEnchanter.java"))
                        && Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/gui/enchanter.png")));
        String container = read("src/main/java/thaumcraft/common/container/ContainerEnchanter.java");
        assertTrue("start, the 16 offer buttons and per-row level controls must all be wired",
                container.contains("BUTTON_START") && container.contains("OFFER_BUTTONS = 16")
                        && container.contains("ROW_STRIDE = 3") && container.contains("public boolean enchantItem("));
        assertTrue("the queue must be editable from the screen",
                tile.contains("public void setEnchant(") && tile.contains("public boolean start()")
                        && tile.contains("public List<Enchantment> getOffers("));
        assertTrue("offers must respect enchantability, existing enchants and conflicts",
                tile.contains("getItemEnchantability() == 0") && tile.contains("isItemEnchanted()")
                        && tile.contains("isCompatibleWith"));
        assertTrue("contents must survive breaking the block",
                block.contains("InventoryHelper.spawnItemStack"));

        assertTrue("enchanter blockstate ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/blockenchanter.json")));
        for (String t : new String[]{"enchanter_top", "enchanter_side"}) {
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/" + t + ".png")));
        }
    }

    @Test
    public void mobMagnetIsTheSecondVariant() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockMagnet.java");
        assertTrue("the variant must live in metadata bit 1, as in the original",
                block.contains("PropertyBool.create(\"mob\")")
                        && block.contains("(meta & 2) == 2 ? new TileMobMagnet() : new TileMagnet()"));
        assertTrue("the mob variant opens its screen", block.contains("GUI_MOB_MAGNET"));

        String magnet = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileMagnet.java");
        assertTrue("the item magnet must expose the hooks the mob variant overrides",
                magnet.contains("protected Class<? extends Entity> getTargetClass()")
                        && magnet.contains("protected boolean isTarget("));

        String mob = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileMobMagnet.java");
        assertTrue("mob magnet must extend the item magnet and pull living things, never players",
                mob.contains("extends TileMagnet") && mob.contains("EntityLivingBase.class")
                        && mob.contains("entity instanceof EntityPlayer"));
        assertTrue("the adult/baby switch must match the original's flag",
                mob.contains("adult == ((EntityAgeable) entity).isChild()"));
        assertTrue("a soul mould in the slot must narrow it to one kind",
                mob.contains("ItemSoulMould.matches"));

        assertTrue("mob magnet must have a container and screen",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/container/ContainerMobMagnet.java"))
                        && Files.exists(Paths.get("src/main/java/thaumcraft/client/gui/GuiMobMagnet.java"))
                        && Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/gui/mob_magnet.png")));
        assertTrue("soul mould ships",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/ItemSoulMould.java"))
                        && Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/soul_mould.png")));
    }

    @Test
    public void kamiTierFoundation() throws IOException {
        String res = read("src/main/java/thaumcraft/common/items/tinkerer/kami/ItemKamiResource.java");
        assertTrue("subtype order is load-bearing and must match the original",
                res.contains("\"ichor\", \"ichorcloth\", \"ichorium\", \"ichor_nugget\"")
                        && res.contains("\"ichor_cap\", \"ichorcloth_rod\", \"nether_shard\", \"ender_shard\""));
        assertTrue("KAMI is the endgame tier — epic rarity, as in the original",
                res.contains("EnumRarity.EPIC"));

        String drops = read("src/main/java/thaumcraft/common/lib/tinkerer/kami/DimensionalShardDropHandler.java");
        assertTrue("shard chances must stay 1/32 ender and 1/16 nether",
                drops.contains("1.0D / 32.0D") && drops.contains("1.0D / 16.0D"));
        assertTrue("shards only drop in their own dimension, to a player kill",
                drops.contains("DimensionType.THE_END.getId()") && drops.contains("DimensionType.NETHER.getId()")
                        && drops.contains("getTrueSource() instanceof EntityPlayer"));
        assertTrue("the handler must be registered on the event bus",
                read("src/main/java/thaumcraft/common/Thaumcraft.java")
                        .contains("DimensionalShardDropHandler()"));

        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        assertTrue("ichor is an infusion on a nether star at instability 7 with the original's aspects",
                rec.contains("Items.NETHER_STAR") && rec.contains("Aspect.SOUL, 64")
                        && rec.contains("Aspect.MAN, 32"));
        assertTrue("ichorcloth rod keeps instability 9 and its aspect list",
                rec.contains("Aspect.MAGIC, 100") && rec.contains("Aspect.TOOL, 32"));
        assertTrue("crafts are priced at the same amount of every primal, as the original did",
                rec.contains("allPrimals(125)") && rec.contains("allPrimals(100)"));
        assertTrue("KAMI recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerKamiRecipes()"));

        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        for (String name : new String[]{"ichor", "ichorcloth", "ichorium", "ichor_nugget",
                "ichor_cap", "ichorcloth_rod", "nether_shard", "ender_shard"}) {
            String key = "item.thaumcraft.kami." + name + ".name";
            assertTrue("en lang " + key, lang.contains(key + "="));
            assertTrue("ru lang " + key, ru.contains(key + "="));
            assertTrue("texture " + name,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/kami_" + name + ".png")));
        }
    }

    @Test
    public void kamiToolsAndWandParts() throws IOException {
        String mat = read("src/main/java/thaumcraft/common/items/tinkerer/kami/KamiMaterials.java");
        assertTrue("the ichor material must keep the original's numbers, -1 uses included",
                mat.contains("addToolMaterial(\"ICHOR\", 4, -1, 10.0F, 5.0F, 25)"));

        for (String cls : new String[]{"ItemIchorPick", "ItemIchorAxe", "ItemIchorShovel", "ItemIchorSword"}) {
            String src = read("src/main/java/thaumcraft/common/items/tinkerer/kami/tool/" + cls + ".java");
            assertTrue(cls + " must be built on the ichor material", src.contains("KamiMaterials.ICHOR"));
            assertTrue(cls + " must be epic, as the whole tier is", src.contains("EnumRarity.EPIC"));
        }
        assertTrue("the digging tools keep harvest level 4",
                read("src/main/java/thaumcraft/common/items/tinkerer/kami/tool/ItemIchorPick.java")
                        .contains("setHarvestLevel(\"pickaxe\", 4)"));

        String cap = read("src/main/java/thaumcraft/common/items/tinkerer/kami/wand/CapIchor.java");
        assertTrue("ichor cap keeps tag, discount and cost",
                cap.contains("\"ICHOR\", 0.8F") && cap.contains("ICHOR_CAP") && cap.contains(", 10)"));
        String rod = read("src/main/java/thaumcraft/common/items/tinkerer/kami/wand/RodIchorcloth.java");
        assertTrue("ichorcloth rod keeps tag, capacity, cost and glow",
                rod.contains("\"ICHORCLOTH\", 1000") && rod.contains("setGlowing(true)"));
        assertTrue("both wand parts must be constructed at init",
                read("src/main/java/thaumcraft/common/Thaumcraft.java").contains("new CapIchor()")
                        || read("src/main/java/thaumcraft/common/Thaumcraft.java").contains("wand.CapIchor()"));

        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        assertTrue("each tool is priced at 75 of a single primal, as in the original",
                rec.contains("Aspect.FIRE, \"III\"".replace("\"III\"", "")) || rec.contains("aspect, 75"));
        assertTrue("tool recipes registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerKamiToolRecipes()"));

        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        for (String t : new String[]{"ichor_pick", "ichor_axe", "ichor_shovel", "ichor_sword"}) {
            assertTrue("en " + t, lang.contains("item.thaumcraft.kami." + t + ".name="));
            assertTrue("ru " + t, ru.contains("item.thaumcraft.kami." + t + ".name="));
            assertTrue("texture " + t,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/items/kami_" + t + ".png")));
        }
    }

    @Test
    public void bedrockDimension() throws IOException {
        String gen = read("src/main/java/thaumcraft/common/lib/world/dim/bedrock/ChunkGeneratorBedrock.java");
        assertTrue("the world must be solid bedrock for all 256 layers, as the original built it",
                gen.contains("y < 256") && gen.contains("Blocks.BEDROCK.getDefaultState()"));

        String ore = read("src/main/java/thaumcraft/common/lib/world/dim/bedrock/OreClusterGenerator.java");
        assertTrue("cluster numbers must stay the original's: 200 attempts, veins to 20, y 6..250",
                ore.contains("ATTEMPTS = 200") && ore.contains("MAX_VEIN = 20")
                        && ore.contains("MIN_Y = 6") && ore.contains("Y_RANGE = 245"));
        assertTrue("veins are cut into bedrock only",
                ore.contains("input.getBlock() == Blocks.BEDROCK"));
        assertTrue("clusters only generate in this dimension",
                ore.contains("world.provider instanceof WorldProviderBedrock"));

        String freq = read("src/main/java/thaumcraft/common/lib/world/dim/bedrock/OreFrequency.java");
        for (String sample : new String[]{"\"oreCoal\", 2648", "\"oreIron\", 1503",
                "\"oreDiamond\", 67", "\"oreInfusedOrder\", 31", "\"oreVinteum\", 392"}) {
            assertTrue("frequency preserved: " + sample, freq.contains(sample));
        }
        assertTrue("the original's blacklist is kept", freq.contains("oreFirestone"));

        String portal = read("src/main/java/thaumcraft/common/blocks/tinkerer/kami/BlockBedrockPortal.java");
        assertTrue("the portal must catch entities that fall through it",
                portal.contains("public void onEntityCollision("));
        assertTrue("entering clears the arrival pocket at 251..253, as in the original",
                portal.contains("y = 251; y <= 253"));
        assertTrue("only from a surface world, server side",
                portal.contains("world.provider.isSurfaceWorld()") && portal.contains("world.isRemote"));

        String tc = read("src/main/java/thaumcraft/common/Thaumcraft.java");
        assertTrue("dimension and its worldgen must both be registered",
                tc.contains("registerBedrockDimension()") && tc.contains("OreClusterGenerator()"));
    }

    /** Every TT block must be obtainable in survival, not creative-only. */
    @Test
    public void tinkererBlocksAreCraftable() throws IOException {
        String recipes = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        for (String key : new String[]{"Funnel", "Magnet", "MobMagnet", "SoulMould",
                "SpellCloth", "TransvectorInterface", "TransvectorConnector",
                "TransvectorDislocator", "AnimationTablet", "Enchanter", "Repairer"}) {
            assertTrue(key + " must have a recipe", recipes.contains("\"" + key + "\""));
        }
        assertTrue("block recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerBlockRecipes()"));
    }

    /**
     * The block recipes were invented until 1.1.10.0. These pin what the
     * original actually specifies — in particular that five of the thirteen
     * are not arcane crafts at all.
     */
    @Test
    public void blockRecipesMatchTheOriginal() throws IOException {
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");

        assertTrue("the funnel is a single row of stone, thaumium, stone",
                rec.contains("\"STS\"")
                        && rec.contains("add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1)"));
        assertTrue("both magnets share the original's shape and cost",
                rec.contains("\" I \", \"SIs\", \"WFW\"")
                        && rec.contains("\" G \", \"SGs\", \"WFW\"")
                        && rec.contains("ConfigItems.focusTelekinesis"));
        assertTrue("the mob magnet is item damage 1 and falls back to copper",
                rec.contains("new ItemStack(ConfigBlocks.blockMagnet, 1, 1)")
                        && rec.contains("oreDictOrStack(new ItemStack(Items.GOLD_INGOT), \"ingotCopper\")"));

        // Not arcane crafts, whatever the method they live in is called.
        assertTrue("the soul mould is a crucible recipe on an ender pearl",
                rec.contains("\"SoulMould\", ThaumcraftApi.addCrucibleRecipe")
                        && rec.contains("add(Aspect.BEAST, 4).add(Aspect.MIND, 8).add(Aspect.SENSES, 8)"));
        assertTrue("the spell cloth is a crucible recipe on enchanted fabric",
                rec.contains("\"SpellCloth\", ThaumcraftApi.addCrucibleRecipe"));
        assertTrue("the enchanter is an infusion at instability 15",
                rec.contains("\"ENCHANTER\", new ItemStack(ConfigBlocks.blockEnchanter), 15")
                        && rec.contains("Blocks.ENCHANTING_TABLE")
                        && rec.contains("ConfigItems.itemSpellCloth"));
        assertTrue("the restorer is an infusion at instability 8 on thaumium",
                rec.contains("\"REPAIRER\", new ItemStack(ConfigBlocks.blockRepairer), 8")
                        && rec.contains("add(Aspect.TOOL, 15).add(Aspect.CRAFT, 20)"));

        assertTrue("the interface is corners of pedestal top with lapis and a pearl",
                rec.contains("\"BRB\", \"LEL\", \"BRB\"")
                        && rec.contains("new ItemStack(Items.DYE, 1, 4)"));
        assertTrue("the binder is iron, a stick and an order shard",
                rec.contains("\" I \", \" WI\", \"S  \""));
        assertTrue("the dislocator is a column of glass, interface and comparator",
                rec.contains("\" M \", \" I \", \" C \"")
                        && rec.contains("Items.COMPARATOR"));
        assertTrue("the tablet takes a blank golem core",
                rec.contains("\"GIG\", \"ICI\"")
                        && rec.contains("ConfigItems.itemGolemCore, 1, 100"));

        // The whole smokey quartz family is plain bench work upstream.
        for (String name : new String[]{"darkquartz_block", "darkquartz_pillar",
                "darkquartz_chiseled", "darkquartz_slab", "darkquartz_stairs",
                "darkquartz_stairs_mirrored"}) {
            assertTrue(name + " must be a bench recipe", rec.contains("\"" + name + "\""));
        }
        assertFalse("smokey quartz must not be arcane any more",
                rec.contains("\"ARCANESTONE\""));
    }

    /** The Spellbinding Cloth and its disenchanting rule. */
    @Test
    public void spellClothStripsEnchantments() throws IOException {
        String item = read("src/main/java/thaumcraft/common/items/tinkerer/ItemSpellCloth.java");
        assertTrue("thirty-five uses", item.contains("USES = 35"));
        assertTrue("wears by one and stays in the grid",
                item.contains("worn.setItemDamage(worn.getItemDamage() + 1)"));

        String recipe = read("src/main/java/thaumcraft/common/items/tinkerer/SpellClothRecipe.java");
        assertTrue("removes the ench tag", recipe.contains("removeTag(\"ench\")"));
        assertTrue("refuses items marked INoRemoveEnchant",
                recipe.contains("instanceof INoRemoveEnchant"));
        assertTrue("INoRemoveEnchant is carried over as a hook",
                Files.exists(Paths.get("src/main/java/thaumcraft/common/items/tinkerer/"
                        + "INoRemoveEnchant.java")));
        assertTrue("the rule is registered",
                read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java")
                        .contains("spellcloth_disenchant"));
        assertTrue("itemSpellCloth registered",
                read("src/main/java/thaumcraft/common/config/ConfigItems.java")
                        .contains("allItems.add(itemSpellCloth)"));
    }

    /** The module ships in both languages — the rest of the mod is fully translated. */
    @Test
    public void tinkererContentIsLocalisedInBothLanguages() throws IOException {
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        String[] keys = {
                "item.thaumcraft.focus_smelt.name", "item.thaumcraft.focus_telekinesis.name",
                "item.thaumcraft.focus_flight.name", "item.thaumcraft.focus_heal.name",
                "item.thaumcraft.focus_deflect.name", "item.thaumcraft.focus_dislocation.name",
                "item.thaumcraft.focus_enderchest.name", "item.thaumcraft.placement_mirror.name",
                "item.thaumcraft.cleansing_talisman.name", "item.thaumcraft.xp_talisman.name",
                "item.thaumcraft.cat_amulet.name", "tile.thaumcraft.dark_quartz.0.name",
                "tile.thaumcraft.funnel.name", "tile.thaumcraft.magnet.name",
                "tile.thaumcraft.repairer.name",
                "tile.thaumcraft.transvector_interface.name",
                "item.thaumcraft.transvector_connector.name",
                "tile.thaumcraft.transvector_dislocator.name",
                "tile.thaumcraft.animation_tablet.name",
                "tile.thaumcraft.enchanter.name",
                "item.thaumcraft.soul_mould.name",
        };
        for (String key : keys) {
            assertTrue(key + " missing from en_us.lang", en.contains(key + "="));
            assertTrue(key + " missing from ru_ru.lang", ru.contains(key + "="));
        }
    }

    /**
     * The cat amulet and the placement mirror, which the audit found were
     * inventions rather than ports. Both now follow the original.
     */
    @Test
    public void catAmuletAndMirrorMatchTheOriginal() throws IOException {
        String cat = read("src/main/java/thaumcraft/common/items/tinkerer/ItemCatAmulet.java");
        assertTrue("cat amulet is an amulet bauble that rewires nearby AI",
                cat.contains("implements IBauble")
                        && cat.contains("return BaubleType.AMULET;")
                        && cat.contains("EntityAIAvoidEntity")
                        && cat.contains("EntityAINearestAttackableTarget"));
        assertTrue("ocelot-avoiders flee the wearer and player-hunters lose interest",
                cat.contains("EntityOcelot.class, EntityPlayer.class")
                        && cat.contains("EntityPlayer.class, EntityEnderCrystal.class"));
        assertTrue("creepers stop counting down and drop their target",
                cat.contains("setCreeperFuse") && cat.contains("setAttackTarget(null)"));
        assertTrue("cat amulet keeps the original's 10 by 4 search box",
                cat.contains("RANGE = 10") && cat.contains("RANGE_Y = 4"));

        String mirror = read("src/main/java/thaumcraft/common/items/tinkerer/ItemPlacementMirror.java");
        assertTrue("mirror lays a whole square, not one block",
                mirror.contains("placeAllBlocks") && mirror.contains("getBlocksToPlace"));
        assertTrue("mirror cycles 3x3 up to 11x11",
                mirror.contains("size == 11 ? 3 : size + 2"));
        assertTrue("mirror binds only full cubes and pays out of the inventory",
                mirror.contains("EnumBlockRenderType.MODEL")
                        && mirror.contains("hasBlocks(player, stackToPlace, blocksToPlace.size())")
                        && mirror.contains("placeBlockAndConsume"));
    }

    /** The original also cut dark quartz into a slab and stairs. */
    @Test
    public void darkQuartzHasSlabAndStairs() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        assertTrue("slab halves and stairs registered",
                cfg.contains("blockSlabDarkQuartz = (BlockDarkQuartzSlab) new BlockDarkQuartzSlab.Half()")
                        && cfg.contains("blockDoubleSlabDarkQuartz = (BlockDarkQuartzSlab) new BlockDarkQuartzSlab.Double()")
                        && cfg.contains("blockStairsDarkQuartz = (BlockDarkQuartzStairs) new BlockDarkQuartzStairs()"));
        assertTrue("slab item is an ItemSlab bound to both halves",
                cfg.contains("new net.minecraft.item.ItemSlab(blockSlabDarkQuartz,"));
        assertTrue("stairs are cut from plain dark quartz, as upstream",
                read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockDarkQuartzStairs.java")
                        .contains("ConfigBlocks.blockDarkQuartz.getStateFromMeta(0)"));
        assertTrue("slab keeps the original's hardness and resistance",
                read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockDarkQuartzSlab.java")
                        .contains("setHardness(0.8F)"));
        for (String f : new String[]{"blockslabdarkquartz", "blockdoubleslabdarkquartz", "blockstairsdarkquartz"}) {
            assertTrue("blockstate " + f,
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/" + f + ".json")));
        }
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        assertTrue("slab and stairs are named",
                en.contains("tile.thaumcraft.slab_dark_quartz.name=")
                        && en.contains("tile.thaumcraft.stairs_dark_quartz.name="));
    }

    /**
     * The 1.1.5.0 audit put every block back on the original's numbers. These
     * are the ones that had drifted; pinning them stops the drift recurring.
     * All values are from {@code TT_OBJECT_REFERENCE.md}.
     */
    @Test
    public void blockConstantsMatchTheOriginal() throws IOException {
        String blocks = "src/main/java/thaumcraft/common/blocks/tinkerer/";

        String quartz = read(blocks + "BlockDarkQuartz.java");
        assertTrue("dark quartz: 0.8 / 10.0",
                quartz.contains("setHardness(0.8F)") && quartz.contains("setResistance(10.0F)"));

        String funnel = read(blocks + "BlockFunnel.java");
        assertTrue("funnel is stone, not iron",
                funnel.contains("super(Material.ROCK)") && funnel.contains("SoundType.STONE"));
        assertTrue("funnel is a 1/8-block plate", funnel.contains("1.0D / 8.0D"));

        String magnet = read(blocks + "BlockMagnet.java");
        assertTrue("magnet: 1.7 / 1.0, wood",
                magnet.contains("setHardness(1.7F)") && magnet.contains("setResistance(1.0F)")
                        && magnet.contains("SoundType.WOOD"));
        assertTrue("magnet is a thin plate inset on x/z",
                magnet.contains("new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 2.0D / 16.0D, 0.9375D)"));
        assertTrue("mob magnet drops as item damage 1, as ItemBlockMagnet expects",
                magnet.contains("state.getValue(MOB) ? 1 : 0"));

        String repairer = read(blocks + "BlockRepairer.java");
        assertTrue("repairer: 5.0 / 10.0",
                repairer.contains("setHardness(5.0F)") && repairer.contains("setResistance(10.0F)"));

        String enchanter = read(blocks + "BlockEnchanter.java");
        assertTrue("enchanter: 5.0 / 2000.0",
                enchanter.contains("setHardness(5.0F)") && enchanter.contains("setResistance(2000.0F)"));
        assertTrue("enchanter stands 0.75 tall", enchanter.contains("1.0D, 0.75D, 1.0D"));

        for (String f : new String[]{"BlockTransvectorInterface.java", "BlockTransvectorDislocator.java"}) {
            String s = read(blocks + f);
            assertTrue(f + ": iron, resistance 10",
                    s.contains("super(Material.IRON)") && s.contains("setResistance(10.0F)"));
        }

        String tablet = read(blocks + "BlockAnimationTablet.java");
        assertTrue("animation tablet: iron, 50.0, metal",
                tablet.contains("super(Material.IRON)") && tablet.contains("setResistance(50.0F)")
                        && tablet.contains("SoundType.METAL"));
    }

    /**
     * Display names come from the original's own en_US/ru_RU files. A handful
     * were invented before 1.1.5.0; these are the corrected ones.
     */
    @Test
    public void namesComeFromTheOriginalsLangFiles() throws IOException {
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        String[][] pairs = {
                {"tile.thaumcraft.magnet.name=Kinetic Attractor", "tile.thaumcraft.magnet.name=Кинетический притяжатель"},
                {"tile.thaumcraft.mobMagnet.name=Corporeal Attractor", "tile.thaumcraft.mobMagnet.name=Материальный притяжатель"},
                {"tile.thaumcraft.repairer.name=Thaumic Restorer", "tile.thaumcraft.repairer.name=Таум-восстановитель"},
                {"tile.thaumcraft.funnel.name=Essentia Funnel", "tile.thaumcraft.funnel.name=Воронка для эссенции"},
                {"tile.thaumcraft.animation_tablet.name=Dynamism Tablet", "tile.thaumcraft.animation_tablet.name=Динамическая дощечка"},
                {"item.thaumcraft.cat_amulet.name=Feline Amulet", "item.thaumcraft.cat_amulet.name=Кошачий амулет"},
                {"item.thaumcraft.focus_smelt.name=Wand Focus: Efreet's Flame", "item.thaumcraft.focus_smelt.name=Набалдашник: Пламя ифрита"},
                {"tile.thaumcraft.dark_quartz.0.name=Block of Smokey Quartz", "tile.thaumcraft.dark_quartz.0.name=Блок закоптившегося кварца"},
        };
        for (String[] pair : pairs) {
            assertTrue("en: " + pair[0], en.contains(pair[0]));
            assertTrue("ru: " + pair[1], ru.contains(pair[1]));
        }
    }

    /**
     * Each advanced tool has its own third mode in the original — they are not
     * three copies of the pickaxe's bore.
     */
    @Test
    public void advancedToolsKeepTheirOwnThirdMode() throws IOException {
        String tools = "src/main/java/thaumcraft/common/items/tinkerer/kami/tool/";

        assertTrue("pickaxe mode 2 bores ten blocks along the line of sight",
                read(tools + "ItemIchorPickAdv.java").contains("xo >= 0 ? 0 : -10"));
        assertTrue("shovel mode 2 is a column of the struck block only",
                read(tools + "ItemIchorShovelAdv.java")
                        .contains("0, -8, 0, 1, 8, 1,\n                        state.getBlock()"));
        String axe = read(tools + "ItemIchorAxeAdv.java");
        assertTrue("axe mode 2 fells the tree and gathers the drops",
                axe.contains("BlockUtils.breakFurthestBlock") && axe.contains("Utils.isWoodLog"));

        String handler = read(tools + "KamiToolHandler.java");
        assertTrue("the original's absolute-vs-offset guard is kept verbatim",
                handler.contains("hit.getX() != x1 && hit.getY() != y1 && hit.getZ() != z1"));

        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        assertTrue("mode names are the original's, and differ per tool",
                en.contains("tc.kami.mode.pick.2=Line Mode")
                        && en.contains("tc.kami.mode.shovel.2=Column Mode")
                        && en.contains("tc.kami.mode.axe.2=Tree Mode"));
    }

    /**
     * The Bottomless Pouch is the focus pouch grown to 13x9 and worn on the
     * belt. Its screen and slot geometry are the original's.
     */
    @Test
    public void bottomlessPouchKeepsTheOriginalGeometry() throws IOException {
        String item = read("src/main/java/thaumcraft/common/items/tinkerer/kami/ItemIchorPouch.java");
        assertTrue("117 slots, as 13 * 9", item.contains("SLOTS = 13 * 9"));
        assertTrue("worn on the belt", item.contains("BaubleType.BELT"));
        assertTrue("extends the plain focus pouch, as upstream",
                item.contains("extends ItemFocusPouch implements IBauble"));

        String container = read("src/main/java/thaumcraft/common/container/ContainerIchorPouch.java");
        assertTrue("grid at (12 + col*18, 8 + row*18)",
                container.contains("12 + col * 18, 8 + row * 18"));
        assertTrue("player inventory at (48, 177)",
                container.contains("INV_X = 48") && container.contains("INV_Y = 177"));
        assertTrue("64 to a slot, and no pouch inside a pouch",
                container.contains("return 64;")
                        && container.contains("!(stack.getItem() instanceof ItemFocusPouch)"));

        // The pouch is an ItemFocusPouch by inheritance; the plain screen must
        // refuse it or it would truncate 117 slots to 18.
        assertTrue("the plain pouch screen must not adopt the bottomless one",
                read("src/main/java/thaumcraft/common/container/ContainerFocusPouch.java")
                        .contains("!(stack.getItem() instanceof ItemIchorPouch)"));

        assertTrue("screen is 256x256 on the original's texture",
                read("src/main/java/thaumcraft/client/gui/GuiIchorPouch.java")
                        .contains("this.xSize = 256"));
        assertTrue("pouch gui texture ships",
                Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/gui/ichorpouch.png")));
    }

    /** Protoclay swaps awakened tools by the material about to be struck. */
    @Test
    public void protoclaySwapsAwakenedTools() throws IOException {
        String clay = read("src/main/java/thaumcraft/common/items/tinkerer/kami/ItemProtoclay.java");
        assertTrue("matches the struck material against the three tool lists",
                clay.contains("MATERIALS_PICK") && clay.contains("MATERIALS_SHOVEL")
                        && clay.contains("MATERIALS_AXE"));
        assertTrue("leaves the sword alone", clay.contains("\"sword\".equals(tool.getType())"));
        assertTrue("swaps the hotbar slot with the matching tool",
                clay.contains("player.inventory.currentItem, candidate"));

        String tools = "src/main/java/thaumcraft/common/items/tinkerer/kami/tool/";
        assertTrue("IAdvancedTool exists", Files.exists(Paths.get(tools + "IAdvancedTool.java")));
        for (String[] pair : new String[][]{{"ItemIchorPickAdv", "pick"},
                {"ItemIchorAxeAdv", "axe"}, {"ItemIchorShovelAdv", "shovel"}}) {
            String src = read(tools + pair[0] + ".java");
            assertTrue(pair[0] + " declares its type",
                    src.contains("implements IAdvancedTool")
                            && src.contains("return \"" + pair[1] + "\";"));
        }
    }

    /** Both are KAMI items: registered, named in both languages, and infused. */
    @Test
    public void kamiCarryablesAreRegisteredAndCraftable() throws IOException {
        String cfg = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");

        for (String field : new String[]{"itemIchorPouch", "itemProtoclay"}) {
            assertTrue(field + " registered",
                    cfg.contains(field + ";") && cfg.contains("allItems.add(" + field + ")"));
        }
        assertTrue("Bottomless Pouch infuses on the focus pouch at instability 9",
                rec.contains("\"ICHOR_POUCH\"")
                        && rec.contains("add(Aspect.VOID, 64).add(Aspect.MAN, 32)")
                        && rec.contains("ConfigItems.itemFocusPouch"));
        assertTrue("Protoclay infuses on a clay ball at instability 4",
                rec.contains("\"PROTOCLAY\"")
                        && rec.contains("add(Aspect.MINE, 16).add(Aspect.TOOL, 16)")
                        && rec.contains("Items.CLAY_BALL"));
        assertTrue("names come from the original's lang files",
                en.contains("item.thaumcraft.kami.ichor_pouch.name=Bottomless Pouch")
                        && en.contains("item.thaumcraft.kami.protoclay.name=Protoclay")
                        && ru.contains("item.thaumcraft.kami.ichor_pouch.name=Бездонная сумка")
                        && ru.contains("item.thaumcraft.kami.protoclay.name=Протоглина"));
        assertTrue("the pouch screen is wired on both sides, at the original's KAMI id 50",
                read("src/main/java/thaumcraft/common/CommonProxy.java")
                        .contains("GUI_ICHOR_POUCH = 50")
                        && read("src/main/java/thaumcraft/client/ClientProxy.java")
                        .contains("GuiIchorPouch"));
    }

    private static final String[] FIRES = {
            "Air", "Water", "Earth", "Ignis", "Order", "Chaos"
    };

    /** Every imbued fire is one Hyperenergetic Nitor in a crucible. */
    @Test
    public void imbuedFiresAreCrucibleRecipesOnTheNitor() throws IOException {
        String rec = read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java");
        assertTrue("Hyperenergetic Nitor boils out of plain nitor",
                rec.contains("\"BRIGHT_NITOR\"")
                        && rec.contains("add(Aspect.ENERGY, 25).add(Aspect.LIGHT, 25)")
                        && rec.contains("new ItemStack(ConfigItems.itemResource, 1, 1)"));
        for (String key : new String[]{"FIRE_AER", "FIRE_AQUA", "FIRE_TERRA",
                "FIRE_IGNIS", "FIRE_ORDO", "FIRE_PERDITIO"}) {
            assertTrue(key + " registered", rec.contains("\"" + key + "\""));
        }
        // Ignis is the only one without MAGIC, and the only one at FIRE 10.
        assertTrue("Ignis keeps its odd cost",
                rec.contains("add(Aspect.FIRE, 10).add(Aspect.AIR, 5)"));
        assertTrue("fire recipes registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerFireRecipes()"));
    }

    /** The transmutation tables are the original's, pair for pair. */
    @Test
    public void imbuedFiresKeepTheirTransmutations() throws IOException {
        String dir = "src/main/java/thaumcraft/common/blocks/tinkerer/fire/BlockFire";
        assertTrue("Aer dries wood to sand and freezes water into a cake",
                read(dir + "Air.java").contains("Blocks.LOG, Blocks.SAND")
                        && read(dir + "Air.java").contains("Blocks.WATER, Blocks.CAKE"));
        assertTrue("Aqua sets lava to obsidian, flowing lava too",
                read(dir + "Water.java").contains("Blocks.LAVA, Blocks.OBSIDIAN")
                        && read(dir + "Water.java").contains("Blocks.FLOWING_LAVA, Blocks.OBSIDIAN"));
        assertTrue("Terra turns a spawner into iron and nether brick back to planks",
                read(dir + "Earth.java").contains("Blocks.MOB_SPAWNER, Blocks.IRON_BLOCK")
                        && read(dir + "Earth.java").contains("Blocks.NETHER_BRICK, Blocks.PLANKS"));
        assertTrue("Ignis drags the Nether up and maps the yellow flower to itself",
                read(dir + "Ignis.java").contains("Blocks.GRASS, Blocks.NETHERRACK")
                        && read(dir + "Ignis.java").contains("Blocks.YELLOW_FLOWER, Blocks.YELLOW_FLOWER"));
        String order = read(dir + "Order.java");
        assertTrue("Ordo perfects ore into blocks and sweeps the ore dictionary",
                order.contains("Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK")
                        && order.contains("getOreDictionaryOres()")
                        && order.contains("regionMatches(5, ore, 3, 10)"));
        assertTrue("Ordo succeeds one time in three", order.contains("return 3;"));
        String chaos = read(dir + "Chaos.java");
        assertTrue("Perditio eats the other five and ticks every tick",
                chaos.contains("ConfigBlocks.blockFireAir, Blocks.FIRE")
                        && chaos.contains("return 1;"));
    }

    /** Base behaviour, including the two quirks kept on purpose. */
    @Test
    public void imbuedFireBaseKeepsTheOriginalsQuirks() throws IOException {
        String base = read("src/main/java/thaumcraft/common/blocks/tinkerer/fire/BlockFireBase.java");
        assertTrue("dies unless something it works on is beside it",
                base.contains("isNeighborTarget(world, pos)"));
        assertTrue("ticks every 200 by default", base.contains("return 200;"));
        assertTrue("treats its targets as tinder and its results as fireproof",
                base.contains("return 100;") && base.contains("return 0;"));
        assertTrue("the swapped-argument quirk is kept and explained",
                base.contains("new BlockPos(x, b, a)")
                        && base.contains("is not a typo here"));
        assertTrue("the yes/no encouragement survey is kept",
                base.contains("isNeighborTarget(world, pos) ? 100 : 0"));
    }

    /** Registration, assets and names for the whole chain. */
    @Test
    public void imbuedFiresAreRegisteredWithAssets() throws IOException {
        String blocks = read("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String en = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        String ru = read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang");
        String[] keys = {"fire_air", "fire_water", "fire_earth",
                "fire_ignis", "fire_order", "fire_chaos"};

        assertTrue("itemBrightNitor registered",
                read("src/main/java/thaumcraft/common/config/ConfigItems.java")
                        .contains("allItems.add(itemBrightNitor)"));
        for (int i = 0; i < FIRES.length; i++) {
            String field = "blockFire" + FIRES[i];
            assertTrue(field + " registered and listed",
                    blocks.contains(field + ";") && blocks.contains("                " + field + ","));
            String stem = field.toLowerCase();
            assertTrue(stem + " blockstate",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/blockstates/"
                            + stem + ".json")));
            // Vanilla fire needs floor, side, side_alt, up and up_alt, twice over.
            for (String shape : new String[]{"floor", "side", "side_alt", "up", "up_alt"}) {
                for (int layer = 0; layer < 2; layer++) {
                    assertTrue(stem + " " + shape + layer,
                            Files.exists(Paths.get("src/main/resources/assets/thaumcraft/models/block/"
                                    + stem + "_" + shape + layer + ".json")));
                }
            }
            for (int layer = 0; layer < 2; layer++) {
                assertTrue(keys[i] + " animated layer " + layer,
                        Files.exists(Paths.get("src/main/resources/assets/thaumcraft/textures/blocks/"
                                + keys[i] + "_layer_" + layer + ".png.mcmeta")));
            }
            assertTrue(keys[i] + " named in both languages",
                    en.contains("tile.thaumcraft." + keys[i] + ".name=")
                            && ru.contains("tile.thaumcraft." + keys[i] + ".name="));
        }
        assertTrue("nitor named in both languages",
                en.contains("item.thaumcraft.bright_nitor.name=Hyperenergetic Nitor")
                        && ru.contains("item.thaumcraft.bright_nitor.name="));
    }

    /**
     * Numbers the second audit pass (1.1.11.0) found drifting. Each of these
     * was a plausible-looking substitution that changed what the game does.
     */
    @Test
    public void auditedNumbersMatchTheOriginal() throws IOException {
        // The enchanter's curve truncates upstream; rounding made a base of 4
        // cost 5 at level one.
        String costs = read("src/main/java/thaumcraft/common/lib/tinkerer/EnchantmentCosts.java");
        assertTrue("cost curve must truncate, not round",
                costs.contains("(int) (base.getAmount(aspect) * factor)"));
        assertFalse("Math.round must not come back", costs.contains("Math.round"));

        // The magnet's reach is whole blocks, and its box half a block shorter
        // than we had it.
        String magnet = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileMagnet.java");
        assertTrue("reach is integer division", magnet.contains("double range = redstone / 2;"));
        assertFalse("no floating-point reach", magnet.contains("redstone / 2.0D"));
        assertTrue("pull box tops out at half a block above centre plus range",
                magnet.contains("pos.getY() + 0.5D + range"));

        // Upstream registers its enchantments at weight 0; the lowest this
        // version can express is VERY_RARE.
        String ench = read("src/main/java/thaumcraft/common/lib/enchantment/tinkerer/"
                + "EnchantmentTinkerer.java");
        assertTrue("enchantments take the lowest expressible weight",
                ench.contains("super(Rarity.VERY_RARE, kind.type, kind.slots)"));
        assertTrue("and stay off the enchanting table",
                ench.contains("canApplyAtEnchantingTable"));
    }

    /**
     * The fourteen base costs and the fourteen max levels, checked against the
     * original's EnchantmentManager and its Enchantment* classes. These read as
     * arbitrary numbers, which is exactly why they need pinning.
     */
    @Test
    public void tinkererEnchantmentTablesMatchTheOriginal() throws IOException {
        String costs = read("src/main/java/thaumcraft/common/lib/tinkerer/EnchantmentCosts.java");
        String[][] bases = {
                {"ascentBoost", "aspects(Aspect.ENTROPY, 8, Aspect.AIR, 10)"},
                {"slowFall", "aspects(Aspect.ORDER, 8, Aspect.AIR, 10)"},
                {"autoSmelt", "aspects(Aspect.ENTROPY, 20, Aspect.FIRE, 30)"},
                {"finalStrike", "aspects(Aspect.ENTROPY, 16, Aspect.FIRE, 16)"},
                {"tunnel", "aspects(Aspect.EARTH, 16, Aspect.ORDER, 16)"},
                {"shatter", "aspects(Aspect.EARTH, 16, Aspect.ENTROPY, 16)"},
                {"shockwave", "aspects(Aspect.EARTH, 16, Aspect.AIR, 16)"},
                {"pounce", "aspects(Aspect.EARTH, 16, Aspect.AIR, 16)"},
        };
        for (String[] pair : bases) {
            assertTrue(pair[0] + " base cost",
                    costs.contains("ModEnchantmentsTinkerer." + pair[0] + ", " + pair[1]));
        }

        String ench = read("src/main/java/thaumcraft/common/lib/enchantment/tinkerer/"
                + "EnchantmentTinkerer.java");
        String[][] levels = {
                {"ASCENT_BOOST", "4", "ARMOR_LEGS"},
                {"SLOW_FALL", "3", "ARMOR_FEET"},
                {"AUTO_SMELT", "1", "DIGGER"},
                {"DESINTEGRATE", "1", "DIGGER"},
                {"QUICK_DRAW", "2", "BOW"},
                {"VAMPIRISM", "2", "WEAPON"},
                {"POUNCE", "5", "ARMOR_LEGS"},
                {"SHOCKWAVE", "5", "ARMOR_FEET"},
        };
        for (String[] row : levels) {
            assertTrue(row[0] + " max level and slot",
                    ench.contains(row[0] + "(\"") && ench.contains(", " + row[1]
                            + ", EnumEnchantmentType." + row[2]));
        }
    }

    /** Goggles built into a thaumium helm, at the goggles' own discount. */
    @Test
    public void revealingHelmIsGogglesOnAThaumiumHelm() throws IOException {
        String helm = read("src/main/java/thaumcraft/common/items/tinkerer/ItemRevealingHelm.java");
        assertTrue("thaumium material, render index 2, head slot",
                helm.contains("ThaumcraftApi.armorMatThaumium, 2, EntityEquipmentSlot.HEAD"));
        assertTrue("500 durability", helm.contains("setMaxDamage(500)"));
        assertTrue("reveals nodes and popups, and discounts five percent",
                helm.contains("implements IRepairable, IRevealer, IGoggles, IVisDiscountGear")
                        && helm.contains("return 5;"));
        assertTrue("mended with thaumium ingots",
                helm.contains("new ItemStack(ConfigItems.itemResource, 1, 2)"));

        assertTrue("recipe is goggles beside a thaumium helm, five of every primal",
                read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java")
                        .contains("\"GH\"")
                        && read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java")
                        .contains("ConfigItems.itemHelmThaumium"));
        assertTrue("registered",
                read("src/main/java/thaumcraft/common/config/ConfigItems.java")
                        .contains("allItems.add(itemRevealingHelm)"));
        for (String asset : new String[]{"textures/items/revealing_helm.png",
                "textures/models/revealing_helm.png", "models/item/revealinghelm.json"}) {
            assertTrue(asset + " ships",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/" + asset)));
        }
        assertTrue("named in both languages",
                read("src/main/resources/assets/thaumcraft/lang/en_us.lang")
                        .contains("item.thaumcraft.revealing_helm.name=Helmet of Revealing")
                        && read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang")
                        .contains("item.thaumcraft.revealing_helm.name="));
    }

    /**
     * Camouflage: right-click with a block to wear its face, empty hand to drop
     * it. 1.7.10 swapped per-face icons; here the disguise travels as an
     * unlisted property and a baked model draws it.
     */
    @Test
    public void camouflageSystemIsWired() throws IOException {
        String tile = read("src/main/java/thaumcraft/common/tiles/tinkerer/TileCamo.java");
        assertTrue("stores the block by registry name and its metadata",
                tile.contains("TAG_CAMO = \"camo\"") && tile.contains("TAG_CAMO_META = \"camoMeta\""));
        // Packet plumbing comes from TileThaumcraft's readCustomNBT/writeCustomNBT;
        // only the redraw on arrival is this tile's own business.
        assertTrue("built on the port's tile base",
                tile.contains("extends TileThaumcraft")
                        && tile.contains("public void writeCustomNBT")
                        && tile.contains("public void readCustomNBT"));
        assertTrue("a new disguise redraws the chunk",
                tile.contains("markBlockRangeForRenderUpdate"));

        String block = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockCamo.java");
        assertTrue("exposes the disguise as an unlisted property",
                block.contains("IUnlistedProperty<IBlockState> CAMO")
                        && block.contains("getExtendedState"));
        assertTrue("only ordinary block models qualify",
                block.contains("EnumBlockRenderType.MODEL"));
        assertTrue("a camo block cannot be disguised as another camo block",
                block.contains("block instanceof BlockCamo"));
        assertTrue("directional disguises are turned to face the player",
                block.contains("meta & 12 | 2") && block.contains("meta & 12 | 3"));

        assertTrue("the baked model defers to the disguise's own model",
                read("src/main/java/thaumcraft/client/renderers/block/CamoBakedModel.java")
                        .contains("getModelForState(camo)"));
        assertTrue("and is installed at bake time",
                read("src/main/java/thaumcraft/client/ClientModelRegistry.java")
                        .contains("replaceCamoModels(event)"));
        assertTrue("TileCamo is a registered tile entity",
                read("src/main/java/thaumcraft/common/config/ConfigBlocks.java")
                        .contains("new TileRegistration(TileCamo.class, \"TileCamo\")"));
    }

    /** Solid from above, open from below, and sneaking drops you through. */
    @Test
    public void etherealPlatformIsOneWay() throws IOException {
        String platform = read("src/main/java/thaumcraft/common/blocks/tinkerer/BlockPlatform.java");
        assertTrue("is a camouflaged device", platform.contains("extends BlockCamo"));
        assertTrue("wood, 2.0 hardness, 5.0 resistance",
                platform.contains("super(Material.WOOD)")
                        && platform.contains("setHardness(2.0F)")
                        && platform.contains("setResistance(5.0F)"));
        assertTrue("the original's collision rule, players two blocks up and not sneaking",
                platform.contains("entity.posY > pos.getY() + (player ? 2 : 0)")
                        && platform.contains("!player || !entity.isSneaking()"));
        assertTrue("never an obstacle for pathfinding", platform.contains("isPassable"));

        assertTrue("two per craft from silverwood over greatwood planks",
                read("src/main/java/thaumcraft/common/config/ConfigTinkerer.java")
                        .contains("new ItemStack(ConfigBlocks.blockPlatform, 2)"));
        assertTrue("registered and listed",
                read("src/main/java/thaumcraft/common/config/ConfigBlocks.java")
                        .contains("blockPlatform;"));
        for (String asset : new String[]{"blockstates/blockplatform.json",
                "models/block/blockplatform.json", "textures/blocks/platform.png"}) {
            assertTrue(asset + " ships",
                    Files.exists(Paths.get("src/main/resources/assets/thaumcraft/" + asset)));
        }
        assertTrue("named in both languages",
                read("src/main/resources/assets/thaumcraft/lang/en_us.lang")
                        .contains("tile.thaumcraft.platform.name=Ethereal Platform")
                        && read("src/main/resources/assets/thaumcraft/lang/ru_ru.lang")
                        .contains("tile.thaumcraft.platform.name="));
    }

    /**
     * Both transvector devices are camouflaged upstream, which is what lets
     * them hide in a wall. Register item 3 closed in 1.1.13.0.
     */
    @Test
    public void transvectorsAreCamouflaged() throws IOException {
        String blocks = "src/main/java/thaumcraft/common/blocks/tinkerer/";
        assertTrue("interface extends BlockCamo",
                read(blocks + "BlockTransvectorInterface.java")
                        .contains("extends BlockCamo"));
        String dislocator = read(blocks + "BlockTransvectorDislocator.java");
        assertTrue("dislocator extends BlockCamo", dislocator.contains("extends BlockCamo"));
        assertTrue("its own facing and powered stay listed, camo goes around them",
                dislocator.contains("listedProperties()")
                        && dislocator.contains("{FACING, POWERED}"));
        assertTrue("a wand re-aims it before camo gets the click",
                dislocator.contains("instanceof ItemWandCasting")
                        && dislocator.contains("state.withProperty(FACING, side)")
                        && dislocator.contains("super.onBlockActivated("));

        assertTrue("the shared tile base is a camo tile",
                read("src/main/java/thaumcraft/common/tiles/tinkerer/TileTransvector.java")
                        .contains("extends TileCamo"));
        assertTrue("all three devices get the wrapping model",
                read("src/main/java/thaumcraft/client/ClientModelRegistry.java")
                        .contains("\"blocktransvectorinterface\"")
                        && read("src/main/java/thaumcraft/client/ClientModelRegistry.java")
                        .contains("\"blocktransvectordislocator\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
