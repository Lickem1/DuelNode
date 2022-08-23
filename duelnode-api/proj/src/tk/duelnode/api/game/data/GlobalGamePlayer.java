package tk.duelnode.api.game.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GlobalGamePlayer {

    private final String name;
    private final UUID uuid;
}
