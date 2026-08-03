package thaumcraft.common.lib.endgame;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

/**
 * Infuses wings into <b>any</b> chestplate — End Legacy module (owner's
 * revision of 2026-08-03: the wings are an NBT mark on the armour, not an
 * enchantment, and the armour wears it in its name: «Парящий алмазный
 * нагрудник», «Таум нагрудник Вознесения»).
 *
 * <p>The output rides the altar's native NBT-merge channel
 * ({@code Object[]{label, NBTBase}} → {@code stack.setTagInfo}), so the
 * chestplate keeps everything it already is — enchantments, durability, its
 * own NBT — and gains the {@code Wings} byte. Tier 1 takes any bare-winged
 * chest armour; tier 2 takes a chestplate already at tier 1: the ascension is
 * an upgrade, not a fresh forging.</p>
 *
 * <p>The display input shown on the research page is a thaumium chestplate;
 * {@link #matches} is what actually decides, and it accepts any
 * {@link ItemArmor} worn on the chest.</p>
 */
public class InfusionWingsRecipe extends InfusionRecipe {

    private final int tier;

    public InfusionWingsRecipe(String research, int tier, int instability, AspectList aspects,
                               ItemStack displayInput, ItemStack[] components) {
        super(research, new Object[]{SoaringHandler.TAG_WINGS, new NBTTagByte((byte) tier)},
                instability, aspects, displayInput, components);
        this.tier = tier;
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        if (this.getResearch().length() > 0
                && !ThaumcraftApiHelper.isResearchComplete(player.getName(), this.getResearch())) {
            return false;
        }
        if (central.isEmpty() || !(central.getItem() instanceof ItemArmor)
                || ((ItemArmor) central.getItem()).armorType != EntityEquipmentSlot.CHEST) {
            return false;
        }
        // Tier 1 wants wingless armour; tier 2 wants exactly tier 1 under it.
        if (SoaringHandler.getTier(central) != this.tier - 1) {
            return false;
        }
        return componentsMatch(input);
    }

    /** The component check, as the parent runs it — order-free, one-for-one. */
    private boolean componentsMatch(ArrayList<ItemStack> input) {
        ArrayList<ItemStack> pool = new ArrayList<>();
        for (ItemStack is : input) {
            pool.add(is.copy());
        }
        for (ItemStack comp : this.getComponents()) {
            boolean found = false;
            for (int a = 0; a < pool.size(); ++a) {
                ItemStack candidate = pool.get(a).copy();
                if (comp.getMetadata() == Short.MAX_VALUE) {
                    candidate.setItemDamage(Short.MAX_VALUE);
                }
                if (!InfusionRecipe.areItemStacksEqual(candidate, comp, true)) {
                    continue;
                }
                pool.remove(a);
                found = true;
                break;
            }
            if (!found) {
                return false;
            }
        }
        return pool.isEmpty();
    }
}
