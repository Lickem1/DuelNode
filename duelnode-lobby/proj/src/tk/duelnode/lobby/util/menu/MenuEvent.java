package tk.duelnode.lobby.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuEvent {
    void e(Player player, ClickType type, int slot);
}
