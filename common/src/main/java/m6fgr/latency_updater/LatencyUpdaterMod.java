package m6fgr.latency_updater;

import m6fgr.latency_updater.platform.services.IPlatformHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;


public abstract class LatencyUpdaterMod {
    public static final IPlatformHelper PLATFORM = LatencyUpdaterMod.getAndLoadPlatform();
    public static final String MOD_ID = "latency_updater";
    public static final String MOD_NAME = "LatencyUpdater";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static void init() {}

    private static IPlatformHelper getAndLoadPlatform() {
        return ServiceLoader.load(IPlatformHelper.class).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + IPlatformHelper.class.getName()));
    }
}
