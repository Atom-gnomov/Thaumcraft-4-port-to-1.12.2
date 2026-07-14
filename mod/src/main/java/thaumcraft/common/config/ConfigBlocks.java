package thaumcraft.common.config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockArcaneDoor;
import thaumcraft.common.blocks.BlockCandle;
import thaumcraft.common.blocks.BlockCosmeticOpaque;
import thaumcraft.common.blocks.BlockCosmeticSlab;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import thaumcraft.common.blocks.BlockCrystal;
import thaumcraft.common.blocks.BlockCustomOre;
import thaumcraft.common.blocks.BlockCustomPlant;
import thaumcraft.common.blocks.BlockDeviceStub;
import thaumcraft.common.blocks.BlockEldritch;
import thaumcraft.common.blocks.BlockFluidDeath;
import thaumcraft.common.blocks.BlockFluidPure;
import thaumcraft.common.blocks.BlockFluxGas;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.blocks.BlockInvisibleTC;
import thaumcraft.common.blocks.BlockLoot;
import thaumcraft.common.blocks.BlockMagicalLeaves;
import thaumcraft.common.blocks.BlockMagicalLog;
import thaumcraft.common.blocks.BlockManaPod;
import thaumcraft.common.blocks.BlockStairsTC;
import thaumcraft.common.blocks.BlockTaint;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.lib.block.ItemBlockTC;
import thaumcraft.common.lib.block.ItemSlabTC;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class ConfigBlocks {
    public static Block blockCustomOre;
    public static Block blockCosmeticSolid;
    public static Block blockCosmeticOpaque;
    public static Block blockMagicalLog;
    public static Block blockMagicalLeaves;
    public static Block blockCustomPlant;
    public static Block blockCrystal;
    public static Block blockTaint;
    public static Block blockTaintFibres;
    public static Block blockStairsArcaneStone;
    public static Block blockStairsEldritch;
    public static Block blockStairsGreatwood;
    public static Block blockStairsSilverwood;
    public static Block blockSlabWood;
    public static Block blockDoubleSlabWood;
    public static Block blockSlabStone;
    public static Block blockDoubleSlabStone;
    public static Block blockCandle;
    public static Block blockLootUrn;
    public static Block blockLootCrate;
    public static Block blockEldritch;
    public static Block blockManaPod;
    // B2 devices (Phase 1 inert stubs)
    public static Block blockMetalDevice;
    public static Block blockWoodenDevice;
    public static Block blockStoneDevice;
    public static Block blockTable;
    public static Block blockJar;
    public static Block blockTube;
    public static Block blockMirror;
    public static Block blockArcaneFurnace;
    public static Block blockAlchemyFurnace;
    public static Block blockChestHungry;
    public static Block blockLifter;
    public static Block blockMagicBox;
    public static Block blockEssentiaReservoir;
    public static Block blockAiry;
    public static Block blockWarded;
    // B3 special blocks (Phase 1)
    public static Block blockArcaneDoor;
    public static Block blockHole;
    public static Block blockEldritchNothing;
    public static Block blockEldritchPortal;
    /** Door item (vanilla {@link ItemDoor}); referenced by {@link BlockArcaneDoor} for drops/pick-block. */
    public static Item itemArcaneDoor;

    // B4 fluids. The Fluid objects carry the physical props (luminosity/density/viscosity/rarity);
    // still + flowing both point at the single animated TC texture, matching TC4 (iconStill==iconFlow).
    public static final Fluid FLUXGOO = new Fluid("fluxGoo",
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluxgoo"),
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluxgoo"))
            .setGaseous(false).setLuminosity(7).setDensity(8).setViscosity(6000);
    public static final Fluid FLUXGAS = new Fluid("fluxGas",
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluxgas"),
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluxgas"))
            .setGaseous(true).setLuminosity(7).setDensity(-4).setViscosity(2500);
    public static final Fluid FLUIDPURE = new Fluid("fluidPure",
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluidpure"),
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluidpure"))
            .setGaseous(false).setLuminosity(10).setViscosity(1000).setRarity(EnumRarity.RARE);
    public static final Fluid FLUIDDEATH = new Fluid("fluidDeath",
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluiddeath"),
            new ResourceLocation(Thaumcraft.MODID, "blocks/fluiddeath"))
            .setGaseous(false).setLuminosity(8).setViscosity(1500).setRarity(EnumRarity.RARE);
    public static Block blockFluxGoo;
    public static Block blockFluxGas;
    public static Block blockFluidPure;
    public static Block blockFluidDeath;

    private static final String[] SLAB_WOOD_NAMES = { "greatwood", "silverwood" };
    private static final String[] SLAB_STONE_NAMES = { "arcane", "eldritch" };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> reg = event.getRegistry();
        reg.register(blockCustomOre = register(new BlockCustomOre(), "blockCustomOre", "customore"));
        reg.register(blockCosmeticSolid = register(new BlockCosmeticSolid(), "blockCosmeticSolid", "cosmeticsolid"));
        reg.register(blockCosmeticOpaque = register(new BlockCosmeticOpaque(), "blockCosmeticOpaque", "cosmeticopaque"));
        reg.register(blockMagicalLog = register(new BlockMagicalLog(), "blockMagicalLog", "magicallog"));
        reg.register(blockMagicalLeaves = register(new BlockMagicalLeaves(), "blockMagicalLeaves", "magicalleaves"));
        reg.register(blockCustomPlant = register(new BlockCustomPlant(), "blockCustomPlant", "customplant"));
        reg.register(blockCrystal = register(new BlockCrystal(), "blockCrystal", "crystal"));
        reg.register(blockTaint = register(new BlockTaint(), "blockTaint", "taint"));
        reg.register(blockTaintFibres = register(new BlockTaintFibres(), "blockTaintFibres", "taintfibres"));
        reg.register(blockCandle = register(new BlockCandle(), "blockCandle", "candle"));
        reg.register(blockLootUrn = register(
                new BlockLoot(Material.CLAY, true, ConfigSounds.SOUND_URNBREAK), "blockLootUrn", "loot_urn"));
        reg.register(blockLootCrate = register(
                new BlockLoot(Material.WOOD, false, SoundType.WOOD), "blockLootCrate", "loot_crate"));
        reg.register(blockEldritch = register(new BlockEldritch(), "blockEldritch", "eldritch"));
        // Mana pod is worldgen-only (grows hanging under magical logs) — registered as a block with no item/creative entry.
        reg.register(blockManaPod = register(new BlockManaPod(), "blockManaPod", "mana_pod"));

        // ---- B2 devices: Phase 1 inert stubs. Correct material/sound/hardness/resistance/light/creative
        //      metas + a representative cube texture per meta. TileEntity/TESR/GUI mechanics = TODO Phase 3.
        //      Args: (material, sound, hardness, resistance, light, creativeMetas, hasTab, transparent, noCollision)
        reg.register(blockMetalDevice = register(new BlockDeviceStub(Material.IRON, SoundType.METAL, 3.0f, 17.0f, 0f,
                new int[] { 0, 1, 2, 3, 5, 7, 8, 9, 12, 13, 14 }, true, false, false), "blockMetalDevice", "metal_device"));
        reg.register(blockWoodenDevice = register(new BlockDeviceStub(Material.WOOD, SoundType.WOOD, 2.5f, 10.0f, 0f,
                new int[] { 0, 1, 2, 4, 5, 6, 7, 8 }, true, false, false), "blockWoodenDevice", "wooden_device"));
        reg.register(blockStoneDevice = register(new BlockDeviceStub(Material.ROCK, SoundType.STONE, 3.0f, 25.0f, 0f,
                new int[] { 0, 1, 2, 5, 8, 9, 10, 11, 12, 13, 14 }, true, false, false), "blockStoneDevice", "stone_device"));
        reg.register(blockTable = register(new BlockDeviceStub(Material.WOOD, SoundType.WOOD, 2.5f, 0f, 0f,
                new int[] { 0, 14, 15 }, true, false, false), "blockTable", "table"));
        reg.register(blockJar = register(new BlockDeviceStub(Material.GLASS, SoundType.GLASS, 0.3f, 0f, 0.66f,
                new int[] { 0, 1, 3 }, true, true, false), "blockJar", "jar"));
        reg.register(blockTube = register(new BlockDeviceStub(Material.IRON, SoundType.METAL, 0.5f, 5.0f, 0f,
                new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }, true, true, false), "blockTube", "tube"));
        reg.register(blockMirror = register(new BlockDeviceStub(Material.GLASS, SoundType.GLASS, 1.0f, 10.0f, 0f,
                new int[] { 0, 6 }, true, true, false), "blockMirror", "mirror"));
        // Furnaces have no creative tab in TC4 (built/obtained through gameplay) — item exists for /give only.
        reg.register(blockArcaneFurnace = register(new BlockDeviceStub(Material.ROCK, SoundType.STONE, 10.0f, 500.0f, 0.2f,
                new int[] { 0 }, false, false, false), "blockArcaneFurnace", "arcane_furnace"));
        reg.register(blockAlchemyFurnace = register(new BlockDeviceStub(Material.IRON, SoundType.METAL, 3.0f, 17.0f, 0f,
                new int[] { 0 }, false, false, false), "blockAlchemyFurnace", "alchemy_furnace"));
        reg.register(blockChestHungry = register(new BlockDeviceStub(Material.WOOD, SoundType.WOOD, 2.5f, 0f, 0f,
                new int[] { 0 }, true, false, false), "blockChestHungry", "chest_hungry"));
        reg.register(blockLifter = register(new BlockDeviceStub(Material.WOOD, SoundType.WOOD, 2.5f, 15.0f, 0f,
                new int[] { 0 }, true, false, false), "blockLifter", "lifter"));
        // Magic box: has a tab but an empty creative list in TC4 (obtained via gameplay), so it never shows in creative.
        reg.register(blockMagicBox = register(new BlockDeviceStub(Material.WOOD, SoundType.WOOD, 2.5f, 0f, 0f,
                new int[] {}, true, false, false), "blockMagicBox", "magic_box"));
        reg.register(blockEssentiaReservoir = register(new BlockDeviceStub(Material.IRON, SoundType.METAL, 2.0f, 17.0f, 0f,
                new int[] { 0 }, true, false, false), "blockEssentiaReservoir", "essentia_reservoir"));
        // Airy: invisible aura-node holder (MaterialAiry, blank texture, no collision, cloth step sound).
        reg.register(blockAiry = register(new BlockDeviceStub(new MaterialAiry(MapColor.AIR), SoundType.CLOTH, 0f, 0f, 0f,
                new int[] { 0 }, true, true, true), "blockAiry", "airy"));
        // Warded: unbreakable, no creative item (placed by the Warding focus, masks another block — Phase 3).
        reg.register(blockWarded = register(new BlockDeviceStub(Material.ROCK, SoundType.STONE, -1.0f, 999.0f, 0f,
                new int[] {}, false, false, false), "blockWarded", "warded"));

        // ---- B3 special blocks. arcane_door is a real working door; the three below are invisible,
        //      unbreakable, worldgen/mechanic-only blocks (no item, no creative) whose active behaviour
        //      (owner gate / entity damage / teleport TileEntity) is TODO Phase 3.
        reg.register(blockArcaneDoor = register(new BlockArcaneDoor(), "blockArcaneDoor", "arcane_door"));
        // Hole: the temporary passage the Portable Hole focus phases through walls. Blank cutout cube,
        // no collision, softly lit (0.7); unbreakable so it can't be mined while active.
        reg.register(blockHole = register(new BlockDeviceStub(Material.ROCK, SoundType.GLASS, -1.0f, 6000000.0f, 0.7f,
                new int[] {}, false, true, true), "blockHole", "hole"));
        // Eldritch Nothingness: invisible void cube with a centred 0.75 collision core you bump into,
        // faint glow (0.2), cloth step sound.
        reg.register(blockEldritchNothing = register(new BlockInvisibleTC(Material.ROCK, SoundType.CLOTH, 0.2f, 6000000.0f,
                new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 0.875, 0.875)), "blockEldritchNothing", "eldritch_nothing"));
        // Eldritch Portal: invisible, intangible, full-bright (15) gateway sandwiched between two
        // eldritch blocks; airy material, no collision.
        reg.register(blockEldritchPortal = register(new BlockInvisibleTC(new MaterialAiry(MapColor.AIR), SoundType.STONE,
                1.0f, 200000.0f, null), "blockEldritchPortal", "eldritch_portal"));

        // ---- B4 fluids. Register the Fluid objects first, then their blocks (BlockFluidClassic/Finite
        //      base handles flow/quanta + rendering via the forge:fluid model). Active effects
        //      (dissolve damage / warp-removal / slime & taint spread), MaterialTaint and buckets
        //      are TODO Phase 3.
        registerFluid(FLUXGOO);
        registerFluid(FLUXGAS);
        registerFluid(FLUIDPURE);
        registerFluid(FLUIDDEATH);
        reg.register(blockFluxGoo = register(new BlockFluxGoo(FLUXGOO), "blockFluxGoo", "fluxgoo"));
        reg.register(blockFluxGas = register(new BlockFluxGas(FLUXGAS), "blockFluxGas", "fluxgas"));
        reg.register(blockFluidPure = register(new BlockFluidPure(FLUIDPURE), "blockFluidPure", "fluidpure"));
        reg.register(blockFluidDeath = register(new BlockFluidDeath(FLUIDDEATH), "blockFluidDeath", "fluiddeath"));

        // Stairs — BlockStairs copies hardness/resistance/sound from the model state's block.
        reg.register(blockStairsArcaneStone = register(
                new BlockStairsTC(blockCosmeticSolid.getStateFromMeta(7)), "blockStairsArcaneStone", "stairs_arcane"));
        reg.register(blockStairsEldritch = register(
                new BlockStairsTC(blockCosmeticSolid.getStateFromMeta(11)), "blockStairsEldritch", "stairs_eldritch"));
        reg.register(blockStairsGreatwood = register(
                new BlockStairsTC(Blocks.PLANKS.getDefaultState()), "blockStairsGreatwood", "stairs_greatwood"));
        reg.register(blockStairsSilverwood = register(
                new BlockStairsTC(Blocks.PLANKS.getDefaultState()), "blockStairsSilverwood", "stairs_silverwood"));

        // Slabs — half slab + paired double slab per material family (wood: greatwood/silverwood, stone: arcane/eldritch).
        blockSlabWood = new BlockCosmeticSlab(Material.WOOD, false, SLAB_WOOD_NAMES, null, SoundType.WOOD);
        blockDoubleSlabWood = new BlockCosmeticSlab(Material.WOOD, true, SLAB_WOOD_NAMES, blockSlabWood, SoundType.WOOD);
        blockSlabStone = new BlockCosmeticSlab(Material.ROCK, false, SLAB_STONE_NAMES, null, SoundType.STONE);
        blockDoubleSlabStone = new BlockCosmeticSlab(Material.ROCK, true, SLAB_STONE_NAMES, blockSlabStone, SoundType.STONE);
        reg.register(registerSlab(blockSlabWood, "blockCosmeticSlabWood", "slab_wood", 2.0f, 5.0f));
        reg.register(registerSlab(blockDoubleSlabWood, "blockCosmeticSlabWood", "double_slab_wood", 2.0f, 5.0f));
        reg.register(registerSlab(blockSlabStone, "blockCosmeticSlabStone", "slab_stone", 2.0f, 10.0f));
        reg.register(registerSlab(blockDoubleSlabStone, "blockCosmeticSlabStone", "double_slab_stone", 2.0f, 10.0f));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();
        reg.register(makeItemBlock(blockCustomOre));
        reg.register(makeItemBlock(blockCosmeticSolid));
        reg.register(makeItemBlock(blockCosmeticOpaque));
        reg.register(makeItemBlock(blockMagicalLog));
        reg.register(makeItemBlock(blockMagicalLeaves));
        reg.register(makeItemBlock(blockCustomPlant));
        reg.register(makeItemBlock(blockCrystal));
        reg.register(makeItemBlock(blockTaint));
        reg.register(makeItemBlock(blockTaintFibres));
        reg.register(makeItemBlock(blockCandle));
        reg.register(makeItemBlock(blockLootUrn));
        reg.register(makeItemBlock(blockLootCrate));
        reg.register(makeItemBlock(blockEldritch));
        // blockManaPod: no ItemBlock — it only exists via worldgen and drops Mana Beans (Phase 3), never itself.

        // B2 devices. Furnaces/magic box keep an ItemBlock (for /give + recipes) even though they never appear
        // in the creative tab; blockWarded has no item at all (created by the Warding focus).
        reg.register(makeItemBlock(blockMetalDevice));
        reg.register(makeItemBlock(blockWoodenDevice));
        reg.register(makeItemBlock(blockStoneDevice));
        reg.register(makeItemBlock(blockTable));
        reg.register(makeItemBlock(blockJar));
        reg.register(makeItemBlock(blockTube));
        reg.register(makeItemBlock(blockMirror));
        reg.register(makeItemBlock(blockArcaneFurnace));
        reg.register(makeItemBlock(blockAlchemyFurnace));
        reg.register(makeItemBlock(blockChestHungry));
        reg.register(makeItemBlock(blockLifter));
        reg.register(makeItemBlock(blockMagicBox));
        reg.register(makeItemBlock(blockEssentiaReservoir));
        reg.register(makeItemBlock(blockAiry));
        // blockWarded: no ItemBlock (cannot be picked up).

        // B3: the Arcane Door is placed by a vanilla ItemDoor (handles the two-halves placement).
        // hole / eldritch_nothing / eldritch_portal have no item — they only exist via mechanics/worldgen.
        ItemDoor doorItem = new ItemDoor(blockArcaneDoor);
        doorItem.setRegistryName(blockArcaneDoor.getRegistryName());
        // Reuses the original TC4 lang key item.ItemArcaneDoor.name (already present in en_us.lang).
        doorItem.setUnlocalizedName("ItemArcaneDoor");
        doorItem.setCreativeTab(Thaumcraft.tabTC);
        itemArcaneDoor = doorItem;
        reg.register(doorItem);

        // B4 fluids: plain ItemBlocks so all four show in the creative tab and can be placed as
        // source blocks for testing; custom flux goo/gas item behaviour + buckets are TODO Phase 3.
        reg.register(makePlainItemBlock(blockFluxGoo));
        reg.register(makePlainItemBlock(blockFluxGas));
        reg.register(makePlainItemBlock(blockFluidPure));
        reg.register(makePlainItemBlock(blockFluidDeath));

        reg.register(makePlainItemBlock(blockStairsArcaneStone));
        reg.register(makePlainItemBlock(blockStairsEldritch));
        reg.register(makePlainItemBlock(blockStairsGreatwood));
        reg.register(makePlainItemBlock(blockStairsSilverwood));
        // Only the half slabs get an item (ItemSlab handles placing/combining into the double); doubles have no item.
        reg.register(makeItemSlab(blockSlabWood, blockDoubleSlabWood));
        reg.register(makeItemSlab(blockSlabStone, blockDoubleSlabStone));
    }

    /** Registers a Fluid unless another mod already claimed the name (then that one wins). */
    private static void registerFluid(Fluid fluid) {
        if (!FluidRegistry.isFluidRegistered(fluid.getName())) {
            FluidRegistry.registerFluid(fluid);
        }
    }

    private static Block register(Block block, String translationKey, String registryName) {
        block.setUnlocalizedName(translationKey);
        block.setRegistryName(new ResourceLocation(Thaumcraft.MODID, registryName));
        return block;
    }

    private static Block registerSlab(Block block, String translationKey, String registryName,
                                      float hardness, float resistance) {
        block.setUnlocalizedName(translationKey);
        block.setRegistryName(new ResourceLocation(Thaumcraft.MODID, registryName));
        block.setHardness(hardness);
        block.setResistance(resistance);
        return block;
    }

    private static Item makeItemBlock(Block block) {
        ItemBlock ib = new ItemBlockTC(block);
        ib.setRegistryName(block.getRegistryName());
        return ib;
    }

    private static Item makePlainItemBlock(Block block) {
        ItemBlock ib = new ItemBlock(block);
        ib.setRegistryName(block.getRegistryName());
        return ib;
    }

    private static Item makeItemSlab(Block half, Block dbl) {
        ItemSlabTC ib = new ItemSlabTC(half, (BlockSlab) half, (BlockSlab) dbl);
        ib.setRegistryName(half.getRegistryName());
        return ib;
    }
}
