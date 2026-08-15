package ciomek.loon.mqtt.payload.world.chunks;

import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.ChunkData;
import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;

import java.util.List;

public record ChunkMapPayload(
	ChunkData chunk
) implements Payload {
	@Override
	public List<String> topics() {
		return List.of("loon/world/chunk/" + chunk.x() + "/" + chunk.z(), "loon/world/chunk/" + chunk.x() + "/" + chunk.z() + "/response");
	}
}
