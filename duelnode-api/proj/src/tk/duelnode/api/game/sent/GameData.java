package tk.duelnode.api.game.sent;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    private final GameType gameType;

    public boolean send(String gameServer, RedisManager redis) {
        try {
            redis.getRedisConnection().async().publish("dn/gameserver/" + gameServer, API.getGson().toJson(this));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
