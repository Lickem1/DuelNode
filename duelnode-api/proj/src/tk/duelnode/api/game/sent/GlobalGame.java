package tk.duelnode.api.game.sent;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tk.duelnode.api.API;
import tk.duelnode.api.util.redis.RedisManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class GlobalGame {

    private final UUID gameID = UUID.randomUUID();
    private final List<UUID> team1 = new ArrayList<>();
    private final List<UUID> team2 = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final GlobalGameType gameType;
    @Setter
    private String arenaID;

    public GlobalGame(GlobalGameType gameType) {
        this.gameType = gameType;
    }

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

        list.removeIf(uuid -> Bukkit.getServer().getPlayer(uuid) == null);
        for (UUID uuid : list) {
            Player p = Bukkit.getServer().getPlayer(uuid);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public boolean isInGame(UUID uuid) {
        List<UUID> list = new ArrayList<>();
        list.addAll(team1);
        list.addAll(team2);
        list.addAll(spectators);
        return list.contains(uuid);
    }

    public boolean post(String gameID, RedisManager redis) {
        try {
            redis.getRedisConnection().async().set("dn/gamedata/" + gameID, API.getGson().toJson(this));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(String gameID, RedisManager redis) {
        try {
            redis.getRedisConnection().async().del("dn/gamedata/" + gameID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean message(String gameServer, RedisManager redis) {
        try {
            redis.getRedisConnection().async().publish("dn/server/gameserver/" + gameServer, API.getGson().toJson(this));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static GlobalGame getGameData(String gameServer, RedisManager redis) {
        try {
            String string = redis.getRedisConnection().async().get("dn/server/gameserver/" + gameServer).get();

            return API.getGson().fromJson(string, GlobalGame.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GlobalGame> getAllGameData(String gameServer, RedisManager redis) {
        List<GlobalGame> globalGameList = new ArrayList<>();
        try {
            List<String> string = redis.getRedisConnection().async().keys("dn/server/gameserver/*").get();

            for(String s : string) {
                GlobalGame game = API.getGson().fromJson(redis.getRedisConnection().async().get(s).get(), GlobalGame.class);
                globalGameList.add(game);
            }

            return globalGameList;
        } catch (Exception e) {
            return globalGameList;
        }
    }
}
