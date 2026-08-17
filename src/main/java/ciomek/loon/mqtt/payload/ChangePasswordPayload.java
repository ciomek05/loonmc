package ciomek.loon.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record ChangePasswordPayload (
	@JsonIgnore String uuid,
	String password
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/auth/register/" + uuid + "/change_password");
	}
}
