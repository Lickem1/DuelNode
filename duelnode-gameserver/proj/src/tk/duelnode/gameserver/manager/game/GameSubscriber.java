package tk.duelnode.gameserver.manager.game;

import org.bukkit.Bukkit;
import tk.duelnode.api.API;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.annotations.PostInit;

@PostInit(classType = ClassType.CONSTRUCT)
public class GameSubscriber {

    public GameSubscriber() {
        RedisManager redis = GameServer.getInstance().getRedisManager();

        redis.subscribe("dn/server/gameserver/" + Bukkit.getServer().getServerName(), ((channel, message) -> {

            System.out.println("[DUELSERVER] Message received, trying to initiate game creation stage....");
            GameManager manager = DynamicManager.get(GameManager.class);

            GlobalGame globalGame = API.getGson().fromJson(message, GlobalGame.class);
            manager.createGame(globalGame);


        }));

    }
}
