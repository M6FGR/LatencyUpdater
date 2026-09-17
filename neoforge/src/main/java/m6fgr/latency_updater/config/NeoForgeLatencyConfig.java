package m6fgr.latency_updater.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeLatencyConfig extends AbstractLatencyConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue PING_UPDATE_TICKS;
    public static final ModConfigSpec.BooleanValue DEBUG_LOG;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("ping_settings");

        PING_UPDATE_TICKS = builder
                .comment("Interval in ticks between broadcasting tab list ping updates")
                .defineInRange("pingUpdateTicks", 20, 1, 600);

        DEBUG_LOG = builder
                .comment("Logs a debug message everytime the ping updates")
                .define("debug_log", false);

        builder.pop();
        SPEC = builder.build();
    }

    @Override
    public int getPingUpdateTicks() {
        return PING_UPDATE_TICKS.getAsInt();
    }

    @Override
    public boolean shouldDebugLog() {
        return DEBUG_LOG.getAsBoolean();
    }
}