package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.inventory.PlayerInventoryPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

import java.util.Arrays;
import java.util.UUID;

public record InventoryRequest(UUID playerUuid) implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		PlayerServer player = server.playerList.playerEntities.stream()
			.filter(playerServer -> playerServer.uuid.equals(playerUuid))
			.findFirst()
			.orElse(null);

		if (player == null) {
			return;
		}

		PlayerInventoryPayload payload = new PlayerInventoryPayload(
			new PlayerIdentity(player.uuid, player.username),
			Arrays.stream(player.inventory.mainInventory)
				.map(ItemStackData::from)
				.toList(),
			Arrays.stream(player.inventory.armorInventory)
				.map(ItemStackData::from)
				.toList()
		);

		payload.send();
	}
}
