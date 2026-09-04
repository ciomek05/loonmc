package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.list.PlayerListSyncPayload;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.stream.Collectors;

public record PlayerListSyncRequest() implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		List<PlayerIdentity> players = server.playerList.playerEntities.stream()
			.map(player -> new PlayerIdentity(player.uuid, player.username))
			.collect(Collectors.toList());

		PlayerListSyncPayload payload = new PlayerListSyncPayload(players);

		payload.send();
	}
}
