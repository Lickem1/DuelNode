package tk.duelnode.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class EnvironmentListener extends DynamicListener {


    @EventHandler
    public void spawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void root(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void bBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void bPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void weatherChange(WeatherChangeEvent e) {
        if(e.toWeatherState()) e.setCancelled(true);
    }
}
