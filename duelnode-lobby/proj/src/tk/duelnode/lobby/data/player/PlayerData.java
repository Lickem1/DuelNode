package tk.duelnode.lobby.data.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    @Setter(AccessLevel.NONE)
    private final UUID UUID;
    private Player player;

    public PlayerData(UUID uuid) {
        this.UUID = uuid;
    }
}
