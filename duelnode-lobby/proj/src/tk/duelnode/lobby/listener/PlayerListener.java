package tk.duelnode.lobby.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.data.menu.info.InfoMenu;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.data.queue.QueueManager;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.PlayerDataManager;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class PlayerListener extends DynamicListener {

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventory(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();
        if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK || e.getItem() == null || e.getItem().getType() == Material.AIR)
            return;
        Player p = e.getPlayer();
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(p);
        QueueManager queueManager = DynamicManager.get(QueueManager.class);

        switch (e.getItem().getType()) {

            case DIAMOND_SWORD:

                //if(Bukkit.getServer().getOnlinePlayers().size() >= 2) {
                //    p.sendMessage(ChatColor.GREEN + "» " + ChatColor.GRAY + "You have joined the queue, please allow a few seconds for matchmaking!");
                //    data.queueItems();
                //} else p.sendMessage(ChatColor.RED + "» " + ChatColor.GRAY + "Unable to join queue when no other players are online!");

                p.sendMessage(ChatColor.GREEN + "» " + ChatColor.GRAY + "You have joined the queue, please allow a few seconds for matchmaking!");
                data.queueItems();
                queueManager.addToQueue(data);

                break;

            case RED_ROSE:
                p.kickPlayer("Disconnected");
                break;

            case BOOK:
                DynamicManager.get(InfoMenu.class).open(p);
                break;

            case REDSTONE:
                p.sendMessage(ChatColor.RED + "» " + ChatColor.GRAY + "You have left the queue!");
                data.createLobbyPlayer();
                queueManager.removeFromQueue(data);
                break;

            default: break;
        }

    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String format = ChatColor.GRAY + "[%s"+ ChatColor.GRAY + "] %s" + p.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage();

        if(p.getName().equalsIgnoreCase("Lickem")) format = String.format(format, ChatColor.YELLOW + "Dev", ChatColor.GOLD);
        else format = String.format(format, ChatColor.WHITE + "Member", ChatColor.GRAY);
        e.setFormat(format);
    }
}
