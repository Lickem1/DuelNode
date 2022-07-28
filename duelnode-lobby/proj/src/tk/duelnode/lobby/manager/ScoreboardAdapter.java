package tk.duelnode.lobby.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.duelnode.api.util.plasma.PlasmaAdapter;

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
        List<String> s = new ArrayList<>();

        s.add("&7&m------------------");
        s.add("&6Info &7(" + player.getName() + ")");
        s.add(" &6Ping&7: &f" + player.spigot().getPing() + "ms");
        s.add(" &6Balance&7: &a$&f0.0");
        s.add("");
        s.add("&6Queueing:&f 0");
        s.add("&6Players: &f"+ Bukkit.getServer().getOnlinePlayers().size());
        s.add("");
        s.add("&7&oContact Lickem#9444");
        s.add("&7&m------------------");

        return s;
    }
}
