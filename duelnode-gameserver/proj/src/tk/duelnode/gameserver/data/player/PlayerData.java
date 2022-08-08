package tk.duelnode.gameserver.data.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import tk.duelnode.gameserver.data.game.LocalGame;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private final UUID uuid;
    private Player player;
    private String name;
    private LocalGame game;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setSpectator() {
        player.setGameMode(GameMode.CREATIVE);
        player.setHealth(20D);

    }
}
