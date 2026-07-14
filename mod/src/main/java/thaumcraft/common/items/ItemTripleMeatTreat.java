package thaumcraft.common.items;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import thaumcraft.common.Thaumcraft;

public class ItemTripleMeatTreat extends ItemFood {
    public ItemTripleMeatTreat() {
        super(6, 0.8f, true);
        this.setAlwaysEdible();
        this.setPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0), 0.66f);
        this.setCreativeTab(Thaumcraft.tabTC);
    }
}
