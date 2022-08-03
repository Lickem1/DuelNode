package tk.duelnode.api.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuBuilder {

    private final MenuHolder holder = new MenuHolder();
    private Inventory inventory;

    public MenuBuilder(int size) {
        inventory = Bukkit.createInventory(holder, size);
    }

    public MenuBuilder(InventoryType type) {
        inventory = Bukkit.createInventory(holder, type);
    }

    public MenuBuilder(int size, String name) {
        inventory = Bukkit.createInventory(holder, size, name);
    }

    public MenuBuilder(InventoryType type, String name) {
        inventory = Bukkit.createInventory(holder, type, name);
    }

    public Inventory get() {
        return inventory;
    }

    {
        holder.inventory = inventory;
    }

    public MenuBuilder set(int slot, ItemStack stack, MenuEvent event) {
        inventory.setItem(slot, stack);
        holder.setEvent(slot, event);
        return this;
    }

    public MenuBuilder add(ItemStack stack, MenuEvent event) {
        inventory.addItem(stack);
        holder.setEvent(event);
        return this;
    }

    public MenuBuilder add(ItemStack stack) {
        inventory.addItem(stack);
        return this;
    }

    public MenuBuilder setDefault(MenuEvent event) {
        this.holder.setDefaultEvent(event);
        return this;
    }

}
