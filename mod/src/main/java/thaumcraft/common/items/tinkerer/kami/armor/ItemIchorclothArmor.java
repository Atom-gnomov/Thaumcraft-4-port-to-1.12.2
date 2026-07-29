package thaumcraft.common.items.tinkerer.kami.armor;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Ichorcloth armour — ported from Thaumic Tinkerer's
 * {@code ItemIchorclothArmor} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>The KAMI tier's cloth: it never wears out, absorbs damage in proportion to
 * its own reduction rating, and discounts vis by four percent a piece — three
 * for the boots.</p>
 */
public class ItemIchorclothArmor extends ItemArmor implements IVisDiscountGear, ISpecialArmor {

    public ItemIchorclothArmor(EntityEquipmentSlot slot) {
        super(KamiMaterials.ICHORCLOTH, 0, slot);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    /** Legs take the second sheet, everything else the first — as upstream. */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return slot == EntityEquipmentSlot.LEGS
                ? "thaumcraft:textures/models/ichor2.png"
                : "thaumcraft:textures/models/ichor1.png";
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return this.armorType == EntityEquipmentSlot.FEET ? 3 : 4;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tc.visdiscount")
                + ": " + getVisDiscount(stack, null, null) + "%");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    // Upstream also overrode isItemTool() to true. That method is gone in
    // 1.12.2; what it governed — being enchantable — ItemArmor already answers
    // from the material, whose enchantability is 20 here.

    /** The original's ratio: reduction × 0.0425, at priority 0, without limit. */
    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor,
                                                       DamageSource source, double damage, int slot) {
        return new ISpecialArmor.ArmorProperties(0, this.damageReduceAmount * 0.0425D, Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return this.damageReduceAmount;
    }

    /** Deliberately empty: KAMI armour does not wear, exactly as upstream. */
    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
    }
}
