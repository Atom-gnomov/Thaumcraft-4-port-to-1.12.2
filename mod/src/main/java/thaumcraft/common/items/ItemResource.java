package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;

/**
 * Multi-subtype resource item (amber, quicksilver, nitor, thaumium ingot, charm, ...).
 * Ported as a stub: subtypes + textures only. TODO: port behavior — alumentum throw,
 * nitor block placement, knowledge fragment research, charm/primal effects, taint poison,
 * label essentia overlay, IEssentiaContainerItem NBT aspects.
 */
public class ItemResource extends Item {
    public ItemResource() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (int a = 0; a <= 18; ++a) {
            if (a == 5) {
                continue; // brain: obtained from zombies, not shown in creative
            }
            items.add(new ItemStack(this, 1, a));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }
}
