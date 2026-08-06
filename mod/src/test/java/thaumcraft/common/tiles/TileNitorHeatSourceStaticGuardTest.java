package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Нитор как вечный источник тепла IC2 (интеграция Unbound Technology):
 * 20 HU/t с любой стороны, интерфейс строго за @Optional (без IC2 в рантайме
 * класс обязан загружаться как раньше). Канон величины —
 * docs/integration/ic2_v3_audit.md П-4 (v5 §2: «без изменений»).
 */
public class TileNitorHeatSourceStaticGuardTest {

    @Test
    public void tileNitorShouldExposeOptionalIc2HeatSourceAt20HuPerTick() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileNitor.java");

        // Интерфейс подключается только при наличии IC2.
        assertTrue(source.contains(
                "@Optional.Interface(iface = \"ic2.api.energy.tile.IHeatSource\", modid = \"ic2\")"));
        assertTrue(source.contains(
                "implements ITickable, ic2.api.energy.tile.IHeatSource"));

        // Канон: 20 HU/t, любая сторона; отдаём не больше запрошенного.
        assertTrue(source.contains("public static final int HEAT_PER_TICK = 20;"));
        assertTrue(source.contains("@Optional.Method(modid = \"ic2\")"));
        assertTrue(source.contains("public int maxrequestHeatTick(EnumFacing side) {"));
        assertTrue(source.contains("return HEAT_PER_TICK;"));
        assertTrue(source.contains("public int requestHeat(EnumFacing side, int request) {"));
        assertTrue(source.contains("return Math.min(request, HEAT_PER_TICK);"));

        // Родное поведение нитора (клиентские частицы) не тронуто.
        assertTrue(source.contains("Thaumcraft.proxy.wispFX3("));
        assertTrue(source.contains("public boolean shouldRenderInPass(int pass) {"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
