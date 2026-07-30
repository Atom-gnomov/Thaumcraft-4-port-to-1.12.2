package thaumcraft.common.items.tinkerer.kami;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
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

    /**
     * Ichorcloth armour: {@code addArmorMaterial("ICHOR", 0, {3, 8, 6, 3}, 20)}
     * upstream. The durability factor of zero costs nothing because the armour
     * never takes damage — {@code damageArmor} is a no-op, as it is upstream.
     *
     * <p>The reduction array is reordered, not changed: 1.7.10 indexed it by
     * armour type (helm, chest, legs, boots) and 1.12.2 indexes it by slot
     * (feet, legs, chest, head), so the same four numbers are written back to
     * front.</p>
     */
    public static final ItemArmor.ArmorMaterial ICHORCLOTH =
            EnumHelper.addArmorMaterial("ICHORCLOTH", "thaumcraft:ichorcloth", 0,
                    new int[]{3, 6, 8, 3}, 20, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    private KamiMaterials() {
    }
}
