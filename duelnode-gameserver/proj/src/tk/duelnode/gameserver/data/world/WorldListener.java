package tk.duelnode.gameserver.data.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.data.world.chunk.NMSChunk;
import tk.duelnode.gameserver.manager.dynamic.DynamicListener;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class WorldListener extends DynamicListener {

    @EventHandler
    public void worldGen(WorldInitEvent e) {
        new NMSChunk(e.getWorld());
    }
}
