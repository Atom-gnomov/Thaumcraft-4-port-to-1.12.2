package thaumcraft.client.lib;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockCandle;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.lib.block.BlockTC;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Thaumcraft.MODID)
public class ModelHandler {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(ConfigItems.itemTripleMeatTreat, 0, "triplemeattreat");
        for (int meta = 0; meta <= 6; ++meta) {
            registerItemModel(ConfigItems.itemShard, meta, "itemshard_" + meta);
        }
        Item customOre = Item.getItemFromBlock(ConfigBlocks.blockCustomOre);
        for (int meta = 0; meta <= 7; ++meta) {
            registerItemModel(customOre, meta, "customore_" + meta);
        }
        Item cosmeticSolid = Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid);
        for (int meta = 0; meta <= 15; ++meta) {
            registerItemModel(cosmeticSolid, meta, "cosmeticsolid_" + meta);
        }
        Item cosmeticOpaque = Item.getItemFromBlock(ConfigBlocks.blockCosmeticOpaque);
        for (int meta = 0; meta <= 2; ++meta) {
            registerItemModel(cosmeticOpaque, meta, "cosmeticopaque_" + meta);
        }
        Item magicalLog = Item.getItemFromBlock(ConfigBlocks.blockMagicalLog);
        for (int meta = 0; meta <= 1; ++meta) {
            registerItemModel(magicalLog, meta, "magicallog_" + meta);
        }
        Item magicalLeaves = Item.getItemFromBlock(ConfigBlocks.blockMagicalLeaves);
        for (int meta = 0; meta <= 1; ++meta) {
            registerItemModel(magicalLeaves, meta, "magicalleaves_" + meta);
        }
        Item customPlant = Item.getItemFromBlock(ConfigBlocks.blockCustomPlant);
        for (int meta = 0; meta <= 5; ++meta) {
            registerItemModel(customPlant, meta, "customplant_" + meta);
        }
        Item crystal = Item.getItemFromBlock(ConfigBlocks.blockCrystal);
        for (int meta = 0; meta <= 6; ++meta) {
            registerItemModel(crystal, meta, "crystal_" + meta);
        }
        Item taint = Item.getItemFromBlock(ConfigBlocks.blockTaint);
        for (int meta = 0; meta <= 2; ++meta) {
            registerItemModel(taint, meta, "taint_" + meta);
        }
        Item taintFibres = Item.getItemFromBlock(ConfigBlocks.blockTaintFibres);
        for (int meta = 0; meta <= 3; ++meta) {
            registerItemModel(taintFibres, meta, "taintfibres_" + meta);
        }

        Item candle = Item.getItemFromBlock(ConfigBlocks.blockCandle);
        for (int meta = 0; meta <= 15; ++meta) {
            registerItemModel(candle, meta, "candle");
        }
        Item lootUrn = Item.getItemFromBlock(ConfigBlocks.blockLootUrn);
        for (int meta = 0; meta <= 2; ++meta) {
            registerItemModel(lootUrn, meta, "loot_urn_" + meta);
        }
        Item lootCrate = Item.getItemFromBlock(ConfigBlocks.blockLootCrate);
        for (int meta = 0; meta <= 2; ++meta) {
            registerItemModel(lootCrate, meta, "loot_crate_" + meta);
        }
        // Eldritch: only meta 4 (Glowing Crusted Stone) is ever an item; other metas are worldgen-only.
        registerItemModel(Item.getItemFromBlock(ConfigBlocks.blockEldritch), 4, "eldritch_4");

        // B2 devices — one item model per creative meta (matches the generated block models).
        registerDeviceItemModels(ConfigBlocks.blockMetalDevice, "metal_device", new int[] { 0, 1, 2, 3, 5, 7, 8, 9, 12, 13, 14 });
        registerDeviceItemModels(ConfigBlocks.blockWoodenDevice, "wooden_device", new int[] { 0, 1, 2, 4, 5, 6, 7, 8 });
        registerDeviceItemModels(ConfigBlocks.blockStoneDevice, "stone_device", new int[] { 0, 1, 2, 5, 8, 9, 10, 11, 12, 13, 14 });
        registerDeviceItemModels(ConfigBlocks.blockTable, "table", new int[] { 0, 14, 15 });
        registerDeviceItemModels(ConfigBlocks.blockJar, "jar", new int[] { 0, 1, 3 });
        registerDeviceItemModels(ConfigBlocks.blockTube, "tube", new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
        registerDeviceItemModels(ConfigBlocks.blockMirror, "mirror", new int[] { 0, 6 });
        registerDeviceItemModels(ConfigBlocks.blockArcaneFurnace, "arcane_furnace", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockAlchemyFurnace, "alchemy_furnace", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockChestHungry, "chest_hungry", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockLifter, "lifter", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockMagicBox, "magic_box", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockEssentiaReservoir, "essentia_reservoir", new int[] { 0 });
        registerDeviceItemModels(ConfigBlocks.blockAiry, "airy", new int[] { 0 });
        // blockWarded: no item.

        // B3 Arcane Door: flat inventory icon, plus a state mapper that drops POWERED from the
        // blockstate key (exactly like vanilla doors) so the JSON only needs the 32 real variants.
        // hole / eldritch_nothing / eldritch_portal have no item, so no item model is registered.
        registerItemModel(ConfigBlocks.itemArcaneDoor, 0, "arcane_door");
        ModelLoader.setCustomStateMapper(ConfigBlocks.blockArcaneDoor,
                new StateMap.Builder().ignore(BlockDoor.POWERED).build());

        // B4 fluids: the forge:fluid model renders both the in-world block and the inventory item;
        // the state mapper collapses the fluid's LEVEL property onto that single model.
        registerFluidModel(ConfigBlocks.blockFluxGoo);
        registerFluidModel(ConfigBlocks.blockFluxGas);
        registerFluidModel(ConfigBlocks.blockFluidPure);
        registerFluidModel(ConfigBlocks.blockFluidDeath);

        registerItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsArcaneStone), 0, "stairs_arcane");
        registerItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsEldritch), 0, "stairs_eldritch");
        registerItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsGreatwood), 0, "stairs_greatwood");
        registerItemModel(Item.getItemFromBlock(ConfigBlocks.blockStairsSilverwood), 0, "stairs_silverwood");
        Item slabWood = Item.getItemFromBlock(ConfigBlocks.blockSlabWood);
        registerItemModel(slabWood, 0, "slab_wood_bottom_0");
        registerItemModel(slabWood, 1, "slab_wood_bottom_1");
        Item slabStone = Item.getItemFromBlock(ConfigBlocks.blockSlabStone);
        registerItemModel(slabStone, 0, "slab_stone_bottom_0");
        registerItemModel(slabStone, 1, "slab_stone_bottom_1");

        for (int meta = 0; meta <= 18; ++meta) {
            registerItemModel(ConfigItems.itemResource, meta, "resource_" + meta);
        }
    }

    // Silverwood leaves use a fixed silver-blue tint; greatwood uses the biome foliage color.
    private static final int SILVERWOOD_LEAF_COLOR = 0x8899AA;

    // Tainted Soil (meta 1) has a grayscale texture tinted the taint biome color (0x6D4189, from
    // BiomeGenTaint.func_76739_b(7160201)); Crusted Taint / Flesh Block keep their own texture colors.
    private static final int TAINT_SOIL_COLOR = 0x6D4189;

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        // Infused ore (meta 1..6) tinted by primal aspect, mirroring the shard tinting.
        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int meta = state.getValue(BlockTC.META);
            return (meta >= 1 && meta <= 6) ? ItemShard.COLORS[meta] : 0xFFFFFF;
        }, ConfigBlocks.blockCustomOre);

        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
            if (state.getValue(BlockTC.META) == 1) {
                return SILVERWOOD_LEAF_COLOR;
            }
            return (world != null && pos != null)
                    ? BiomeColorHelper.getFoliageColorAtPos(world, pos)
                    : ColorizerFoliage.getFoliageColorBasic();
        }, ConfigBlocks.blockMagicalLeaves);

        // Aura crystal cluster tinted by primal aspect (meta 0..5); meta 6 = mixed, left untinted.
        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int meta = state.getValue(BlockTC.META);
            return meta < 6 ? ItemShard.COLORS[meta + 1] : 0xFFFFFF;
        }, ConfigBlocks.blockCrystal);

        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) ->
                state.getValue(BlockTC.META) == 1 ? TAINT_SOIL_COLOR : 0xFFFFFF,
                ConfigBlocks.blockTaint);

        // Candle tinted by dye colour (meta 0..15).
        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) ->
                BlockCandle.COLORS[state.getValue(BlockTC.META) & 15], ConfigBlocks.blockCandle);
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            int meta = stack.getMetadata();
            return meta == 6 ? 0xFFFFFF : ItemShard.COLORS[meta + 1];
        }, ConfigItems.itemShard);

        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            int meta = stack.getMetadata();
            return (meta >= 1 && meta <= 6) ? ItemShard.COLORS[meta] : 0xFFFFFF;
        }, Item.getItemFromBlock(ConfigBlocks.blockCustomOre));

        event.getItemColors().registerItemColorHandler((stack, tintIndex) ->
                stack.getMetadata() == 1 ? SILVERWOOD_LEAF_COLOR : ColorizerFoliage.getFoliageColorBasic(),
                Item.getItemFromBlock(ConfigBlocks.blockMagicalLeaves));

        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            int meta = stack.getMetadata();
            return meta < 6 ? ItemShard.COLORS[meta + 1] : 0xFFFFFF;
        }, Item.getItemFromBlock(ConfigBlocks.blockCrystal));

        event.getItemColors().registerItemColorHandler((stack, tintIndex) ->
                stack.getMetadata() == 1 ? TAINT_SOIL_COLOR : 0xFFFFFF,
                Item.getItemFromBlock(ConfigBlocks.blockTaint));

        event.getItemColors().registerItemColorHandler((stack, tintIndex) ->
                BlockCandle.COLORS[stack.getMetadata() & 15],
                Item.getItemFromBlock(ConfigBlocks.blockCandle));
    }

    private static void registerItemModel(Item item, int meta, String path) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(Thaumcraft.MODID + ":" + path, "inventory"));
    }

    private static void registerDeviceItemModels(net.minecraft.block.Block block, String base, int[] metas) {
        Item item = Item.getItemFromBlock(block);
        for (int meta : metas) {
            registerItemModel(item, meta, base + "_" + meta);
        }
    }

    // Points a fluid block (and its ItemBlock) at the built-in forge:fluid model declared in the
    // block's blockstate JSON, and maps every LEVEL value onto that one model.
    private static void registerFluidModel(net.minecraft.block.Block block) {
        final ModelResourceLocation mrl = new ModelResourceLocation(
                Thaumcraft.MODID + ":" + block.getRegistryName().getResourcePath(), "fluid");
        Item item = Item.getItemFromBlock(block);
        if (item != null && item != net.minecraft.init.Items.AIR) {
            ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
        }
        ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return mrl;
            }
        });
    }
}
