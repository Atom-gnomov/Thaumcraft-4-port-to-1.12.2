package thaumcraft.common.items.tinkerer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Cat's Amulet — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. While carried, negates fall damage (a cat always lands on its feet).
 */
public class ItemCatAmulet extends Item {

    public ItemCatAmulet() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof EntityPlayer)) return;
        // Zeroing fallDistance each tick prevents the fall-damage calculation from
        // ever accumulating a lethal drop while the amulet is carried.
        if (entity.fallDistance > 0.0F && entity.motionY < 0.0D) {
            entity.fallDistance = 0.0F;
        }
    }
}
