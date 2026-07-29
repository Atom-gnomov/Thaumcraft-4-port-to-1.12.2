package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Imbued Grain — ported from Thaumic Tinkerer's {@code ItemInfusedGrain}
 * (pixlepix / nekosune, originally Vazkii). What an infused crop yields, one
 * kind per primal, and what the matching potion is brewed from.
 */
public class ItemInfusedGrain extends Item {

    public ItemInfusedGrain() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    public PrimalCrop getCrop(ItemStack stack) {
        return PrimalCrop.byMeta(stack.getItemDamage());
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (PrimalCrop crop : PrimalCrop.values()) {
            items.add(new ItemStack(this, 1, crop.ordinal()));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(getCrop(stack).getAspect().getName());
    }
}
