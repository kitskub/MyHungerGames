package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ActivityListener implements Listener, Runnable {
	private Map<HungerGame, Map<String, Long>> times = new HashMap<HungerGame, Map<String, Long>>();

	public ActivityListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getInstance(), this, 10*20, 10*20);
	}
	
	public void run() {
		for (HungerGame game : times.keySet()) {
			if (times.get(game) == null) times.put(game, new HashMap<String, Long>());
			long maxTime = Config.getTimeout(game.getSetup()) * 1000;
			if (maxTime <= 0) continue;
			for (String s : times.get(game).keySet()) {
				if ((System.currentTimeMillis() - times.get(game).get(s)) >= maxTime) {
					Player p = Bukkit.getPlayer(s);
					if (p != null) {
						game.leave(p, true);	
					}
					times.get(game).remove(p.getName());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		update(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(AsyncPlayerChatEvent event) {
		update(event.getPlayer());
	}
	
	public void update(Player p) {
		HungerGame game = GameManager.INSTANCE.getPlayingSession(p);
		if (game == null) return;
		if (!times.containsKey(game)) times.put(game, new HashMap<String, Long>());
		times.get(game).put(p.getName(), System.currentTimeMillis());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		times.remove(event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		times.remove(event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		if (!times.containsKey(event.getGame())) times.put(event.getGame(), new HashMap<String, Long>());
		for (Player p : event.getGame().getRemainingPlayers()) {
			times.get(event.getGame()).put(p.getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		if (!times.containsKey(event.getGame())) times.put(event.getGame(), new HashMap<String, Long>());
		if (event.getGame().getState() == HungerGame.GameState.RUNNING) {
			times.get(event.getGame()).put(event.getPlayer().getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKillEvent event) {
		if (event.getGame().getPlayerStat(event.getKilled()).getState() == PlayerStat.PlayerState.DEAD) {
			times.get(event.getGame()).remove(event.getKilled().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		times.get(event.getGame()).remove(event.getPlayer().getName());
	}
}
