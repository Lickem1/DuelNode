package tk.duelnode.api.game.kit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class Kit {

    private final String name;
    private Map<Integer, ItemStack> armor;
    private Map<Integer, ItemStack> inventory;
    private boolean build;

    public void apply(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(armor.values().toArray(new ItemStack[0]));
        for(Integer i : inventory.keySet()) {
            p.getInventory().setItem(i, inventory.get(i));
        }
        p.updateInventory();
    }
}
