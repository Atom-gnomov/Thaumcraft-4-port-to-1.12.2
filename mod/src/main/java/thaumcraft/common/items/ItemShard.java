package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.Thaumcraft;

public class ItemShard extends Item {
    // Primal shard tint colors, indexed by meta+1; meta 6 (balanced) is untinted. Was BlockCustomOreItem.colors in TC4.
    public static final int[] COLORS = { 0xFFFFFF, 0xFFFF7E, 16727041, 37119, 40960, 0xEECCFF, 0x555577 };

    public ItemShard() {
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
        for (int a = 0; a <= 6; ++a) {
            items.add(new ItemStack(this, 1, a));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }
}
