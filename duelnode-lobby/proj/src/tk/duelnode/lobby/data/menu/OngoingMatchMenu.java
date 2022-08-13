package tk.duelnode.lobby.data.menu;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.api.game.sent.GlobalGame;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.menu.info.InfoMenu;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OngoingMatchMenu {

    public OngoingMatchMenu(Player p) {
        List<MenuBuilder> menus = new ArrayList<>();
        LinkedList<ItemStack> items = new LinkedList<>();

        List<GlobalGame> globalGameList = GlobalGame.getAllGameData(Plugin.getInstance().getRedisManager());

        for (GlobalGame game : globalGameList) {
            NBTTagCompound gameID = new NBTTagCompound();
            gameID.setString("game-id", game.getGameID().toString());

            ItemBuilder item = new ItemBuilder(Material.WOOD, 1, 0).setName("&b" + game.getTeam1().get(0).getName() + " &7vs &b" + game.getTeam2().get(0).getName());
            item.setLore(
                    "&fLocation:&7 " + game.getGameServer(),
                    "&fArena: &7" + game.getArenaID(),
                    "&fSpectators:&7 " + game.getSpectators().size(),
                    "",
                    " &b* &fClick to spectate game!"
            );

            item.addTag(gameID);
            items.add(item.build());
        }

        MenuBuilder menu = new MenuBuilder(54, "Ongoing Matches #1");
        fillGlass(menu);
        for (ItemStack item : items) {
            if (menu.get().firstEmpty() == 34) {
                menu.add(item);
                menus.add(menu);
                int index = menus.indexOf(menu) + 2;
                menu = new MenuBuilder(54, "Ongoing Matches #" + index);
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
                            "&7Page &b" + (nextIndex+1) + " &7of&3 " + menus.size(),
                            "",
                            " &b* &fMiddle click to travel to the last page!"
                    ).build();
            ItemStack lastPage = new ItemBuilder(Material.ARROW, 1, 0).setName("&cLast Page")
                    .setLore(
                            "&7Page &b" + (lastIndex+1) + " &7of&3 " + menus.size(),
                            "",
                            " &b* &fMiddle click to travel to the first page!"
                    ).build();
            ItemStack noMorePages = new ItemBuilder(Material.BARRIER, 1, 0).setName("&cNo more pages").build();
            ItemStack backToMain = new ItemBuilder(Material.FEATHER, 1, 0).setName("&cBack").build();

            if (currentIndex != menus.size()) m.set(41, nextPage, ((player, type, slot) -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                if(type == ClickType.MIDDLE) player.openInventory(menus.get(menus.size()-1).get());
                else player.openInventory(menus.get(nextIndex).get());
            }));
            else m.set(41, noMorePages, ((player, type, slot) -> player.sendMessage("§cNo more pages")));

            if (currentIndex != 1) m.set(39, lastPage, ((player, type, slot) -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                if(type == ClickType.MIDDLE) player.openInventory(menus.get(0).get());
                else player.openInventory(menus.get(lastIndex).get());
            }));
            else m.set(39, noMorePages, ((player, type, slot) -> player.sendMessage("§cNo more pages")));

            m.set(49, backToMain, ((player, type, slot) -> DynamicManager.get(InfoMenu.class).open(player)));
        }

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
        p.openInventory(menus.get(0).get());
    }

    private void fillGlass(MenuBuilder b) {
        int[] slots = {
                0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,40,42,43,44,45,46,47,48,50,51,52,53
        };
        for(int g : slots) {
            b.set(g, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").build(), null);
        }
    }
}
