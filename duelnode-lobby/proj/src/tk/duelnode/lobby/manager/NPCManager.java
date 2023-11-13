package tk.duelnode.lobby.manager;


import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.data.npc.NPC;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@Init(classType = ClassType.CONSTRUCT)
public class NPCManager {

    private final LinkedHashMap<Integer, NPC> npcList = new LinkedHashMap<>();

    public NPC get(int id) {
        return npcList.get(id);
    }

    public void delete(int id) {
        npcList.remove(id);
    }

    public void create(NPC npc) {
        npcList.put(npc.getPid(), npc);
    }

    public Collection<NPC> getAllNPCs() {
        return npcList.values();
    }
}
