package tk.duelnode.gameserver.manager.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tk.duelnode.api.API;
import tk.duelnode.api.game.data.GameCondition;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

import java.util.regex.Pattern;

@Init(priority = 90, classType = ClassType.CONSTRUCT)
public class GameSubscriber {

    public GameSubscriber() {
        RedisManager redis = GameServer.getInstance().getRedisManager();

        redis.subscribe("dn/server/gameserver/" + Bukkit.getServer().getServerName(), ((channel, message) -> {
            String[] msg = message.split(Pattern.quote("|"));
            GameCondition condition = GameCondition.valueOf(msg[0]);
            GameManager manager = DynamicManager.get(GameManager.class);


            if(condition == GameCondition.CREATE) {
                System.out.println("[DUEL-SERVER] Message received, trying to initiate game creation stage....");
                GlobalGame globalGame = API.getGson().fromJson(msg[1], GlobalGame.class);
                manager.createGame(globalGame);
            }
            else if(condition == GameCondition.UPDATE) {
                GlobalGame globalGame = API.getGson().fromJson(msg[1], GlobalGame.class);
                LocalGame localGame = manager.getGame(globalGame.getGameID());
                localGame.setGlobalGame(globalGame);
            }

        }));

        redis.subscribe("dn/server/gameserver-chat", ((channel, message) -> {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));

        }));

    }
}
