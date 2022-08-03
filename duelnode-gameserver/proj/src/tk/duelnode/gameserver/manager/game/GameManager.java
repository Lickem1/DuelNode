package tk.duelnode.gameserver.manager.game;

import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.game.arena.ArenaState;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.manager.ArenaManager;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

import java.util.LinkedHashMap;
import java.util.UUID;

@Init(classType = ClassType.CONSTRUCT)
public class GameManager extends BukkitRunnable {

    private final LinkedHashMap<UUID, LocalGame> gameMap = new LinkedHashMap<>();

    public GameManager() {
        runTaskTimerAsynchronously(GameServer.getInstance(), 20, 20);
    }

    @Override
    public void run() {

        for(LocalGame games : gameMap.values()) {
            if(games.getGameTick() != null) {
                games.getGameTick().doTick(games);
            }
        }
    }

    public void createGame(GlobalGame globalGame) {
        LocalGame localGame = new LocalGame(globalGame.getGameID());
        ArenaManager arenaManager = DynamicManager.get(ArenaManager.class);
        Arena arena = arenaManager.getFreeArena();

        // global stuff
        globalGame.setArenaID(arena.getDisplayName() +"|" + arena.getID());

        //local stuff
        localGame.getTeam1().addAll(globalGame.getTeam1());
        localGame.getTeam2().addAll(globalGame.getTeam2());
        localGame.getSpectators().addAll(globalGame.getSpectators());
        localGame.setArena(arena);
        arenaManager.setArenaState(arena, ArenaState.UNAVAILABLE);

        // finalize stuff
        System.out.println("[DUELSERVER] Game " + localGame.getID() + " created and is awaiting connection");
        globalGame.post(globalGame.getGameID().toString(), DynamicManager.get(RedisManager.class));
        gameMap.put(localGame.getID(), localGame);
    }

    public LocalGame isInGame(UUID uuid) {
        for(LocalGame game : gameMap.values()) {
            if(game.isInGame(uuid)) {
                return game;
            } else System.out.println("No");
        }
        return null;
    }
}
