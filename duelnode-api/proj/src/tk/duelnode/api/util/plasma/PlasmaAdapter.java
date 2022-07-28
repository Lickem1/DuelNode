package tk.duelnode.api.util.plasma;

import org.bukkit.entity.Player;

import java.util.List;

public interface PlasmaAdapter {

	String getTitle(Player player);

	List<String> getLines(Player player);

}
