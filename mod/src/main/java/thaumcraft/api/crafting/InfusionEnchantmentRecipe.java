/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.oredict.OreDictionary
 */
package thaumcraft.api.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

public class InfusionEnchantmentRecipe {
    public AspectList aspects;
    public String research;
    public ItemStack[] components;
    public Enchantment enchantment;
    public int recipeXP;
    public int instability;

    public InfusionEnchantmentRecipe(String research, Enchantment input, int inst, AspectList aspects2, ItemStack[] recipe) {
        this.research = research;
        this.enchantment = input;
        this.aspects = aspects2;
        this.components = recipe;
        this.instability = inst;
        this.recipeXP = Math.max(1, input.getMinEnchantability(1) / 3);
    }

    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), this.research)) {
            return false;
        }
        if (!this.enchantment.canApply(central) || !central.isItemStackDamageable()) {
            return false;
        }
        Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(central);
        for (Enchantment ench : map1.keySet()) {
            if (ench == this.enchantment && EnchantmentHelper.getEnchantmentLevel(ench, central) >= ench.getMaxLevel()) {
                return false;
            }
            if (this.enchantment == ench || this.enchantment.isCompatibleWith(ench)) continue;
            return false;
        }
        ItemStack i2 = null;
        ArrayList<ItemStack> ii = new ArrayList<ItemStack>();
        for (ItemStack is : input) {
            ii.add(is.copy());
        }
        for (ItemStack comp : this.components) {
            boolean b = false;
            for (int a = 0; a < ii.size(); ++a) {
                i2 = ((ItemStack)ii.get(a)).copy();
                if (comp.getMetadata() == Short.MAX_VALUE) {
                    i2.setItemDamage(Short.MAX_VALUE);
                }
                if (!this.areItemStacksEqual(i2, comp, true)) continue;
                ii.remove(a);
                b = true;
                break;
            }
            if (b) continue;
            return false;
        }
        return ii.size() == 0;
    }

    protected boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, boolean fuzzy) {
        ItemStack[] ores;
        Integer od;
        if (stack0 == null && stack1 != null) {
            return false;
        }
        if (stack0 != null && stack1 == null) {
            return false;
        }
        if (stack0 == null && stack1 == null) {
            return true;
        }
        boolean t1 = ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(stack0, stack1);
        if (!t1) {
            return false;
        }
        if (fuzzy) {
            for (int oid : OreDictionary.getOreIDs(stack0)) {
                if (ThaumcraftApiHelper.containsMatch(false, new ItemStack[]{stack1}, OreDictionary.getOres(OreDictionary.getOreName(oid)).toArray(new ItemStack[0]))) {
                    return true;
                }
            }
        }
        return stack0.getItem() != stack1.getItem() ? false : (stack0.getMetadata() != stack1.getMetadata() ? false : stack0.getCount() <= stack0.getMaxStackSize());
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public AspectList getAspects() {
        return this.aspects;
    }

    public String getResearch() {
        return this.research;
    }

    public int calcInstability(ItemStack recipeInput) {
        int i = 0;
        Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(recipeInput);
        for (Enchantment ench : map1.keySet()) {
            i += EnchantmentHelper.getEnchantmentLevel(ench, recipeInput);
        }
        return i / 2 + this.instability;
    }

    public int calcXP(ItemStack recipeInput) {
        return this.recipeXP * (1 + EnchantmentHelper.getEnchantmentLevel(this.enchantment, recipeInput));
    }

    public float getEssentiaMod(ItemStack recipeInput) {
        float mod = EnchantmentHelper.getEnchantmentLevel(this.enchantment, recipeInput);
        Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(recipeInput);
        for (Enchantment ench : map1.keySet()) {
            if (ench == this.enchantment) continue;
            mod += (float)EnchantmentHelper.getEnchantmentLevel(ench, recipeInput) * 0.1f;
        }
        return mod;
    }
}

