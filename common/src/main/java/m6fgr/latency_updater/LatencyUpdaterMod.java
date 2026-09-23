package m6fgr.latency_updater;

import com.mojang.logging.LogUtils;
import m6fgr.latency_updater.platform.services.IPlatformHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.util.ServiceLoader;


public abstract class LatencyUpdaterMod {
    public static final IPlatformHelper PLATFORM = LatencyUpdaterMod.loadService(IPlatformHelper.class);

    public static final String MOD_ID = "latency_updater";
    public static final String MOD_NAME = "LatencyUpdater";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    // Instead of the method LatencyUpdaterMod#init, I thought of calling this constructor
    // In NeoForge and Fabric's constructors, so we avoid methods
    public LatencyUpdaterMod() {
        LOG.info("Common main is initialized");
    }

    private static NullPointerException throwNoPlatform() {
        return new NullPointerException("Couldn't load " + StackLocatorUtil.getCallerClass(2).getSimpleName() + "'s platform");
    }

    public static <T> T loadService(Class<T> loadableClass) {
        return ServiceLoader.load(loadableClass)
                .findFirst()
                .orElseThrow(LatencyUpdaterMod::throwNoPlatform);
    }
}
