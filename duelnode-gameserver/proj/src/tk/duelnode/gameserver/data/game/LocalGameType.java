package tk.duelnode.gameserver.data.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocalGameType {

    DUEL (2);

    private final int startMinimum;
}
