package tk.duelnode.gameserver.data.game.impl;

import lombok.Getter;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;

@Getter
public class GameLogic implements LocalGameTick {

    private int duration = 0;

    @Override
    public void doTick(LocalGame game) {
        duration++;
    }

    public String getFormattedDuration() {
        int ms = duration / 60;
        int ss = duration % 60;
        String m = ((ms < 10) ? "0" : "") + ms;
        String s = ((ss < 10) ? "0" : "") + ss;
        return m + ":" + s;
    }
}
