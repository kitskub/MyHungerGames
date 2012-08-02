package com.randude14.hungergames.stats;

import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.games.HungerGame.GameState;
import com.randude14.hungergames.stats.PlayerStat.PlayerState;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
	public void onPlayerKick(PlayerKickGameEvent event) {
		playerStopped(event.getGame(), event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKillEvent event) {
		if (event.getGame().getPlayerStat(event.getKilled()).getState() == PlayerState.DEAD) {
			playerStopped(event.getGame(), event.getKilled());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		playerStopped(event.getGame(), event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitGameEvent event) {
		playerStopped(event.getGame(), event.getPlayer());
	}
	
	private void playerStarted(Player p) {
		startTimes.put(p.getName(), new Date().getTime());
	}
	
	private void playerStopped(HungerGame game, Player p) {
		Long l = startTimes.get(p.getName());
		if (l != null) {
			game.getPlayerStat(p).addTime(new Date().getTime() - l);
			startTimes.remove(p.getName());
		}
	}
}
