package tk.duelnode.gameserver.manager.game;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.game.arena.ArenaState;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.api.game.data.GlobalGameState;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.impl.FinishLogic;
import tk.duelnode.gameserver.manager.ArenaManager;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Init(classType = ClassType.CONSTRUCT)
public class GameManager extends BukkitRunnable {

    private final ConcurrentHashMap<UUID, LocalGame> gameMap = new ConcurrentHashMap<>();

    public GameManager() {
        runTaskTimerAsynchronously(GameServer.getInstance(), 20, 20);
    }

    @Override
    public void run() {

        for(Iterator<LocalGame> games = gameMap.values().iterator(); games.hasNext();) {

            LocalGame game = games.next();
            if( game!= null && game.getGameTick() != null) {
                game.getGameTick().doTick(game);
            }
        }
    }

    public void createGame(GlobalGame globalGame) {
        LocalGame localGame = new LocalGame(globalGame.getGameID());
        ArenaManager arenaManager = DynamicManager.get(ArenaManager.class);
        Arena arena = arenaManager.getFreeArena();

        // global stuff
        globalGame.setArenaID(arena.getDisplayName());
        globalGame.setGameState(GlobalGameState.STARTING);
        globalGame.setGameServerLocation(GameServer.getInstance().getConfig().getString("server-location"));

        //local stuff
        localGame.setArena(arena);
        localGame.setGlobalGame(globalGame);
        arenaManager.setArenaState(arena, ArenaState.UNAVAILABLE);

        // finalize stuff
        System.out.println("[DUEL-SERVER] Game " + localGame.getID() + " created and is awaiting connection");
        globalGame.post(localGame.getID().toString(), GameServer.getInstance().getRedisManager());
        gameMap.put(localGame.getID(), localGame);
    }

    public void deleteGame(LocalGame localGame) {
        ArenaManager arenaManager = DynamicManager.get(ArenaManager.class);

        //arena stuff
        arenaManager.rollBackArena(localGame);
        arenaManager.setArenaState(localGame.getArena(), ArenaState.AVAILABLE);

        // finalize stuff
        localGame.getGlobalGame().delete(localGame.getID().toString(), GameServer.getInstance().getRedisManager());
        System.out.println("[DUEL-SERVER] Game " + localGame.getID() + " finished");
        gameMap.remove(localGame.getID());
    }

    public LocalGame getGame(UUID gameID) {
        return gameMap.get(gameID);
    }

    public LocalGame isInGame(UUID uuid) {
        for(LocalGame game : gameMap.values()) {
            if((!(game.getGameTick() instanceof FinishLogic)) && game.getGlobalGame().isInGame(uuid)) {
                return game;
            }
        }
        return null;
    }
}
