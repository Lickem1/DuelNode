package tk.duelnode.lobby.data.menu.info;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;
import tk.duelnode.lobby.util.menu.MenuBuilder;

@Init(classType = ClassType.CONSTRUCT)
public class InfoMenu {

    private final MenuBuilder menu = new MenuBuilder(54, "DuelNode Info");

    public InfoMenu() {

        ItemBuilder netherStar = new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("Placeholder 1");
        ItemBuilder discordSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&bDiscord: &7Lickem#9444");
        ItemBuilder githubSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&fGit&8Hub");
        ItemBuilder youtubeSkull = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setName("&fYou&cTube");
        ItemBuilder updateLogs = new ItemBuilder(Material.ENCHANTED_BOOK, 1, 0).setName("&7Update Logs");

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
        menu.set(40, updateLogs.build(), ((player, type, slot) -> DynamicManager.get(UpdateLogMenu.class).open(player)));
        fillGlass();
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
}
