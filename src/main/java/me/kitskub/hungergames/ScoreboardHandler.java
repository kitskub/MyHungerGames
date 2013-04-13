package me.kitskub.hungergames;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.utils.EquatableWeakReference;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class ScoreboardHandler implements Listener {
	private static final Map<WeakReference<Game>, Scoreboard> boards = new WeakHashMap<WeakReference<Game>, Scoreboard>();
	
	@EventHandler
	public void onGameStart(GameStartEvent event) {
		if (!Defaults.Config.USE_SCOREBOARD.getBoolean(event.getGame().getSetup())) return;
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		boards.put(new EquatableWeakReference<Game>(event.getGame()), board);
		Objective players = board.registerNewObjective("Players", "dummy");
		players.setDisplaySlot(DisplaySlot.SIDEBAR);
		players.setDisplayName("Players - Lives left");
		for (Player p : event.getGame().getRemainingPlayers()) {
			players.getScore(p).setScore(event.getGame().getPlayerStat(p).getLivesLeft());
		}
	}
}
