package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.PlayerOnlinePayload;
import ciomek.loon.mqtt.payload.player.inventory.PlayerInventoryPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

import java.util.Arrays;
import java.util.UUID;

public record PlayerOnlineRequest(UUID playerUuid) implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		boolean online = server.playerList.playerEntities.stream()
			.anyMatch(p -> p.uuid.equals(playerUuid));

		PlayerServer player = server.playerList.playerEntities.stream()
			.filter(p -> p.uuid.equals(playerUuid))
			.findFirst()
			.orElse(null);

		PlayerIdentity identity;
		if(player == null) {
			identity = new PlayerIdentity(playerUuid, "");
		}
		else {
			identity = new PlayerIdentity(player.uuid, player.username);
		}

		PlayerOnlinePayload payload = new PlayerOnlinePayload(
			identity,
			online);

		payload.send();
	}
}
