package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * A Thaumonomicon entry in Thaumic Tinkerer's own tab — the port of the
 * original's {@code TTResearchItem} (pixlepix / nekosune / Vazkii).
 *
 * <p>It exists for the same reason the original's did: the module keeps its
 * strings under its own keys, so a name is {@code ttresearch.name.KEY}, the
 * hover lore is {@code ttresearch.lore.KEY}, and a text page numbered "0"
 * becomes {@code ttresearch.page.KEY.0}. Those keys are copied verbatim out of
 * the original's own en_US and ru_RU files rather than translated here.</p>
 */
public class TinkererResearchItem extends ResearchItem {

    public TinkererResearchItem(String key, AspectList tags, int col, int row, int complexity, ItemStack icon) {
        super(key, ConfigResearchTinkerer.CATEGORY, tags, col, row, complexity, icon);
    }

    @Override
    public String getName() {
        return "ttresearch.name." + this.key;
    }

    @Override
    public String getText() {
        return "ttresearch.lore." + this.key;
    }

    /** Text pages carry the module's own prefix, exactly as upstream did. */
    @Override
    public ResearchItem setPages(ResearchPage... pages) {
        for (ResearchPage page : pages) {
            if (page != null && page.text != null && page.recipe == null) {
                page.text = "ttresearch.page." + this.key + "." + page.text;
            }
        }
        return super.setPages(pages);
    }
}
