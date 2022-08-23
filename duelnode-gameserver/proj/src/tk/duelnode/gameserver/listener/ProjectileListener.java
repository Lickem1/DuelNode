package tk.duelnode.gameserver.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.manager.dynamic.DynamicListener;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

@Init(classType = ClassType.CONSTRUCT)
public class ProjectileListener extends DynamicListener {

    @EventHandler
    public void onhit(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if(e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if(!(arrow.getShooter() instanceof Player)) return;

                Player shooter = (Player) arrow.getShooter();
                double health = Math.ceil(p.getHealth() - e.getFinalDamage()) / 2.0;
                if(health > 0.0) {
                    shooter.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6(*) &b" + p.getName() + " &fhas been shot &7(&b" + health +"&c ❤'s&7)"));
                }
                arrow.remove();
            }
        }
    }
}
