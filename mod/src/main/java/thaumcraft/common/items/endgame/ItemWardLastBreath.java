package thaumcraft.common.items.endgame;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.Thaumcraft;

/**
 * Ward of the Last Breath — the totem of undying, answered from the charm
 * slot: the dying hand rarely gets to choose what it holds, so this one hangs
 * from the neck and refuses death on its owner's behalf.
 *
 * <p>One refusal, then it shatters into {@link ItemWardLastBreathCracked} and
 * leaves two points of temporary warp behind — cheating death is not free.
 * The refusal itself lives in {@code WardHandler}. End Legacy module, phase 2
 * ({@code END_LEGACY_PLAN.md}).</p>
 */
public class ItemWardLastBreath extends Item implements IBauble {

    public ItemWardLastBreath() {
        this.setMaxStackSize(1);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.CHARM;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
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
