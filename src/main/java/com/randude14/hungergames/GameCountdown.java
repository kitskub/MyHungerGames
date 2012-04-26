package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;

public class GameCountdown implements Runnable {
	private static final int DEFAULT_COUNTDOWN = 10;
	private final HungerGame game;
	private int countdown;
	private int taskId;

	public GameCountdown(final HungerGame game, int num) {
		this.game = game;
		countdown = num;
		taskId = Plugin.scheduleTask(this, 20L, 20L);
		Plugin.broadcast(String.format("Starting %s in %d second(s)...",
				game.getName(), countdown));
	}

	public GameCountdown(final HungerGame game) {
		this(game, DEFAULT_COUNTDOWN);
	}

	public void run() {
		if (countdown <= 1) {
			Plugin.cancelTask(taskId);
			game.startGame();
			game.setCounting(false);
			Plugin.broadcastRaw("Start!!");
			return;
		}
		countdown--;
		Plugin.broadcastRaw(countdown + "...");
	}

}
