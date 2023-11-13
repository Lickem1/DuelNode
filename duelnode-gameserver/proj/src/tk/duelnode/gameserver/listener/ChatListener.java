package tk.duelnode.gameserver.listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
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
        net.md_5.bungee.api.ChatColor gray = net.md_5.bungee.api.ChatColor.GRAY;
        String format = gray + "[" + "%s" + gray + "]%s " + p.getName() + gray + ": ";

        if(p.getName().equalsIgnoreCase("Lickem")) format = String.format(format, net.md_5.bungee.api.ChatColor.DARK_AQUA + "Dev", net.md_5.bungee.api.ChatColor.AQUA);
        else format = String.format(format, net.md_5.bungee.api.ChatColor.WHITE + "Member", net.md_5.bungee.api.ChatColor.GRAY.toString() + net.md_5.bungee.api.ChatColor.ITALIC);

        ComponentBuilder rank = new ComponentBuilder(format);
        rank.event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(ChatColor.GRAY + "Click to view " + ChatColor.AQUA + p.getName() + "'s" + ChatColor.GRAY + " profile!")
        ));
        rank.event(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/profile " + p.getName()
        ));
        rank.append(e.getMessage(), ComponentBuilder.FormatRetention.NONE);


        if(e.getMessage().equalsIgnoreCase("paste")) {
            e.setCancelled(true);
            Arena a = arenaManager.getFreeArena();
            p.sendMessage(ChatColor.GRAY + "Pasting new arena " + a.getID());
        }

        else if(e.getMessage().equalsIgnoreCase("-gs")) {
            e.setCancelled(true);
            DynamicManager.get(GameServerMenu.class).open(p);
        } else {
            e.setCancelled(true);
            GameServer.getInstance().getRedisManager().publish("dn/server/gameserver-chat", ComponentSerializer.toString(rank.create()));
        }
    }
}
