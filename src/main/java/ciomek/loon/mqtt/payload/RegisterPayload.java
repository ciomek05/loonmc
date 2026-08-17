package ciomek.loon.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record RegisterPayload (
	@JsonIgnore String uuid,
	String password,
	String internalUsername
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/auth/register/" + uuid + "/request");
	}
}
