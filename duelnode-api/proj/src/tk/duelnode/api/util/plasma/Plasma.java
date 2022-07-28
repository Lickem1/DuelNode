package tk.duelnode.api.util.plasma;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Plasma {

	private JavaPlugin plugin;
	private PlasmaAdapter adapter;
	private Map<UUID, PlasmaBoard> boards;
	private PlasmaThread thread;
	private PlasmaListener listeners;
	private long ticks = 2;
	private boolean hook = false;
	private PlasmaStyle plasmaStyle = PlasmaStyle.MODERN;

	public Plasma(JavaPlugin plugin, PlasmaAdapter adapter) {
		if (plugin == null) {
			throw new RuntimeException("No plugin instance");
		}

		this.plugin = plugin;
		this.adapter = adapter;
		this.boards = new ConcurrentHashMap<>();

		this.setup();
	}

	public void setup() {
		listeners = new PlasmaListener(this);

		this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {

			getBoards().put(player.getUniqueId(), new PlasmaBoard(player, this));
		}

		this.thread = new PlasmaThread(this);
	}

	public void cleanup() {
		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}

		if (listeners != null) {
			HandlerList.unregisterAll(listeners);
			listeners = null;
		}

		for (UUID uuid : getBoards().keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			getBoards().remove(uuid);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

}
