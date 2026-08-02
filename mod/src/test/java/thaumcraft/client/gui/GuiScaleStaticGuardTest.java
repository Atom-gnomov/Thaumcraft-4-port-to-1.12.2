package thaumcraft.client.gui;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Thaumcraft's container screens draw a tenth smaller than Minecraft would.
 *
 * <p><b>Owner's decision, 2026-07-30</b> — not upstream behaviour and not a
 * defect being fixed. This guard exists so it is not quietly undone by someone
 * restoring "parity", and so the one mechanism stays the only mechanism.</p>
 */
public class GuiScaleStaticGuardTest {

    private static final Path GUI = Paths.get("src/main/java/thaumcraft/client/gui");

    @Test
    public void everyContainerScreenGoesThroughTheScaledBase() throws IOException {
        List<String> unscaled = new ArrayList<>();
        for (Path file : guiSources()) {
            // The base is the one class that must extend vanilla GuiContainer;
            // it is where the scale is applied.
            if (file.getFileName().toString().equals("GuiContainerScaled.java")) {
                continue;
            }
            if (read(file).matches("(?s).*extends GuiContainer\\s*[<{].*")) {
                unscaled.add(file.getFileName().toString());
            }
        }
        assertTrue("these extend vanilla GuiContainer directly and so escape the ten percent"
                + " reduction; they belong on GuiContainerScaled: " + unscaled, unscaled.isEmpty());
    }

    /**
     * The trap this design exists to avoid. Most of these screens draw their own
     * tooltips and gauges after the panel; a subclass that overrode
     * {@code drawScreen} would run that body outside the transform and against
     * unconverted coordinates, so the panel would shrink and its tooltips would
     * not. {@code drawScreen} is final for that reason.
     */
    @Test
    public void subclassesCannotOverrideDrawScreen() throws IOException {
        String base = read(GUI.resolve("GuiContainerScaled.java"));
        assertTrue("drawScreen must be final so no subclass can step outside the scale",
                base.contains("public final void drawScreen("));
        assertTrue("and there must be a hook for them to use instead",
                base.contains("protected void drawScaledScreen("));

        List<String> offenders = new ArrayList<>();
        for (Path file : guiSources()) {
            String source = read(file);
            if (source.contains("extends GuiContainerScaled") && source.contains("void drawScreen(")) {
                offenders.add(file.getFileName().toString());
            }
        }
        assertTrue("these override drawScreen instead of drawScaledScreen: " + offenders,
                offenders.isEmpty());
    }

    /**
     * The screen is enlarged and then drawn small, rather than drawn small and
     * every mouse coordinate converted by hand. That is what keeps subclasses
     * from having to know anything: Minecraft derives the pointer from
     * {@code width} and {@code height}, so it lands in the enlarged space by
     * itself.
     */
    @Test
    public void theMechanismIsTheEnlargedScreenNotHandConvertedInput() throws IOException {
        String scale = read(GUI.resolve("GuiScale.java"));
        // 2026-08-02: the owner chose a config knob over the hard-wired 0.9
        // that shipped in 1.1.39.1 and was then suspended. The factor now comes
        // from Config.guiScale, default 1.0 — off until the player opts in.
        assertTrue("the factor comes from the mod's config, not a constant",
                scale.contains("return Config.guiScale;"));
        String config = read(java.nio.file.Paths.get(
                "src/main/java/thaumcraft/common/config/Config.java"));
        assertTrue("the knob defaults to the identity transform",
                config.contains("\"gui_scale\", 1.0D"));
        assertTrue("and is clamped so a typo cannot make screens unusable",
                config.contains("if (guiScale < 0.5F) guiScale = 0.5F;")
                        && config.contains("if (guiScale > 1.0F) guiScale = 1.0F;"));
        assertTrue("and it is recorded as the owner's call, not a fix",
                scale.contains("Owner's decision"));

        String base = read(GUI.resolve("GuiContainerScaled.java"));
        assertTrue("initGui must claim the enlarged screen",
                base.contains("this.width = GuiScale.enlarge(this.width)")
                        && base.contains("this.height = GuiScale.enlarge(this.height)"));
        assertTrue("and re-centre the panel against it",
                base.contains("this.guiLeft = (this.width - this.xSize) / 2"));
    }

    private static List<Path> guiSources() throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> tree = Files.walk(GUI)) {
            tree.filter(p -> p.toString().endsWith(".java")).forEach(files::add);
        }
        return files;
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
