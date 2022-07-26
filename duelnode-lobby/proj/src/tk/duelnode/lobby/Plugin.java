package tk.duelnode.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Plugin extends JavaPlugin {

    @Getter private static Plugin instance;
    private final Gson gson = new GsonBuilder().create();

    public void onEnable() {
        instance = this;
    }

    public void onDisable() {
        instance = null;
    }
}
