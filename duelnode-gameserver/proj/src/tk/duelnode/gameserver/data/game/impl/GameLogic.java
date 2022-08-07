package tk.duelnode.gameserver.data.game.impl;

import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;
import tk.duelnode.gameserver.data.game.LocalGameType;
import tk.duelnode.gameserver.data.player.PlayerData;

public class GameLogic implements LocalGameTick {

    private int duration = 0;

    @Override
    public void doTick(LocalGame game) {
        duration++;
        LocalGameType gameType = game.getGameType();

        if(gameType == LocalGameType.DUEL) {
            if(game.getPlayersAlive().size() == 1) {
                PlayerData winner = game.getPlayersAlive().get(0);
                game.sendMessage(winner.getName() + " has won the match!");
                game.setGameTick(new FinishLogic(duration));
            }
        }
    }

    public String getGameTime() {
        int ms = duration / 60;
        int ss = duration % 60;
        String m = ((ms < 10) ? "0" : "") + ms;
        String s = ((ss < 10) ? "0" : "") + ss;
        return m + ":" + s;
    }
}
