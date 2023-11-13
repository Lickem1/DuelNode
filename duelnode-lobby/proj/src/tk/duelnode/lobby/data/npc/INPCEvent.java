package tk.duelnode.lobby.data.npc;

import org.bukkit.entity.Player;

public interface INPCEvent {

    void run(NPC npc, Player player);
}
