package tk.duelnode.gameserver.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.data.menu.GameServerMenu;
import tk.duelnode.gameserver.manager.ArenaManager;
import tk.duelnode.gameserver.manager.DynamicManager;
import tk.duelnode.gameserver.manager.dynamic.DynamicListener;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class ChatListener extends DynamicListener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        ArenaManager arenaManager = DynamicManager.get(ArenaManager.class);


        if(e.getMessage().equalsIgnoreCase("paste")) {
            e.setCancelled(true);
            Arena a = arenaManager.getFreeArena();
            p.sendMessage(ChatColor.GRAY + "Pasting new arena " + a.getID());
        }

        else if(e.getMessage().equalsIgnoreCase("-gs")) {
            e.setCancelled(true);
            DynamicManager.get(GameServerMenu.class).open(p);
        }
    }
}
