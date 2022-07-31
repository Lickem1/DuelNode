package tk.duelnode.api.util.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketInjector {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<PacketEventReference>> packetMap = new ConcurrentHashMap<>();

    public void injectHandler(Player player) {
        executor.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            Channel channel = playerConnection.networkManager.channel;
            ChannelPipeline pipeline = channel.pipeline();

            pipeline.addBefore("packet_handler", "dn-packethandler", new PacketHandler(player));
        });
    }

    public void ejectHandler(Player player) {
        executor.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            if (playerConnection != null && !playerConnection.isDisconnected()) { // check if connection is null
                Channel channel = playerConnection.networkManager.channel;
                ChannelPipeline pipeline = channel.pipeline();

                channel.eventLoop().execute(() -> {
                    if (pipeline.get("dn-packethandler") != null) pipeline.remove("whole-packethandler");
                });
            }
        });
    }

    public void add(Class<?> clazz, PacketEventReference reference) {
        List<PacketEventReference> list = list(clazz);
        list.add(reference);
    }

    private CopyOnWriteArrayList<PacketEventReference> list(Class<?> clazz) {
        return packetMap.computeIfAbsent(clazz,(c)->new CopyOnWriteArrayList<>());
    }

    public List<PacketEventReference> getPacketReference(Class<?> clazz) {
        return packetMap.get(clazz);
    }
}
