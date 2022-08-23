package tk.duelnode.gameserver.data.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.impl.FinishLogic;
import tk.duelnode.gameserver.data.game.impl.GameLogic;
import tk.duelnode.gameserver.data.game.impl.StartingLogic;
import tk.duelnode.gameserver.data.player.PlayerData;

import java.util.*;

@Getter
@Setter
public class LocalGame {

    private final UUID ID;
    private final LinkedList<PlayerData> team1 = new LinkedList<>();
    private final LinkedList<PlayerData> team2 = new LinkedList<>();
    private final LinkedList<PlayerData> spectators = new LinkedList<>();
    private final LinkedList<PlayerData> playersAlive = new LinkedList<>();
    private final LinkedList<PlayerData> playersDead = new LinkedList<>();
    private final LinkedList<Block> blocksPlaced = new LinkedList<>();
    private LocalGameTick gameTick;
    private LocalGameType gameType;
    private Arena arena;
    private GlobalGame globalGame;
    private boolean cancelled = false;

    public LocalGame(UUID id) {
        this.ID = id;
        this.gameTick = new StartingLogic();
        this.gameType = LocalGameType.DUEL;
    }

    public boolean isInGame(PlayerData uuid) {
        return getAllPlayers().contains(uuid);
    }

    public List<PlayerData> getAllPlayers() {
        List<PlayerData> list = new ArrayList<>();
        list.addAll(team1);
        list.addAll(team2);
        list.addAll(spectators);
        return list;
    }

    public List<PlayerData> getPlayablePlayers() {
        List<PlayerData> list = new ArrayList<>();
        list.addAll(team1);
        list.addAll(team2);
        return list;
    }

    public void sendMessage(String... message) {
        for(PlayerData data : getAllPlayers()) {
            for(String s : message) {
                if(data!=null && data.getPlayer() != null)
                    data.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',s));
            }
        }
    }

    public Location getPlayerSpawn(PlayerData p) {
        Location location;
        if(globalGame.containsSpectator(p.getUuid())) location = arena.getCenter().clone();
        else if(globalGame.containsTeam1(p.getUuid())) location = arena.getLoc1().clone();
        else location = arena.getLoc2().clone();
        return location;
    }

    public boolean isSpectator(PlayerData p) {
        return spectators.contains(p) || playersDead.contains(p);
    }

    public boolean isAlive(PlayerData p) {
        return playersAlive.contains(p);
    }

    public boolean isReady() {
        int ready = getPlayablePlayers().size();
        return (ready >= gameType.getStartMinimum());
    }

    public void cancel() {
        cancelled = true;
        gameTick = new FinishLogic(this, 0);
    }

    public void handleDeath(PlayerData p) {
        if(gameTick != null) {
            if(spectators.contains(p)) {

                globalGame.removeSpectator(p.getUuid());
                globalGame.post(getID().toString(), GameServer.getInstance().getRedisManager());
                sendMessage("&b * &f&o" + p.getName() + "&7&o is no longer spectating");
                spectators.remove(p);
                return;
            }
            sendMessage(String.format(DeathMessages.getRandomMessage(), p.getName()));
            playersAlive.remove(p);
            playersDead.add(p);

            for(PlayerData data : getAllPlayers()) if(data != p) data.getPlayer().hidePlayer(GameServer.getInstance(), p.getPlayer());
            p.setSpectator();

            // ending code
            if(checkEnd()) {
                if(gameType == LocalGameType.DUEL) {
                    if(gameTick instanceof StartingLogic) setGameTick(new FinishLogic(this, 1)); // since 00:00 = cancelled game, we don't want to cancel the game
                    else if(gameTick instanceof GameLogic) setGameTick(new FinishLogic(this, ((GameLogic) gameTick).getDuration()));

                    PlayerData winner = playersAlive.get(0);
                    sendMessage(winner.getName() + " has won the match!");
                }
            }
        }
    }

    public boolean checkEnd() {
        if(gameType == LocalGameType.DUEL) {
            return playersAlive.size() == 1;
        }
        return false;

    }

    @Getter
    @AllArgsConstructor
    public enum DeathMessages {
        DEATH1("&b%s &fdied"),
        DEATH2("&b%s &fcommitted not feeling so well"),
        DEATH3("&b%s &fwas sent to the upside down"),
        DEATH4("&b%s &fjoined the land of the dead"),
        DEATH5("&b%s &ftook an L");

        private final String message;

        public static String getRandomMessage() {
            return DeathMessages.values()[new Random().nextInt(values().length)].getMessage();
        }
    }
}
