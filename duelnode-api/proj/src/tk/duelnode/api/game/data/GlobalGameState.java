package tk.duelnode.api.game.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalGameState {

    STARTING("Starting"),
    ONGOING("Ongoing"),
    FINISHED("Finished");

    private final String formattedString;
}
