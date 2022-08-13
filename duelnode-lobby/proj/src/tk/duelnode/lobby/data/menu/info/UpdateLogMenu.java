package tk.duelnode.lobby.data.menu.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Init(classType = ClassType.CONSTRUCT)
public class UpdateLogMenu {

    private final List<MenuBuilder> menus = new ArrayList<>();

    public UpdateLogMenu() {

        LinkedList<ItemStack> items = new LinkedList<>();

        for(int i = 0; i < Update.values().length; i++) {
            Update update = Update.values()[i];
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Commit Date: &b" + update.getDate()));
            lore.add(" ");
            for(String string : update.getLore()) lore.add(ChatColor.translateAlternateColorCodes('&', string));

            ItemBuilder item = new ItemBuilder(Material.BOOK, 1, 0);
            if((i+1) == Update.values().length) {
                item = new ItemBuilder(Material.ENCHANTED_BOOK, 1, 0);
                item.setName("&b" + update.getName() + " &f* Most Recent Push");

            } else item.setName("&b" + update.getName());
            item.setLore(lore);
            items.addFirst(item.build());
        }

        MenuBuilder menu = new MenuBuilder(54, "Update Logs #1");
        fillGlass(menu);
        for (ItemStack item : items) {
            if (menu.get().firstEmpty() == 34) {
                menus.add(menu);
                int index = menus.indexOf(menu) + 2;
                menu = new MenuBuilder(54, "Update Logs #" + index);
                fillGlass(menu);
            }

            menu.add(item);
        }
        menus.add(menu);

        for (MenuBuilder m : menus) {
            int nextIndex = menus.indexOf(m) + 1;
            int currentIndex = menus.indexOf(m) + 1;
            int lastIndex = menus.indexOf(m) - 1;

            ItemStack nextPage = new ItemBuilder(Material.ARROW, 1, 0).setName("Page #" + (nextIndex + 1) + " ->").build();
            ItemStack noMorePages = new ItemBuilder(Material.BARRIER, 1, 0).setName("&cNo more pages").build();
            ItemStack lastPage = new ItemBuilder(Material.REDSTONE, 1, 0).setName("&cLast Page #" + (lastIndex + 1)).build();
            ItemStack backToMain = new ItemBuilder(Material.FEATHER, 1, 0).setName("&cBack").build();

            if (currentIndex != menus.size()) m.set(41, nextPage, ((player, type, slot) -> player.openInventory(menus.get(nextIndex).get())));
            else m.set(41, noMorePages, null);

            if (currentIndex != 1) m.set(39, lastPage, ((player, type, slot) -> player.openInventory(menus.get(lastIndex).get())));
            else m.set(39, noMorePages, null);

            m.set(49, backToMain, ((player, type, slot) -> DynamicManager.get(InfoMenu.class).open(player)));
        }
    }

    public void open(Player p) {
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

    @Getter
    @AllArgsConstructor
    public enum Update {

        LOG_1("Log #1", "Jul 26, 2022 @ 5:46PM EST", new String[]{
                "&bCommit ID:&f 54bff06bc81372c2c19b066fcb19b136786fef31",
                "&7 ",
                "&7Just an initial commit, setting up main project for spigot implementation.",
                "&7Getting maven and the project setup for further development.",
        }),

        LOG_2("Log #2", "Jul 26, 2022 @ 11:38PM EST", new String[]{
                "&bCommit ID:&f e6a4c0d3325a4d950fbfec32844326e9369d9a0c",
                "&7 ",
                "&7Main focus in this push is for the lobby, getting things",
                "&7looking presentable, implementing our core class files, apis, etc.",
                "&7(Highlighting the AirChunk class as it's a nice way to prevent normal",
                "&7chunks from generating) :D"
        }),

        LOG_3("Log #3", "Jul 27, 2022 @ 1:05PM EST", new String[]{
                "&bCommit ID:&f 0f27022be509a2e7b60affe4c448dcb579c99e88",
                "&7 ",
                "&7Some more work done on the lobby portion. Again, making things look",
                "&7pretty and presentable, this time adding some more dependencies such as",
                "&7LettuceIO (which we will use to communicate between servers)",
                " ",
                "&cBug Fixes",
                "&7I noticed a small bug when you'd jump off the map, that is now fixed."
        }),

        LOG_4("Log #4", "Jul 28, 2022 @ 1:04PM EST", new String[]{
                "&bCommit ID:&f b15205db26f7c0c92c98df2486f9cdaef11b114f",
                "&7 ",
                "&7AGAIN, some more work done on the lobby portion, hopefully this upcoming",
                "&7commit will be my last one on the design on lobby, now we will start implementing",
                "&7cross-server communication and duel creation (See game server for more)"
        }),

        LOG_5("Log #5", "Jul 29, 2022 @ 1:30AM EST", new String[]{
                "&bCommit ID:",
                "&7 - &f168a694a90595a27c378490e7ca3b5f02c733424",
                "&7 - &f094e615e8aae28657028d6f8ff3e3cf00d5c01f7",
                "&7 ",
                "&7AGAIN, some more work done on the lobby portion, making it look pretty",
                "&7Wow we should be able to start implementing",
                "&7cross-server communication and duel creation (See gameserver for more)"
        }),

        LOG_6("Log #6", "Jul 30, 2022 @ 11:04AM EST", new String[]{
                "&bCommit ID:&f 71d7a3100c872a7275e5b51d16df7810ddcaf308",
                "&7 ",
                "&7Implemented basic queue system",
                "&7Starting development on the game server portion."
        }),

        LOG_7("Log #7", "Jul 30, 2022 @ 11:08PM EST", new String[]{
            "&bCommit ID:&f 8ca2a07f2f40a68f4eb012345210a71e957630db",
                    "&7 ",
                    "&7Loads of work done on the game server portion",
                    "&7Still haven't linked the servers together quite yet, but it's very close to completion",
                    "&7Infinite arena feature implemented, arenas get pasted and spread out within the world",
                    "&7Developing a system to increase performance with arenas, so they only get generated if needed",
                    " ",
                    "&cBug Fixes",
                    "&7A minor, but annoying bug within the chunk generation where it doesn't update light sources"
        }),

        LOG_8("Log #8", "Aug 3, 2022 @ 5:37PM EST", new String[]{
                "&bCommit ID:&f 70f61a4ca27c5cfa0603a35238d79a4de69652be",
                "&7 ",
                "&7Done some more work on the game server portion",
                "&7Creating stuff for local games, basically just general game functionality",
                "&7Also creating a global game system to view/manage active games from other locations (servers)"
        }),

        LOG_9("Log #9", "Aug 6, 2022 @ 11:22PM EST", new String[]{
                "&bCommit ID:&f 4258c48ccfd1220d44ef8ecb34569611351fb675",
                "&7 ",
                "&7Done some more work on the game server portion",
                "&7Local game logic should almost be complete, if not very near",
                "&7Got annoyed with multiple Init classes, so i condensed it all into one, ",
                "&7using an integer as a priority key",
        }),

        LOG_10("Log #10", "Aug 7, 2022 @ 9:57PM EST", new String[]{
                "&bCommit ID:&f 050231ec186653f2edf0b6539f89e1f6fa8da391",
                "&7 ",
                "&7Everything works!",
                "&7Project is very close to completion! Just need to do a few testings (not sure how to find people lol)",
                "&7Implemented bungeecord server linking to send players to the (one) game server!!",
                "&7Could add multiple gameserver functionality but this isn't a project that should be used in production!"
        });


        private final String name, date;
        private final String[] lore;
    }
}
