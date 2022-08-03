package tk.duelnode.gameserver.data.menu;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.util.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ArenaListMenu {

    public ArenaListMenu(Player p, Collection<Arena> arenaList, String name) {
        Iterator<Arena> arenaIterator = arenaList.iterator();

        List<MenuBuilder> allMenus = new ArrayList<>();
        List<ItemStack> entries = new ArrayList<>();

        while (arenaIterator.hasNext()) {
            Arena arena = arenaIterator.next();

            ItemBuilder builder = new ItemBuilder(arena.getIcon()).setName(ChatColor.AQUA + arena.getDisplayName()).setLore(
                    "&7UID:&f " + arena.getID(),
                    "&7Locations:",
                    "&f * &7Center location:&f " + arena.getCenter().getX() + ", " + arena.getCenter().getY() + ", " + arena.getCenter().getZ(),
                    "&f * &7Spawn 1:&f " + arena.getLoc1().getX() + ", " + arena.getLoc1().getY() + ", " + arena.getLoc1().getZ(),
                    "&f * &7Spawn 2:&f " + arena.getLoc2().getX() + ", " + arena.getLoc2().getY() + ", " + arena.getLoc2().getZ(),
                    "&7Cuboid:",
                    "&f * &7X:&f " + arena.getCuboid().getBlockX().getX() + ", " + arena.getCuboid().getBlockX().getY() + ", " + arena.getCuboid().getBlockX().getZ(),
                    "&f * &7Z:&f " + arena.getCuboid().getBlockZ().getX() + ", " + arena.getCuboid().getBlockZ().getY() + ", " + arena.getCuboid().getBlockZ().getZ(),
                    "&f ",
                    "&f » &bClick to teleport to area!"
            );

            NBTTagCompound center = new NBTTagCompound();
            center.setString("data", arena.getID() + "," + arena.getCenter().getX() + "," + arena.getCenter().getY() + "," + arena.getCenter().getZ() + "," + arena.getCenter().getWorld().getName());
            builder.addTag(center);
            entries.add(builder.build());
        }

        MenuBuilder menu = new MenuBuilder(36, name + " - Page #1");
        fillGlass(menu);
        for (int i = 0; i < 3; i++) menu.add(new ItemBuilder(Material.MAP, 1, 0).setName("Airblock " + i).build());
        for (ItemStack stack : entries) {
            if (menu.get().firstEmpty() == 35) {
                menu.add(stack);
                allMenus.add(menu);
                int index = allMenus.indexOf(menu) + 2;
                menu = new MenuBuilder(36, name + " - Page #" + index);
                fillGlass(menu);
                for (int i = 0; i < 3; i++)
                    menu.add(new ItemBuilder(Material.MAP, 1, 0).setName("Airblock " + i).build());
            } else {
                menu.add(stack);
            }
        }
        allMenus.add(menu);

        for (MenuBuilder m : allMenus) {
            int nextIndex = allMenus.indexOf(m) + 1;
            int currentIndex = allMenus.indexOf(m) + 1;
            int lastIndex = allMenus.indexOf(m) - 1;

            ItemStack nextPage = new ItemBuilder(Material.ARROW, 1, 0).setName("&aNext Page" + "&f ->").setLore(
                    "&7Page &b" + (nextIndex+1) + " &7of &3" + allMenus.size(),
                    " ",
                    " &e* &7Middle click to travel to the last page!"
            ).build();
            ItemStack lastPage = new ItemBuilder(Material.ARROW, 1, 0).setName("&f<- &cLast Page").setLore(
                    "&7Page &b" + (lastIndex+1) + " &7of &3" + allMenus.size(),
                    " ",
                    " &e* &7Middle click to travel to the first page!"
            ).build();
            ItemStack noMorePages = new ItemBuilder(Material.BARRIER, 1, 0).setName("&cNo more pages").build();
            ItemStack backToMain = new ItemBuilder(Material.FEATHER, 1, 0).setName("&cBack").build();

            if (currentIndex != allMenus.size()) {
                m.set(8, nextPage, ((player, type, slot) -> {

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                    if(type == ClickType.MIDDLE) {
                        player.openInventory(allMenus.get(allMenus.size()-1).get());
                    } else player.openInventory(allMenus.get(nextIndex).get());

                }));
            } else m.set(8, noMorePages, ((player, type, slot) -> player.sendMessage(ChatColor.RED + "No more pages")));


            if (currentIndex != 1) m.set(0, lastPage, ((player, type, slot) -> {

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                if(type == ClickType.MIDDLE) {
                    player.openInventory(allMenus.get(0).get());
                } else player.openInventory(allMenus.get(lastIndex).get());

            }));
            else m.set(0, noMorePages, ((player, type, slot) -> player.sendMessage(ChatColor.RED + "No more pages")));

            m.set(4, backToMain, ((player, type, slot) -> DynamicManager.get(GameServerMenu.class).open(player)));
        }

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
        p.openInventory(allMenus.get(0).get());
    }

    private void fillGlass(MenuBuilder menu) {
        int[] slots = {1, 2, 3, 5, 6, 7};
        int[] slots2 = {9, 10, 11, 12, 13, 14, 15, 16, 17};
        for (int i : slots) menu.set(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).setName(" ").build(), null);
        for (int i : slots2)
            menu.set(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").build(), null);
    }
}
