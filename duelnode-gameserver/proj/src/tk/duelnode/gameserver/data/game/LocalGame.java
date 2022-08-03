package tk.duelnode.gameserver.data.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.gameserver.data.game.impl.StartingTick;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LocalGame {

    private final UUID ID;
    private final LinkedList<UUID> team1 = new LinkedList<>();
    private final LinkedList<UUID> team2 = new LinkedList<>();
    private final LinkedList<UUID> spectators = new LinkedList<>();
    private LocalGameTick gameTick;
    private Arena arena;

    public LocalGame(UUID id) {
        this.ID = id;
        this.gameTick = new StartingTick();
    }

    public boolean isInGame(UUID uuid) {
        return getAllPlayers().contains(uuid);
    }

    public List<UUID> getAllPlayers() {
        List<UUID> list = new ArrayList<>();
        list.addAll(team1);
        list.addAll(team2);
        list.addAll(spectators);
        return list;
    }

    public Location getPlayerSpawn(Player p) {
        Location location;
        if(spectators.contains(p.getUniqueId())) location = arena.getCenter().clone();
        else if(team1.contains(p.getUniqueId())) location = arena.getLoc1().clone();
        else location = arena.getLoc2().clone();
        return location;
    }

    public boolean isReady() {
        int ready = 0;
        for(UUID uuid : getAllPlayers()) {
            Player player = Bukkit.getServer().getPlayer(uuid);
            if(player == null ) ready++;
        }
        return (ready == 0);
    }
}
