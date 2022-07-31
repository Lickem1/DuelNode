package tk.duelnode.gameserver.data.world.chunk;

import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkLoader;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.lang.reflect.Field;

public class NMSChunk {

    public NMSChunk(World world) {

        try {
            Field chunkProvider = net.minecraft.server.v1_12_R1.World.class.getDeclaredField("chunkProvider");
            chunkProvider.setAccessible(true);

            Field chunkLoader = ChunkProviderServer.class.getDeclaredField("chunkLoader");
            chunkLoader.setAccessible(true);

            WorldServer nmsWorld = ((CraftWorld) world).getHandle();
            ChunkProviderServer provider = nmsWorld.getChunkProviderServer();

            AirChunkProvider wrapper = new AirChunkProvider(nmsWorld, (IChunkLoader) chunkLoader.get(provider), provider.chunkGenerator);
            chunkProvider.set(nmsWorld, wrapper);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
