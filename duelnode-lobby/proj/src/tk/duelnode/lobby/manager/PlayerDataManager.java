package tk.duelnode.lobby.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.data.packet.PacketInjector;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.manager.dynamic.annotations.PreInit;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

@PreInit
public class PlayerDataManager extends DynamicListener {

    public PlayerDataManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Plugin.getInstance());
    }

    private final Map<UUID, PlayerData> playerMaps = new WeakHashMap<>();

    public PlayerData getProfile(UUID uuid) {
        return playerMaps.get(uuid);
    }

    public PlayerData getProfile(Player p) {
        return playerMaps.get(p.getUniqueId());
    }

    public void createData(UUID uuid) {
        playerMaps.remove(uuid);
        playerMaps.put(uuid, new PlayerData(uuid));
    }

    public void createData(Player p) {
        playerMaps.put(p.getUniqueId(), new PlayerData(p.getUniqueId()));
    }

    public void delete(UUID uuid) {
        playerMaps.remove(uuid);
    }

    public void delete(Player p) {
        playerMaps.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(AsyncPlayerPreLoginEvent e) {
        createData(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void leave(PlayerQuitEvent e) {
        delete(e.getPlayer());

        DynamicManager.get(PacketInjector.class).ejectHandler(e.getPlayer()); // packet injector

    }
}
