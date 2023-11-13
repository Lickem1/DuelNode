package tk.duelnode.lobby.data.npc;
import net.minecraft.server.v1_12_R1.EnumHand;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import org.bukkit.entity.Player;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.packet.PacketEvent;
import tk.duelnode.api.util.packet.PacketState;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.NPCManager;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

@Init(classType = ClassType.PACKET_LISTENER)
public class NPCPacketListener {

    @PacketEvent(packet = PacketPlayInUseEntity.class)
    public void handle(Player player, PacketState state, PacketPlayInUseEntity packet) {
        NPCManager npcManager = DynamicManager.get(NPCManager.class);
        NPC npc = npcManager.get(packet.getEntityId());

        PacketPlayInUseEntity.EnumEntityUseAction action = packet.a();
        EnumHand hand = packet.b();

        if (npc != null && npc.hasEvent()) {
            if ((action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT && hand == EnumHand.MAIN_HAND) // apparently you can interact with entities using both hands ;D
                    || action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                npc.getEvent().run(npc, player);
            }
        }
    }
}
