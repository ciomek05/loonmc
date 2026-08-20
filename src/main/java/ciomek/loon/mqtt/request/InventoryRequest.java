package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.inventory.PlayerInventoryPayload;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
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

		if (player != null) {
			PlayerInventoryPayload payload =  new PlayerInventoryPayload(
				new PlayerIdentity(player.uuid, player.username),
				Arrays.stream(player.inventory.mainInventory).map(ItemStackData::from).toList(),
				Arrays.stream(player.inventory.armorInventory).map(ItemStackData::from).toList()
			);

			payload.send();
			return;
		}

		CompoundTag data = server.getDimensionWorld(0).getLevelStorage().getPlayerData(null, playerUuid);
		if (data == null) {
			return;
		}

		ContainerInventory inventory = new ContainerInventory(null);
		inventory.load(data.getList("Inventory"));

		String username = data.containsKey("Nickname") ? data.getString("Nickname") : "";
		PlayerInventoryPayload payload =  new PlayerInventoryPayload(
			new PlayerIdentity(playerUuid, username),
			Arrays.stream(inventory.mainInventory).map(ItemStackData::from).toList(),
			Arrays.stream(inventory.armorInventory).map(ItemStackData::from).toList()
		);

		payload.send();
	}
}
