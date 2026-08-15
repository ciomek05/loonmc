package ciomek.loon.mqtt.payload.player;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerTeleportedPayload(
	PlayerIdentity player,
	double fromX,
	double fromY,
	double fromZ,
	double toX,
	double toY,
	double toZ,
	int dimension
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/teleported");
	}
}
