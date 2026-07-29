package thaumcraft.common.items.tinkerer;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Helmet of Revealing — ported from Thaumic Tinkerer's
 * {@code ItemRevealingHelm} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A thaumium helmet with the goggles built in: it shows nodes and in-game
 * popups and carries the goggles' five percent vis discount, while protecting
 * as thaumium does. Repaired with thaumium ingots.</p>
 */
public class ItemRevealingHelm extends ItemArmor implements IRepairable, IRevealer, IGoggles, IVisDiscountGear {

    public ItemRevealingHelm() {
        // The original's super(armorMatThaumium, 2, 0) — render index 2, head slot.
        super(ThaumcraftApi.armorMatThaumium, 2, EntityEquipmentSlot.HEAD);
        this.setMaxDamage(500);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public boolean showNodes(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return 5;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/revealing_helm.png";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    /** Thaumium ingots mend it, as they mend the helmet it is built from. */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 2))
                || super.getIsRepairable(toRepair, repair);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tc.visdiscount")
                + ": " + getVisDiscount(stack, null, null) + "%");
        super.addInformation(stack, world, tooltip, flag);
    }
}
