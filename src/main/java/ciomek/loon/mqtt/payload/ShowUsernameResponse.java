package ciomek.loon.mqtt.payload;

public record ShowUsernameResponse(
	boolean success,
	String error,
	String internalUsername
) {}
