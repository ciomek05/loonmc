package ciomek.loon.mixin;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.player.list.PlayerJoinedPayload;
import ciomek.loon.mqtt.payload.player.list.PlayerLeftPayload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerListMQTTMixin {
	@Inject(method = "playerLoggedIn", at = @At(value = "TAIL"))
	public void onPayerLoggedIn(PlayerServer player, CallbackInfo ci) {
		PlayerJoinedPayload payload = new PlayerJoinedPayload(new PlayerIdentity(player.uuid, player.username));

		payload.send();
	}

	@Inject(method = "playerLoggedOut", at = @At(value = "TAIL"))
	public void onPayerLoggedOut(PlayerServer entityplayermp, CallbackInfo ci) {
		PlayerLeftPayload payload = new PlayerLeftPayload(new PlayerIdentity(entityplayermp.uuid, entityplayermp.username));

		payload.send();
	}
}
