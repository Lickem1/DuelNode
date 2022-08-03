package tk.duelnode.api.game.arena;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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
    private ItemStack icon;

    public Arena clone() {
        Arena clone = new Arena(UUID.randomUUID().toString().replaceAll("-", ""));
        clone.cuboid = new Cube();
        clone.clipboard = this.clipboard;
        clone.displayName = this.displayName;
        clone.loc1 = this.loc1.clone();
        clone.loc2 = this.loc2.clone();
        clone.center = this.center.clone();
        clone.icon = this.icon.clone();
        clone.cuboid.setWorld(this.cuboid.getWorld());
        clone.cuboid.setBlockZ(this.cuboid.getBlockZ().clone());
        clone.cuboid.setBlockX(this.cuboid.getBlockX().clone());
        return clone;
    }
}
