package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsModernVanillaTagCoverageTest {

    /**
     * Vanilla items added in 1.8–1.12.2 that cannot inherit aspects from a crafting
     * recipe (raw drops, loot-only and furnace-only items — tag generation does not
     * walk furnace recipes) must keep explicit object tags, otherwise
     * ScanManager.validScan rejects them and the thaumometer cannot scan them.
     */
    @Test
    public void configAspectsKeepsExplicitTagsForUncraftableModernVanilla() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("Both sponge metas are loot/furnace-only since 1.8 and need explicit tags",
                source.contains("new ItemStack(Blocks.SPONGE, 1, 0)")
                        && source.contains("new ItemStack(Blocks.SPONGE, 1, 1)"));
        assertTrue("Guardian drops need explicit tags",
                source.contains("new ItemStack(Items.PRISMARINE_SHARD)")
                        && source.contains("new ItemStack(Items.PRISMARINE_CRYSTALS)"));
        assertTrue("Raw mob drops added in 1.8 need explicit tags",
                source.contains("new ItemStack(Items.RABBIT)")
                        && source.contains("new ItemStack(Items.RABBIT_FOOT)")
                        && source.contains("new ItemStack(Items.RABBIT_HIDE)")
                        && source.contains("new ItemStack(Items.MUTTON)"));
        assertTrue("Furnace-only outputs need explicit tags (no furnace recipe derivation)",
                source.contains("new ItemStack(Items.COOKED_RABBIT)")
                        && source.contains("new ItemStack(Items.COOKED_MUTTON)")
                        && source.contains("new ItemStack(Items.CHORUS_FRUIT_POPPED)"));
        assertTrue("End/loot exclusives need explicit tags",
                source.contains("new ItemStack(Items.DRAGON_BREATH)")
                        && source.contains("new ItemStack(Items.SHULKER_SHELL)")
                        && source.contains("new ItemStack(Items.TOTEM_OF_UNDYING)")
                        && source.contains("new ItemStack(Items.ELYTRA, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Items.CHORUS_FRUIT)"));
        assertTrue("Dragon head (SKULL meta 5, added 1.9) needs an explicit tag",
                source.contains("new ItemStack(Items.SKULL, 1, 5)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
