package ciomek.loon.mixin;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.CommandPayload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.list.PlayerKickedPayload;
import ciomek.loon.mqtt.payload.player.PlayerTeleportedPayload;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerServer.class, remap = false)
public class PacketHandlerServerMQTTMixin {
	@Shadow
	private PlayerServer playerEntity;

	@Inject(method = "handleSlashCommand", at = @At("TAIL"))
	private void onSlashCommand(String command, CallbackInfo ci) {
		CommandPayload payload = new CommandPayload(sanitizeCommand(command), playerEntity.username);

		payload.send();
	}

	@Inject(method = "kickPlayer", at = @At("HEAD"))
	private void onKickPlayer(String reason, CallbackInfo ci) {
		PlayerKickedPayload payload = new PlayerKickedPayload(
			new PlayerIdentity(playerEntity.uuid, playerEntity.username),
			reason
		);

		payload.send();
	}

	@Inject(method = "teleport(DDD)V", at = @At("HEAD"))
	private void onTeleport(double x, double y, double z, CallbackInfo ci) {
		publishTeleport(x, y, z, playerEntity.dimension);
	}

	@Inject(method = "teleportAndRotate", at = @At("HEAD"))
	private void onTeleportAndRotate(double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
		publishTeleport(x, y, z, playerEntity.dimension);
	}

	private void publishTeleport(double toX, double toY, double toZ, int dimension) {
		PlayerTeleportedPayload payload = new PlayerTeleportedPayload(
			new PlayerIdentity(playerEntity.uuid, playerEntity.username),
			playerEntity.x,
			playerEntity.y,
			playerEntity.z,
			toX,
			toY,
			toZ,
			dimension
		);

		payload.send();
	}

	private String sanitizeCommand(String command) {
		String normalized = command.startsWith("/") ? command.substring(1) : command;
		String[] parts = normalized.split(" ");

		if (parts.length == 0) {
			return command;
		}

		if ("register".equals(parts[0]) && parts.length >= 3) {
			parts[2] = "[REDACTED]";
			return String.join(" ", parts);
		}

		if ("change-password".equals(parts[0]) && parts.length >= 2) {
			parts[1] = "[REDACTED]";
			return String.join(" ", parts);
		}

		return command;
	}
}
