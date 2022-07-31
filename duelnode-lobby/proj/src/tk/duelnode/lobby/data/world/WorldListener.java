package tk.duelnode.lobby.data.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.data.world.chunk.NMSChunk;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class WorldListener extends DynamicListener {

    @EventHandler
    public void worldGen(WorldInitEvent e) {
        new NMSChunk(e.getWorld());
    }
}
