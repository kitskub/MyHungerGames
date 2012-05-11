package com.randude14.hungergames;

import com.randude14.hungergames.games.HungerGame;

public class GameCountdown implements Runnable {
	private final HungerGame game;
	private int countdown;
	private int taskId;

	public GameCountdown(final HungerGame game, int num, boolean isResuming) {
		this.game = game;
		countdown = num;
		taskId = Plugin.scheduleTask(this, 20L, 20L);
		if(isResuming) {
			Plugin.broadcast("Resuming %s in %d second(s)...",
					game.getName(), countdown);
		}
		else {
			Plugin.broadcast("Starting %s in %d second(s)...",
					game.getName(), countdown);
		}

	}

	public GameCountdown(final HungerGame game, int num) {
		this(game, num, false);
	}
	
	public GameCountdown(final HungerGame game, boolean isResuming) {
		this(game, Config.getDefaultTime(game.getSetup()), isResuming);
	}
	
	public GameCountdown(final HungerGame game) {
		this(game, Config.getDefaultTime(game.getSetup()), false);
	}
	
	public void cancel() {
		Plugin.cancelTask(taskId);
	}

	public void run() {
		if (countdown <= 1) {
			Plugin.cancelTask(taskId);
			game.startGame();
			Plugin.broadcastRaw("Start!!");
			return;
		}
		countdown--;
		Plugin.broadcastRaw(countdown + "...");
	}

}
