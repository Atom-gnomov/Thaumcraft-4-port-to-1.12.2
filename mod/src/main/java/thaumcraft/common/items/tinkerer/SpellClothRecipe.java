package thaumcraft.common.items.tinkerer;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * The Spellbinding Cloth's crafting rule — ported from Thaumic Tinkerer's
 * {@code SpellClothRecipe} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>A grid holding exactly one cloth and exactly one enchanted item, and
 * nothing else, returns that item with its {@code ench} tag removed. Items
 * marked {@link INoRemoveEnchant} are not accepted.</p>
 */
public class SpellClothRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final Item cloth;

    public SpellClothRecipe(Item cloth) {
        this.cloth = cloth;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        boolean foundCloth = false;
        boolean foundEnchanted = false;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.isItemEnchanted() && !(stack.getItem() instanceof INoRemoveEnchant) && !foundEnchanted) {
                foundEnchanted = true;
            } else if (stack.getItem() == this.cloth && !foundCloth) {
                foundCloth = true;
            } else {
                // Anything else in the grid breaks the recipe, as upstream.
                return false;
            }
        }
        return foundCloth && foundEnchanted;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.isItemEnchanted()) {
                ItemStack result = stack.copy();
                NBTTagCompound tag = result.getTagCompound();
                if (tag != null) {
                    tag = tag.copy();
                    tag.removeTag("ench");
                    result.setTagCompound(tag);
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == this.cloth) {
                // The cloth wears by one and stays in the grid.
                ItemStack worn = stack.copy();
                worn.setItemDamage(worn.getItemDamage() + 1);
                remaining.set(i, worn.getItemDamage() > worn.getMaxDamage() ? ItemStack.EMPTY : worn);
            }
        }
        return remaining;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
