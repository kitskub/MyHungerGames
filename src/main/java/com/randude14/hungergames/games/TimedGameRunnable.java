package com.randude14.hungergames.games;

import com.randude14.hungergames.Config;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.event.GamePauseEvent;
import com.randude14.hungergames.api.event.GameStartEvent;
import com.randude14.hungergames.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TimedGameRunnable implements Runnable, Listener{
	private static Map<HungerGame, TimedGameRunnable> runnables = new HashMap<HungerGame, TimedGameRunnable>();
	private HungerGame game;
	private int taskId;
	private long timeLeft;
	
	private TimedGameRunnable setGame(HungerGame game) {
		this.game = game;
		return this;
	}

//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public static void onGameEnd(GameEndEvent event) {
//		TimedGameRunnable get = runnables.get(event.getGame());
//		if (get != null) {
//			get.stop();
//			runnables.put(event.getGame(), null);
//		}
//	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onGamePause(GamePauseEvent event) {
		TimedGameRunnable get = runnables.get(event.getGame());
		if (get != null) {
			get.pause();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onGameStart(GameStartEvent event) {
		if (!event.isResuming()) {
			new TimedGameRunnable().setGame(event.getGame()).start();
		}
		else {
			TimedGameRunnable get = runnables.get(event.getGame());
			if (get != null) {
				get.resume();
			}	
		}
	}

	public void run() {
		game.stopGame(false);
		ChatUtils.broadcast(true, "Game %s has ended because it ran out of time!", game.getName());
		stop();
	}
	
	private void resume() {
		if (timeLeft <= 0) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), this, 5 * 20);
			return;
		}
		Bukkit.getScheduler().scheduleAsyncDelayedTask(HungerGames.getInstance(), this, timeLeft * 20);
	}
	
	private void pause() {
		long startTime = game.getStartTimes().get(game.getStartTimes().size() - 1);
		long endTime = game.getEndTimes().get(game.getEndTimes().size() - 1);
		long elapsed = (endTime - startTime) / 1000;
		timeLeft -= elapsed;
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	private void stop() {
		Bukkit.getScheduler().cancelTask(taskId);
		runnables.put(game, null);
	}
	
	private void start() {
		timeLeft = Config.getMaxGameDuration(game.getSetup()) * 1000;
		if (timeLeft <= 0) return;
		runnables.put(game, this);
		Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), this, timeLeft * 20);
	}

}
