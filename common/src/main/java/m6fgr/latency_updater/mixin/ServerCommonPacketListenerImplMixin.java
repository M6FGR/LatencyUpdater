package m6fgr.latency_updater.mixin;

import m6fgr.latency_updater.LatencyUpdaterMod;
import m6fgr.latency_updater.config.AbstractLatencyConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerCommonPacketListenerImpl.class, priority = 1005)
public abstract class ServerCommonPacketListenerImplMixin {

    @Shadow private long keepAliveTime;
    @Shadow private boolean keepAlivePending;
    @Shadow private long keepAliveChallenge;

    @Shadow
    public abstract void send(net.minecraft.network.protocol.Packet<?> packet);

    @Shadow
    @Final
    protected MinecraftServer server;

    @Shadow
    public abstract void disconnect(Component pReason);

    @Inject(method = "keepConnectionAlive", at = @At("HEAD"), cancellable = true)
    private void onKeepConnectionAlive(CallbackInfo ci) {
        long currentTime = System.nanoTime() / 1000000L;
        long intervalMs = AbstractLatencyConfig.get().getPingUpdateTicks() * 50L;
        boolean shouldDebug = AbstractLatencyConfig.get().shouldDebugLog();

        if (currentTime - this.keepAliveTime >= intervalMs) {
            if (this.keepAlivePending) {
                long time = currentTime - keepAliveTime;
                this.disconnect(Component.literal(
                "keepAliveTime was more than " + time + "ms while it was pending! Issue came from Ping Updater mod."
                ));
            } else {
                this.keepAlivePending = true;
                this.keepAliveTime = currentTime;
                this.keepAliveChallenge = currentTime;
                this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
                if (shouldDebug) {
                    LatencyUpdaterMod.LOG.debug("From SCPLI: Sent ClientBoundKeepAlivePacket");
                    LatencyUpdaterMod.LOG.debug("Sent a message to keep the connection alive from ServerCommonPacketListenerImpl class");
                }
            }
        }
        ci.cancel();
    }
}