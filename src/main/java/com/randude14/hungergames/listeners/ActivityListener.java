package com.randude14.hungergames.listeners;

import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.GameManager;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.Game;
import com.randude14.hungergames.api.event.*;
import com.randude14.hungergames.games.HungerGame;
import com.randude14.hungergames.stats.PlayerStat;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ActivityListener implements Listener, Runnable {
	private Map<EquatableWeakReference<? extends Game>, Map<String, Long>> times = new HashMap<EquatableWeakReference<? extends Game>, Map<String, Long>>();

	public ActivityListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getInstance(), this, 10*20, 10*20);
	}
	
	public void run() {
		for (Iterator<EquatableWeakReference<? extends Game>> it = times.keySet().iterator(); it.hasNext();) {
			EquatableWeakReference<? extends Game> ref = (EquatableWeakReference<? extends Game>) it.next();
			if (ref.get() == null) {
				it.remove();
				continue;
			}
			Game game = ref.get();
			if (times.get(ref) == null) times.put(ref, new HashMap<String, Long>());
			long maxTime = Config.TIMEOUT.getInt(game.getSetup()) * 1000;
			if (maxTime <= 0) continue;
			for (String s : times.get(ref).keySet()) {
				if ((System.currentTimeMillis() - times.get(ref).get(s)) >= maxTime) {
					Player p = Bukkit.getPlayer(s);
					if (p != null) {
						game.leave(p, true);	
					}
					times.get(ref).remove(p.getName());
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
		WeakReference<HungerGame> game = GameManager.INSTANCE.getPlayingSession(p);
		if (game == null) return;
		EquatableWeakReference<HungerGame> eGame = new EquatableWeakReference<HungerGame>(game);
		if (!times.containsKey(eGame)) times.put(eGame, new HashMap<String, Long>());
		times.get(eGame).put(p.getName(), System.currentTimeMillis());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		times.remove(new EquatableWeakReference<Game>(event.getGame()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		times.remove(new EquatableWeakReference<Game>(event.getGame()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		EquatableWeakReference<Game> eGame = new EquatableWeakReference<Game>(event.getGame());
		if (!times.containsKey(eGame)) times.put(eGame, new HashMap<String, Long>());
		for (Player p : event.getGame().getRemainingPlayers()) {
			times.get(eGame).put(p.getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		EquatableWeakReference<Game> eGame = new EquatableWeakReference<Game>(event.getGame());
		if (!times.containsKey(eGame)) times.put(eGame, new HashMap<String, Long>());
		if (event.getGame().getState() == Game.GameState.RUNNING) {
			times.get(eGame).put(event.getPlayer().getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKillEvent event) {
		if (event.getGame().getPlayerStat(event.getKilled()).getState() == PlayerStat.PlayerState.DEAD) {
			times.get(new EquatableWeakReference<Game>(event.getGame())).remove(event.getKilled().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		times.get(new EquatableWeakReference<Game>(event.getGame())).remove(event.getPlayer().getName());
	}
}
