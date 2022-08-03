package tk.duelnode.gameserver.data.game.impl;

import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.data.game.LocalGameTick;

public class StartingTick implements LocalGameTick {

    @Override
    public void doTick(LocalGame game) {

        if(game.isReady()) {
            System.out.println("Game is ready");
        }

    }
}
