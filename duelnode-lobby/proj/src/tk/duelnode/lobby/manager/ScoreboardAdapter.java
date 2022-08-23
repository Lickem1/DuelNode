package tk.duelnode.lobby.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.api.util.plasma.PlasmaAdapter;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.data.queue.Queue;
import tk.duelnode.lobby.data.queue.QueueManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoreboardAdapter implements PlasmaAdapter {


    @Override
    public String getTitle(Player player) {
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);
        if(data == null) return "";

        String newTitle = "";

        if (data.scoreboard_title_tick >= 9 * 5) {
            data.scoreboard_title_tick = 0;
            newTitle = "%s%s%sNODE &7(Lobby)";

        } else if (data.scoreboard_title_tick <= 5) {
            newTitle = "%s%sN%sODE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 2 * 5) {
            newTitle = "%sN%sO%sDE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 3 * 5) {
            newTitle = "%sNO%sD%sE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 4 * 5) {
            newTitle = "%sNOD%sE%s &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 5 * 5) {
            newTitle = "&b&lNODE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 6 * 5) {
            newTitle = "&f&lNODE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 7 * 5) {
            newTitle = "&b&lNODE &7(Lobby)";
        } else if (data.scoreboard_title_tick <= 8 * 5) {
            newTitle = "%s%s%sNODE &7(Lobby)";
        } else {
            newTitle = "%s%s%sNODE &7(Lobby)";

        }
        data.scoreboard_title_tick++;

        return String.format(newTitle, "&f&l", "&b&l", "&f&l");
    }

    @Override
    public List<String> getLines(Player player) {
        QueueManager queue = DynamicManager.get(QueueManager.class);
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);
        List<String> s = new ArrayList<>();

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat day = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat time = new SimpleDateFormat("h:mmaa");

        s.add("&7&m------------------");
        s.add("&8" + day.format(now) + " &7" + time.format(now).toLowerCase());
        s.add(" ");
        s.add("&fPlayers&7: &7"+ Plugin.getInstance().getFixedPlayerCount());
        s.add("&fQueueing&7:&7 " + queue.getCurrentQueuedPlayers());
        s.add("&fPing&7: &7" + player.spigot().getPing() + "ms");
        if(data.isInQueue()) {
            s.add("");
            s.add("&fQueueing &7(Default Kit)");
            s.add("&b *&f Time&7: &7"+ data.getCurrent_Queue().getTimeInQueue() + getDots(data.getCurrent_Queue()));
        }
        s.add("");
        if(player.isOp()) {
            s.add("&3Dev Mode &7(" + getOps() + ")");
            StringBuilder tps = new StringBuilder();

            for(double t : Bukkit.getServer().getTPS()) {
                String tpsString  = String.valueOf(t).substring(0, 4);
                String format = "";
                double d = Double.parseDouble(tpsString);

                if(d >= 17) format = "&a" + tpsString;
                else if(d<=16 && d >= 14) format = "&e" + tpsString;
                else format = "&c" + tpsString;
                tps.append(format).append("&f, ");
            }
            char c = ' ';
            tps.setCharAt(tps.length()-2, c);
            s.add(tps.toString());
            s.add("");
        }
        s.add("&bLickem#9444");
        s.add("&7&m------------------");

        return s;
    }

    private int getOps() {
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        players.removeIf(p ->!p.isOp());
        return players.size();
    }

    private String getDots(Queue q) {
        String test;
        if (q.queue_animation_dots >= 9 * 5) {
            q.queue_animation_dots = 0;
            test = "";
        } else if (q.queue_animation_dots <= 5) {
            test = ".";
        } else if (q.queue_animation_dots <= 2 * 5) {
            test = "..";
        } else if (q.queue_animation_dots <= 3 * 5) {
            test = "...";
        } else if (q.queue_animation_dots <= 4 * 5) {
            test = "....";
        }else if (q.queue_animation_dots <= 5 * 5) {
            test = "....";
        } else if (q.queue_animation_dots <= 6 * 5) {
            test = "...";
        }  else if (q.queue_animation_dots <= 7 * 5) {
            test = "..";
        }  else if (q.queue_animation_dots <= 8 * 5) {
            test = ".";
        } else {
            test = "";
        }
        q.queue_animation_dots++;
        return test;
    }
}
