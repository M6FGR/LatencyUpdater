package m6fgr.latency_updater;


import m6fgr.latency_updater.config.NeoForgeLatencyConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = LatencyUpdaterMod.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class LatencyUpdaterNeoForge extends LatencyUpdaterMod {

    public LatencyUpdaterNeoForge(ModContainer container) {
        super();
        LOG.info("NeoForge is initialized");
        container.registerConfig(ModConfig.Type.SERVER, NeoForgeLatencyConfig.SPEC);
    }
}
