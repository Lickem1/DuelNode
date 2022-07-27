package tk.duelnode.lobby.listener;

import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class PlayerListener extends DynamicListener {
}
