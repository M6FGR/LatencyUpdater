package m6fgr.latency_updater.config;

import m6fgr.latency_updater.LatencyUpdaterMod;

public abstract class AbstractLatencyConfig {

    private static final AbstractLatencyConfig INSTANCE = LatencyUpdaterMod.loadService(AbstractLatencyConfig.class);

    public static AbstractLatencyConfig getInstance() {
        return INSTANCE;
    }

    /**
     * @return The interval in ticks between tab-list ping updates (e.g., 20 ticks = 1s).
     */
    public abstract int getPingUpdateTicks();

    /**
     * @return Weather a debug message should log or no in the server logs
     */
    public abstract boolean shouldDebugLog();
}