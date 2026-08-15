package ciomek.loon.mqtt.request;

import ciomek.loon.TPSTracker;
import ciomek.loon.mixin.WorldServerMixin;
import ciomek.loon.mqtt.payload.ServerInfoPayload;
import net.minecraft.server.MinecraftServer;

public record ServerInfoRequest() implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		ServerInfoPayload payload = new ServerInfoPayload(
			TPSTracker.getTPS(),
			server.getMinecraftVersion(),
			server.motd,
			server.onlineMode,
			server.sleepPercentage,
			server.playerList.playerEntities.size(),
			server.maxPlayers,
			server.difficulty,
			server.pvpOn,
			server.viewDistance);

		payload.send();
	}
}
