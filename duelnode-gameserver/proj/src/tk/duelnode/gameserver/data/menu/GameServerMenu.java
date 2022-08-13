package tk.duelnode.gameserver.data.menu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.util.menu.MenuBuilder;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.manager.ArenaManager;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;
import tk.duelnode.gameserver.util.itembuilder.ItemBuilder;

@Init(classType = ClassType.CONSTRUCT)
public class GameServerMenu extends BukkitRunnable {

    private final MenuBuilder menu = new MenuBuilder(45, "DuelNode - Game Server");

    private final ItemBuilder templates = new ItemBuilder(Material.BOOK_AND_QUILL, 1, 0).setLore("&7Click to view available templates!");
    private final ItemBuilder available = new ItemBuilder(Material.WOOL, 1, 13).setLore("&7Click to view available arenas!");
    private final ItemBuilder unavailable = new ItemBuilder(Material.WOOL, 1, 14).setLore("&7Click to view unavailable arenas!");

    public GameServerMenu() {
        ItemBuilder netherStar = new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("Placeholder 1");
        menu.set(4, netherStar.build(), null);

        int[] slots = {0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 16, 17, 18, 26, 27, 28, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int i : slots) menu.set(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setName(" ").build(), null);

        runTaskTimerAsynchronously(GameServer.getInstance(), 3 * 20, 3 * 20);
    }

    public void open(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.3F, 0.3f);
        player.openInventory(menu.get());
    }

    @Override
    public void run() {
        ArenaManager arenaManager = DynamicManager.get(ArenaManager.class);

        int allArenaSize = arenaManager.getAllArenas().size();
        int availableArenaSize = arenaManager.getAvailableArenas().size();
        int unavailableArenaSize = arenaManager.getArenasInUse().size();

        if (availableArenaSize >= 1) {
            available.addEnchant(Enchantment.MENDING, 1);
            available.addFlag(ItemFlag.HIDE_ENCHANTS);
        } else available.removeEnchant(Enchantment.MENDING);

        if (unavailableArenaSize >= 1) {
            unavailable.addEnchant(Enchantment.MENDING, 1);
            unavailable.addFlag(ItemFlag.HIDE_ENCHANTS);
        } else unavailable.removeEnchant(Enchantment.MENDING);

        if (allArenaSize >= 1) {
            templates.addEnchant(Enchantment.MENDING, 1);
            templates.addFlag(ItemFlag.HIDE_ENCHANTS);
        } else templates.removeEnchant(Enchantment.MENDING);

        templates.getItemStack().setAmount(fixedAmount(allArenaSize));
        available.getItemStack().setAmount(fixedAmount(availableArenaSize));
        unavailable.getItemStack().setAmount(fixedAmount(unavailableArenaSize));
        templates.setName("&bArena Templates&f (" + allArenaSize + ")");
        available.setName("&aAvailable Arenas&f (" + availableArenaSize + ")");
        unavailable.setName("&cUnavailable Arenas&f (" + unavailableArenaSize + ")");

        menu.set(20, templates.build(), ((player, type, slot) -> new ArenaListMenu(player, arenaManager.getAllArenas().values(), "Arena Templates")));
        menu.set(22, available.build(), ((player, type, slot) -> new ArenaListMenu(player, arenaManager.getAvailableArenas().values(), "Available Arenas")));
        menu.set(24, unavailable.build(), ((player, type, slot) -> new ArenaListMenu(player, arenaManager.getArenasInUse().values(), "Arena Templates")));
    }

    public int fixedAmount(int size) {
        return (size == 0 ? 1 : Math.min(size, 64));
    }
}
