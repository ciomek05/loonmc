package ciomek.loon.mqtt.payload.data;

public record BlockData(
	String blockName,
	int blockId,
	int color
) {
}
