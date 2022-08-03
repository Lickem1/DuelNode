package tk.duelnode.lobby;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import io.lettuce.core.RedisClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import tk.duelnode.api.util.menu.MenuListener;
import tk.duelnode.api.util.plasma.Plasma;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.lobby.data.world.chunk.NMSChunk;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.ScoreboardAdapter;
import tk.duelnode.lobby.util.WorldEditUtil;

import java.io.File;

@Getter
public class Plugin extends JavaPlugin {

    @Getter private static Plugin instance;
    private RedisManager redisManager;

    private Location spawnLocation;

    public void onEnable() {
        instance = this;
        new Plasma(this, new ScoreboardAdapter());
        new MenuListener(this);
        DynamicManager.init(this.getClassLoader());


        for(World worlds : getServer().getWorlds()) new NMSChunk(worlds);

        try {
            World world = Bukkit.getServer().getWorld("world");
            File file = new File(Plugin.getInstance().getDataFolder(), "spawn.schematic");
            WorldEditUtil worldEditUtil = DynamicManager.get(WorldEditUtil.class);

            Clipboard clipboard = worldEditUtil.load(file);

            worldEditUtil.paste(clipboard, new Vector(0, 70, 0), world);


        } catch (Exception e) {
            e.printStackTrace();
        }

        spawnLocation = new Location(Bukkit.getServer().getWorld("world"), 0.500, 71, 0.500, -90, 0);

    }

    public void onDisable() {
        instance = null;
    }
}
