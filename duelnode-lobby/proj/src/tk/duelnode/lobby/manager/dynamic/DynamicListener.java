package tk.duelnode.lobby.manager.dynamic;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import tk.duelnode.lobby.Plugin;

public class DynamicListener implements Listener {

    public DynamicListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Plugin.getInstance());
    }
}
