package m6fgr.latency_updater;

import net.fabricmc.api.DedicatedServerModInitializer;

import java.lang.reflect.Constructor;

public class LatencyUpdaterFabric extends LatencyUpdaterMod implements DedicatedServerModInitializer {

    public LatencyUpdaterFabric() {
        super();
        LOG.info("Fabric is initialized");
    }

    @Override
    public void onInitializeServer() {
        new LatencyUpdaterFabric();
    }
}
