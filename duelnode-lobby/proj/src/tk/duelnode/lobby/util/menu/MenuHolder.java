package tk.duelnode.lobby.util.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashSet;

@Getter
public class MenuHolder implements InventoryHolder {

    private static final MenuEvent DEFAULT_DEFAULT_EVENT = (a, b, c)->{};

    public Inventory inventory;
    private final HashSet<EventRule> list = new HashSet<>();
    @Setter
    private MenuEvent defaultEvent = DEFAULT_DEFAULT_EVENT;

    private EventRule getRule(int slot) {
        for (EventRule r : list)
            if (r.slot == slot)
                return r;
        return null;
    }

    public MenuEvent getEvent(int slot) {
        EventRule rule = getRule(slot);
        if (rule == null)
            return defaultEvent;
        return rule.event;
    }

    public void setEvent(int slot, MenuEvent event) {
        list.add(new EventRule(slot,event));
    }
    public void setEvent(MenuEvent event) {
        list.add(new EventRule(event));
    }

    @AllArgsConstructor
    private static class EventRule {
        private int slot;
        private MenuEvent event;

        public EventRule(MenuEvent event) {
            this.event = event;
        }
    }

}
