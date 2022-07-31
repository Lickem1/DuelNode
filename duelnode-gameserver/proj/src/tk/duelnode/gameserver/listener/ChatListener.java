package tk.duelnode.gameserver.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.gameserver.data.arena.Arena;
import tk.duelnode.gameserver.data.arena.Cube;
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

            Arena a = arenaManager.getFreeArena();
            p.sendMessage("§7Commencing teleportation to arena " + a.getID());

            Bukkit.getServer().getScheduler().runTaskLater(GameServer.getInstance(), new Runnable() {
                @Override
                public void run() {
                    p.teleport(a.getLoc1());
                }
            }, 3*20L);
        }

        else if(e.getMessage().equalsIgnoreCase("list")) {
            e.setCancelled(true);
            int templateArenas = arenaManager.getAllArenas().size();

            p.sendMessage(" ");
            p.sendMessage("Template Arenas (" + templateArenas + ")");
            p.sendMessage("Available Arenas ("+ arenaManager.getAvailableArenas().size()+ ")");
            for(Arena arena : arenaManager.getAvailableArenas()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', arena.getDisplayName() + " - " + arena.getID() + " - (" + arena.getCenter().getX() + ", " + arena.getCenter().getY() + ", " + arena.getCenter().getZ() + ")"));
            }
            p.sendMessage("Unavailable Arenas ("+ arenaManager.getArenasInUse().size()+ ")");
        }

        else if(e.getMessage().equalsIgnoreCase("test")) {
            e.setCancelled(true);
            Arena arena= arenaManager.getAllArenas().get("battlepit");
            Cube cube = arena.getCuboid();

            String[] s = {
                    "&3ID: &f" + arena.getID(),
                    "&7Clipboard: " + (arena.getClipboard() != null ? "&aACTIVE" : "&cINACTIVE"),
                    "&7DisplayName:&f " + arena.getDisplayName(),
                    "&7Cube: ",
                    "   &f* &8World:&f " + cube.getWorld().getName(),
                    "   &f* &8X:&f " + cube.getBlockX().getX() + ", " + cube.getBlockX().getY() + ", " + cube.getBlockX().getZ(),
                    "   &f* &8Z:&f " + cube.getBlockZ().getX() + ", " + cube.getBlockZ().getY() + ", " + cube.getBlockZ().getZ(),
                    "&7Loc 1:&f " + arena.getLoc1().getX() + ", " + arena.getLoc1().getY() + ", " + arena.getLoc1().getZ(),
                    "&7Loc 2:&f " + arena.getLoc2().getX() + ", " + arena.getLoc2().getY() +", " + arena.getLoc2().getZ(),
                    "&7Loc Center:&f " + arena.getCenter().getX() + ", " + arena.getCenter().getY() + ", " + arena.getCenter().getZ()
            };

            for(String ss : s) p.sendMessage(ChatColor.translateAlternateColorCodes('&', ss));

        }
    }
}
