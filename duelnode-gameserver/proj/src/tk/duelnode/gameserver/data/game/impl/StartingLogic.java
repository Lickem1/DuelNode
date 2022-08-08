package tk.duelnode.gameserver.data.game.impl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;
import tk.duelnode.gameserver.data.game.LocalGameType;
import tk.duelnode.gameserver.data.player.PlayerData;

public class StartingLogic implements LocalGameTick {

    private int countdown = 5;
    private int waiting_time = 0;
    private final ChatColor[] colors = {ChatColor.DARK_RED,ChatColor.RED,ChatColor.YELLOW,ChatColor.GOLD,ChatColor.GREEN};

    @Override
    public void doTick(LocalGame game) {

        if(game.isReady()) {

            if(countdown == 0) {
                for(PlayerData player : game.getAllPlayers()) {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BELL, 1F, 1f);
                    player.getPlayer().sendTitle(ChatColor.GREEN + "GO!", ChatColor.GRAY.toString() + ChatColor.ITALIC + "Good luck and have fun!");
                    player.getPlayer().setHealth(20D);
                    player.getPlayer().setFoodLevel(20);
                    player.getPlayer().setSaturation(20);

                    player.getPlayer().getInventory().clear();
                    player.getPlayer().getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
                    player.getPlayer().getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
                    player.getPlayer().getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
                    player.getPlayer().getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
                    player.getPlayer().getInventory().setItem(0, new ItemStack(Material.IRON_SWORD, 1));
                    player.getPlayer().getInventory().setItem(1, new ItemStack(Material.FISHING_ROD, 1));
                    player.getPlayer().getInventory().setItem(2, new ItemStack(Material.BOW, 1));
                    player.getPlayer().getInventory().setItem(7, new ItemStack(Material.COOKED_BEEF, 16));
                    player.getPlayer().getInventory().setItem(8, new ItemStack(Material.ARROW, 16));
                    player.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.SHIELD, 1));
                    player.getPlayer().updateInventory();
                    player.getPlayer().teleport(game.getPlayerSpawn(player));
                }
                game.sendMessage(ChatColor.GREEN + "GO! " + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Good luck and have fun!");
                game.setGameTick(new GameLogic()); // todo game logic
            } else {

                if(countdown == 5) {
                    if(game.getGameType() == LocalGameType.DUEL) {
                        game.sendMessage(
                                "",
                                "&bStarting match &f" + game.getTeam1().get(0).getName() + " vs " + game.getTeam2().get(0).getName(),
                                "&bArena:&f " + game.getArena().getDisplayName(), "&bServer:&f Ashburn, Virginia");
                    }
                }
                String message = colors[(countdown-1)].toString() + countdown +"...";
                for(PlayerData player : game.getAllPlayers()) {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.getPlayer().sendTitle(message, "");
                }
                game.sendMessage(message);
                countdown--;
            }
        } else {

            if(waiting_time >= 10) { // wait for other player for 10 secs before cancelling game
                game.cancel();
            } else {
                for(PlayerData player : game.getAllPlayers()) {
                    player.getPlayer().sendTitle("Waiting for players", "");
                }
            }

            waiting_time++;
        }
    }
}
