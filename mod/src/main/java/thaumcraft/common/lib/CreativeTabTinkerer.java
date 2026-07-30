package thaumcraft.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;

/**
 * The creative tab holding everything Thaumic Tinkerer adds.
 *
 * <p>A deliberate departure from the original, on the owner's call. Upstream
 * puts its content in Thaumcraft's own tab and hides the KAMI tier behind the
 * {@code enableKami} config; here all of it — KAMI included — is visible in
 * creative under its own tab, and what gates progression is the research tree
 * alone. In survival nothing changes: KAMI entries are still hidden behind the
 * whole Thaumonomicon by {@code TinkererKamiResearchItem}.</p>
 */
public final class CreativeTabTinkerer extends CreativeTabs {

    public static final CreativeTabTinkerer tabTinkerer = new CreativeTabTinkerer();

    public CreativeTabTinkerer() {
        super("thaumictinkerer");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        if (ConfigItems.itemDarkQuartz != null) {
            return new ItemStack(ConfigItems.itemDarkQuartz);
        }
        return net.minecraft.init.Items.AIR.getDefaultInstance();
    }
}
