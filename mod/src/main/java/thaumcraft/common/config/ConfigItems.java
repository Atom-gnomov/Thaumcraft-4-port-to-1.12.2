package thaumcraft.common.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemShard;
import thaumcraft.common.items.ItemTripleMeatTreat;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class ConfigItems {
    public static Item itemTripleMeatTreat;
    public static Item itemShard;
    public static Item itemResource;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();
        reg.register(itemTripleMeatTreat = register(new ItemTripleMeatTreat(), "TripleMeatTreat", "triplemeattreat"));
        reg.register(itemShard = register(new ItemShard(), "ItemShard", "itemshard"));
        reg.register(itemResource = register(new ItemResource(), "ItemResource", "itemresource"));
    }

    private static Item register(Item item, String translationKey, String registryName) {
        item.setUnlocalizedName(translationKey);
        item.setRegistryName(new ResourceLocation(Thaumcraft.MODID, registryName));
        return item;
    }
}
