package thaumcraft.client.gui;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.research.ScanManager;

/**
 * Background thread that walks every registered item once (including every creative-tab
 * subtype/meta variant it has - wool colors, potions, etc.) and populates
 * {@link GuiResearchRecipe#cache}: a reverse lookup from {@link ScanManager#generateItemHash}
 * back to a representative {@link ItemStack}.
 * <p>
 * The per-player scanned-items list ({@code IPlayerKnowledge#getScannedItems()}) only stores
 * the one-way hash string, so without this cache the Thaumonomicon "known items for this
 * aspect" hover feature (see {@code GuiResearchRecipe#buildAspectsPages}) has no way to turn a
 * stored hash back into a renderable {@link ItemStack}.
 * <p>
 * Mirrors the original 1.7.10 {@code MappingThread}, adapted from unified numeric item/block
 * IDs (1.7.10's shared {@code GameData} registry) to 1.12.2's item-only {@link Item#REGISTRY}.
 */
public class MappingThread implements Runnable {

    @Override
    public void run() {
        NonNullList<ItemStack> subItems = NonNullList.create();
        for (Item item : Item.REGISTRY) {
            if (item == null) {
                continue;
            }
            try {
                subItems.clear();
                item.getSubItems(CreativeTabs.SEARCH, subItems);
                if (subItems.isEmpty()) {
                    // Items with no creative tab never match CreativeTabs.SEARCH; fall back to
                    // a plain meta-0 stack so they're still reverse-lookupable.
                    subItems.add(new ItemStack(item, 1, 0));
                }
                for (ItemStack stack : subItems) {
                    if (stack == null || stack.isEmpty()) {
                        continue;
                    }
                    int hash = ScanManager.generateItemHash(stack.getItem(), stack.getMetadata());
                    GuiResearchRecipe.putToCache(hash, stack.copy());
                }
            } catch (Exception ignored) {
                // Some third-party items throw from getSubItems in unusual states; skip them.
            }
        }
    }
}
