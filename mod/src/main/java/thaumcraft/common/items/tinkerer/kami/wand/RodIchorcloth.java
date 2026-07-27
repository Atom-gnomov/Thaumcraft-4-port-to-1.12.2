package thaumcraft.common.items.tinkerer.kami.wand;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tinkerer.kami.ItemKamiResource;

/**
 * Ichorcloth wand rod — ported from Thaumic Tinkerer's {@code RodIchorcloth}
 * (pixlepix/nekosune, originally Vazkii). Keeps the original's tag
 * {@code ICHORCLOTH}, 1000 vis capacity, craft cost 10, and the glow.
 */
public class RodIchorcloth extends WandRod {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/wand_rod_ichorcloth.png");

    public RodIchorcloth() {
        super("ICHORCLOTH", 1000,
                new ItemStack(ConfigItems.itemKamiResource, 1, ItemKamiResource.ICHORCLOTH_ROD), 10);
        this.setGlowing(true);
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}
