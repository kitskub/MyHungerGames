package me.kitskub.hungergames;

import java.util.Map;
import java.util.WeakHashMap;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.event.GameEndEvent;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.api.event.PlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class ScoreboardHandler implements Listener {
	private static final Map<Game, Scoreboard> boards = new WeakHashMap<Game, Scoreboard>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameStart(GameStartEvent event) {
		if (!Defaults.Config.USE_SCOREBOARD.getBoolean(event.getGame().getSetup())) return;
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		boards.put(event.getGame(), board);
		Objective players = board.registerNewObjective("Players", "dummy");
		players.setDisplaySlot(DisplaySlot.SIDEBAR);
		players.setDisplayName("Players - Lives left");
		for (Player p : event.getGame().getRemainingPlayers()) {
			players.getScore(p).setScore(event.getGame().getPlayerStat(p).getLivesLeft());
			p.setScoreboard(board);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		if (!Defaults.Config.USE_SCOREBOARD.getBoolean(event.getGame().getSetup())) return;
		Scoreboard get = boards.get(event.getGame());
		get.resetScores(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(GameEndEvent event) {
		if (!Defaults.Config.USE_SCOREBOARD.getBoolean(event.getGame().getSetup())) return;
		Scoreboard get = boards.get(event.getGame());
		for (Player p : event.getGame().getRemainingPlayers()) {
			get.resetScores(p);
		}
	}
	
	public static void updateLives(Game game, OfflinePlayer p, int newLives) {
		Scoreboard get = boards.get(game);
		if (get != null) get.getObjective("Players").getScore(p).setScore(newLives);
	}
}
