package thaumcraft.common.items.endgame;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import thaumcraft.common.items.armor.ItemThaumiumArmor;
import thaumcraft.common.lib.endgame.SoaringHandler;

/**
 * Test Wings — a chestplate that spawns already carrying the full Ascension
 * tier, so flight can be tested without the infusion chain. End Legacy module,
 * owner's request of 2026-08-03: «mock айтем для тестов, только в креативе».
 *
 * <p>Creative-only by construction: no recipe exists, no research references
 * it, and it is not obtainable in survival by any path. It is an ordinary
 * thaumium chestplate otherwise — armour model, texture and repairability come
 * from the parent — with the {@code Wings} byte pre-set on the creative-tab
 * stack. Everything downstream (the K-key modes, the elytra state, the vis
 * cost of climbing) treats it exactly like an armour that earned its tag at
 * the altar, which is the point of a mock.</p>
 */
public class ItemTestWings extends ItemThaumiumArmor {

    public ItemTestWings(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
    }

    /** The tab hands out the ready-flagged stack — the whole reason this item exists. */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        ItemStack winged = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte(SoaringHandler.TAG_WINGS, (byte) SoaringHandler.TIER_ASCENSION);
        winged.setTagCompound(nbt);
        items.add(winged);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    public void addInformation(ItemStack stack, net.minecraft.world.World world,
                               List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag) {
        tooltip.add(net.minecraft.util.text.TextFormatting.GRAY
                + net.minecraft.util.text.translation.I18n.translateToLocal("endlegacy.testwings.tip"));
        super.addInformation(stack, world, tooltip, flag);
    }
}
