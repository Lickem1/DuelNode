package tk.duelnode.gameserver.data.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.impl.FinishLogic;
import tk.duelnode.gameserver.data.game.impl.StartingLogic;
import tk.duelnode.gameserver.data.player.PlayerData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LocalGame {

    private final UUID ID;
    private final LinkedList<PlayerData> team1 = new LinkedList<>();
    private final LinkedList<PlayerData> team2 = new LinkedList<>();
    private final LinkedList<PlayerData> spectators = new LinkedList<>();
    private final LinkedList<PlayerData> playersAlive = new LinkedList<>();
    private final LinkedList<PlayerData> playersDead = new LinkedList<>();
    private LocalGameTick gameTick;
    private LocalGameType gameType;
    private Arena arena;
    private GlobalGame globalGame;

    public LocalGame(UUID id) {
        this.ID = id;
        this.gameTick = new StartingLogic();
        this.gameType = LocalGameType.DUEL; // todo send this over for global usage
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
                data.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',s));
            }
        }
    }

    public Location getPlayerSpawn(PlayerData p) {
        Location location;
        if(globalGame.getSpectators().contains(p.getUuid())) location = arena.getCenter().clone();
        else if(globalGame.getTeam1().contains(p.getUuid())) location = arena.getLoc1().clone();
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
        setGameTick(new FinishLogic(0));
    }

    public void handleDeath(PlayerData p) {
        playersAlive.remove(p);
        playersDead.add(p);

        for(PlayerData data : getAllPlayers()) {
            if(data != p) data.getPlayer().hidePlayer(GameServer.getInstance(), p.getPlayer());
        }
        p.setSpectator();
        // do more but later
    }
}
