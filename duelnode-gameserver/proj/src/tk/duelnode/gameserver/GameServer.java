package tk.duelnode.gameserver;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lettuce.core.RedisClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import tk.duelnode.api.util.menu.MenuListener;
import tk.duelnode.api.util.plasma.Plasma;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.gameserver.data.world.chunk.NMSChunk;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.ScoreboardAdapter;


@Getter
public class GameServer extends JavaPlugin implements PluginMessageListener {

    @Getter private static GameServer instance;

    private RedisManager redisManager;

    @Override
    public void onEnable() {
        instance = this;
        new Plasma(this, new ScoreboardAdapter());
        new MenuListener(this);
        for(World worlds : Bukkit.getWorlds()) new NMSChunk(worlds);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        RedisClient client = RedisClient.create("redis://yourmom@" + "localhost" + ":" + "6379");
        this.redisManager = new RedisManager(client, "yourmom");

        if(!getDataFolder().exists()) getDataFolder().mkdirs();


        DynamicManager.init(this.getClassLoader());


    }

    @Override
    public void onDisable() {
        instance = null;

    }

    public void sendToLobby(Player player) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF("lobby"); // todo change this
        player.sendPluginMessage(this, "BungeeCord", dataOutput.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }
}
