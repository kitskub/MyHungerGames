package com.randude14.hungergames.games;

import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.event.GameStopEvent;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerQueueHandler implements Listener, Runnable {
	private static final Queue<String> queuedPlayers = new LinkedList<String>();
	private static final Queue<HungerGame> queuedGames = new LinkedList<HungerGame>();
	private static boolean enabled = false;

	public PlayerQueueHandler() {
		enabled = true;
	}

	public static void addPlayer(Player player) {
		if (!enabled) return;
		queuedPlayers.offer(player.getName());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent event) {
		gameDone(event.getGame());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStop(GameStopEvent event) {
		gameDone(event.getGame());
	}
	
	public void gameDone(HungerGame game) {
		if (!enabled) return;
		Bukkit.getScheduler().scheduleAsyncDelayedTask(HungerGames.getInstance(), this, 20 * 10);
		queuedGames.offer(game);
	}
	
	public void run() {
		HungerGame game = queuedGames.poll();
		if (game.isRunning() || game.isCounting() || game.isPaused() || !game.isEnabled()) return;
		for (int i = 0; i < Math.min(game.getSize(), queuedPlayers.size()); i++) {
			Player p = Bukkit.getPlayer(queuedPlayers.poll());
			if (p == null) continue;
			ChatUtils.send(p, "You have been selected to join the game %s", game.getName());
			game.join(p);
		}
	}

}
