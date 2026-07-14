package thaumcraft.common.config;

import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.Thaumcraft;

// Custom TC4 block sounds. In 1.7.10 these were CustomStepSound("crystal",1,1) and
// CustomSoundType("gore",0.5,0.8); ported here as registered SoundEvents wrapped in SoundType.
// The SoundEvent/SoundType objects are built eagerly at class-load so BlockCrystal/BlockTaint
// can reference the SoundType constants during block construction (which happens before the
// SoundEvent registry event fires); registerSounds only adds them to the registry.
@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class ConfigSounds {
    public static final SoundEvent CRYSTAL = create("crystal");
    public static final SoundEvent GORE = create("gore");
    public static final SoundEvent URNBREAK = create("urnbreak");

    // volume, pitch, break, step, place, hit, fall — original applied the custom sound to break + step.
    public static final SoundType SOUND_CRYSTAL =
            new SoundType(1.0f, 1.0f, CRYSTAL, CRYSTAL, CRYSTAL, CRYSTAL, CRYSTAL);
    public static final SoundType SOUND_GORE =
            new SoundType(0.5f, 0.8f, GORE, GORE, GORE, GORE, GORE);
    public static final SoundType SOUND_URNBREAK =
            new SoundType(1.0f, 0.7f, URNBREAK, URNBREAK, URNBREAK, URNBREAK, URNBREAK);

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(CRYSTAL, GORE, URNBREAK);
    }

    private static SoundEvent create(String name) {
        ResourceLocation loc = new ResourceLocation(Thaumcraft.MODID, name);
        return new SoundEvent(loc).setRegistryName(loc);
    }
}
