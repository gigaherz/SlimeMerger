package dev.gigaherz.slimemerger;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SlimeMerger.MODID)
public class SlimeMerger
{
    public static final String MODID = "slimemerger";

    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static DeferredHolder<SoundEvent, SoundEvent> SOUND_SHLOP = SOUND_EVENTS.register("shlop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "mob.slime.merge")));

    public SlimeMerger(IEventBus modBus)
    {
        SOUND_EVENTS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigManager.SERVER_SPEC);
    }
}