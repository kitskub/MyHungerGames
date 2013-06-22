package me.kitskub.hungergames.stats;

import me.kitskub.hungergames.api.event.GamePauseEvent;
import me.kitskub.hungergames.api.event.GameEndEvent;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.api.event.PlayerKilledEvent;
import me.kitskub.hungergames.api.event.PlayerLeaveGameEvent;
import me.kitskub.hungergames.api.event.PlayerJoinGameEvent;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.Game.GameState;
import me.kitskub.hungergames.stats.PlayerStat.PlayerState;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.kitskub.hungergames.games.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Used for stats per-player
 */
public class TimeListener implements Listener {
	private Map<String, Long> startTimes = new HashMap<String, Long>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		for (User p : event.getGame().getRemainingPlayers()) {
			playerStopped(event.getGame(), p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		for (User p : event.getGame().getRemainingPlayers()) {
			playerStopped(event.getGame(), p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		for (User p : event.getGame().getRemainingPlayers()) {
			playerStarted(p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		if (event.getGame().getState() == GameState.RUNNING) playerStarted(User.get(event.getPlayer()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKilledEvent event) {
		if (User.get(event.getKilled()).getState() == PlayerState.DEAD) {
			playerStopped(event.getGame(), User.get(event.getKilled()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		playerStopped(event.getGame(), User.get(event.getPlayer()));
	}
	
	private void playerStarted(User p) {
		startTimes.put(p.getPlayer().getName(), new Date().getTime());
	}
	
	private void playerStopped(Game game, User p) {
		Long l = startTimes.get(p.getPlayer().getName());
		if (l != null) {
			p.getStat().get(game).addTime(new Date().getTime() - l);
			startTimes.remove(p.getPlayer().getName());
		}
	}
}
