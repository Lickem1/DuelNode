package tk.duelnode.api.util.plasma;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;

public class PlasmaThread extends Thread {

    private final Plasma plasma;

    PlasmaThread(Plasma assemble) {
        this.plasma = assemble;
        this.start();
    }

    @Override
    public void run() {
        while(true) {
            //Tick
            try {
                tick();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            //Thread Sleep
            try {
                sleep(plasma.getTicks() * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        for (Player player : this.plasma.getPlugin().getServer().getOnlinePlayers()) {
            PlasmaBoard board = this.plasma.getBoards().get(player.getUniqueId());

            if (board == null) {
                continue;
            }

            Scoreboard scoreboard = board.getScoreboard();
            Objective objective = board.getObjective();

            String title = ChatColor.translateAlternateColorCodes('&', this.plasma.getAdapter().getTitle(player));

            if (!objective.getDisplayName().equals(title)) {
                objective.setDisplayName(title);
            }

            List<String> newLines = this.plasma.getAdapter().getLines(player);

            if (newLines == null || newLines.isEmpty()) {
                board.getEntries().forEach(PlasmaBoardEntry::remove);
                board.getEntries().clear();
            } else {
                if (!this.plasma.getPlasmaStyle().isDecending()) {
                    Collections.reverse(newLines);
                }

                if (board.getEntries().size() > newLines.size()) {
                    for (int i = newLines.size(); i < board.getEntries().size(); i++) {
                        PlasmaBoardEntry entry = board.getEntryAtPosition(i);

                        if (entry != null) {
                            entry.remove();
                        }
                    }
                }

                int cache = this.plasma.getPlasmaStyle().getStartNumber();
                for (int i = 0; i < newLines.size(); i++) {
                    PlasmaBoardEntry entry = board.getEntryAtPosition(i);

                    String line = ChatColor.translateAlternateColorCodes('&', newLines.get(i));

                    if (entry == null) {
                        entry = new PlasmaBoardEntry(board, line);
                    }

                    entry.setText(line);
                    entry.setup();
                    entry.send(
                            this.plasma.getPlasmaStyle().isDecending() ? cache-- : cache++
                    );
                }
            }
        }
    }
}
