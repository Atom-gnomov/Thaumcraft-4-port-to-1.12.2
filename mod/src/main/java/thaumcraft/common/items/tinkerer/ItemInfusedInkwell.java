package thaumcraft.common.items.tinkerer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IScribeTools;

/**
 * Infused Scribing Tools — ported from Thaumic Tinkerer's
 * {@code ItemInfusedInkwell} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Ordinary scribing tools with more than twice the ink: 800 uses against
 * the usual 350. Rewritten with a ring of ink sacs when it runs low.</p>
 *
 * <p>Upstream's own name for it ends in "(NYI)" and it is kept out of the
 * creative tab there; both are carried over rather than tidied, because the
 * item exists and works — it simply never got whatever else was planned.</p>
 */
public class ItemInfusedInkwell extends Item implements IScribeTools {

    /** The original's ink supply. */
    public static final int USES = 800;

    public ItemInfusedInkwell() {
        this.setMaxDamage(USES);
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        // No creative tab: upstream's shouldDisplayInTab is false.
    }

    @Override
    public boolean isRepairable() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
