package ciomek.loon.mqtt.payload.player.list;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerKickedPayload(
	PlayerIdentity player,
	String reason
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/playerlist/kicked", "loon/player/" + player.uuid() + "/kicked");
	}
}
