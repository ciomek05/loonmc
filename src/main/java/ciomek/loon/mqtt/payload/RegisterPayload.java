package ciomek.loon.mqtt.payload;

import java.util.List;

public record RegisterPayload (
	String uuid,
	String password,
	String internalUsername
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/register/request");
	}
}
