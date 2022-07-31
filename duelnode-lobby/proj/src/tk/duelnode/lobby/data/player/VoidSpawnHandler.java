package tk.duelnode.lobby.data.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class VoidSpawnHandler extends BukkitRunnable {

    {runTaskTimerAsynchronously(Plugin.getInstance(), 3*20, 3*20);}


    @Override
    public void run() {

        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.getLocation().getY() <= 0) {
                p.teleport(Plugin.getInstance().getSpawnLocation());
            }
        }
    }
}
