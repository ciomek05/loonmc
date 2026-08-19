package ciomek.loon.mixin;

import ciomek.loon.Loon;
import ciomek.loon.TPSTracker;
import ciomek.loon.mqtt.payload.ServerInfoPayload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.data.PositionData;
import ciomek.loon.mqtt.payload.player.PlayerPositionPayload;
import ciomek.loon.mqtt.request.IRequest;
import ciomek.loon.mqtt.request.RequestManager;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = WorldServer.class, remap = false)
public class WorldServerMixin {
	@Unique
	private static final int POSITION_CHECK_INTERVAL_TICKS = 20;

	@Unique
	private static final int TPS_CHECK_INTERVAL_TICKS = 20;

	@Shadow
	public MinecraftServer mcServer;

	@Unique
	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	@Unique
	private static long lastTickNanos;

	@Unique
	private static long gapSumNanos;

	@Unique
	private static int gapTicks;

	@Unique
	private static int positionCheckTicks;

	@Unique
	private static int tpsCheckTicks;

	@Unique
	private static double lastTPS = 0;

	@Unique
	private static final Map<UUID, PositionData> lastSentPositions = new HashMap<>();

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		countTicks();
		positionCountdown();
		tpsCountdown();
		handleRequests();
	}

	@Unique
	private void broadcastMovedPlayerPositions() {
		for (PlayerServer player : mcServer.playerList.playerEntities) {
			PositionData position = new PositionData(player.x, player.y, player.z);

			if (!position.equals(lastSentPositions.put(player.uuid, position))) {
				PlayerPositionPayload payload = new PlayerPositionPayload(
					new PlayerIdentity(player.uuid, player.username),
					position,
					player.dimension
				);

				payload.send();
			}
		}
	}

	@Unique private void countTicks()
	{
		WorldServer self = (WorldServer) (Object) this;
		if (self.dimension == Dimension.OVERWORLD) {
			long now = System.nanoTime();
			if (lastTickNanos != 0) {
				gapSumNanos += now - lastTickNanos;
				gapTicks++;
				if (gapSumNanos >= 1_000_000_000L) {
					TPSTracker.setTPS(Math.min(20.0, gapTicks / (gapSumNanos / 1e9)));
					gapSumNanos = 0;
					gapTicks = 0;
				}
			}
			lastTickNanos = now;
		}
	}

	@Unique private void positionCountdown()
	{
		if (++positionCheckTicks >= POSITION_CHECK_INTERVAL_TICKS) {
			positionCheckTicks = 0;
			broadcastMovedPlayerPositions();
		}
	}

	@Unique private void tpsCountdown()
	{
		if (++tpsCheckTicks >= TPS_CHECK_INTERVAL_TICKS) {
			tpsCheckTicks = 0;

			double currentTPS = TPSTracker.getTPSRounded();

			if (Math.round(lastTPS * 10) != currentTPS * 10) {
				lastTPS = currentTPS;

				WorldServer world = (WorldServer) (Object) this;
				MinecraftServer server = world.mcServer;

				String domain = System.getenv(Loon.EnvVarPrefix + "DOMAIN");
				if (domain == null) {
					domain = "";
				}

				ServerInfoPayload payload = new ServerInfoPayload(
					TPSTracker.getTPSRounded(),
					server.getMinecraftVersion(),
					domain,
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
	}

	@Unique private void handleRequests()
	{
		IRequest request;
		while ((request = RequestManager.pollRequest()) != null) {
			IRequest current = request;
			if (current.requireTickThread()) {
				runRequest(current);
			} else {
				EXECUTOR.submit(() -> runRequest(current));
			}
		}
	}

	@Unique
	private void runRequest(IRequest request) {
		try {
			request.handle(mcServer);
		} catch (Exception e) {
			Loon.LOGGER.error("Failed to handle request: {}, {}", request.getClass().getSimpleName(), e);
		}
	}
}
