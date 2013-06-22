package me.kitskub.hungergames.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.event.GameEndEvent;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.api.event.PlayerJoinGameEvent;
import me.kitskub.hungergames.api.event.PlayerKilledEvent;
import me.kitskub.hungergames.api.event.PlayerLeaveGameEvent;
import me.kitskub.hungergames.games.User;
import me.kitskub.hungergames.stats.PlayerStat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ActivityListener implements Listener, Runnable {
	private Map<Game, Map<String, Long>> times = new HashMap<Game, Map<String, Long>>();

	public ActivityListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.getInstance(), this, 10*20, 10*20);
	}
	
	public void run() {
		for (Iterator<Game> it = times.keySet().iterator(); it.hasNext();) {
			Game game = it.next();
			if (times.get(game) == null) times.put(game, new HashMap<String, Long>());
			long maxTime = Config.TIMEOUT.getInt(game.getSetup()) * 1000;
			if (maxTime <= 0) continue;
			for (String s : times.get(game).keySet()) {
				if ((System.currentTimeMillis() - times.get(game).get(s)) >= maxTime) {
					Player p = Bukkit.getPlayer(s);
					if (p != null) {
						game.quit(p, true);	
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
		User user = User.get(p);
		Game game = user.getGameInEntry().getGame();
		if (game == null) return;
		if (!times.containsKey(game)) times.put(game, new HashMap<String, Long>());
		times.get(game).put(p.getName(), System.currentTimeMillis());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		times.remove(event.getGame());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		if (!times.containsKey(event.getGame())) times.put(event.getGame(), new HashMap<String, Long>());
		for (User u : event.getGame().getRemainingPlayers()) {
			times.get(event.getGame()).put(u.getPlayer().getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinGameEvent event) {
		if (!times.containsKey(event.getGame())) times.put(event.getGame(), new HashMap<String, Long>());
		if (event.getGame().getState() == Game.GameState.RUNNING) {
			times.get(event.getGame()).put(event.getPlayer().getName(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKill(PlayerKilledEvent event) {
		User user = User.get(event.getKilled());
		if (user.getState() == PlayerStat.PlayerState.DEAD) {
			times.get(event.getGame()).remove(event.getKilled().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerLeaveGameEvent event) {
		times.get(event.getGame()).remove(event.getPlayer().getName());
	}
}
