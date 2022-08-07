package tk.duelnode.gameserver.data.game.impl;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;
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
                }
                game.setGameTick(new GameLogic()); // todo game logic
            } else {
                for(PlayerData player : game.getAllPlayers()) {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                    player.getPlayer().sendTitle(colors[(countdown-1)].toString() + countdown +"...", "");
                }
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
