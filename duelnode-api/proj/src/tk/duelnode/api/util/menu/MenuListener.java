package tk.duelnode.api.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class MenuListener implements Listener {

    public MenuListener(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void e(InventoryClickEvent e) {
        Inventory inventory = e.getView().getTopInventory();

        if (inventory == null || (!(inventory.getHolder() instanceof MenuHolder))) return;
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || (!e.getCurrentItem().hasItemMeta())) {
            return;
        }
        MenuHolder holder = (MenuHolder) inventory.getHolder();
        MenuEvent menuEvent = holder.getEvent(e.getSlot());
        if (menuEvent == null) return;
        try {
            menuEvent.e(player, e.getClick(), e.getSlot());
        } catch (Exception io) {
            player.closeInventory();
            player.sendMessage("§cAn Error occurred.");
            io.printStackTrace();
        }
    }
}
