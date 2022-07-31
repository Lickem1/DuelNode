package tk.duelnode.gameserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.duelnode.api.util.plasma.PlasmaAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements PlasmaAdapter {

    private int tick = 0;

    @Override
    public String getTitle(Player player) {
        String newTitle = "";

        if (tick >= 9 * 5) {
            tick = 0;
            newTitle = "%s%s%sDUEL &7* " + Bukkit.getServerName();

        } else if (tick <= 5) {
            newTitle = "%s%sD%sUEL &7* " + Bukkit.getServerName();
        } else if (tick <= 2 * 5) {
            newTitle = "%sD%sU%sEL &7* " + Bukkit.getServerName();
        } else if (tick <= 3 * 5) {
            newTitle = "%sDU%sE%sL &7* " + Bukkit.getServerName();
        } else if (tick <= 4 * 5) {
            newTitle = "%sDUE%sL%s &7* " + Bukkit.getServerName();
        } else if (tick <= 5 * 5) {
            newTitle = "&e&lDUEL &7* " + Bukkit.getServerName();
        } else if (tick <= 6 * 5) {
            newTitle = "&f&lDUEL &7* " + Bukkit.getServerName();
        } else if (tick <= 7 * 5) {
            newTitle = "&e&lDUEL &7* " + Bukkit.getServerName();
        } else if (tick <= 8 * 5) {
            newTitle = "%s%s%sDUEL &7* " + Bukkit.getServerName();
        } else {
            newTitle = "%s%s%sDUEL &7* " + Bukkit.getServerName();

        }
        tick++;

        return String.format(newTitle, "&f&l", "&e&l", "&f&l");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> s = new ArrayList<>();
        s.add("&7&m------------------");
        s.add("Debug");
        s.add("&7&m------------------");

        return s;
    }
}
