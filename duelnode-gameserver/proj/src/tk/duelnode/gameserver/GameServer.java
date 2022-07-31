package tk.duelnode.gameserver;

import io.lettuce.core.RedisClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import tk.duelnode.api.util.plasma.Plasma;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.gameserver.data.world.chunk.NMSChunk;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.ScoreboardAdapter;


@Getter
public class GameServer extends JavaPlugin {

    @Getter private static GameServer instance;

    private RedisManager redisManager;

    @Override
    public void onEnable() {
        instance = this;
        DynamicManager.init(this.getClassLoader());
        new Plasma(this, new ScoreboardAdapter());
        for(World worlds : Bukkit.getWorlds()) new NMSChunk(worlds);

        RedisClient client = RedisClient.create("redis://yourmom@" + "localhost" + ":" + "6379");
        this.redisManager = new RedisManager(client, "yourmom");

        if(!getDataFolder().exists()) getDataFolder().mkdirs();

    }

    @Override
    public void onDisable() {
        instance = null;

    }
}
