package thaumcraft.common.config;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import thaumcraft.common.blocks.*;
import thaumcraft.common.blocks.ItemBlocks.*;
import thaumcraft.common.blocks.tinkerer.*;
import thaumcraft.common.compat.ThaumcraftSixCompatibility;
import thaumcraft.common.tiles.*;
import thaumcraft.common.tiles.tinkerer.*;

import java.util.Locale;

public class ConfigBlocks {

    // Fluid instances
    public static Fluid FLUIDPURE;
    public static Fluid FLUIDDEATH;
    public static Fluid FLUXGOO;
    public static Fluid FLUXGAS;

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;
    public static BlockTable blockTable;
    public static BlockStoneDevice blockStoneDevice;
    public static BlockWoodenDevice blockWoodenDevice;
    public static BlockMetalDevice blockMetalDevice;
    public static BlockTube blockTube;
    public static BlockMirror blockMirror;
    public static BlockEssentiaReservoir blockEssentiaReservoir;
    public static BlockArcaneFurnace blockArcaneFurnace;
    public static BlockAlchemyFurnace blockAlchemyFurnace;
    public static BlockMagicalLog blockMagicalLog;
    public static BlockMagicalLeaves blockMagicalLeaves;
    public static BlockCustomOre blockCustomOre;
    public static BlockCustomPlant blockCustomPlant;
    public static BlockCosmeticSolid blockCosmeticSolid;
    public static BlockCosmeticOpaque blockCosmeticOpaque;
    public static BlockTaint blockTaint;
    public static BlockTaintFibres blockTaintFibres;
    public static BlockAiry blockAiry;
    public static BlockFluxGoo blockFluxGoo;
    public static BlockFluxGas blockFluxGas;
    public static BlockFluidPure blockFluidPure;
    public static BlockFluidDeath blockFluidDeath;
    public static BlockManaPod blockManaPod;
    public static BlockEldritch blockEldritch;
    public static BlockEldritchNothing blockEldritchNothing;
    public static BlockEldritchPortal blockEldritchPortal;
    public static BlockStairsArcaneStone blockStairsArcaneStone;
    public static BlockStairsGreatwood blockStairsGreatwood;
    public static BlockStairsSilverwood blockStairsSilverwood;
    public static BlockStairsEldritch blockStairsEldritch;
    public static BlockCosmeticWoodSlab blockSlabWood;
    public static BlockCosmeticWoodSlab blockDoubleSlabWood;
    public static BlockCosmeticStoneSlab blockSlabStone;
    public static BlockCosmeticStoneSlab blockDoubleSlabStone;
    public static BlockLoot blockLootUrn;
    public static BlockLoot blockLootCrate;
    public static BlockChestHungry blockChestHungry;
    public static BlockArcaneDoor blockArcaneDoor;
    public static BlockLifter blockLifter;
    public static BlockHole blockHole;
    public static BlockWarded blockWarded;
    public static BlockCandle blockCandle;

    // ItemBlock instances
    public static BlockMagicalLeavesItem blockMagicalLeavesItem;
    public static BlockCustomOreItem blockCustomOreItem;
    public static BlockCustomPlantItem blockCustomPlantItem;
    public static BlockCosmeticSolidItem blockCosmeticSolidItem;
    // Thaumic Tinkerer decorative block
    public static BlockDarkQuartz blockDarkQuartz;
    public static BlockDarkQuartzItem blockDarkQuartzItem;
    // Thaumic Tinkerer functional block
    public static BlockFunnel blockFunnel;
    public static BlockDarkQuartzSlab blockSlabDarkQuartz;
    public static BlockDarkQuartzSlab blockDoubleSlabDarkQuartz;
    public static BlockDarkQuartzStairs blockStairsDarkQuartz;
    public static BlockMagnet blockMagnet;
    public static BlockRepairer blockRepairer;
    public static BlockTransvectorInterface blockTransvectorInterface;
    public static BlockTransvectorDislocator blockTransvectorDislocator;
    public static BlockAnimationTablet blockAnimationTablet;
    public static BlockEnchanter blockEnchanter;
    public static BlockPlatform blockPlatform;
    public static BlockForcefield blockForcefield;
    public static BlockSummon blockSummon;
    public static BlockMobilizer blockMobilizer;
    public static BlockMobilizerRelay blockMobilizerRelay;
    /** One per primal, indexed by {@link thaumcraft.common.items.tinkerer.PrimalCrop}. */
    public static BlockInfusedGrain[] blockInfusedGrain = new BlockInfusedGrain[4];
    public static thaumcraft.common.blocks.tinkerer.gas.BlockGaseousLight blockGaseousLight;
    public static thaumcraft.common.blocks.tinkerer.gas.BlockGaseousShadow blockGaseousShadow;
    public static thaumcraft.common.blocks.tinkerer.gas.BlockNitorGas blockNitorGas;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireAir blockFireAir;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireWater blockFireWater;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireEarth blockFireEarth;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireIgnis blockFireIgnis;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireOrder blockFireOrder;
    public static thaumcraft.common.blocks.tinkerer.fire.BlockFireChaos blockFireChaos;
    public static thaumcraft.common.blocks.tinkerer.kami.BlockBedrockPortal blockBedrockPortal;
    public static BlockCosmeticOpaqueItem blockCosmeticOpaqueItem;
    public static BlockTaintItem blockTaintItem;
    public static BlockTaintFibresItem blockTaintFibresItem;
    public static BlockAiryItem blockAiryItem;
    public static BlockFluxGooItem blockFluxGooItem;
    public static BlockFluxGasItem blockFluxGasItem;
    public static BlockCrystalItem blockCrystalItem;

    public static void init() {
        initFluids();

        blockJar = (BlockJar) new BlockJar()
                .setRegistryName("thaumcraft", legacyPath("blockJar"))
                .setTranslationKey("thaumcraft.jar");

        blockCrystal = (BlockCrystal) new BlockCrystal()
                .setRegistryName("thaumcraft", legacyPath("blockCrystal"))
                .setTranslationKey("thaumcraft.crystal");

        blockTable = (BlockTable) new BlockTable()
                .setRegistryName("thaumcraft", legacyPath("blockTable"))
                .setTranslationKey("thaumcraft.table");

        blockStoneDevice = (BlockStoneDevice) new BlockStoneDevice()
                .setRegistryName("thaumcraft", legacyPath("blockStoneDevice"))
                .setTranslationKey("thaumcraft.stone_device");

        blockWoodenDevice = (BlockWoodenDevice) new BlockWoodenDevice()
                .setRegistryName("thaumcraft", legacyPath("blockWoodenDevice"))
                .setTranslationKey("thaumcraft.wooden_device");

        blockMetalDevice = (BlockMetalDevice) new BlockMetalDevice()
                .setRegistryName("thaumcraft", legacyPath("blockMetalDevice"))
                .setTranslationKey("thaumcraft.metal_device");

        blockTube = (BlockTube) new BlockTube()
                .setRegistryName("thaumcraft", legacyPath("blockTube"))
                .setTranslationKey("thaumcraft.tube");

        blockMirror = (BlockMirror) new BlockMirror()
                .setRegistryName("thaumcraft", legacyPath("blockMirror"))
                .setTranslationKey("thaumcraft.mirror");

        blockEssentiaReservoir = (BlockEssentiaReservoir) new BlockEssentiaReservoir()
                .setRegistryName("thaumcraft", legacyPath("blockEssentiaReservoir"))
                .setTranslationKey("thaumcraft.essentia_reservoir");

        blockArcaneFurnace = (BlockArcaneFurnace) new BlockArcaneFurnace()
                .setRegistryName("thaumcraft", legacyPath("blockArcaneFurnace"))
                .setTranslationKey("thaumcraft.arcane_furnace");

        blockAlchemyFurnace = (BlockAlchemyFurnace) new BlockAlchemyFurnace()
                .setRegistryName("thaumcraft", legacyPath("blockAlchemyFurnace"))
                .setTranslationKey("thaumcraft.alchemy_furnace_advanced");

        blockMagicalLog = (BlockMagicalLog) new BlockMagicalLog()
                .setRegistryName("thaumcraft", legacyPath("blockMagicalLog"))
                .setTranslationKey("thaumcraft.magical_log");

        blockMagicalLeaves = (BlockMagicalLeaves) new BlockMagicalLeaves()
                .setRegistryName("thaumcraft", legacyPath("blockMagicalLeaves"))
                .setTranslationKey("thaumcraft.magical_leaves");

        blockCustomOre = (BlockCustomOre) new BlockCustomOre()
                .setRegistryName("thaumcraft", legacyPath("blockCustomOre"))
                .setTranslationKey("thaumcraft.custom_ore");

        blockCustomPlant = (BlockCustomPlant) new BlockCustomPlant()
                .setRegistryName("thaumcraft", legacyPath("blockCustomPlant"))
                .setTranslationKey("thaumcraft.custom_plant");

        blockCosmeticSolid = (BlockCosmeticSolid) new BlockCosmeticSolid()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticSolid"))
                .setTranslationKey("thaumcraft.cosmetic_solid");

        blockDarkQuartz = (BlockDarkQuartz) new BlockDarkQuartz()
                .setRegistryName("thaumcraft", legacyPath("blockDarkQuartz"))
                .setTranslationKey("thaumcraft.dark_quartz");

        blockFunnel = (BlockFunnel) new BlockFunnel()
                .setRegistryName("thaumcraft", legacyPath("blockFunnel"))
                .setTranslationKey("thaumcraft.funnel");

        blockSlabDarkQuartz = (BlockDarkQuartzSlab) new BlockDarkQuartzSlab.Half()
                .setRegistryName("thaumcraft", legacyPath("blockSlabDarkQuartz"))
                .setTranslationKey("thaumcraft.slab_dark_quartz");
        blockDoubleSlabDarkQuartz = (BlockDarkQuartzSlab) new BlockDarkQuartzSlab.Double()
                .setRegistryName("thaumcraft", legacyPath("blockDoubleSlabDarkQuartz"))
                .setTranslationKey("thaumcraft.slab_dark_quartz");
        blockStairsDarkQuartz = (BlockDarkQuartzStairs) new BlockDarkQuartzStairs()
                .setRegistryName("thaumcraft", legacyPath("blockStairsDarkQuartz"))
                .setTranslationKey("thaumcraft.stairs_dark_quartz");

        blockMagnet = (BlockMagnet) new BlockMagnet()
                .setRegistryName("thaumcraft", legacyPath("blockMagnet"))
                .setTranslationKey("thaumcraft.magnet");

        blockRepairer = (BlockRepairer) new BlockRepairer()
                .setRegistryName("thaumcraft", legacyPath("blockRepairer"))
                .setTranslationKey("thaumcraft.repairer");

        blockTransvectorInterface = (BlockTransvectorInterface) new BlockTransvectorInterface()
                .setRegistryName("thaumcraft", legacyPath("blockTransvectorInterface"))
                .setTranslationKey("thaumcraft.transvector_interface");

        blockTransvectorDislocator = (BlockTransvectorDislocator) new BlockTransvectorDislocator()
                .setRegistryName("thaumcraft", legacyPath("blockTransvectorDislocator"))
                .setTranslationKey("thaumcraft.transvector_dislocator");

        blockAnimationTablet = (BlockAnimationTablet) new BlockAnimationTablet()
                .setRegistryName("thaumcraft", legacyPath("blockAnimationTablet"))
                .setTranslationKey("thaumcraft.animation_tablet");

        blockEnchanter = (BlockEnchanter) new BlockEnchanter()
                .setRegistryName("thaumcraft", legacyPath("blockEnchanter"))
                .setTranslationKey("thaumcraft.enchanter");

        blockBedrockPortal = (thaumcraft.common.blocks.tinkerer.kami.BlockBedrockPortal)
                new thaumcraft.common.blocks.tinkerer.kami.BlockBedrockPortal()
                        .setRegistryName("thaumcraft", legacyPath("blockBedrockPortal"))
                        .setTranslationKey("thaumcraft.bedrock_portal");

        blockCosmeticOpaque = (BlockCosmeticOpaque) new BlockCosmeticOpaque()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticOpaque"))
                .setTranslationKey("thaumcraft.cosmetic_opaque");

        blockTaint = (BlockTaint) new BlockTaint()
                .setRegistryName("thaumcraft", legacyPath("blockTaint"))
                .setTranslationKey("thaumcraft.taint");

        blockTaintFibres = (BlockTaintFibres) new BlockTaintFibres()
                .setRegistryName("thaumcraft", legacyPath("blockTaintFibres"))
                .setTranslationKey("thaumcraft.taint_fibres");

        blockAiry = (BlockAiry) new BlockAiry()
                .setRegistryName("thaumcraft", legacyPath("blockAiry"))
                .setTranslationKey("thaumcraft.airy");

        blockFluxGoo = (BlockFluxGoo) new BlockFluxGoo()
                .setRegistryName("thaumcraft", legacyPath("blockFluxGoo"))
                .setTranslationKey("thaumcraft.flux_goo");

        blockFluxGas = (BlockFluxGas) new BlockFluxGas()
                .setRegistryName("thaumcraft", legacyPath("blockFluxGas"))
                .setTranslationKey("thaumcraft.flux_gas");

        blockFluidPure = (BlockFluidPure) new BlockFluidPure()
                .setRegistryName("thaumcraft", legacyPath("blockFluidPure"))
                .setTranslationKey("thaumcraft.fluid_pure");

        blockFluidDeath = (BlockFluidDeath) new BlockFluidDeath()
                .setRegistryName("thaumcraft", legacyPath("blockFluidDeath"))
                .setTranslationKey("thaumcraft.fluid_death");

        blockManaPod = (BlockManaPod) new BlockManaPod()
                .setRegistryName("thaumcraft", legacyPath("blockManaPod"))
                .setTranslationKey("thaumcraft.mana_pod");

        blockEldritch = (BlockEldritch) new BlockEldritch()
                .setRegistryName("thaumcraft", legacyPath("blockEldritch"))
                .setTranslationKey("thaumcraft.eldritch");

        blockEldritchNothing = (BlockEldritchNothing) new BlockEldritchNothing()
                .setRegistryName("thaumcraft", legacyPath("blockEldritchNothing"))
                .setTranslationKey("thaumcraft.eldritch_nothing");

        blockEldritchPortal = (BlockEldritchPortal) new BlockEldritchPortal()
                .setRegistryName("thaumcraft", legacyPath("blockPortalEldritch"))
                .setTranslationKey("thaumcraft.eldritch_portal");

        blockStairsArcaneStone = (BlockStairsArcaneStone) new BlockStairsArcaneStone()
                .setRegistryName("thaumcraft", legacyPath("blockStairsArcaneStone"))
                .setTranslationKey("thaumcraft.stairs_arcane");

        blockStairsGreatwood = (BlockStairsGreatwood) new BlockStairsGreatwood()
                .setRegistryName("thaumcraft", legacyPath("blockStairsGreatwood"))
                .setTranslationKey("thaumcraft.stairs_greatwood");

        blockStairsSilverwood = (BlockStairsSilverwood) new BlockStairsSilverwood()
                .setRegistryName("thaumcraft", legacyPath("blockStairsSilverwood"))
                .setTranslationKey("thaumcraft.stairs_silverwood");

        blockStairsEldritch = (BlockStairsEldritch) new BlockStairsEldritch()
                .setRegistryName("thaumcraft", legacyPath("blockStairsEldritch"))
                .setTranslationKey("thaumcraft.stairs_eldritch");

        blockSlabWood = (BlockCosmeticWoodSlab) new BlockCosmeticWoodSlab.Half()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticSlabWood"))
                .setTranslationKey("blockCosmeticSlabWood");

        blockDoubleSlabWood = (BlockCosmeticWoodSlab) new BlockCosmeticWoodSlab.Double()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticDoubleSlabWood"))
                .setTranslationKey("blockCosmeticSlabWood");

        blockSlabStone = (BlockCosmeticStoneSlab) new BlockCosmeticStoneSlab.Half()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticSlabStone"))
                .setTranslationKey("blockCosmeticSlabStone");

        blockDoubleSlabStone = (BlockCosmeticStoneSlab) new BlockCosmeticStoneSlab.Double()
                .setRegistryName("thaumcraft", legacyPath("blockCosmeticDoubleSlabStone"))
                .setTranslationKey("blockCosmeticSlabStone");

        blockLootUrn = (BlockLoot) new BlockLoot(net.minecraft.block.material.Material.CIRCUITS, 1)
                .setRegistryName("thaumcraft", legacyPath("blockLootUrn"))
                .setTranslationKey("thaumcraft.loot_urn");

        blockLootCrate = (BlockLoot) new BlockLoot(net.minecraft.block.material.Material.WOOD, 2)
                .setRegistryName("thaumcraft", legacyPath("blockLootCrate"))
                .setTranslationKey("thaumcraft.loot_crate");

        blockFireAir = (thaumcraft.common.blocks.tinkerer.fire.BlockFireAir)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireAir()
                .setRegistryName("thaumcraft", legacyPath("blockFireAir"))
                .setTranslationKey("thaumcraft.fire_air");

        blockFireWater = (thaumcraft.common.blocks.tinkerer.fire.BlockFireWater)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireWater()
                .setRegistryName("thaumcraft", legacyPath("blockFireWater"))
                .setTranslationKey("thaumcraft.fire_water");

        blockFireEarth = (thaumcraft.common.blocks.tinkerer.fire.BlockFireEarth)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireEarth()
                .setRegistryName("thaumcraft", legacyPath("blockFireEarth"))
                .setTranslationKey("thaumcraft.fire_earth");

        blockFireIgnis = (thaumcraft.common.blocks.tinkerer.fire.BlockFireIgnis)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireIgnis()
                .setRegistryName("thaumcraft", legacyPath("blockFireIgnis"))
                .setTranslationKey("thaumcraft.fire_ignis");

        blockFireOrder = (thaumcraft.common.blocks.tinkerer.fire.BlockFireOrder)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireOrder()
                .setRegistryName("thaumcraft", legacyPath("blockFireOrder"))
                .setTranslationKey("thaumcraft.fire_order");

        blockFireChaos = (thaumcraft.common.blocks.tinkerer.fire.BlockFireChaos)
                new thaumcraft.common.blocks.tinkerer.fire.BlockFireChaos()
                .setRegistryName("thaumcraft", legacyPath("blockFireChaos"))
                .setTranslationKey("thaumcraft.fire_chaos");

        blockGaseousLight = (thaumcraft.common.blocks.tinkerer.gas.BlockGaseousLight)
                new thaumcraft.common.blocks.tinkerer.gas.BlockGaseousLight()
                .setRegistryName("thaumcraft", legacyPath("blockGaseousLight"))
                .setTranslationKey("thaumcraft.gaseous_light");

        blockGaseousShadow = (thaumcraft.common.blocks.tinkerer.gas.BlockGaseousShadow)
                new thaumcraft.common.blocks.tinkerer.gas.BlockGaseousShadow()
                .setRegistryName("thaumcraft", legacyPath("blockGaseousShadow"))
                .setTranslationKey("thaumcraft.gaseous_shadow");

        blockNitorGas = (thaumcraft.common.blocks.tinkerer.gas.BlockNitorGas)
                new thaumcraft.common.blocks.tinkerer.gas.BlockNitorGas()
                .setRegistryName("thaumcraft", legacyPath("blockNitorGas"))
                .setTranslationKey("thaumcraft.nitor_gas");

        blockInfusedGrain[0] = (BlockInfusedGrain) new BlockInfusedGrain(
                thaumcraft.common.items.tinkerer.PrimalCrop.AIR)
                .setRegistryName("thaumcraft", legacyPath("blockInfusedGrainAer"))
                .setTranslationKey("thaumcraft.infused_grain_aer");

        blockInfusedGrain[1] = (BlockInfusedGrain) new BlockInfusedGrain(
                thaumcraft.common.items.tinkerer.PrimalCrop.FIRE)
                .setRegistryName("thaumcraft", legacyPath("blockInfusedGrainIgnis"))
                .setTranslationKey("thaumcraft.infused_grain_ignis");

        blockInfusedGrain[2] = (BlockInfusedGrain) new BlockInfusedGrain(
                thaumcraft.common.items.tinkerer.PrimalCrop.EARTH)
                .setRegistryName("thaumcraft", legacyPath("blockInfusedGrainTerra"))
                .setTranslationKey("thaumcraft.infused_grain_terra");

        blockInfusedGrain[3] = (BlockInfusedGrain) new BlockInfusedGrain(
                thaumcraft.common.items.tinkerer.PrimalCrop.WATER)
                .setRegistryName("thaumcraft", legacyPath("blockInfusedGrainAqua"))
                .setTranslationKey("thaumcraft.infused_grain_aqua");

        blockMobilizer = (BlockMobilizer) new BlockMobilizer()
                .setRegistryName("thaumcraft", legacyPath("blockMobilizer"))
                .setTranslationKey("thaumcraft.mobilizer");

        blockMobilizerRelay = (BlockMobilizerRelay) new BlockMobilizerRelay()
                .setRegistryName("thaumcraft", legacyPath("blockMobilizerRelay"))
                .setTranslationKey("thaumcraft.mobilizer_relay");

        blockSummon = (BlockSummon) new BlockSummon()
                .setRegistryName("thaumcraft", legacyPath("blockSummon"))
                .setTranslationKey("thaumcraft.summon");

        blockForcefield = (BlockForcefield) new BlockForcefield()
                .setRegistryName("thaumcraft", legacyPath("blockForcefield"))
                .setTranslationKey("thaumcraft.forcefield");

        blockPlatform = (BlockPlatform) new BlockPlatform()
                .setRegistryName("thaumcraft", legacyPath("blockPlatform"))
                .setTranslationKey("thaumcraft.platform");

        blockChestHungry = (BlockChestHungry) new BlockChestHungry()
                .setRegistryName("thaumcraft", legacyPath("blockChestHungry"))
                .setTranslationKey("thaumcraft.hungry_chest");

        blockArcaneDoor = (BlockArcaneDoor) new BlockArcaneDoor()
                .setRegistryName("thaumcraft", legacyPath("blockArcaneDoor"))
                .setTranslationKey("thaumcraft.arcane_door");

        blockLifter = (BlockLifter) new BlockLifter()
                .setRegistryName("thaumcraft", legacyPath("blockLifter"))
                .setTranslationKey("thaumcraft.lifter");

        blockHole = (BlockHole) new BlockHole()
                .setRegistryName("thaumcraft", legacyPath("blockHole"))
                .setTranslationKey("thaumcraft.hole");

        blockWarded = (BlockWarded) new BlockWarded()
                .setRegistryName("thaumcraft", legacyPath("blockWarded"))
                .setTranslationKey("thaumcraft.warded");

        blockCandle = (BlockCandle) new BlockCandle()
                .setRegistryName("thaumcraft", legacyPath("blockCandle"))
                .setTranslationKey("blockCandle");

        // ItemBlock instances (cast needed because setRegistryName returns Item)
        blockMagicalLeavesItem = (BlockMagicalLeavesItem) new BlockMagicalLeavesItem(blockMagicalLeaves)
                .setRegistryName(blockMagicalLeaves.getRegistryName());

        blockCustomOreItem = (BlockCustomOreItem) new BlockCustomOreItem(blockCustomOre)
                .setRegistryName(blockCustomOre.getRegistryName());

        blockCustomPlantItem = (BlockCustomPlantItem) new BlockCustomPlantItem(blockCustomPlant)
                .setRegistryName(blockCustomPlant.getRegistryName());

        blockDarkQuartzItem = (BlockDarkQuartzItem) new BlockDarkQuartzItem(blockDarkQuartz)
                .setRegistryName(blockDarkQuartz.getRegistryName());

        blockCosmeticSolidItem = (BlockCosmeticSolidItem) new BlockCosmeticSolidItem(blockCosmeticSolid)
                .setRegistryName(blockCosmeticSolid.getRegistryName());

        blockCosmeticOpaqueItem = (BlockCosmeticOpaqueItem) new BlockCosmeticOpaqueItem(blockCosmeticOpaque)
                .setRegistryName(blockCosmeticOpaque.getRegistryName());

        blockTaintItem = (BlockTaintItem) new BlockTaintItem(blockTaint)
                .setRegistryName(blockTaint.getRegistryName());

        blockTaintFibresItem = (BlockTaintFibresItem) new BlockTaintFibresItem(blockTaintFibres)
                .setRegistryName(blockTaintFibres.getRegistryName());

        blockAiryItem = (BlockAiryItem) new BlockAiryItem(blockAiry)
                .setRegistryName(blockAiry.getRegistryName());

        blockFluxGooItem = (BlockFluxGooItem) new BlockFluxGooItem(blockFluxGoo)
                .setRegistryName(blockFluxGoo.getRegistryName());

        blockFluxGasItem = (BlockFluxGasItem) new BlockFluxGasItem(blockFluxGas)
                .setRegistryName(blockFluxGas.getRegistryName());

        blockCrystalItem = (BlockCrystalItem) new BlockCrystalItem(blockCrystal)
                .setRegistryName(blockCrystal.getRegistryName());

        ThaumcraftSixCompatibility.initBlockAliases();
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal,
                blockTable,
                blockStoneDevice,
                blockWoodenDevice,
                blockMetalDevice,
                blockTube,
                blockMirror,
                blockEssentiaReservoir,
                blockArcaneFurnace,
                blockAlchemyFurnace,
                blockMagicalLog,
                blockMagicalLeaves,
                blockCustomOre,
                blockCustomPlant,
                blockCosmeticSolid,
                blockDarkQuartz,
                blockFunnel,
                blockSlabDarkQuartz,
                blockDoubleSlabDarkQuartz,
                blockStairsDarkQuartz,
                blockMagnet,
                blockRepairer,
                blockTransvectorInterface,
                blockTransvectorDislocator,
                blockAnimationTablet,
                blockEnchanter,
                blockBedrockPortal,
                blockCosmeticOpaque,
                blockTaint,
                blockTaintFibres,
                blockAiry,
                blockFluxGoo,
                blockFluxGas,
                blockFluidPure,
                blockFluidDeath,
                blockManaPod,
                blockEldritch,
                blockEldritchNothing,
                blockEldritchPortal,
                blockStairsArcaneStone,
                blockStairsGreatwood,
                blockStairsSilverwood,
                blockStairsEldritch,
                blockSlabWood,
                blockDoubleSlabWood,
                blockSlabStone,
                blockDoubleSlabStone,
                blockLootUrn,
                blockLootCrate,
                blockGaseousLight,
                blockGaseousShadow,
                blockNitorGas,
                blockInfusedGrain[0],
                blockInfusedGrain[1],
                blockInfusedGrain[2],
                blockInfusedGrain[3],
                blockMobilizer,
                blockMobilizerRelay,
                blockSummon,
                blockForcefield,
                blockPlatform,
                blockFireAir,
                blockFireWater,
                blockFireEarth,
                blockFireIgnis,
                blockFireOrder,
                blockFireChaos,
                blockChestHungry,
                blockArcaneDoor,
                blockLifter,
                blockHole,
                blockWarded,
                blockCandle
        };
    }

    public static void registerItemBlocks(net.minecraftforge.registries.IForgeRegistry<net.minecraft.item.Item> registry) {
        registry.registerAll(
            blockMagicalLeavesItem,
            blockCustomOreItem,
            blockCustomPlantItem,
            blockCosmeticSolidItem,
            blockDarkQuartzItem,
            blockCosmeticOpaqueItem,
            blockTaintItem,
            blockTaintFibresItem,
            blockAiryItem,
            blockFluxGooItem,
            blockFluxGasItem,
            blockCrystalItem
        );
        registry.register(new BlockMetadataItem(blockTable)
                .setRegistryName(blockTable.getRegistryName()));
        registry.register(new BlockMetadataItem(blockStoneDevice)
                .setRegistryName(blockStoneDevice.getRegistryName()));
        registry.register(new BlockWoodenDeviceItem(blockWoodenDevice)
                .setRegistryName(blockWoodenDevice.getRegistryName()));
        registry.register(new BlockMetadataItem(blockMetalDevice)
                .setRegistryName(blockMetalDevice.getRegistryName()));
        registry.register(new BlockTubeItem(blockTube)
                .setRegistryName(blockTube.getRegistryName()));
        registry.register(new BlockMirrorItem(blockMirror)
                .setRegistryName(blockMirror.getRegistryName()));
        registry.register(new BlockEssentiaReservoirItem(blockEssentiaReservoir)
                .setRegistryName(blockEssentiaReservoir.getRegistryName()));
        registry.register(new BlockArcaneFurnaceItem(blockArcaneFurnace)
                .setRegistryName(blockArcaneFurnace.getRegistryName()));
        registry.register(new BlockMetadataItem(blockMagicalLog)
                .setRegistryName(blockMagicalLog.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockManaPod)
                .setRegistryName(blockManaPod.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockHole)
                .setRegistryName(blockHole.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockWarded)
                .setRegistryName(blockWarded.getRegistryName()));
        registry.register(new BlockMetadataItem(blockCandle)
                .setRegistryName(blockCandle.getRegistryName()));
        registry.register(new BlockEldritchItem(blockEldritch)
                .setRegistryName(blockEldritch.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchNothing)
                .setRegistryName(blockEldritchNothing.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockEldritchPortal)
                .setRegistryName(blockEldritchPortal.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsArcaneStone)
                .setRegistryName(blockStairsArcaneStone.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsGreatwood)
                .setRegistryName(blockStairsGreatwood.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsSilverwood)
                .setRegistryName(blockStairsSilverwood.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsEldritch)
                .setRegistryName(blockStairsEldritch.getRegistryName()));
        registry.register(new BlockCosmeticWoodSlabItem(blockSlabWood)
                .setRegistryName(blockSlabWood.getRegistryName()));
        registry.register(new BlockCosmeticStoneSlabItem(blockSlabStone)
                .setRegistryName(blockSlabStone.getRegistryName()));
        registry.register(new BlockLootItem(blockLootUrn)
                .setRegistryName(blockLootUrn.getRegistryName()));
        registry.register(new BlockLootItem(blockLootCrate)
                .setRegistryName(blockLootCrate.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockMobilizer)
                .setRegistryName(blockMobilizer.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockMobilizerRelay)
                .setRegistryName(blockMobilizerRelay.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockSummon)
                .setRegistryName(blockSummon.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockPlatform)
                .setRegistryName(blockPlatform.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireAir)
                .setRegistryName(blockFireAir.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireWater)
                .setRegistryName(blockFireWater.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireEarth)
                .setRegistryName(blockFireEarth.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireIgnis)
                .setRegistryName(blockFireIgnis.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireOrder)
                .setRegistryName(blockFireOrder.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFireChaos)
                .setRegistryName(blockFireChaos.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockChestHungry)
                .setRegistryName(blockChestHungry.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockLifter)
                .setRegistryName(blockLifter.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockFunnel)
                .setRegistryName(blockFunnel.getRegistryName()));
        registry.register(new net.minecraft.item.ItemSlab(blockSlabDarkQuartz,
                blockSlabDarkQuartz, blockDoubleSlabDarkQuartz)
                .setRegistryName(blockSlabDarkQuartz.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockStairsDarkQuartz)
                .setRegistryName(blockStairsDarkQuartz.getRegistryName()));
        registry.register(new thaumcraft.common.blocks.ItemBlocks.BlockMagnetItem(blockMagnet)
                .setRegistryName(blockMagnet.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockRepairer)
                .setRegistryName(blockRepairer.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockTransvectorInterface)
                .setRegistryName(blockTransvectorInterface.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockTransvectorDislocator)
                .setRegistryName(blockTransvectorDislocator.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockAnimationTablet)
                .setRegistryName(blockAnimationTablet.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(blockEnchanter)
                .setRegistryName(blockEnchanter.getRegistryName()));
    }

    public static void registerTileEntities() {
        for (TileRegistration entry : TILE_REGISTRATIONS) {
            GameRegistry.registerTileEntity(entry.tileClass, legacyLocation(entry.legacyToken));
        }
    }

    public static String legacyPath(String legacyToken) {
        return legacyToken.toLowerCase(Locale.ROOT);
    }

    private static void initFluids() {
        Fluid existingPure = FluidRegistry.getFluid("fluidPure");
        if (existingPure != null) {
            FLUIDPURE = existingPure;
        } else {
            FLUIDPURE = new Fluid("fluidPure",
                    new ResourceLocation("thaumcraft", "blocks/fluidpure"),
                    new ResourceLocation("thaumcraft", "blocks/fluidpure"))
                    .setGaseous(false)
                    .setLuminosity(8)
                    .setViscosity(1500)
                    .setRarity(EnumRarity.RARE);
            FluidRegistry.registerFluid(FLUIDPURE);
        }

        Fluid existingDeath = FluidRegistry.getFluid("fluidDeath");
        if (existingDeath != null) {
            FLUIDDEATH = existingDeath;
        } else {
            FLUIDDEATH = new Fluid("fluidDeath",
                    new ResourceLocation("thaumcraft", "blocks/fluiddeath"),
                    new ResourceLocation("thaumcraft", "blocks/fluiddeath"))
                    .setGaseous(false)
                    .setLuminosity(8)
                    .setViscosity(1500)
                    .setRarity(EnumRarity.RARE);
            FluidRegistry.registerFluid(FLUIDDEATH);
        }

        Fluid existingFluxGoo = FluidRegistry.getFluid("fluxgoo");
        if (existingFluxGoo != null) {
            FLUXGOO = existingFluxGoo;
        } else {
            FLUXGOO = new Fluid("fluxgoo",
                    new ResourceLocation("thaumcraft", "blocks/fluxgoo"),
                    new ResourceLocation("thaumcraft", "blocks/fluxgoo"))
                    .setGaseous(false)
                    .setLuminosity(7)
                    .setDensity(8)
                    .setViscosity(6000)
                    .setRarity(EnumRarity.RARE);
            FluidRegistry.registerFluid(FLUXGOO);
        }

        Fluid existingFluxGas = FluidRegistry.getFluid("fluxgas");
        if (existingFluxGas != null) {
            FLUXGAS = existingFluxGas;
        } else {
            FLUXGAS = new Fluid("fluxgas",
                    new ResourceLocation("thaumcraft", "blocks/fluxgas"),
                    new ResourceLocation("thaumcraft", "blocks/fluxgas"))
                    .setGaseous(true)
                    .setLuminosity(7)
                    .setDensity(-4)
                    .setViscosity(2500)
                    .setRarity(EnumRarity.RARE);
            FluidRegistry.registerFluid(FLUXGAS);
        }
    }

    private static ResourceLocation legacyLocation(String legacyToken) {
        return new ResourceLocation("thaumcraft", legacyPath(legacyToken));
    }

    private static final TileRegistration[] TILE_REGISTRATIONS = new TileRegistration[]{
            new TileRegistration(TileFunnel.class, "TileFunnel"),
            new TileRegistration(TileMobilizer.class, "TileMobilizer"),
            new TileRegistration(TileMobilizerRelay.class, "TileMobilizerRelay"),
            new TileRegistration(TileSummon.class, "TileSummon"),
            new TileRegistration(TileForcefield.class, "TileForcefield"),
            new TileRegistration(TileCamo.class, "TileCamo"),
            new TileRegistration(TileMagnet.class, "TileMagnet"),
            new TileRegistration(TileMobMagnet.class, "TileMobMagnet"),
            new TileRegistration(TileRepairer.class, "TileRepairer"),
            new TileRegistration(TileTransvectorInterface.class, "TileTransvectorInterface"),
            new TileRegistration(TileTransvectorDislocator.class, "TileTransvectorDislocator"),
            new TileRegistration(TileAnimationTablet.class, "TileAnimationTablet"),
            new TileRegistration(TileEnchanter.class, "TileEnchanter"),
            new TileRegistration(TileJarFillable.class, "TileJar"),
            new TileRegistration(TileJarBrain.class, "TileJarBrain"),
            new TileRegistration(TileJarNode.class, "TileJarNode"),
            new TileRegistration(TileJarFillableVoid.class, "TileJarVoid"),
            new TileRegistration(TileCrystal.class, "TileCrystal"),
            new TileRegistration(TileEldritchCrystal.class, "TileEldritchCrystal"),
            new TileRegistration(TileNode.class, "TileNode"),
            new TileRegistration(TileTable.class, "TileTable"),
            new TileRegistration(TileMagicWorkbench.class, "TileMagicWorkbench"),
            new TileRegistration(TileArcaneWorkbench.class, "TileArcaneWorkbench"),
            new TileRegistration(TileDeconstructionTable.class, "TileDeconstructionTable"),
            new TileRegistration(TileResearchTable.class, "TileResearchTable"),
            new TileRegistration(TilePedestal.class, "TilePedestal"),
            new TileRegistration(TileWandPedestal.class, "TileWandPedestal"),
            new TileRegistration(TileAlchemyFurnace.class, "TileAlchemyFurnace"),
            new TileRegistration(TileAlchemyFurnaceAdvanced.class, "TileAlchemyFurnaceAdvanced"),
            new TileRegistration(TileAlchemyFurnaceAdvancedNozzle.class, "TileAlchemyFurnaceAdvancedNozzle"),
            new TileRegistration(TileInfusionMatrix.class, "TileInfusionStone"),
            new TileRegistration(TileInfusionPillar.class, "TileInfusionPillar"),
            new TileRegistration(TileNodeStabilizer.class, "TileNodeStabilizer"),
            new TileRegistration(TileNodeConverter.class, "TileNodeConverter"),
            new TileRegistration(TileSpa.class, "TileSpa"),
            new TileRegistration(TileFocalManipulator.class, "TileFocalManipulator"),
            new TileRegistration(TileFluxScrubber.class, "TileFluxScrubber"),
            new TileRegistration(TileCrucible.class, "TileCrucible"),
            new TileRegistration(TileManaPod.class, "TileManaPod"),
            new TileRegistration(TileArcaneBore.class, "TileArcaneBore"),
            new TileRegistration(TileArcaneBoreBase.class, "TileArcaneBoreBase"),
            new TileRegistration(TileArcaneFurnace.class, "TileArcaneFurnace"),
            new TileRegistration(TileArcaneFurnaceNozzle.class, "TileArcaneFurnaceNozzle"),
            new TileRegistration(TileBellows.class, "TileBellows"),
            new TileRegistration(TileTube.class, "TileTube"),
            new TileRegistration(TileTubeValve.class, "TileTubeValve"),
            new TileRegistration(TileTubeFilter.class, "TileTubeFilter"),
            new TileRegistration(TileTubeBuffer.class, "TileTubeBuffer"),
            new TileRegistration(TileTubeRestrict.class, "TileTubeRestrict"),
            new TileRegistration(TileTubeOneway.class, "TileTubeOneway"),
            new TileRegistration(TileEssentiaCrystalizer.class, "TileEssentiaCrystalizer"),
            new TileRegistration(TileCentrifuge.class, "TileCentrifuge"),
            new TileRegistration(TileEssentiaReservoir.class, "TileEssentiaReservoir"),
            new TileRegistration(TileMirror.class, "TileMirror"),
            new TileRegistration(TileMirrorEssentia.class, "TileMirrorEssentia"),
            new TileRegistration(TileVisRelay.class, "TileVisRelay"),
            new TileRegistration(TileMagicWorkbenchCharger.class, "TileMagicWorkbenchCharger"),
            new TileRegistration(TileOwned.class, "TileOwned"),
            new TileRegistration(TileArcanePressurePlate.class, "TileArcanePressurePlate"),
            new TileRegistration(TileBanner.class, "TileBanner"),
            new TileRegistration(TileSensor.class, "TileSensor"),
            new TileRegistration(TileLifter.class, "TileLifter"),
            new TileRegistration(TileHole.class, "TileHole"),
            new TileRegistration(TileWarded.class, "TileWarded"),
            new TileRegistration(TileGrate.class, "TileGrate"),
            new TileRegistration(TileAlembic.class, "TileSiphon"),
            new TileRegistration(TileArcaneLamp.class, "TileArcaneLamp"),
            new TileRegistration(TileArcaneLampGrowth.class, "TileArcaneLampGrowth"),
            new TileRegistration(TileArcaneLampFertility.class, "TileArcaneLampFertility"),
            new TileRegistration(TileBrainbox.class, "TileBrainbox"),
            new TileRegistration(TileThaumatorium.class, "TileThaumatorium"),
            new TileRegistration(TileThaumatoriumTop.class, "TileThaumatoriumTop"),
            new TileRegistration(TileEtherealBloom.class, "TilePurifyTotem"),
            new TileRegistration(TileNodeEnergized.class, "TileNodeEnergized"),
            new TileRegistration(TileWardingStone.class, "TileWardingStone"),
            new TileRegistration(TileWardingStoneFence.class, "TileWardingStoneFence"),
            new TileRegistration(TileNitor.class, "TileNitor"),
            new TileRegistration(TileEldritchPortal.class, "TileEldritchPortal"),
            new TileRegistration(TileEldritchNothing.class, "TileEldritchNothing"),
            new TileRegistration(TileEldritchLock.class, "TileEldritchLock"),
            new TileRegistration(TileEldritchCrabSpawner.class, "TileEldritchCrabSpawner"),
            new TileRegistration(TileEldritchAltar.class, "TileEldritchAltar"),
            new TileRegistration(TileEldritchCap.class, "TileEldritchCap"),
            new TileRegistration(TileEldritchObelisk.class, "TileEldritchObelisk"),
            new TileRegistration(TileEldritchTrap.class, "TileEldritchTrap"),
            new TileRegistration(TileChestHungry.class, "TileChestHungry")
    };

    private static final class TileRegistration {
        private final Class<? extends TileEntity> tileClass;
        private final String legacyToken;

        private TileRegistration(Class<? extends TileEntity> tileClass, String legacyToken) {
            this.tileClass = tileClass;
            this.legacyToken = legacyToken;
        }
    }
}
