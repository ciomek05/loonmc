package ciomek.loon.mqtt.payload;


import java.util.List;

public record ServerInfoPayload(
	double tps,
	String version,
	String domain,
	String motd,
	boolean onlineMode,
	int sleepPercentage,
	int currentPlayerCount,
	int maxPlayerCount,
	int difficulty,
	boolean pvpOn,
	int viewDistance
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/server/info");
	}
}
