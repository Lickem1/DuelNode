package tk.duelnode.gameserver.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import tk.duelnode.api.API;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.util.menu.MenuHolder;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.player.PlayerData;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.PlayerDataManager;
import tk.duelnode.gameserver.manager.dynamic.DynamicListener;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;
import tk.duelnode.gameserver.manager.game.GameManager;

@Init(classType = ClassType.CONSTRUCT)
public class PlayerListener extends DynamicListener {

    private final PlayerDataManager playerDataManager = DynamicManager.get(PlayerDataManager.class);

    @EventHandler
    public void login(AsyncPlayerPreLoginEvent e) {

        playerDataManager.createData(e.getUniqueId());
        PlayerData data = playerDataManager.getProfile(e.getUniqueId());
        GameManager gameManager = DynamicManager.get(GameManager.class);

        LocalGame game = gameManager.isInGame(data.getUuid());
        if(gameManager.isInGame(data.getUuid()) != null) {
            data.setGame(game);

            GlobalGame globalGame = game.getGlobalGame();

            if(globalGame.getTeam1().contains(data.getUuid())){
                game.getPlayersAlive().add(data);
                game.getTeam1().add(data);
            }
            else if(globalGame.getTeam2().contains(data.getUuid())) {
                game.getPlayersAlive().add(data);
                game.getTeam2().add(data);
            }
            else if(globalGame.getSpectators().contains(data.getUuid())) game.getSpectators().add(data);

        } else System.out.println("null"); // kick player
    }

    @EventHandler
    public void spawnLocation(PlayerSpawnLocationEvent e) {
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(e.getPlayer());
        if(data.getGame() != null) {
            e.setSpawnLocation(data.getGame().getPlayerSpawn(data));
        } else {
            e.setSpawnLocation(new Location(Bukkit.getServer().getWorld("world"), 0, 70, 0));
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(e.getPlayer());
        data.setPlayer(e.getPlayer());
        data.setName(e.getPlayer().getName());
        e.setJoinMessage(null);
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        PlayerData data = playerDataManager.getProfile(e.getPlayer());

        if(data.getGame() != null) data.getGame().handleDeath(data);

        playerDataManager.delete(e.getPlayer());
        API.getPacketInjector().ejectHandler(e.getPlayer()); // packet injector

    }

    @EventHandler
    public void e(InventoryClickEvent e) {
        Inventory inventory = e.getView().getTopInventory();

        if (inventory == null || (!(inventory.getHolder() instanceof MenuHolder))) return;
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || (!e.getCurrentItem().hasItemMeta())) {
            return;
        }

        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(e.getCurrentItem());
        String var = itemNms.getTag().getString("data");

        if(var != null && !var.isEmpty()) {
            String[] data = var.split(",");
            Location location = new Location(Bukkit.getServer().getWorld(data[4]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), 0, 0);
            player.sendMessage("§7Teleportation to arena " + data[0]);
            player.teleport(location);
        }
    }
}
