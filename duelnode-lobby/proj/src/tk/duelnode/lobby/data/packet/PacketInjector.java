package tk.duelnode.lobby.data.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Init(classType = ClassType.CONSTRUCT)
public class PacketInjector {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void injectHandler(Player player) {
        executor.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            Channel channel = playerConnection.networkManager.channel;
            ChannelPipeline pipeline = channel.pipeline();

            pipeline.addBefore("packet_handler", "whole-packethandler", new PacketHandler(player));
        });
    }

    public void ejectHandler(Player player) {
        executor.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            if (playerConnection != null && !playerConnection.isDisconnected()) { // check if connection is null
                Channel channel = playerConnection.networkManager.channel;
                ChannelPipeline pipeline = channel.pipeline();

                channel.eventLoop().execute(() -> {
                    if (pipeline.get("whole-packethandler") != null) pipeline.remove("whole-packethandler");
                });
            }
        });
    }
}
