package tk.duelnode.api.game.sent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tk.duelnode.api.API;
import tk.duelnode.api.util.redis.RedisManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GameData {

    private final List<UUID> team1 = new ArrayList<>();
    private final List<UUID> team2 = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final GameType gameType;

    public void addTeam1(UUID uuid) {
        team1.add(uuid);
    }

    public void addTeam2(UUID uuid) {
        team2.add(uuid);
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void sendMessage(String message) {
        List<UUID> list = new ArrayList<>();
        list.addAll(team1);
        list.addAll(team2);
        list.addAll(spectators);

        for(UUID uuid : list) {
            Player p = Bukkit.getServer().getPlayer(uuid);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public boolean send(String gameServer, RedisManager redis) {
        try {
            redis.getRedisConnection().async().publish("dn/gameserver/" + gameServer, API.getGson().toJson(this));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
