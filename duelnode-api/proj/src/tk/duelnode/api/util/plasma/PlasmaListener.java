package tk.duelnode.api.util.plasma;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class PlasmaListener implements Listener {

	private final Plasma plasma;

	public PlasmaListener(Plasma plasma) {
		this.plasma = plasma;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		getPlasma().getBoards().put(event.getPlayer().getUniqueId(), new PlasmaBoard(event.getPlayer(), getPlasma()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		getPlasma().getBoards().remove(event.getPlayer().getUniqueId());
		event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
}
