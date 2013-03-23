package com.randude14.hungergames.games;

import com.randude14.hungergames.Defaults.Config;
import com.randude14.hungergames.HungerGames;
import com.randude14.hungergames.Logging;
import com.randude14.hungergames.api.Game;
import com.randude14.hungergames.api.event.GameEndEvent;
import com.randude14.hungergames.api.event.GamePauseEvent;
import com.randude14.hungergames.api.event.GameStartEvent;
import com.randude14.hungergames.utils.ChatUtils;
import com.randude14.hungergames.utils.EquatableWeakReference;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TimedGameListener implements Listener{
	private static WeakHashMap<EquatableWeakReference<Game>, TimedGameRunnable> runnables = new WeakHashMap<EquatableWeakReference<Game>, TimedGameRunnable>();
	private int taskId;
	private long timeLeft;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGamePause(GamePauseEvent event) {
		TimedGameRunnable get = runnables.get(new EquatableWeakReference<Game>(event.getGame()));
		if (get != null) {
			get.pause();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStart(GameStartEvent event) {
		if (!event.isResuming()) {
			new TimedGameRunnable(new EquatableWeakReference<Game>(event.getGame())).start();
		}
		else {
			TimedGameRunnable get = runnables.get(new EquatableWeakReference<Game>(event.getGame()));
			if (get != null) {
				get.resume();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onGameEnd(GameEndEvent event) {
		TimedGameRunnable get = runnables.get(new EquatableWeakReference<Game>(event.getGame()));
		if (get != null) {
			get.stop();
		}
	}

	public class TimedGameRunnable implements Runnable {
		private EquatableWeakReference<Game> game;


		public TimedGameRunnable(EquatableWeakReference<Game> game) {
			this.game = game;
		}

		public void run() {
			if (game.get() == null) {
				stop();
				return;
			}
			game.get().stopGame(false);
			ChatUtils.broadcast(game.get(), "Game %s has ended because it ran out of time!", game.get().getName());
			stop();
		}

		private void resume() {
			runnables.put(game, this);
			if (timeLeft <= 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), this, 5 * 20);
				return;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), this, timeLeft * 20);
		}

		private void pause() {
			if (game.get() == null) {
				stop();
				return;
			}
			long startTime = game.get().getStartTimes().get(game.get().getStartTimes().size() - 1);
			long endTime = game.get().getEndTimes().get(game.get().getEndTimes().size() - 1);
			long elapsed = (endTime - startTime) / 1000;
			timeLeft -= elapsed;
			Bukkit.getScheduler().cancelTask(taskId);
		}

		private void stop() {
			Bukkit.getScheduler().cancelTask(taskId);
			runnables.put(game, null);
		}

		private void start() {
			if (game.get() == null) return;
			timeLeft = Config.MAX_GAME_DURATION.getInt(game.get().getSetup()) * 60;
			if (timeLeft <= 0) return;
			runnables.put(game, this);
			Logging.debug("Scheduled TimedGameRunnable for "  + timeLeft * 20);
			Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.getInstance(), this, timeLeft * 20);
		}
	}
}
