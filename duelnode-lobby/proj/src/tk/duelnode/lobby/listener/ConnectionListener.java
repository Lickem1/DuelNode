package tk.duelnode.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class ConnectionListener extends DynamicListener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Location spawn = new Location(Bukkit.getWorld("world"), 0, 71, 0, -90, 0);
        p.teleport(spawn);
    }
}
