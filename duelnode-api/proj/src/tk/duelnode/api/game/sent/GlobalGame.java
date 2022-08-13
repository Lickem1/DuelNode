package tk.duelnode.api.game.sent;

import lombok.Getter;
import lombok.Setter;
import tk.duelnode.api.API;
import tk.duelnode.api.util.redis.RedisManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class GlobalGame {

    private final UUID gameID = UUID.randomUUID();
    private final List<GlobalGamePlayer> team1 = new ArrayList<>();
    private final List<GlobalGamePlayer> team2 = new ArrayList<>();
    private final List<GlobalGamePlayer> spectators = new ArrayList<>();
    private final GlobalGameType gameType;
    @Setter
    private String arenaID;
    @Setter
    private String gameServer;

    public GlobalGame(GlobalGameType gameType) {
        this.gameType = gameType;
    }

    public void addTeam1(GlobalGamePlayer format) {
        team1.add(format);
    }

    public void addTeam2(GlobalGamePlayer format) {
        team2.add(format);
    }

    public void addSpectator(GlobalGamePlayer format) {
        spectators.add(format);
    }

    public boolean isInGame(UUID gamePlayer) {
        List<GlobalGamePlayer> gamePlayers = new ArrayList<>();
        gamePlayers.addAll(team1);
        gamePlayers.addAll(team2);
        gamePlayers.addAll(spectators);

        int entries = 0;
        for(GlobalGamePlayer gp : gamePlayers) if(gp.getUuid().equals(gamePlayer)) entries++;

        return (entries == 1);
    }

    public boolean containsTeam1(UUID uuid) {
        int entries = 0;
        for(GlobalGamePlayer gp : team1) if(gp.getUuid().equals(uuid)) entries++;
        return (entries == 1);
    }

    public boolean containsTeam2(UUID uuid) {
        int entries = 0;
        for(GlobalGamePlayer gp : team2) if(gp.getUuid().equals(uuid)) entries++;
        return (entries == 1);
    }

    public boolean containsSpectator(UUID uuid) {
        int entries = 0;
        for(GlobalGamePlayer gp : spectators) if(gp.getUuid().equals(uuid)) entries++;
        return (entries == 1);
    }

    public void removeSpectator(UUID uuid) {
        GlobalGamePlayer g = null;
        for(GlobalGamePlayer gp : spectators) if(gp.getUuid().equals(uuid)) g = gp;
        spectators.remove(g);
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

    public boolean message(GameCondition condition, RedisManager redis) {
        try {
            redis.getRedisConnection().async().publish("dn/server/gameserver/" + gameServer, condition.toString() + "|" +API.getGson().toJson(this));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static GlobalGame getGameData(String gameData, RedisManager redis) {
        try {
            String string = redis.getRedisConnection().async().get("dn/gamedata/" + gameData).get();

            return API.getGson().fromJson(string, GlobalGame.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GlobalGame> getAllGameData(RedisManager redis) {
        List<GlobalGame> globalGameList = new ArrayList<>();
        try {
            List<String> string = redis.getRedisConnection().async().keys("dn/gamedata/*").get();

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
