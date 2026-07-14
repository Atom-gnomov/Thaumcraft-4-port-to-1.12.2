package thaumcraft.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Thaumcraft.MODID, name = Thaumcraft.MODNAME, version = Thaumcraft.VERSION,
     dependencies = "required-after:forge@[14.23.5.2847,);required-after:baubles")
public class Thaumcraft {
    public static final String MODID = "thaumcraft";
    public static final String MODNAME = "Thaumcraft";
    public static final String VERSION = "4.2.3.5-1.12.2";

    @Mod.Instance(MODID)
    public static Thaumcraft instance;

    @SidedProxy(clientSide = "thaumcraft.client.ClientProxy", serverSide = "thaumcraft.common.CommonProxy")
    public static CommonProxy proxy;

    public static Logger log;

    public static final CreativeTabs tabTC = new CreativeTabs("thaumcraft") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.NETHER_STAR);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        log.info("Thaumcraft 4 port :: preInit");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        log.info("Thaumcraft 4 port :: init");
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        log.info("Thaumcraft 4 port :: postInit");
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
