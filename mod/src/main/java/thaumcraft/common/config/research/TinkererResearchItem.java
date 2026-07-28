package thaumcraft.common.config.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchPage.PageType;

/**
 * A Thaumonomicon entry in Thaumic Tinkerer's own tab — the port of the
 * original's {@code TTResearchItem} (pixlepix / nekosune / Vazkii).
 *
 * <p>It exists for the same reason the original's did: the module keeps its
 * strings under its own keys, so a name is {@code ttresearch.name.KEY}, the
 * hover lore is {@code [TT] } plus {@code ttresearch.lore.KEY}, and a text page
 * numbered "0" becomes {@code ttresearch.page.KEY.0}. Those strings are copied
 * verbatim out of the original's own en_US and ru_RU files rather than
 * translated here.</p>
 *
 * <p>The second half of {@code setPages} is easy to miss and load bearing: an
 * entry that shows an infusion recipe gains {@code INFUSION} as a hidden
 * parent, so the module's infused items stay behind Thaumcraft's own infusion
 * research instead of offering themselves before the player can build an
 * altar.</p>
 */
public class TinkererResearchItem extends ResearchItem {

    public TinkererResearchItem(String key, AspectList tags, int col, int row, int complexity, ItemStack icon) {
        super(key, ConfigResearchTinkerer.CATEGORY, tags, col, row, complexity, icon);
    }

    @Override
    public String getName() {
        return I18n.translateToLocal("ttresearch.name." + this.key);
    }

    /**
     * The original gates the prefix behind {@code useTootlipIndicators}, which
     * defaults to on; the port carries no such option, so it is always shown.
     */
    @Override
    public String getText() {
        return I18n.translateToLocal(getPrefix()) + I18n.translateToLocal("ttresearch.lore." + this.key);
    }

    String getPrefix() {
        return "ttresearch.prefix";
    }

    @Override
    public ResearchItem setPages(ResearchPage... pages) {
        for (ResearchPage page : pages) {
            if (page.type == PageType.TEXT) {
                page.text = "ttresearch.page." + this.key + "." + page.text;
            }

            if (checkInfusion() && page.type == PageType.INFUSION_CRAFTING) {
                if (this.parentsHidden == null || this.parentsHidden.length == 0) {
                    this.parentsHidden = new String[]{"INFUSION"};
                } else {
                    String[] newParents = new String[this.parentsHidden.length + 1];
                    newParents[0] = "INFUSION";
                    for (int i = 0; i < this.parentsHidden.length; i++) {
                        newParents[i + 1] = this.parentsHidden[i];
                    }
                    this.parentsHidden = newParents;
                }
            }
        }

        return super.setPages(pages);
    }

    boolean checkInfusion() {
        return true;
    }
}
