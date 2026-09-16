package m6fgr.latency_updater;


import m6fgr.latency_updater.config.NeoForgeLatencyConfig;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLLoader;

@Mod(value = LatencyUpdaterMod.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class LatencyUpdaterNeoForge extends LatencyUpdaterMod {

    public LatencyUpdaterNeoForge(IEventBus eventBus, ModContainer container) {
        if (FMLLoader.getDist().isClient()) {
            return;
        }
        LatencyUpdaterMod.init();
        container.registerConfig(ModConfig.Type.SERVER, NeoForgeLatencyConfig.SPEC);
    }
}
