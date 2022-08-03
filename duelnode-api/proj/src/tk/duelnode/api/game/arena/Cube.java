package tk.duelnode.api.game.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class Cube implements Cloneable {

    private World world;
    private Location blockX;
    private Location blockZ;

    public Cube clone() {
        try {
            return (Cube) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
