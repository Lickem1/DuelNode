package tk.duelnode.api.util.plasma;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class PlasmaBoard {

	private final List<PlasmaBoardEntry> entries = new ArrayList<>();
	private final List<String> identifiers = new ArrayList<>();
	private Scoreboard scoreboard;
	private Objective objective;
	private UUID uuid;

	private Plasma plasma;

	public PlasmaBoard(Player player, Plasma assemble) {
		this.plasma = assemble;
		this.setup(player);
		this.uuid = player.getUniqueId();
	}

	private void setup(Player player) {
		if (getPlasma().isHook() || !(player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = player.getScoreboard();
		} else {
			this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		}

		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(getPlasma().getAdapter().getTitle(player));

		player.setScoreboard(this.scoreboard);
	}

	public PlasmaBoardEntry getEntryAtPosition(int pos) {
		if (pos >= this.entries.size()) {
			return null;
		} else {
			return this.entries.get(pos);
		}
	}

	public String getUniqueIdentifier(String text) {
		String identifier = getRandomChatColor() + ChatColor.WHITE;

		while (this.identifiers.contains(identifier)) {
			identifier = identifier + getRandomChatColor() + ChatColor.WHITE;
		}

		if (identifier.length() > 16) {
			return this.getUniqueIdentifier(text);
		}

		this.identifiers.add(identifier);
		return identifier;
	}

	private static String getRandomChatColor() {
		return ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)].toString();
	}
}
