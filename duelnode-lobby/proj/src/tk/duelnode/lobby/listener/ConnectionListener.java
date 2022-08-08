package tk.duelnode.lobby.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.duelnode.api.API;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.PlayerDataManager;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class ConnectionListener extends DynamicListener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(p);
        data.setPlayer(e.getPlayer());
        API.getPacketInjector().injectHandler(e.getPlayer()); // packet injector


        p.teleport(Plugin.getInstance().getSpawnLocation());
        data.createLobbyPlayer();

        p.sendTitle(ChatColor.AQUA + "Node-Lobby " + ChatColor.GRAY + "(ALPHA)", ChatColor.GRAY + "Contact Lickem for bugs");

    }
}
