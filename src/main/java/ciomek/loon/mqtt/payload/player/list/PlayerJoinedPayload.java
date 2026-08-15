package ciomek.loon.mqtt.payload.player.list;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerJoinedPayload(
	PlayerIdentity player
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/playerlist/joined", "loon/player/" + player.uuid() + "/joined");
	}
}
