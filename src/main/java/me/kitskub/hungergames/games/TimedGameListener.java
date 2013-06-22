package me.kitskub.hungergames.games;

import me.kitskub.hungergames.Defaults.Config;
import me.kitskub.hungergames.HungerGames;
import me.kitskub.hungergames.Logging;
import me.kitskub.hungergames.api.Game;
import me.kitskub.hungergames.api.event.GameEndEvent;
import me.kitskub.hungergames.api.event.GameStartEvent;
import me.kitskub.hungergames.utils.ChatUtils;
import me.kitskub.hungergames.utils.EquatableWeakReference;

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
	public void onGameStart(GameStartEvent event) {
		new TimedGameRunnable(new EquatableWeakReference<Game>(event.getGame())).start();
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
