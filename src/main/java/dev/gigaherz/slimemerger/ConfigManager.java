package dev.gigaherz.slimemerger;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

public class ConfigManager
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig
    {
        public final ForgeConfigSpec.BooleanValue mergeSlimes;
        public final ForgeConfigSpec.IntValue maxSize;
        public final ForgeConfigSpec.IntValue minAge;

        ServerConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Settings for slime merging").push("slimes");
            mergeSlimes = builder
                    .comment("If enabled, slimes will have new AI rules to feel attracted to other slimes, and if 4 slimes of the same size are nearby they will merge into a slime of higher size.")
                    .define("merge", true);
            maxSize = builder
                    .comment("The maximum size the slimes can achieve via merging.")
                    .defineInRange("maxSize", 8, 1, Slime.MAX_SIZE);
            minAge = builder
                    .comment("The minimum number of ticks the entity needs to have existed for before it can start looking for merging mates.")
                    .defineInRange("minAge", 200, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static boolean enabled;
    public static int minAge;
    public static int maxSize;

    @Mod.EventBusSubscriber(modid = SlimeMerger.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Events
    {
        @SubscribeEvent
        public static void modConfig(ModConfigEvent event)
        {
            ModConfig config = event.getConfig();
            if (config.getSpec() != SERVER_SPEC)
                return;

            enabled = SERVER.mergeSlimes.get();
            minAge = SERVER.minAge.get();
            maxSize = SERVER.maxSize.get();
        }
    }
}