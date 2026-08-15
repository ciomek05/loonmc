package ciomek.loon.mqtt.request;

import ciomek.loon.mqtt.payload.data.BlockData;
import ciomek.loon.mqtt.payload.data.ChunkData;
import ciomek.loon.mqtt.payload.world.chunks.ChunkMapPayload;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkLoader;
import net.minecraft.core.world.chunk.provider.ChunkProvider;
import net.minecraft.core.world.pos.ChunkPos;
import net.minecraft.core.world.pos.ChunkTilePos;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record ChunkMapRequest(int xStart, int xEnd, int zStart, int zEnd) implements IRequest {
	@Override
	public void handle(MinecraftServer server) {
		World world = server.getDimensionWorld(0);
		ChunkProvider provider = world.getChunkProvider();
		ChunkLoader loader = world.getLevelStorage().getChunkLoader(world.dimension);

		for (int x = xStart; x <= xEnd; x++) {
			for (int z = zStart; z <= zEnd; z++) {
				Chunk currentChunk = resolveChunk(world, provider, loader, x, z);

				if  (currentChunk == null) {
					ChunkData chunkData = new ChunkData(null, "", x, z, false);
					new ChunkMapPayload(chunkData).send();
					continue;
				}

				String biomeName = getBiomeName(currentChunk);

				List<List<BlockData>> chunkMap = new ArrayList<>();
				for (int chunkX = 0; chunkX < 16; chunkX++) {
					chunkMap.add(new ArrayList<>());

					for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
						int highestBlockY = Math.max(0, currentChunk.getHeightValue(chunkX, chunkZ) - 1);

						Block block = currentChunk.getBlock(new ChunkTilePos(chunkX, highestBlockY, chunkZ));

						int meta = currentChunk.getBlockMetadata(chunkX, highestBlockY, chunkZ);
						int rgb = MaterialColor.getColorFromIndex(
							MaterialColor.getColorIndexFromBlock(block, meta)
						);

						BlockData blockData = new BlockData(block.getKey(), block.id(), rgb);

						chunkMap.get(chunkX).add(blockData);
					}
				}

				ChunkData chunkData = new ChunkData(chunkMap, biomeName, x, z, true);
				new ChunkMapPayload(chunkData).send();
			}
		}
	}

	String getBiomeName(Chunk chunk)
	{
		int surfaceY = Math.max(0, chunk.getHeightValue(8, 8) - 1);
		Biome biome = chunk.getBlockBiome(new ChunkTilePos(8, surfaceY, 8));
		return biome != null ? biome.getRegistryKey() : "?";
	}

	Chunk resolveChunk(World world, ChunkProvider provider, ChunkLoader loader, int x, int z)
	{
		ChunkPos chunkPos = new ChunkPos(x, z);

		if (provider.isChunkLoaded(chunkPos))
			return world.getChunk(chunkPos);

		if (loader == null || !loader.chunkExists(world, x, z))
			return null;

		try {
			return loader.loadChunk(world, x, z);
		} catch (IOException e) {
			return null;
		}
	}
}
