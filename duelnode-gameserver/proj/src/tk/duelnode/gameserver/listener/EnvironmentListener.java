package tk.duelnode.gameserver.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.data.player.PlayerData;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.PlayerDataManager;
import tk.duelnode.gameserver.manager.dynamic.DynamicListener;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class EnvironmentListener extends DynamicListener {

    @EventHandler
    public void spawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(e.getPlayer());
        e.setCancelled(true);
        if(data.getGame() == null) return;
        if(!data.getGame().isAlive(data)) return;


        data.getGame().getBlocksPlaced().add(e.getBlock());
        e.setCancelled(false);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(e.getPlayer());
        e.setCancelled(true);

        if(data.getGame() == null) return;
        if(!data.getGame().isAlive(data)) return;
        if(!data.getGame().getBlocksPlaced().contains(e.getBlock())) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed by players");
            return;
        }
        e.setCancelled(false);
        data.getGame().getBlocksPlaced().remove(e.getBlock());
    }

    @EventHandler
    public void weatherChange(WeatherChangeEvent e) {
        if(e.toWeatherState()) e.setCancelled(true);
    }

}
