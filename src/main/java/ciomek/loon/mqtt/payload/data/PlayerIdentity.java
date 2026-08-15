package ciomek.loon.mqtt.payload.data;

import java.util.UUID;

public record PlayerIdentity
(
	UUID uuid,
	String username
) { }
