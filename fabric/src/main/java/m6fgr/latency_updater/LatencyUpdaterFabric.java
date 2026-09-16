package m6fgr.latency_updater;

import net.fabricmc.api.DedicatedServerModInitializer;

public class LatencyUpdaterFabric extends LatencyUpdaterMod implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        LatencyUpdaterMod.init();
    }
}
