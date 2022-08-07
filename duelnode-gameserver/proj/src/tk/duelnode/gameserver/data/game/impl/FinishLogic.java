package tk.duelnode.gameserver.data.game.impl;

import org.bukkit.Bukkit;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;
import tk.duelnode.gameserver.data.player.PlayerData;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.game.GameManager;

public class FinishLogic implements LocalGameTick {

    private final GameManager gameManager = DynamicManager.get(GameManager.class);

    private int send_delay = 3;
    private final int final_duration;

    public FinishLogic(int final_duration) {
        this.final_duration = final_duration;
    }

    @Override
    public void doTick(LocalGame game) {
        if(send_delay == 0) {
            game.setGameTick(null);

            Bukkit.getServer().getScheduler().runTask(GameServer.getInstance(), () -> {
                for(PlayerData data : game.getAllPlayers()) data.getPlayer().kickPlayer("Finished");

                // send inventories back to lobby server
                // send player back to lobby server

            });

            gameManager.deleteGame(game);
        }

        send_delay--;
    }

    public String getFinalTime() {
        int ms = final_duration / 60;
        int ss = final_duration % 60;
        String m = ((ms < 10) ? "0" : "") + ms;
        String s = ((ss < 10) ? "0" : "") + ss;
        return m + ":" + s;
    }
}
