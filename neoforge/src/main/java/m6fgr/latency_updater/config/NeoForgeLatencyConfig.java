package m6fgr.latency_updater.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeLatencyConfig extends AbstractLatencyConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue PING_UPDATE_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("ping_settings");

        PING_UPDATE_TICKS = builder
                .comment("Interval in ticks between broadcasting tab list ping updates")
                .defineInRange("pingUpdateTicks", 20, 1, 600);

        builder.pop();
        SPEC = builder.build();
    }

    @Override
    public int getPingUpdateTicks() {
        return PING_UPDATE_TICKS.get();
    }
}