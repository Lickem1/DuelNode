package tk.duelnode.lobby.data.menu.info;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.API;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.api.game.data.GlobalGameState;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.api.server.EnumServerType;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.menu.GameServerMenu;
import tk.duelnode.lobby.data.menu.OngoingMatchMenu;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.List;

@Init(classType = ClassType.CONSTRUCT)
public class InfoMenu extends BukkitRunnable {

    private final MenuBuilder menu = new MenuBuilder(54, "DuelNode Info");

    public InfoMenu() {

        ItemBuilder netherStar = new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("&f&lDuel&e&lNode &fv" + API.getVersion()).setLore("&7Developed by Lickem");
        ItemBuilder discordSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&bDiscord: &7Lickem#9444");
        ItemBuilder githubSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&fGit&8Hub").setLore(" &b* &fhttps://github.com/Lickem1");
        ItemBuilder youtubeSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&fYou&cTube").setLore(" &b* &fhttps://www.youtube.com/c/Lickem");
        ItemBuilder updateLogs = new ItemBuilder(Material.ENCHANTED_BOOK, 1, 0).setName("&5Update Logs").setLore(" &5* &fClick to view all github updates!");
        ItemBuilder ongoingMatches = new ItemBuilder(Material.COMPASS, 1, 0).setName("&bOngoing Matches");
        ItemBuilder gameServer = new ItemBuilder(Material.COMMAND, 1, 0).setName("&eGame Servers");

        discordSkull.setCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ0MjMzN2JlMGJkY2EyMTI4MDk3ZjFjNWJiMTEwOWU1YzYzM2MxNzkyNmFmNWZiNmZjMjAwMDAwMTFhZWI1MyJ9fX0=");
        githubSkull.setCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZlMjdkYTEyODE5YThiMDUzZGEwY2MyYjYyZGVjNGNkYTkxZGU2ZWVlYzIxY2NmM2JmZTZkZDhkNDQzNmE3In19fQ==");
        youtubeSkull.setCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3Njg4ZGE0NjU4NmI4NTlhMWNkZTQwY2FlMWNkYmMxNWFiZTM1NjE1YzRiYzUyOTZmYWQwOTM5NDEwNWQwIn19fQ==");

        menu.set(4, netherStar.build(), null);
        menu.set(20, discordSkull.build(), null);
        menu.set(22, youtubeSkull.build(), ((player, type, slot) -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7My &fYou&cTube &7page &8»&f https://www.youtube.com/c/Lickem"));
            player.closeInventory();
        }));
        menu.set(24, githubSkull.build(), ((player, type, slot) -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7My &fGitHub &7page &8»&f https://github.com/Lickem1"));
            player.closeInventory();
        }));
        menu.set(39, updateLogs.build(), ((player, type, slot) -> DynamicManager.get(UpdateLogMenu.class).open(player)));
        menu.set(41, ongoingMatches.build(), ((player, type, slot) -> openOngoingMenu(player)));
        menu.set(40, gameServer.build(), ((player, type, slot) -> openServersMenu(player)));
        fillGlass();

        runTaskTimerAsynchronously(Plugin.getInstance(), 3*20, 3*20);
    }

    public void open(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 0.5f);
        player.openInventory(menu.get());
    }

    private void fillGlass() {
        int[] slots = {
                0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 16, 17, 18, 26, 27, 35, 36, 37, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
        };
        for (int i : slots) {
            menu.set(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").build(), null);
        }
    }

    @Override
    public void run() {
        int gamesAmount = fixedGamesAmount();
        int serverAmount = fixedServerAmount();
        ItemBuilder ongoingMatches = new ItemBuilder(Material.COMPASS, (gamesAmount == 0 ? 1 : gamesAmount), 0).setName("&bOngoing Matches &7(" + gamesAmount + ")").setLore(" &b* &fClick to view all ongoing games!");
        ItemBuilder gameServer = new ItemBuilder(Material.COMMAND, (serverAmount == 0 ? 1 : serverAmount), 0).setName("&eGame Servers &7(" + serverAmount + ")").setLore(" &e* &fClick to view all game servers!");

        menu.set(41, ongoingMatches.build(), ((player, type, slot) -> openOngoingMenu(player)));
        menu.set(40, gameServer.build(), ((player, type, slot) -> openServersMenu(player)));
    }

    private int fixedGamesAmount() {
        List<GlobalGame> games = GlobalGame.getAllGameData(Plugin.getInstance().getRedisManager());
        games.removeIf(game -> game.getGameState() == GlobalGameState.FINISHED);
        return games.size();
    }

    private int fixedServerAmount() {
        List<DNServerData> servers = DNServerManager.getAllServerData(Plugin.getInstance().getRedisManager());
        servers.removeIf(server -> !server.online || server.serverType != EnumServerType.DUEL_SERVER);
        return servers.size();
    }

    public boolean openOngoingMenu(Player player) {
        if(fixedGamesAmount() != 0) {
            new OngoingMatchMenu(player);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "ERR | No games available");
            return false;
        }
    }

    public boolean openServersMenu(Player player) {
        if(fixedServerAmount() != 0) {
            new GameServerMenu(player);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "ERR | No servers available");
            return false;
        }
    }
}
