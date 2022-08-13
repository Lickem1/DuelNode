package tk.duelnode.gameserver.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import tk.duelnode.api.API;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.util.menu.MenuHolder;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.impl.GameLogic;
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

        if(gameManager.isInGame(data.getUuid()) != null) {
            LocalGame game = gameManager.isInGame(data.getUuid());
            data.setGame(game);

            GlobalGame globalGame = game.getGlobalGame();

            if(globalGame.containsTeam1(data.getUuid())) {
                game.getPlayersAlive().add(data);
                game.getTeam1().add(data);
            }
            else if(globalGame.containsTeam2(data.getUuid())) {
                game.getPlayersAlive().add(data);
                game.getTeam2().add(data);
            }
            else if(globalGame.containsSpectator(data.getUuid())) game.getSpectators().add(data);

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
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerData data = playerDataManager.getProfile(p);
        e.setDeathMessage(null);

        if(data.getGame() != null) {
            data.getGame().handleDeath(data);
            e.getDrops().clear();
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerData data = playerDataManager.getProfile(p);
            if(data.getGame() != null && (!(data.getGame().getGameTick() instanceof GameLogic)) && data.getGame().isSpectator(data)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            PlayerData data = playerDataManager.getProfile(p);
            if(data.getGame() != null && data.getGame().isSpectator(data)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        PlayerData data = playerDataManager.getProfile(p);

        if(data != null && data.getGame() != null && data.getGame().getGameTick() instanceof GameLogic) {
            LocalGame game = data.getGame();
            if(!game.isAlive(data) || game.isSpectator(data)) {
                e.setCancelled(true);
            }
        } else e.setCancelled(true);
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        PlayerData data = playerDataManager.getProfile(p);

        if(data != null && data.getGame() != null) {
            LocalGame game = data.getGame();
            if(game.isSpectator(data)) e.setCancelled(true);

        } else e.setCancelled(true);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(p);
        data.setPlayer(p);
        data.setName(p.getName());
        e.setJoinMessage(null);

        if(data.getGame() != null && data.getGame().isSpectator(data)) {
            LocalGame game = data.getGame();
            game.sendMessage("&b * &f&o" + e.getPlayer().getName() + "&7&o is now spectating your game");

            data.setSpectator();
            for(PlayerData players : game.getAllPlayers()) {
                if(players != data) {
                    players.getPlayer().hidePlayer(GameServer.getInstance(), p);
                }
            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        e.setQuitMessage(null);
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

    @EventHandler
    public void inventory(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        PlayerData data = playerDataManager.getProfile(p);

        if(data.getGame() != null && data.getGame().isSpectator(data)) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();
        if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK || e.getItem() == null || e.getItem().getType() == Material.AIR)
            return;
        Player p = e.getPlayer();
        PlayerData data = playerDataManager.getProfile(p);

        if(data.getGame() != null && data.getGame().isSpectator(data)) {
            if(e.getItem().getType() == Material.REDSTONE_TORCH_ON) {
                p.sendMessage(ChatColor.GRAY + "Sending you back to lobby...");
                GameServer.getInstance().sendToLobby(p);
            }
        }
    }
}
