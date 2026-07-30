package thaumcraft.common.config.research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * A KAMI entry — the port of the original's {@code KamiResearchItem}.
 *
 * <p>This class <em>is</em> the tier's gate. Every KAMI entry is concealed, and
 * on {@code setPages} it collects every other research in every category into
 * its own hidden parents. The effect is that no KAMI entry opens until the
 * player has finished essentially the whole Thaumonomicon — which is the only
 * thing standing between a new world and ichorium tools.</p>
 *
 * <p>The exclusions are the original's: research that is lost or virtual or has
 * no parents at all, other KAMI entries, anything whose key ends in KAMI, and
 * the blacklist — which upstream seeds with MINILITH alone.</p>
 */
public class TinkererKamiResearchItem extends TinkererResearchItem {

    public static final List<String> BLACKLIST = new ArrayList<String>();

    static {
        BLACKLIST.add("MINILITH");
    }

    public TinkererKamiResearchItem(String key, AspectList tags, int col, int row,
                                    int complexity, ItemStack icon) {
        super(key, tags, col, row, complexity, icon);
        setConcealed();
    }

    /** KAMI entries carry their own prefix, so the tier reads as a later one. */
    @Override
    String getPrefix() {
        return "ttresearch.prefix.kami";
    }

    @Override
    public ResearchItem setPages(ResearchPage... pages) {
        List<String> requirements = this.parentsHidden == null || this.parentsHidden.length == 0
                ? new ArrayList<String>()
                : new ArrayList<String>(Arrays.asList(this.parentsHidden));

        if (!isAutoUnlock()) {
            for (String categoryStr : ResearchCategories.researchCategories.keySet()) {
                ResearchCategoryList category = ResearchCategories.researchCategories.get(categoryStr);
                for (String tag : category.research.keySet()) {
                    ResearchItem research = category.research.get(tag);

                    if (research.isLost()
                            || (research.parentsHidden == null && research.parents == null)
                            || research.isVirtual()
                            || research instanceof TinkererKamiResearchItem
                            || requirements.contains(tag)) {
                        continue;
                    }

                    boolean found = false;
                    for (String black : BLACKLIST) {
                        if (tag.startsWith(black)) {
                            found = true;
                        }
                    }
                    if (tag.endsWith("KAMI")) {
                        found = true;
                    }
                    if (found) {
                        continue;
                    }

                    requirements.add(tag);
                }
            }
        }

        this.parentsHidden = requirements.toArray(new String[requirements.size()]);

        return super.setPages(pages);
    }
}
