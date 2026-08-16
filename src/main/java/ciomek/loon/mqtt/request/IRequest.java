package ciomek.loon.mqtt.request;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

public interface IRequest
{
	void handle(MinecraftServer server);
	default boolean requireTickThread()
	{
		return true;
	}
}
