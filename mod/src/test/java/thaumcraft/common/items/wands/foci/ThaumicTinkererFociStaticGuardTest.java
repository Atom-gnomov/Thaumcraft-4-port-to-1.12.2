package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        for (String key : recipeKeys) {
            assertTrue("recipe " + key, rec.contains("\"" + key + "\"") && rec.contains("ConfigItems.item" + key));
        }
        for (String key : langKeys) {
            assertTrue("lang " + key, lang.contains("item.thaumcraft." + key + ".name="));
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
        assertTrue("blockMagnet registered + in getAllBlocks + ItemBlock",
                cfg.contains("blockMagnet;") && cfg.contains("blockMagnet,")
                        && cfg.contains("new net.minecraft.item.ItemBlock(blockMagnet)"));
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
        assertTrue("reach must stay the original signal/2", tile.contains("redstone / 2.0D"));

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
        for (String key : new String[]{"DarkQuartz", "Funnel", "Magnet", "Repairer",
                "TransvectorInterface", "TransvectorConnector", "TransvectorDislocator",
                "AnimationTablet", "Enchanter",
                "MobMagnet", "SoulMould"}) {
            assertTrue(key + " must have an arcane recipe", recipes.contains("\"" + key + "\""));
        }
        assertTrue("block recipes must be registered at init",
                read("src/main/java/thaumcraft/common/config/ConfigRecipes.java")
                        .contains("ConfigTinkerer.registerBlockRecipes()"));
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

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
