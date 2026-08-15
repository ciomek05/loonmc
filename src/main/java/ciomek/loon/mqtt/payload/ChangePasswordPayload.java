package ciomek.loon.mqtt.payload;

import java.util.List;

public record ChangePasswordPayload (
	String uuid,
	String password
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/register/change_password");
	}
}
