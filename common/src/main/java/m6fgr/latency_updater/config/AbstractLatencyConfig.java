package m6fgr.latency_updater.config;

import java.util.ServiceLoader;

public abstract class AbstractLatencyConfig {

    private static AbstractLatencyConfig INSTANCE;

    public static AbstractLatencyConfig get() {
        if (INSTANCE == null) {
            INSTANCE = ServiceLoader.load(AbstractLatencyConfig.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Failed to load LatencyConfig implementation"));
        }
        return INSTANCE;
    }

    /**
     * @return The interval in ticks between tab-list ping updates (e.g., 20 ticks = 1s).
     */
    public abstract int getPingUpdateTicks();
}