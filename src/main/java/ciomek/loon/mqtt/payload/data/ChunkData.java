package ciomek.loon.mqtt.payload.data;

import java.util.List;

public record ChunkData (
	List<List<BlockData>> map,
	String biome,
	int x,
	int z,
	boolean loaded
) {
}
