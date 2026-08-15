package ciomek.loon.mqtt.payload.player;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerOnlinePayload(
	PlayerIdentity player,
	boolean online
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/online", "loon/player/" + player.uuid() + "/online/response");
	}
}
