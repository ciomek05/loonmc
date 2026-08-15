package ciomek.loon.mqtt.payload;

public record RegisterResponse(
	boolean success,
	String error
) {}
