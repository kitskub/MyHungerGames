package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;

public class GameCountdown implements Runnable {
	private static final int DEFAULT_COUNTDOWN = 10;
	private final Plugin plugin;
	private final HungerGame game;
	private int countdown;
	private int taskId;

	public GameCountdown(final Plugin plugin, final HungerGame game, int num) {
		this.plugin = plugin;
		this.game = game;
		countdown = num;
		taskId = plugin.scheduleTask(this, 20L, 20L);
		plugin.broadcast(String.format("starting %s in %d second(s)...",
				game.getName(), countdown));
	}

	public GameCountdown(final Plugin plugin, final HungerGame game) {
		this(plugin, game, DEFAULT_COUNTDOWN);
	}

	public void run() {
		if (countdown <= 1) {
			plugin.cancelTask(taskId);
			game.startGame();
			plugin.broadcastRaw("Start!!");
			return;
		}
		countdown--;
		plugin.broadcastRaw(countdown + "...");
	}

}
