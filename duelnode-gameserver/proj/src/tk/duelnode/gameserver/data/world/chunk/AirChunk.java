package tk.duelnode.gameserver.data.world.chunk;

import net.minecraft.server.v1_12_R1.*;

public class AirChunk extends Chunk {

    public AirChunk(World world, int i, int j) {
        super(world, i, j);
    }

    @Override
    public IBlockData a(int i, int j, int k) {
        return Blocks.AIR.getBlockData();
    }

    @Override
    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
    }

    @Override
    public void a(ChunkGenerator chunkGenerator) {
    }

    @Override
    public void loadNearby(IChunkProvider iChunkProvider, ChunkGenerator chunkGenerator, boolean newChunk) {
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if ((x != 0) || (z != 0)) {
                    Chunk neighbor = getWorld().getChunkIfLoaded(locX + x, locZ + z);
                    if (neighbor != null) {
                        neighbor.setNeighborLoaded(-x, -z);
                        setNeighborLoaded(x, z);
                        neighbor.initLighting();
                    }
                }
            }
        }
    }
}
