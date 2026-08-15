package ciomek.loon.mqtt.payload.player.inventory;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record PlayerInventoryPayload(
	PlayerIdentity player,
	List<ItemStackData> grid,
	List<ItemStackData> armor
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/player/" + player.uuid() + "/inventory/full/response");
	}
}
