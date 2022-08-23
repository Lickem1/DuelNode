package tk.duelnode.gameserver.data.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.util.itembuilder.ItemBuilder;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private final UUID uuid;
    private Player player;
    private String name;
    private LocalGame game;
    public int scoreboard_title_tick = 0;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setSpectator() {
        player.setGameMode(GameMode.CREATIVE);
        player.setHealth(20D);
        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON, 1, 0).setName("&cStop Spectating").build());
        player.updateInventory();
    }
}
