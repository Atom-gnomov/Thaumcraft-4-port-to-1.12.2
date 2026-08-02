package thaumcraft.common.items.endgame;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.Thaumcraft;

/**
 * Ward of Deflection — a charm-slot bauble that turns projectiles aside.
 *
 * <p>250 durability, one point per arrow it refuses; mendable with the Repair
 * infusion enchantment like any other damageable trinket. The actual
 * deflection lives in {@code WardHandler} — the item itself only exists and
 * wears down. End Legacy module, phase 2 ({@code END_LEGACY_PLAN.md}).</p>
 */
public class ItemWardDeflection extends Item implements IBauble {

    public static final int DURABILITY = 250;

    public ItemWardDeflection() {
        this.setMaxStackSize(1);
        this.setMaxDamage(DURABILITY);
        this.setNoRepair();
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CHARM;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
        return true;
    }
}
