package thaumcraft.common.items.tinkerer.kami;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

/**
 * Material definitions for the KAMI tier — ported from Thaumic Tinkerer's
 * {@code TTCommonProxy} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The ichor tool material keeps the original's numbers exactly:
 * {@code addToolMaterial("ICHOR", 4, -1, 10F, 5F, 25)}. The {@code -1} use
 * count is deliberate — it makes the tools unbreakable, which is the point of
 * the tier.</p>
 */
public final class KamiMaterials {

    public static final Item.ToolMaterial ICHOR =
            EnumHelper.addToolMaterial("ICHOR", 4, -1, 10.0F, 5.0F, 25);

    private KamiMaterials() {
    }
}
