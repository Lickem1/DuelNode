package tk.duelnode.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import tk.duelnode.api.util.plasma.Plasma;
import tk.duelnode.lobby.data.world.chunk.NMSChunk;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.ScoreboardAdapter;
import tk.duelnode.lobby.util.WorldEditUtil;

import java.io.File;

@Getter
public class Plugin extends JavaPlugin {

    @Getter private static Plugin instance;
    private final Gson gson = new GsonBuilder().create();

    private Location spawnLocation;

    public void onEnable() {
        instance = this;
        new Plasma(this, new ScoreboardAdapter());
        DynamicManager.init(this.getClassLoader());


        for(World worlds : getServer().getWorlds()) new NMSChunk(worlds);

        try {
            World world =Bukkit.getServer().getWorld("world");
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
