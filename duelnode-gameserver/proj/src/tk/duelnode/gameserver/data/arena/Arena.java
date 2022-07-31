package tk.duelnode.gameserver.data.arena;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Arena {

    private final String ID;
    private Clipboard clipboard;
    private String displayName;
    private Cube cuboid;
    private Location loc1, loc2, center;

    public static Arena clone(Arena arena) {
        Arena clone = new Arena(UUID.randomUUID().toString());
        clone.cuboid = new Cube();
        clone.clipboard = arena.clipboard;
        clone.displayName = arena.displayName;
        clone.loc1 = arena.loc1.clone();
        clone.loc2 = arena.loc2.clone();
        clone.center = arena.center.clone();
        clone.cuboid.setWorld(arena.cuboid.getWorld());
        clone.cuboid.setBlockZ(arena.cuboid.getBlockZ().clone());
        clone.cuboid.setBlockX(arena.cuboid.getBlockX().clone());
        return clone;
    }
}
