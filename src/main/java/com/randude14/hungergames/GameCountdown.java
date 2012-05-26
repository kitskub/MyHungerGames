package com.randude14.hungergames;

import org.bukkit.ChatColor;

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
			Plugin.broadcast("Resuming %s in %s...",
					game.getName(), Plugin.formatTime(countdown));
		}
		else {
			Plugin.broadcast("Starting %s in %s...",
					game.getName(), Plugin.formatTime(countdown));
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
		ChatColor color = ChatColor.GREEN;
		if(countdown <= 5)
			color = ChatColor.GOLD;
		if(countdown <= 3)
			color = ChatColor.RED;
		Plugin.broadcastRaw(color, "%s...", Plugin.formatTime(countdown));
	}

}
