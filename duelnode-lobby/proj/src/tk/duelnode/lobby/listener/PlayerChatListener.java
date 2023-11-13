package tk.duelnode.lobby.listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tk.duelnode.api.game.data.GameCondition;
import tk.duelnode.api.game.data.GlobalGame;
import tk.duelnode.api.game.data.GlobalGamePlayer;
import tk.duelnode.api.game.data.GlobalGameType;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.manager.dynamic.DynamicListener;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Init(classType = ClassType.CONSTRUCT)
public class PlayerChatListener extends DynamicListener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);

        net.md_5.bungee.api.ChatColor gray = net.md_5.bungee.api.ChatColor.GRAY;
        String format = gray + "[" + "%s" + gray + "]%s " + p.getName() + gray + ": ";

        if (p.getName().equalsIgnoreCase("Lickem"))
            format = String.format(format, net.md_5.bungee.api.ChatColor.DARK_AQUA + "Dev", net.md_5.bungee.api.ChatColor.AQUA);
        else
            format = String.format(format, net.md_5.bungee.api.ChatColor.WHITE + "Member", net.md_5.bungee.api.ChatColor.GRAY.toString() + net.md_5.bungee.api.ChatColor.ITALIC);

        ComponentBuilder rank = new ComponentBuilder(format);
        rank.event(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(ChatColor.GRAY + "Click to view " + ChatColor.AQUA + p.getName() + "'s" + ChatColor.GRAY + " profile!")
        ));
        rank.event(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/profile " + p.getName()
        ));
        rank.append("", ComponentBuilder.FormatRetention.NONE);

        // chat mentioning format
        ComponentBuilder component = new ComponentBuilder("");
        String[] message = e.getMessage().split(" ");
        for (int i = 0; i < message.length; i++) {
            String player = isPlayerMention(message[i]);

            if (isWebsiteMention(message[i])) {
                component.append(net.md_5.bungee.api.ChatColor.GREEN.toString() + "<URL>");
                component.event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(ChatColor.GRAY + message[i])
                ));
                component.event(new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        message[i]
                ));
                component.append(" ", ComponentBuilder.FormatRetention.NONE);
            }
            if (player != null) {
                component.append(TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC + "@" + player));
                component.event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(ChatColor.GRAY + "Click to message player")
                ));
                component.event(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/msg " + player + " Your message here!"
                ));
                component.append(" ", ComponentBuilder.FormatRetention.NONE);
            }
            if (player == null && !isWebsiteMention(message[i])) {
                component.append(message[i]).append(" ");

            }
        }

        rank.append(component.create());

        if (e.getMessage().equalsIgnoreCase("test_games")) {
            for (int i = 0; i < 10; i++) {
                GlobalGame gD = new GlobalGame(GlobalGameType.DUEL);
                gD.setGameServer("na-mini-01");
                gD.addTeam1(new GlobalGamePlayer("Player" + new Random().nextInt(2000), UUID.randomUUID()));
                gD.addTeam2(new GlobalGamePlayer("Player" + new Random().nextInt(2000), UUID.randomUUID()));

                gD.message(GameCondition.CREATE, Plugin.getInstance().getRedisManager());
            }
            p.sendMessage(ChatColor.GRAY + "Creating fake duels");

        } else
            Plugin.getInstance().getRedisManager().publish("dn/server/gameserver-chat", ComponentSerializer.toString(rank.create()));
    }

    private String isPlayerMention(String message) {
        // globally todo make this better
        //List<DNServerData> dataList = DNServerManager.getAllServerData(Plugin.getInstance().getRedisManager());
        //dataList.removeIf(dnServerData -> !dnServerData.online);
        //for(DNServerData data : dataList) {
        //    for(String s : data.onlinePlayers) {
        //        if(message.equalsIgnoreCase(s)) {
        //            return s;
        //        }
        //    }
        //}

        // for now will be locally
        for (Player pp : Bukkit.getServer().getOnlinePlayers()) {
            if (message.equalsIgnoreCase(pp.getName())) {
                return pp.getName();
            }
        }
        return null;
    }

    private boolean isWebsiteMention(String message) {
        String m = message.toLowerCase(Locale.ROOT);
        if (m.startsWith("https://")
                || m.startsWith("http://")
                || m.contains(".com")
                || m.contains(".net")
                || m.contains(".org")
                || m.contains(".tk")
                || m.startsWith("www")) {
            return true;
        }
        return false;
    }
}
