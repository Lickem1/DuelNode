package tk.duelnode.lobby.data.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.lobby.data.queue.Queue;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    @Setter(AccessLevel.NONE)
    private final UUID UUID;
    private Player player;
    private Queue current_Queue;

    public PlayerData(UUID uuid) {
        this.UUID = uuid;
    }

    public void createLobbyPlayer() {
        if(player == null) return;

        ItemStack queue = new ItemBuilder(Material.DIAMOND_SWORD, 1,0).setName("&7» &bClick to join queue &7«").build();
        ItemStack info = new ItemBuilder(Material.BOOK, 1,0).setName("&7» &bInfo &7«").build();
        ItemStack disconnect = new ItemBuilder(Material.RED_ROSE, 1,1).setName("&7» &cDisconnect &7«").build();

        player.getInventory().setItem(1, info);
        player.getInventory().setItem(4, queue);
        player.getInventory().setItem(7, disconnect);
        player.updateInventory();

    }

    public void queueItems() {
        if(player == null) return;
        ItemStack leaveQueue = new ItemBuilder(Material.REDSTONE, 1, 0).setName("&7» &cClick to leave queue &7«").build();
        player.getInventory().setItem(4, leaveQueue);
        player.updateInventory();
    }

    public boolean isInQueue() {
        return (current_Queue != null);
    }
}
