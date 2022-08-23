package tk.duelnode.lobby.data.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.lobby.Plugin;

import java.util.LinkedList;

public class ListCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String sss, String[] strings) {

        if(!(commandSender instanceof Player)) return false;

        Player p = (Player) commandSender;
        if(command.getLabel().equalsIgnoreCase("list")) {
            StringBuilder builder = new StringBuilder();
            LinkedList<String> newList = new LinkedList<>();

            for(DNServerData serverData : DNServerManager.getAllServerData(Plugin.getInstance().getRedisManager())) {
                for(String oldListEntries : serverData.onlinePlayers) {
                    if(oldListEntries.equalsIgnoreCase("Lickem")) newList.addFirst(ChatColor.DARK_AQUA + oldListEntries);
                    else newList.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + oldListEntries);
                }
            }

            for(String newListEntries : newList) {
                builder.append(newListEntries).append(ChatColor.WHITE);
                if(newList.indexOf(newListEntries) < (newList.size()-1)) {
                    builder.append(", ");
                }
            }

            String[] formats = {
                    "&fCurrent Online Players &7(&b" + Plugin.getInstance().getFixedPlayerCount() + "&3/???&7)",
                    "&3Dev&f, &7&oMember",
                    " ",
                    builder.toString()
            };

            for(String s : formats) p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));


        }
        return true;
    }
}
