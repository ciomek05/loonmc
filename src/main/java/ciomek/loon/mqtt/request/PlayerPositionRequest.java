package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.data.PositionData;
import ciomek.loon.mqtt.payload.player.PlayerOnlinePayload;
import ciomek.loon.mqtt.payload.player.PlayerPositionPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

import java.util.UUID;

public record PlayerPositionRequest(UUID playerUuid) implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		PlayerServer player = server.playerList.playerEntities.stream()
			.filter(p -> p.uuid.equals(playerUuid))
			.findFirst()
			.orElse(null);

		if (player == null) {
			PlayerPositionPayload payload = new PlayerPositionPayload(
				new PlayerIdentity(playerUuid, null),
				null,
				null
			);
			payload.send();
		}
		else {
			PositionData playerPosition = new PositionData(player.x, player.y, player.z);

			PlayerPositionPayload payload = new PlayerPositionPayload(
				new PlayerIdentity(player.uuid, player.username),
				playerPosition,
				player.dimension
			);
			payload.send();
		}
	}
}
