package ciomek.loon.mqtt.payload.player;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerDiedPayload(
	PlayerIdentity player,
	String killer,
	String damageType
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/died");
	}
}
