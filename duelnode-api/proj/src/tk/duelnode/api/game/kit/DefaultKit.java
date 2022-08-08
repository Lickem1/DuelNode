package tk.duelnode.api.game.kit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@RequiredArgsConstructor
public class DefaultKit {

    private final String name;
    private ItemStack[] armor;
    private ItemStack[] inventory;
    private boolean build;

    public void apply(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inventory);
        p.updateInventory();
    }
}
