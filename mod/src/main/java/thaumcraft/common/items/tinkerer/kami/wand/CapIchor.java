package thaumcraft.common.items.tinkerer.kami.wand;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandCap;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tinkerer.kami.ItemKamiResource;

/**
 * Ichor wand cap — ported from Thaumic Tinkerer's {@code CapIchor}
 * (pixlepix/nekosune, originally Vazkii). Tag, discount and craft cost are the
 * original's: {@code ICHOR}, 0.8 vis discount, cost 10.
 */
public class CapIchor extends WandCap {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/wand_cap_ichor.png");

    public CapIchor() {
        super("ICHOR", 0.8F,
                new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHOR_CAP), 10);
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}
