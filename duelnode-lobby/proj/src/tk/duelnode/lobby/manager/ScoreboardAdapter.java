package tk.duelnode.lobby.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.duelnode.api.util.plasma.PlasmaAdapter;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.data.queue.QueueManager;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements PlasmaAdapter {

    private int tick = 0;
    private int test = 0;

    @Override
    public String getTitle(Player player) {
        String newTitle = "";

        if (tick >= 9 * 5) {
            tick = 0;
            newTitle = "%s%s%sNODE &7(Lobby)";

        } else if (tick <= 5) {
            newTitle = "%s%sN%sODE &7(Lobby)";
        } else if (tick <= 2 * 5) {
            newTitle = "%sN%sO%sDE &7(Lobby)";
        } else if (tick <= 3 * 5) {
            newTitle = "%sNO%sD%sE &7(Lobby)";
        } else if (tick <= 4 * 5) {
            newTitle = "%sNOD%sE%s &7(Lobby)";
        } else if (tick <= 5 * 5) {
            newTitle = "&e&lNODE &7(Lobby)";
        } else if (tick <= 6 * 5) {
            newTitle = "&f&lNODE &7(Lobby)";
        } else if (tick <= 7 * 5) {
            newTitle = "&e&lNODE &7(Lobby)";
        } else if (tick <= 8 * 5) {
            newTitle = "%s%s%sNODE &7(Lobby)";
        } else {
            newTitle = "%s%s%sNODE &7(Lobby)";

        }
        tick++;

        return String.format(newTitle, "&f&l", "&e&l", "&f&l");
    }

    @Override
    public List<String> getLines(Player player) {
        QueueManager queue = DynamicManager.get(QueueManager.class);
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);
        List<String> s = new ArrayList<>();

        s.add("&7&m------------------");
        s.add("&eInfo &7(" + player.getName() + ")");
        s.add("&f *&6 Ping&7: &f" + player.spigot().getPing() + "ms");
        s.add("&f *&6 Balance&7: &a$&f0.0");
        s.add("");
        if(data.isInQueue()) {
            s.add("&eQueueing &7(Default Kit)");
            s.add("&f *&6 Time: &f"+ data.getCurrent_Queue().getTimeInQueue());
        } else {
            s.add("&6Queueing:&f " + queue.getCurrentQueuedPlayers());
            s.add("&6Players: &f"+ Bukkit.getServer().getOnlinePlayers().size());
        }
        s.add("");
        s.add("&7&oContact Lickem#9444");
        s.add("&7&m------------------");

        return s;
    }
}
