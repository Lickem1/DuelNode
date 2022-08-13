package tk.duelnode.lobby.data.queue;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.game.sent.GameCondition;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.game.sent.GlobalGamePlayer;
import tk.duelnode.api.game.sent.GlobalGameType;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.PlayerDataManager;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

import java.util.LinkedList;

@Init(classType = ClassType.CONSTRUCT)
public class QueueManager extends BukkitRunnable {

    private final LinkedList<PlayerData> queue = new LinkedList<>();

    @Getter
    private int currentQueuedPlayers;

    public QueueManager() {
        runTaskTimerAsynchronously(Plugin.getInstance(), 20, 20);
    }

    public void addToQueue(PlayerData data) {
        queue.add(data);
        data.setCurrent_Queue(new Queue());
    }

    public void removeFromQueue(PlayerData data) {
        queue.remove(data);
        data.setCurrent_Queue(null);
    }

    @Override
    public void run() {

        if(queue.size() >= 2) {
            PlayerData data = queue.get(0);
            PlayerData data2 = queue.get(1);

            GlobalGame gD = new GlobalGame(GlobalGameType.DUEL);
            gD.setGameServer("na-mini-01");
            gD.addTeam1(new GlobalGamePlayer(data.getPlayer().getName(), data.getPlayer().getUniqueId()));
            gD.addTeam2(new GlobalGamePlayer(data2.getPlayer().getName(), data2.getPlayer().getUniqueId()));

            data.createLobbyPlayer();
            data2.createLobbyPlayer();
            data.getPlayer().getInventory().clear();
            data2.getPlayer().getInventory().clear();

            data.getPlayer().sendMessage(ChatColor.GRAY + "Match found, please allow a few seconds to commence...");
            data2.getPlayer().sendMessage(ChatColor.GRAY + "Match found, please allow a few seconds to commence...");
            //gD.sendMessage("&cUnable to start match");

            gD.message(GameCondition.CREATE, Plugin.getInstance().getRedisManager());

            // todo send to gameserver then send players to that server
            Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Plugin.getInstance().sendToGameServer(gD.getGameServer(), data.getPlayer());
                    Plugin.getInstance().sendToGameServer(gD.getGameServer(), data2.getPlayer());
                }
            }, 2*20L);
        }

        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            int current = 0;
            PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);

            if(data != null && data.getCurrent_Queue() != null) {
                current++;

                if(data.getCurrent_Queue().getQueueTimer() == 59){
                    removeFromQueue(data);
                    player.sendMessage(ChatColor.RED + "» " + ChatColor.GRAY + "You have been removed from queue!");
                    data.createLobbyPlayer();
                }
                else data.getCurrent_Queue().increment();
            }
            currentQueuedPlayers = current;
        }
    }
}
