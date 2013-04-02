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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
		for (Player p : event.getGame().getRemainingPlayers()) {
			playerStopped(event.getGame(), p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		for (Player p : event.getGame().getRemainingPlayers()) {
			playerStopped(event.getGame(), p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		for (Player p : event.getGame().getRemainingPlayers()) {
			playerStarted(p);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		if (event.getGame().getState() == GameState.RUNNING) playerStarted(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKilledEvent event) {
		if (event.getGame().getPlayerStat(event.getKilled()).getState() == PlayerState.DEAD) {
			playerStopped(event.getGame(), event.getKilled());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		playerStopped(event.getGame(), event.getPlayer());
	}
	
	private void playerStarted(Player p) {
		startTimes.put(p.getName(), new Date().getTime());
	}
	
	private void playerStopped(Game game, OfflinePlayer p) {
		Long l = startTimes.get(p.getName());
		if (l != null) {
			game.getPlayerStat(p).addTime(new Date().getTime() - l);
			startTimes.remove(p.getName());
		}
	}
}
