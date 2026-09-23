package m6fgr.latency_updater.mixin;

import com.mojang.authlib.GameProfile;
import m6fgr.latency_updater.LatencyUpdaterMod;
import m6fgr.latency_updater.config.AbstractLatencyConfig;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerCommonPacketListenerImpl.class, priority = 1005)
public abstract class ServerCommonPacketListenerImplMixin {

    @Shadow private long keepAliveTime;
    @Shadow private boolean keepAlivePending;
    @Shadow private long keepAliveChallenge;
    @Shadow @Final protected Connection connection;
    @Shadow @Final private static Component TIMEOUT_DISCONNECTION_MESSAGE;

    @Shadow public abstract void send(net.minecraft.network.protocol.Packet<?> packet);

    @Shadow public abstract void disconnect(Component pReason);

    @Shadow protected abstract boolean checkIfClosed(long pTime);

    @Shadow protected abstract boolean isSingleplayerOwner();

    @Shadow private int latency;

    // Unique fields and methods:
    @Unique
    private static final long TIMEOUT_THRESHOLD_MS = 15000L;

    // Using an alternative version of Util#getMills,
    // to keep compatibility from 1.21 to 1.21.11.
    // if I used Util#getMills, it'd throw a NoClassFoundException.
    @Unique
    private long getCurrentTimeMs() {
        return System.nanoTime() / 1_000_000L;
    }

    @Unique
    private String getPlayerName() {
        if ((Object) this instanceof ServerGamePacketListenerImpl gameListener) {
            if (gameListener.player != null) {
                return gameListener.player.getScoreboardName();
            }
        }
        return "Non-Player Connection";
    }

    // actual mixins
    @Inject(
            at = @At("HEAD"),
            method = "keepConnectionAlive",
            cancellable = true
    )
    private void updateKeepAliveProtocol(CallbackInfo ci) {
        // This fixes an issue where the player is stuck at the "Joining world..."
        // Or when the handshake is never complete.
        if (this.connection == null || !this.connection.isConnected()) {
            ci.cancel();
            return;
        }

        long currentTime = this.getCurrentTimeMs();
        long intervalMs = AbstractLatencyConfig.getInstance().getPingUpdateTicks() * 50L;

        if (!this.isSingleplayerOwner() && currentTime - this.keepAliveTime >= intervalMs) {
            if (this.keepAlivePending) {
                long time = currentTime - this.keepAliveTime;
                if (time >= TIMEOUT_THRESHOLD_MS) {
                    this.disconnect(Component.literal("Timed out: No keep-alive response received for " + time + "ms."));
                }
            } else if (this.checkIfClosed(currentTime)) {
                this.keepAlivePending = true;
                this.keepAliveTime = currentTime;
                this.keepAliveChallenge = currentTime;
                this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
            }
        }
        ci.cancel();
    }

    @Inject(
            at = @At("HEAD"),
            method = "handleKeepAlive",
            cancellable = true
    )
    private void logPingUpdates(ServerboundKeepAlivePacket pPacket, CallbackInfo ci) {
        ci.cancel();

        long currentTime = this.getCurrentTimeMs();

        if (this.keepAlivePending && pPacket.getId() == this.keepAliveChallenge) {
            // rtt = Round-Trip Time
            int rtt = (int) (currentTime - this.keepAliveTime);

            if (rtt < 0) {
                LatencyUpdaterMod.LOG.error(
                        "Failed to calculate ping for {}: Invalid negative RTT calculated ({} ms). Skipping calculation.",
                        this.getPlayerName(),
                        rtt
                );
            } else {
                this.latency = (this.latency * 3 + rtt) / 4;
                this.keepAlivePending = false;

                if (AbstractLatencyConfig.getInstance().shouldDebugLog()) {
                    LatencyUpdaterMod.LOG.info(
                            "Updated latency for {}: {} ms (RTT: {} ms)",
                            this.getPlayerName(),
                            this.latency,
                            rtt
                    );
                }
            }
        } else {
            // Invalid packet payload / challenge mismatch debugging
            if (AbstractLatencyConfig.getInstance().shouldDebugLog()) {
                LatencyUpdaterMod.LOG.warn(
                        "Failed to update ping for {}: Challenge ID mismatch or unexpected packet! Received: {}, Expected: {} (Pending: {})",
                        this.getPlayerName(),
                        pPacket.getId(),
                        this.keepAliveChallenge,
                        this.keepAlivePending
                );
            }

            if (!this.isSingleplayerOwner()) {
                this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
            }
        }
    }
}