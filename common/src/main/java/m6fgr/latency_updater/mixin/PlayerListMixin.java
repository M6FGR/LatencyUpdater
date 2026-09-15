package m6fgr.latency_updater.mixin;

import m6fgr.latency_updater.config.AbstractLatencyConfig;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

@Mixin(value = PlayerList.class, priority = 1005)
public abstract class PlayerListMixin {

    @Shadow
    private int sendAllPlayerInfoIn;

    @Shadow
    public abstract void broadcastAll(Packet<?> packet);

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        int updateInterval = AbstractLatencyConfig.get().getPingUpdateTicks();

        if (++this.sendAllPlayerInfoIn > updateInterval) {
            this.broadcastAll(new ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY),
                    this.players
            ));
            this.sendAllPlayerInfoIn = 0;
        }
        ci.cancel();
    }
}