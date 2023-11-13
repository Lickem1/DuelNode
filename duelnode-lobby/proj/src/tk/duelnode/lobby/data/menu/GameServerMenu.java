package tk.duelnode.lobby.data.menu;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.api.game.data.GlobalGameState;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.api.server.EnumServerType;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.menu.info.InfoMenu;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class GameServerMenu {

    public GameServerMenu(Player p) {
        List<MenuBuilder> menus = new ArrayList<>();
        LinkedList<ItemStack> items = new LinkedList<>();

        List<DNServerData> serverDataList = DNServerManager.getAllServerData(Plugin.getInstance().getRedisManager());

        for (DNServerData server : serverDataList) {
            if(server.online && server.serverType == EnumServerType.DUEL_SERVER) {
                NBTTagCompound gameID = new NBTTagCompound();
                gameID.setString("server-id", server.serverName);
                String formattedPlayers = server.onlinePlayers.toString().replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "");

                ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM, (server.playerCount == 0 ? 1 : server.playerCount), 3).setName("&6Server:&f " + server.serverName);
                item.setLore(
                        "&fStatus: " + (server.online ? "&aOnline" : "&cOffline"),
                        "&fWhitelisted: &7" + (server.whitelisted ? "&atrue" : "&cfalse"),
                        "&fLocation: &7" + server.serverLocation,
                        "",
                        "&fPlayers &7(" + server.playerCount + "/" + server.maxPlayers +")",
                        "&7" + (server.onlinePlayers.size() == 0 ? "&cThis server is empty" : formattedPlayers),
                        "",
                        " &e&o* &f&oClick to connect to this server!"
                );
                item.setCustomSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2NhNDVlZjU4MjFhOGIxMDdjYmZiYTdkNjZlOTk3ZmI2YWJlNTUyMWMxNTVjZWUyZjI0YjM0YjNkOTFhNSJ9fX0=");

                item.addTag(gameID);
                items.add(item.build());
            }
        }

        MenuBuilder menu = new MenuBuilder(54, "Game Servers #1");
        fillGlass(menu);
        for (ItemStack item : items) {
            if (menu.get().firstEmpty() == 34) {
                menu.add(item);
                menus.add(menu);
                int index = menus.indexOf(menu) + 2;
                menu = new MenuBuilder(54, "Game Servers #" + index);
                fillGlass(menu);
            } else menu.add(item);
        }
        menus.add(menu);

        for (MenuBuilder m : menus) {
            int nextIndex = menus.indexOf(m) + 1;
            int currentIndex = menus.indexOf(m) + 1;
            int lastIndex = menus.indexOf(m) - 1;

            ItemStack nextPage = new ItemBuilder(Material.ARROW, 1, 0).setName("&bNext Page")
                    .setLore(
                            "&7Page &b" + (nextIndex + 1) + " &7of&3 " + menus.size(),
                            "",
                            " &b* &fMiddle click to travel to the last page!"
                    ).build();
            ItemStack lastPage = new ItemBuilder(Material.ARROW, 1, 0).setName("&cLast Page")
                    .setLore(
                            "&7Page &b" + (lastIndex + 1) + " &7of&3 " + menus.size(),
                            "",
                            " &b* &fMiddle click to travel to the first page!"
                    ).build();
            ItemStack noMorePages = new ItemBuilder(Material.BARRIER, 1, 0).setName("&cNo more pages").build();
            ItemStack refreshMenu = new ItemBuilder(Material.HOPPER, 1, 0).setName("&bRefresh Servers").build();
            ItemStack backToMain = new ItemBuilder(Material.FEATHER, 1, 0).setName("&cBack").build();

            if (currentIndex != menus.size()) m.set(41, nextPage, ((player, type, slot) -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                if (type == ClickType.MIDDLE) player.openInventory(menus.get(menus.size() - 1).get());
                else player.openInventory(menus.get(nextIndex).get());
            }));
            else m.set(41, noMorePages, ((player, type, slot) -> player.sendMessage("§cNo more pages")));

            if (currentIndex != 1) m.set(39, lastPage, ((player, type, slot) -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                if (type == ClickType.MIDDLE) player.openInventory(menus.get(0).get());
                else player.openInventory(menus.get(lastIndex).get());
            }));
            else m.set(39, noMorePages, ((player, type, slot) -> player.sendMessage("§cNo more pages")));

            m.set(49, backToMain, ((player, type, slot) -> DynamicManager.get(InfoMenu.class).open(player)));
            m.set(40, refreshMenu, ((player, type, slot) -> {
                if (!DynamicManager.get(InfoMenu.class).openServersMenu(player)) player.closeInventory();
            }));
        }

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
        p.openInventory(menus.get(0).get());
    }

    private void fillGlass(MenuBuilder b) {
        int[] slots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 48, 50, 51, 52, 53
        };
        for (int g : slots) {
            b.set(g, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").build(), null);
        }
    }
}
