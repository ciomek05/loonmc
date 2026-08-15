package ciomek.loon.mqtt.payload.player.inventory;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerPickUpItemPayload (
	PlayerIdentity player,
	ItemStackData itemStack
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/inventory/picked_up");
	}
}
