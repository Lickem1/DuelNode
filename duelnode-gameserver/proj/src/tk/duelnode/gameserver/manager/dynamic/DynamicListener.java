package tk.duelnode.gameserver.manager.dynamic;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import tk.duelnode.gameserver.GameServer;

public class DynamicListener implements Listener {

    public DynamicListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, GameServer.getInstance());
    }
}
