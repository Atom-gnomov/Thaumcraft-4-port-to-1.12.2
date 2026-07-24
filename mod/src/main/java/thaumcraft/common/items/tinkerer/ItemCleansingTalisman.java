package thaumcraft.common.items.tinkerer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Cleansing Talisman — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. While carried, periodically strips one harmful potion effect from
 * the holder.
 */
public class ItemCleansingTalisman extends Item {

    private static final int INTERVAL = 60;

    public ItemCleansingTalisman() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isRemote || !(entity instanceof EntityPlayer) || world.getTotalWorldTime() % INTERVAL != 0) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        List<Potion> bad = new ArrayList<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            Potion potion = effect.getPotion();
            if (potion.isBadEffect()) {
                bad.add(potion);
            }
        }
        if (!bad.isEmpty()) {
            player.removePotionEffect(bad.get(0));
        }
    }
}
