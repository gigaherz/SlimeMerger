package dev.gigaherz.slimemerger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(SlimeMerger.MODID)
public class SlimeMerger
{
    public static final String MODID = "slimemerger";

    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static RegistryObject<SoundEvent> SOUND_SHLOP = SOUND_EVENTS.register("shlop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "mob.slime.merge")));

    public SlimeMerger()
    {
        SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
    }
}