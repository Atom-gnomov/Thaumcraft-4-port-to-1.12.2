package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Pins the public addon-facing API surface so third-party mods that compile
 * against Thaumcraft-<version>-api.jar do not break silently across releases.
 * See API.md. If a signature here must change, it is a deliberate breaking
 * change: bump the @API apiVersion and note it in the changelog.
 */
public class ThaumcraftApiSurfaceStaticGuardTest {

    @Test
    public void forgeApiTokenIsDeclared() throws IOException {
        String pkg = read("src/main/java/thaumcraft/api/package-info.java");
        assertTrue("thaumcraft.api must carry the Forge @API token addons look up",
                pkg.contains("@API(")
                        && pkg.contains("owner=\"Thaumcraft\"")
                        && pkg.contains("provides=\"Thaumcraft|API\""));
    }

    @Test
    public void thaumcraftApiKeepsRegistrationContract() throws IOException {
        String s = read("src/main/java/thaumcraft/api/ThaumcraftApi.java");

        // object / aspect tags
        assertTrue(s.contains("public static void registerObjectTag(ItemStack item, AspectList aspects)"));
        assertTrue(s.contains("public static void registerObjectTag(ItemStack item, int[] meta, AspectList aspects)"));
        assertTrue(s.contains("public static void registerObjectTag(String oreDict, AspectList aspects)"));
        assertTrue(s.contains("public static void registerComplexObjectTag(ItemStack item, AspectList aspects)"));
        assertTrue(s.contains("public static void registerComplexObjectTag(String oreDict, AspectList aspects)"));

        // recipes
        assertTrue(s.contains("public static ShapedArcaneRecipe addArcaneCraftingRecipe(String research, ItemStack result, AspectList aspects, Object ... recipe)"));
        assertTrue(s.contains("public static ShapelessArcaneRecipe addShapelessArcaneCraftingRecipe(String research, ItemStack result, AspectList aspects, Object ... recipe)"));
        assertTrue(s.contains("public static InfusionRecipe addInfusionCraftingRecipe(String research, Object result, int instability, AspectList aspects, ItemStack input, ItemStack[] recipe)"));
        assertTrue(s.contains("public static InfusionEnchantmentRecipe addInfusionEnchantmentRecipe(String research, Enchantment enchantment, int instability, AspectList aspects, ItemStack[] recipe)"));
        assertTrue(s.contains("public static CrucibleRecipe addCrucibleRecipe(String key, ItemStack result, Object catalyst, AspectList tags)"));
        assertTrue(s.contains("public static CrucibleRecipe getCrucibleRecipe(ItemStack stack)"));
        assertTrue(s.contains("public static InfusionRecipe getInfusionRecipe(ItemStack res)"));

        // research / warp / loot / seeds / scans
        assertTrue(s.contains("public static void registerResearchLocation(ResourceLocation location)"));
        assertTrue(s.contains("public static void addWarpToItem(ItemStack craftresult, int amount)"));
        assertTrue(s.contains("public static void addWarpToResearch(String research, int amount)"));
        assertTrue(s.contains("public static void addLootBagItem(ItemStack item, int weight, int ... bagTypes)"));
        assertTrue(s.contains("public static void registerSeed(Block crop, ItemStack seed)"));
        assertTrue(s.contains("public static void registerScanEventhandler(IScanEventHandler scanEventHandler)"));
        assertTrue(s.contains("public static void registerEntityTag(String entityName, AspectList aspects, EntityTagsNBT ... nbt)"));

        // internal-handler seam must stay public for the mod to swap in the real impl
        assertTrue(s.contains("public static IInternalMethodHandler internalMethods;"));
    }

    @Test
    public void auraAndEssentiaHelpersKeepContract() throws IOException {
        String aura = read("src/main/java/thaumcraft/api/aura/AuraHelper.java");
        assertTrue(aura.contains("public static float getVis(World world, BlockPos pos)"));
        assertTrue(aura.contains("public static float drainVis(World world, BlockPos pos, float amount, boolean simulate)"));
        assertTrue(aura.contains("public static void addVis(World world, BlockPos pos, float amount)"));
        assertTrue(aura.contains("public static float getFlux(World world, BlockPos pos)"));
        assertTrue(aura.contains("public static void addFlux(World world, BlockPos pos, float amount)"));

        String src = read("src/main/java/thaumcraft/api/aspects/AspectSourceHelper.java");
        assertTrue(src.contains("public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range)"));
        assertTrue(src.contains("public static boolean findEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range)"));
    }

    @Test
    public void aspectConstructorsAndProxyStayOpen() throws IOException {
        String aspect = read("src/main/java/thaumcraft/api/aspects/Aspect.java");
        assertTrue(aspect.contains("public Aspect(String tag, int color, Aspect[] components)"));

        String proxy = read("src/main/java/thaumcraft/api/aspects/AspectEventProxy.java");
        assertTrue(proxy.contains("public void registerObjectTag(ItemStack item, AspectList aspects)"));
        assertTrue(proxy.contains("public void registerComplexObjectTag(ItemStack item, AspectList aspects)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
