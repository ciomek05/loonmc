package ciomek.loon.mqtt.payload;

import java.util.List;

public record CommandPayload(
	String command,
	String source
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/commands", "loon/all");
	}
}
