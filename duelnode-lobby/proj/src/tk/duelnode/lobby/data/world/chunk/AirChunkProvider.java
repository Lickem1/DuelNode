package tk.duelnode.lobby.data.world.chunk;

import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_12_R1.ChunkGenerator;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.WorldServer;

public class AirChunkProvider extends ChunkProviderServer {

    public AirChunkProvider(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator chunkgenerator) {
        super(worldserver, ichunkloader, chunkgenerator);
    }

    @Override
    public Chunk originalGetChunkAt(int i, int j) {
        Chunk chunk = originalGetOrLoadChunkAt(i, j);
        if (chunk == null) {
            chunk = loadChunk(i, j);
            if (chunk == null) {
                chunk = new AirChunk(world, i, j);
            }

            this.chunks.put(ChunkCoordIntPair.a(i, j), chunk);
            chunk.addEntities();
            chunk.loadNearby(this, this.chunkGenerator, true);
        }

        return chunk;
    }

    @Override
    public void saveChunk(Chunk chunk, boolean unloaded) {
        if (!(chunk instanceof AirChunk)) super.saveChunk(chunk, unloaded);
    }
}