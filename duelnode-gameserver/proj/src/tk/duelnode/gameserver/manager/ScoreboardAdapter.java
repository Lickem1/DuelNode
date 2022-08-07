package tk.duelnode.gameserver.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.duelnode.api.util.plasma.PlasmaAdapter;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameType;
import tk.duelnode.gameserver.data.game.impl.FinishLogic;
import tk.duelnode.gameserver.data.game.impl.GameLogic;
import tk.duelnode.gameserver.data.game.impl.StartingLogic;
import tk.duelnode.gameserver.data.player.PlayerData;

import java.util.ArrayList;
import java.util.Arrays;
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
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);
        List<String> s = new ArrayList<>();
        s.add("&7&m------------------");
        if(data.getGame() != null) {
            LocalGame game = data.getGame();

            if(game.getGameTick() instanceof StartingLogic) {
                if(game.isReady()) {
                    s.add("Starting...");
                } else {
                    s.add("&fWaiting for player(s)...");
                }
            } else if(game.getGameTick() instanceof GameLogic) {

                if(game.getGameType() == LocalGameType.DUEL) {
                    PlayerData p1 = game.getTeam1().get(0);
                    PlayerData p2 = game.getTeam2().get(0);

                    String[] format = {
                            "&cOpponent: &f" + (game.getTeam1().contains(data) ? p2.getName() : p1.getName()),
                            "&eDuration: &f" + ((GameLogic) game.getGameTick()).getGameTime(),
                            "&bArena: &f" + game.getArena().getDisplayName(),
                            " ",
                            "&fTheir Ping: &a" + (game.getTeam1().contains(data) ? p2.getPlayer().spigot().getPing() : p1.getPlayer().spigot().getPing()) + "ms",
                            "&fYour Ping: &a" + player.spigot().getPing() + "ms",
                    };

                    s.addAll(Arrays.asList(format));
                }
            } else if(game.getGameTick() instanceof FinishLogic) {

                if(game.getGameType() == LocalGameType.DUEL) {
                    PlayerData p1 = game.getPlayersAlive().get(0);

                    if(((FinishLogic) game.getGameTick()).getFinalTime().equalsIgnoreCase("00:00")) {
                        s.add("Game cancelled");
                    } else {

                        String[] format = {
                                "&aWinner: &f" + p1.getName(),
                                "&eDuration: &f" + ((FinishLogic) game.getGameTick()).getFinalTime()
                        };

                        s.addAll(Arrays.asList(format));

                    }
                }
            }

            else {
                s.add("Awaiting connection...");
            }



        } else {
            s.add(" ");
            s.add("&bDEV Mode");
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
            s.add("&f * &7&lTPS");
            s.add("    * " + tps.toString());
        }
        s.add("");
        s.add("&7&m------------------");

        return s;
    }
}
