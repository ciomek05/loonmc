package ciomek.loon.mqtt.payload.player;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.data.PositionData;

import java.util.List;

public record PlayerPositionPayload(
	PlayerIdentity player,
	PositionData position,
	Integer dimension
	) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/position", "loon/player/" + player.uuid() + "/position/response");
	}
}
